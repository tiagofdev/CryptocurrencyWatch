package io.github.tiagofdev.applicationgla.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    /**
     *
     */
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     *
     * @param jwtAuthenticationFilter ,
     */
//    public SecurityConfig(@Lazy JwtAuthenticationFilter jwtAuthenticationFilter) {
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//    }

    /**
     *
     * @param http ,
     * @return SecurityFilterChain
     * @throws Exception ,
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.cors() // Enable CORS support
                .and()
                .csrf().disable() // Disable CSRF if not needed
                .authorizeHttpRequests()
                .anyRequest().permitAll(); // Adjust this to your needs





        /*http.csrf(csrf -> csrf
            .ignoringRequestMatchers("/register", "/auth/login", "/actuator/prometheus")) // NOSONAR: CSRF is disabled
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/register", "/auth/login", "/actuator/**").permitAll()  // Allow unauthenticated access to register and login
                .anyRequest().authenticated()  // Require authentication for all other requests
            )
            .cors(Customizer.withDefaults())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);*/

        return http.build();
    }

    /**
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    /**
     *
     * @param configuration ,
     * @return Authentication Manager
     * @throws Exception ,
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
