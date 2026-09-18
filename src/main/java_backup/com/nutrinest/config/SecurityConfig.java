package com.nutrinest.config;

import com.nutrinest.serviceimpl.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService,
            CustomAuthenticationSuccessHandler successHandler) {

        this.customUserDetailsService = customUserDetailsService;
        this.successHandler = successHandler;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                // =====================================================
                // CSRF
                // =====================================================

                .csrf(Customizer.withDefaults())


                // =====================================================
                // USER DETAILS SERVICE
                // =====================================================

                .userDetailsService(customUserDetailsService)


                // =====================================================
                // AUTHORIZATION
                // =====================================================

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/terms",
                                "/products",
                                "/products/**",
                                "/about",
                                "/contact",
                                "/categories",
                                "/categories/**",
                                "/search",
                                "/forgot-password",
                                "/reset-password",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/uploads/**"
                        ).permitAll()


                        .requestMatchers("/cart/**")
                        .authenticated()


                        .requestMatchers("/wishlist/**")
                        .authenticated()


                        .requestMatchers(HttpMethod.POST, "/api/products")
                        .hasRole("ADMIN")


                        .requestMatchers(HttpMethod.PUT, "/api/products/**")
                        .hasRole("ADMIN")


                        .requestMatchers(HttpMethod.DELETE, "/api/products/**")
                        .hasRole("ADMIN")


                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")


                        .anyRequest()
                        .authenticated()
                )


                // =====================================================
                // LOGIN
                // =====================================================

                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .permitAll()
                )


                // =====================================================
                // LOGOUT
                // =====================================================

                .logout(logout -> logout

                        .logoutSuccessHandler(
                                (request, response, authentication) ->
                                        response.sendRedirect(
                                                request.getContextPath()
                                                        + "/login?logout"
                                        )
                        )

                        .permitAll()
                )


                // =====================================================
                // HTTP BASIC
                // =====================================================

                .httpBasic(Customizer.withDefaults());


        return http.build();
    }
}