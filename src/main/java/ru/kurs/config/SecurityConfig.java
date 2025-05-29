package ru.kurs.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final UserDetailsService userDetailsService;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return NoOpPasswordEncoder.getInstance();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authenticationProvider(authenticationProvider())
                                .authorizeHttpRequests(auth -> {
                                        auth.requestMatchers("/login", "/error", "/css/**", "/js/**", "/images/**")
                                                        .permitAll();
                                        auth.requestMatchers("/users", "/users/new", "/users/edit/**",
                                                        "/users/delete/**").hasAuthority("ADMIN");
                                        auth.requestMatchers("/quarries/new", "/quarries/edit/**",
                                                        "/quarries/delete/**").hasAnyAuthority("ADMIN", "MANAGER");
                                        auth.requestMatchers("/equipment/new", "/equipment/edit/**",
                                                        "/equipment/delete/**").hasAnyAuthority("ADMIN", "MANAGER");
                                        auth.requestMatchers("/workers/new", "/workers/edit/**", "/workers/delete/**")
                                                        .hasAnyAuthority("ADMIN", "MANAGER");
                                        auth.requestMatchers("/explosions/new", "/explosions/edit/**",
                                                        "/explosions/delete/**")
                                                        .hasAnyAuthority("ADMIN", "MANAGER", "BLASTING_ENGINEER");
                                        auth.requestMatchers("/photos/new").hasAnyAuthority("ADMIN", "MANAGER", "USER",
                                                        "BLASTING_ENGINEER");
                                        auth.requestMatchers("/photos/edit/**", "/photos/delete/**")
                                                        .hasAnyAuthority("ADMIN", "MANAGER");
                                        auth.requestMatchers("/reports/new").hasAnyAuthority("ADMIN", "MANAGER", "USER",
                                                        "BLASTING_ENGINEER");
                                        auth.requestMatchers("/reports/edit/**", "/reports/delete/**")
                                                        .hasAnyAuthority("ADMIN", "MANAGER");
                                        auth.requestMatchers("/safety-inspections/new", "/safety-inspections/edit/**",
                                                        "/safety-inspections/delete/**")
                                                        .hasAnyAuthority("ADMIN", "MANAGER");
                                        auth.requestMatchers("/equipment-licenses/new", "/equipment-licenses/edit/**",
                                                        "/equipment-licenses/delete/**")
                                                        .hasAnyAuthority("ADMIN", "MANAGER");
                                        auth.requestMatchers("/explosion-licenses/new", "/explosion-licenses/edit/**",
                                                        "/explosion-licenses/delete/**")
                                                        .hasAnyAuthority("ADMIN", "MANAGER");
                                        auth.requestMatchers("/quarries", "/equipment", "/workers", "/explosions",
                                                        "/photos", "/reports",
                                                        "/safety-inspections", "/equipment-licenses",
                                                        "/explosion-licenses")
                                                        .hasAnyAuthority("ADMIN", "MANAGER", "USER",
                                                                        "BLASTING_ENGINEER", "GUEST");
                                        auth.anyRequest().authenticated();
                                })
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll())
                                .csrf(csrf -> csrf.disable());

                return http.build();
        }
}