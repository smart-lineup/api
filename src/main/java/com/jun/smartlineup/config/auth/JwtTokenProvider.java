package com.jun.smartlineup.config.auth;

import com.jun.smartlineup.user.dto.CustomUserDetails;
import com.jun.smartlineup.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements AuthenticationSuccessHandler {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private long EXPIRATION_TIME;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Value("${frontend.url}")
    private String frontendUrl;
    @Value("${frontend.domain}")
    private String frontendDomain;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(decodedKey);
    }

    public String createToken(Authentication authentication) {
        OAuth2User user = (OAuth2User) authentication.getPrincipal();

        return Jwts.builder()
                .subject(user.getAttribute("email"))
                .claim("name", user.getAttribute("name"))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String createToken(com.jun.smartlineup.user.domain.User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("name", user.getName())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date expiration = claims.getExpiration();
            return expiration.after(new Date());
        } catch (Exception e) {
            System.out.println("valid Token Error: " + e.getMessage());
            return false;
        }
    }

    public Authentication getAuthenticationFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String email = claims.getSubject();
        String name = claims.get("name", String.class);

        CustomUserDetails userDetails = new CustomUserDetails(email, name, Collections.emptyList());
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Cookie jwtCookie = getJwtCookie(authentication);

        response.addCookie(jwtCookie);
        response.sendRedirect(frontendUrl + "/");
    }

    public Cookie getJwtCookie(Authentication authentication) {
        String token = createToken(authentication);
        return cookieFactory(token, 7 * 24 * 60 * 60);
    }

    public Cookie getJwtCookie(com.jun.smartlineup.user.domain.User user) {
        String token = createToken(user);
        return cookieFactory(token, 7 * 24 * 60 * 60);
    }

    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontendUrl));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    public Cookie cookieFactory(String token, int maxAge) {
        if (activeProfile.equals("local")) {
            Cookie jwtCookie = new Cookie("Authorization", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(maxAge);
            jwtCookie.setDomain(frontendDomain);
            jwtCookie.setAttribute("SameSite", "Lax");

            return jwtCookie;
        }

        // prod
        Cookie jwtCookie = new Cookie("Authorization", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true); // todo: need to change
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(maxAge);
        jwtCookie.setDomain(frontendDomain);
        jwtCookie.setAttribute("SameSite", "None"); // cors
        return jwtCookie;
    }

    public String getTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
