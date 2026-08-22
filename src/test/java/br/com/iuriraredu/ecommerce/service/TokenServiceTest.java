package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.entity.User;
import br.com.iuriraredu.ecommerce.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "my-secret-key-very-secure-ecommerce-iuri-for-testing-purposes");
    }

    @Test
    @DisplayName("Should generate valid JWT token and validate it successfully")
    void generateAndValidateTokenSuccess() {
        // Arrange
        final User user = new User();
        user.setLogin("iuri@email.com");
        final UserDetailsImpl userDetails = new UserDetailsImpl(user);

        // Act
        final String token = tokenService.generateToken(userDetails);
        final String subject = tokenService.validateToken(token);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals("iuri@email.com", subject);
    }

    @Test
    @DisplayName("Should return empty string when validating invalid or malformed token")
    void validateInvalidTokenReturnsEmpty() {
        // Arrange
        final String invalidToken = "invalid.jwt.token.string";

        // Act
        final String subject = tokenService.validateToken(invalidToken);

        // Assert
        assertNotNull(subject);
        assertTrue(subject.isEmpty());
    }
}