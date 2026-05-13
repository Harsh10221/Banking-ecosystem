package com.banking.net_banking_system.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IpRateLimitingFilter extends OncePerRequestFilter {

    private static final String DASHBOARD_SECRET = "1133557799";
    private final ConcurrentHashMap<String, Integer> ipRequestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String clientIp = request.getRemoteAddr();
        String providedSecret = request.getParameter("secret");

        boolean hasValidSecret = DASHBOARD_SECRET.equals(providedSecret);

        // We only care about checking our specific WebSocket or API paths
        if (path.contains("/dashboard-stream") || path.contains("/api/transaction/transfer")) {

            System.out.println("\n--- SECURITY FILTER INTERCEPTED REQUEST ---");
            System.out.println("Path: " + path + " | IP: " + clientIp);

             if (path.contains("/api/transaction/transfer")) {
                if (!hasValidSecret) {
                    int count = ipRequestCounts.getOrDefault(clientIp, 0);
                    System.out.println(">> IP " + clientIp + " connection count: " + count);

                    if (count >= 2) {
                        System.out.println(">> REJECTED: Free trial over for this IP.");
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Limit reached.");
                        return; // Stop the chain! Connection dropped.
                    }

                    ipRequestCounts.put(clientIp, count + 1);
                    System.out.println(">> ALLOWED: Free pass used.");
                } else {
                    System.out.println(">> ALLOWED: Valid password provided.");
                }
            }
        }

        // If it passes our checks (or is a completely different URL), let Spring Security continue
        System.out.println("Running before filterchain");
        filterChain.doFilter(request, response);
    }
}



