package io.github.tiagofdev.applicationgla.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 */
@Configuration
public class WebConfig {

    /**
     *
     * @return WebMvcConfigurer
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Apply to all endpoints
                        .allowedOrigins("http://localhost:4200") // Allow Angular app
//                        .allowedOrigins("*")
// Testing phase, if I allow ANY origins, I should disable BOTH
//  allowCredentials(true) AND .allowedOrigins("http://localhost:4200")
// because allowing credentials requires specifying explicit origins.
// Browsers will block requests if ("*") and credentials are used together.
                        .allowCredentials(true)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("Authorization", "Content-Type");

            }
        };
    }
}
