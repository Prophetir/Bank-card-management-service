package com.example.bankcards.security;

import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@Component
public class RsaKeyAuthorityFilter extends OncePerRequestFilter {

    @Resource
    private JwtTokenService jwtTokenService;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {

        if (!(request.getServletPath().startsWith("auth/reg") || request.getServletPath().startsWith("auth/log")) &&
                (request.getHeader("Authorization") != null && request.getHeader("Authorization").startsWith("Bearer "))) {

            JWTClaimsSet claimToken = jwtTokenService.decoderToken(request.getHeader("Authorization").replace("Bearer ", "").trim());

            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(claimToken.getClaims().get("role").toString()));

            System.out.println(
                    "\n------| GrantedAuthority role |------\n"
                    + authorities.get(0) +
                    "\n------| GrantedAuthority role |------\n"
            );

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(claimToken.getSubject(), null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
