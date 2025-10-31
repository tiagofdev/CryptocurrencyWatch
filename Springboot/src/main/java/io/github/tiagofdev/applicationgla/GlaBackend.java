package io.github.tiagofdev.applicationgla;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class GlaBackend {

    public static void main(String[] args) {

        SpringApplication.run(GlaBackend.class, args);

    }
/*
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource ubccs = new UrlBasedCorsConfigurationSource();
        CorsConfiguration cc = new CorsConfiguration();
        cc.setAllowCredentials(true);
        cc.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        cc.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
                "Accept", "Jwt-Token", "Authorization", "Origin, Accept", "X-Requested-With",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        cc.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Jwt-Token",
                "Authorization", "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials", "Filename"));
        cc.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        ubccs.registerCorsConfiguration("/**", cc );
        return new CorsFilter(ubccs);
    }*/

}
