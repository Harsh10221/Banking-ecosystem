package com.banking.net_banking_system.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@Controller
public class JwtAuthFilter extends OncePerRequestFilter {


    @Value("${app.jwt.secret}")
    private String secretKey;

    private String accessToken;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        System.out.println("This is cookies" + Arrays.toString(cookies));


        if (cookies != null) {
        System.out.println(cookies[0].getName());

        for (Cookie cookie : cookies) {
            if (Objects.equals(cookie.getName(), "accessToken")) {
                System.out.println("I reached here");
                accessToken = cookie.getValue();
                break;
            }

        }

//        System.out.println("Access token" + accessToken);
        }
        if (accessToken != null) {

            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

            try {
                Jws<Claims> jws = Jwts.parser().verifyWith(key) // Modern replacement for setSigningKey()
                        .build().parseSignedClaims(cookies[0].getValue()); // Returns a Jws object containing payload & header

                Claims claims = jws.getPayload();

                String userId = claims.getSubject();
                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);


                    WebAuthenticationDetails details = (WebAuthenticationDetails) authToken.getDetails();

                    String ipAddress = details.getRemoteAddress();
//                    String sessionId = details.getSessionId();


//                    System.out.println("User IP Address: " + ipAddress);
//                    System.out.println("User authenticated: " + userId);

                    filterChain.doFilter(request, response);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            System.out.println("I am inside cookie if statment");
            return;


        }

        filterChain.doFilter(request, response);

    }
}

