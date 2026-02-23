package gdg.challenge.poom.global.security;

import gdg.challenge.poom.domain.member.service.MemberQueryService;
import gdg.challenge.poom.global.data.CorsConfigData;
import gdg.challenge.poom.global.security.filter.JwtFilter;
import gdg.challenge.poom.global.util.JwtUtil;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private static final String API_PREFIX = "/api/v1";
    private final CorsConfigData corsConfigData;
    private final JwtUtil jwtUtil;
    private final MemberQueryService memberQueryService;

    private String[] allowUrl = {
            API_PREFIX + "/auth/**",
            API_PREFIX + "/voice/**",
            API_PREFIX + "/chat/**",

            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",

            "/oauth2/authorization/**",
            "/login/oauth2/**",

            "/auth/google/callback/**",
            "favicon.ico",
            "/error"
    };


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(request -> request
                        .requestMatchers(allowUrl).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .cors( cors -> cors.configurationSource(corsConfigurationSource()))
                ;

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        corsConfigData.getUrls().forEach(configuration::addAllowedOrigin); // 실배포 주소 나중에 추가
        corsConfigData.getMethods().forEach(configuration::addAllowedMethod);
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    Filter jwtFilter() {
        return new JwtFilter(jwtUtil, memberQueryService);
    }
}
