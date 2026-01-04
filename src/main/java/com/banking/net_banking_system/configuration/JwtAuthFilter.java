package com.banking.net_banking_system.configuration;

import com.banking.net_banking_system.utils.ResponseUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
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
//        System.out.println("This is cookies" + Arrays.toString(cookies));


        if (cookies != null) {
            System.out.println(cookies[0].getName());

            for (Cookie cookie : cookies) {
                if (Objects.equals(cookie.getName(), "AccessToken")) {
                    accessToken = cookie.getValue();
                    break;
                }

            }

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

            } catch (ExpiredJwtException e) {
                ResponseUtils.writeErrorResponse(response, 401, "Token has expired");
                return;
            } catch (SignatureException e) {

                ResponseUtils.writeErrorResponse(response, 401, "Invalid token signature");
                return;
            } catch (Exception e) {
                ResponseUtils.writeErrorResponse(response, 401, "Authentication failed: " + e.getMessage());
                return; // Crucial: return so doFilter is NOT called
            }

            return;


        }

        filterChain.doFilter(request, response);

    }
}

