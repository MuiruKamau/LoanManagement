package com.myapplication.LoanManagementSystem.config;




import com.myapplication.LoanManagementSystem.security.CustomUserDetailsService;
import com.myapplication.LoanManagementSystem.security.JwtAuthenticationFilter;
import com.myapplication.LoanManagementSystem.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@SuppressWarnings("ALL")
@Configuration
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManager(CustomUserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "http://localhost:8080/swagger-ui/index.html",
                                "/authlogin",
                                "/auth/**",
                                "/loans/**",
                                "/loans/all",
                                "/loans/get/**",
                                "/loans/post",
                                "/loans/update/**",
                                "/loans/delete/**",
                                "/loans/calculate",
                                "/customers/**",
                                "/payments/**",
                                "/stats/**",
                                "/repayment-schedules/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}


    // Disabling security
    /*public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Keep CSRF disabled if you intend to disable it
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() //  <--  THIS IS THE KEY CHANGE: Allow ALL requests
                );
        return http.build(); // Make sure this line is present and *not* commented out
    } */













