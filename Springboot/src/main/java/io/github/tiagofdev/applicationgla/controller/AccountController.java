package io.github.tiagofdev.applicationgla.controller;

import io.github.tiagofdev.applicationgla.dto.Response;
import io.github.tiagofdev.applicationgla.service.CustomUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

/**
 *
 */
@RestController
public class AccountController {

    /**
     *
     */
    private final CustomUserDetailsService userService;

    /**
     *
     * @param userService ,
     */
    public AccountController(CustomUserDetailsService userService) {
        this.userService = userService;
    }

    /**
     *
     * @param request ,
     * @return ,
     */
    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody RegisterRequest request) {
        try {
            userService.createUser(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(
                    Response.builder()
                            .timeStamp(now())
                            .status(OK)
                            .statusCode(OK.value())
                            .message("Success - New Account Created")
                            .data(Map.of("result", request.getUsername()))
                            .build()
            );
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .timeStamp(now())
                            .status(BAD_REQUEST)
                            .statusCode(BAD_REQUEST.value())
                            .message(e.getMessage())
                            .build()
            );
        }

    }

    /**
     * DTO for registration request
     */
    public static class RegisterRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        /**
         *
         * @param username ,
         */
        public void setUsername(String username) {
            this.username = username;
        }

        /**
         *
         * @return ,
         */
        public String getPassword() {
            return password;
        }

        /**
         *
         * @param password ,
         */
        public void setPassword(String password) {
            this.password = password;
        }

    }
}
