/*
 * ----- @author Pasang Gelbu Sherpa ------ *
 */

package com.pasang.projectarchiver.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pasang.projectarchiver.constant.SystemMessage;
import com.pasang.projectarchiver.global.GlobalErrorResponse;
import com.pasang.projectarchiver.users.repository.UsersRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final UsersRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws IOException, ServletException {
        if (
                request.getRequestURI().startsWith("/api/v1/auth/login")
                        || request.getRequestURI().startsWith("/api/v1/users/register")
                        || request.getRequestURI().startsWith("/api/v1/auth/refresh")
                        || request.getRequestURI().startsWith("/files/")
                        || request.getRequestURI().startsWith("/v1")
                        || request.getRequestURI().startsWith("/api/v1/users/validate")
                        || request.getRequestURI().startsWith("/api/v1/users/set-password")

        ) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String authorizationHeader = request.getHeader("Authorization");

            String username = null;
            String jwt = null;
            String tokenType = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtUtil.extractUsername(jwt);
                tokenType = jwtUtil.extractClaim(jwt, claims -> claims.get("tokenType", String.class));
            }

            if (!"access".equals(tokenType)) {
                throw new BadCredentialsException(SystemMessage.INVALID_TOKEN_TYPE);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                userRepository.findByEmail(username)
                        .ifPresent(user -> {
                            if (Boolean.TRUE.equals(user.getIsDeleted()) || Boolean.FALSE.equals(user.getIsActive())) {
                                throw new BadCredentialsException(SystemMessage.USER_DEACTIVATED_OR_LOGGED_OUT_MSG);
                            }
                        });

                if (Boolean.TRUE.equals(jwtUtil.validateToken(jwt, userDetails))) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            handleException(response, e, String.valueOf(HttpServletResponse.SC_UNAUTHORIZED), SystemMessage.TOKEN_EXPIRED);
        } catch (Exception e) {
            handleException(response, e, String.valueOf(HttpServletResponse.SC_UNAUTHORIZED), SystemMessage.INVALID_TOKEN);
        }
    }

    private void handleException(HttpServletResponse response, Exception e, String status, String message) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        response.setStatus(Integer.parseInt(status));
        response.setContentType("application/json");
        GlobalErrorResponse exceptionResponse = new GlobalErrorResponse(
                LocalDateTime.now(),
                message,
                e.getMessage(),
                status
        );
        response.getWriter().write(objectMapper.writeValueAsString(exceptionResponse));
        response.getWriter().flush();
        response.getWriter().close();
    }
}