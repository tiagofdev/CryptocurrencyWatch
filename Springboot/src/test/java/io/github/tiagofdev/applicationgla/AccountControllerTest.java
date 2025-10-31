package io.github.tiagofdev.applicationgla;

import io.github.tiagofdev.applicationgla.controller.AccountController;
import io.github.tiagofdev.applicationgla.dto.Response;
import io.github.tiagofdev.applicationgla.service.CustomUserDetailsService;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import org.junit.Test;

public class AccountControllerTest {

    @Mock
    private CustomUserDetailsService userService;

    @InjectMocks
    private AccountController accountController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterSuccess() {
        // Arrange
        AccountController.RegisterRequest request = new AccountController.RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("testpassword");

        doNothing().when(userService).createUser(request.getUsername(), request.getPassword());

        // Act
        ResponseEntity<Response> response = accountController.register(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success - Historical Retrieved", response.getBody().getMessage());
        verify(userService, times(1)).createUser(request.getUsername(), request.getPassword());
    }

    @Test
    public void testRegisterFailure() {
        // Arrange
        AccountController.RegisterRequest request = new AccountController.RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("testpassword");

        doThrow(new IllegalArgumentException("Username already exists"))
                .when(userService)
                .createUser(request.getUsername(), request.getPassword());

        // Act
        ResponseEntity<Response> response = accountController.register(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already exists", response.getBody().getMessage());
        verify(userService, times(1)).createUser(request.getUsername(), request.getPassword());
    }

    

}
