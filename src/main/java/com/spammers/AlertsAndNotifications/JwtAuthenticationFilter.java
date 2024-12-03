package com.spammers.AlertsAndNotifications;

import com.spammers.AlertsAndNotifications.service.implementations.ApiClient;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final ApiClient apiClient;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String token = getTokenFromRequest(request);
        System.out.println("token = " + token);
        if(token != null){
            boolean isValid = apiClient.validateToken(token);
            if(isValid){
                String role = decodePayload(token);
                UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken("12335"
                        ,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_"+role)));
                userToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(userToken);

            }
        }
        filterChain.doFilter(request,response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")){
            return authHeader.substring(7);
        }
        return null;
    }
    private String decodePayload(String token){
        // Separar el payload
        String[] parts = token.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));

        // Buscar el valor del claim "role"
        if (payload.contains("\"role\":")) {
            int startIndex = payload.indexOf("\"role\":") + 7;
            int endIndex = payload.indexOf("\"", startIndex + 1);
            String role = payload.substring(startIndex + 1, endIndex);
            System.out.println("Role: " + role);
            return role;
        } else {
           throw new RuntimeException("no existe ese rol");
        }

    }
}