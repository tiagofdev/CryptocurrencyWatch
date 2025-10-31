package io.github.tiagofdev.applicationgla.controller;

import io.github.tiagofdev.applicationgla.dto.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

/**
 * Controller for login authentication endpoint
 */
@RestController
@RequestMapping("/auth") // This is optional, all endpoints in this class will have path: auth/*
public class AuthController {

    /**
     *
     */
    private final AuthenticationManager authenticationManager;

    /**
     *
     */
    private final JwtTokenManager tokenManager;

    /**
     *
     * @param authenticationManager ,
     * @param tokenManager ,
     */
    public AuthController(AuthenticationManager authenticationManager, JwtTokenManager tokenManager) {
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
    }

    /**
     *
     * @param request ,
     * @return ResponseEntity<Response>
     */
    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            String[] data = {tokenManager.generateToken(authentication.getName()), request.getUsername()};
            return ResponseEntity.ok(
                    Response.builder()
                            .timeStamp(now())
                            .status(OK)
                            .statusCode(OK.value())
                            .message("Success - Historical Retrieved")
                            .data(Map.of("results", data))
                            .build()
            );
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .timeStamp(now())
                            .status(BAD_REQUEST)
                            .statusCode(BAD_REQUEST.value())
                            .message("e.getMessage()")
                            .build()
            );

        }

    }


}

/**
 * Class to receive requests from client
 */
class AuthRequest {
    private String username;
    private String password;

    /**
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }


    // Getters and setters
}
