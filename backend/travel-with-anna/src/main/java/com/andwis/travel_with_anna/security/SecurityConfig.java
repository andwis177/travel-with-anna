package com.andwis.travel_with_anna.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.andwis.travel_with_anna.role.Role.getAdminAuthority;
import static com.andwis.travel_with_anna.role.Role.getUserAuthority;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig  {

    private final JwtFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers(
                                        "/auth/**",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-resources",
                                        "/swagger-resources/**",
                                        "/configuration/ui",
                                        "/configuration/security",
                                        "/swagger-ui/**",
                                        "/webjars/**",
                                        "/swagger-ui.html",
                                        "/role/all-names",
                                        "/static/**",
                                        "/public/**",
                                        "/resources/**"
                                ).permitAll()
                                .requestMatchers(
                                        "/user/**",
                                        "/avatar/**"
                                ).hasAnyAuthority(getUserAuthority(), getAdminAuthority())
                                .requestMatchers(
                                        "/expanse/**",
                                        "/note/**",
                                        "/day/**",
                                        "/budget/**",
                                        "/backpack/**",
                                        "/trip/**",
                                        "/api/country/**",
                                        "/pdf/reports/**"
                                ).hasAuthority(getUserAuthority())
                                .requestMatchers(
                                        "/admin/**"
                                ).hasAuthority(getAdminAuthority())
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/execute_logout")
                    .invalidateHttpSession(true)
                    .logoutSuccessHandler(customLogoutSuccessHandler)
                    .deleteCookies("JSESSIONID")
                )
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                );

        return http.build();
    }
}
