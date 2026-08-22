package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.entity.User;
import br.com.iuriraredu.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

    @Test
    @DisplayName("Should load user by username successfully when user exists")
    void loadByUsernameSuccess() {
        // Arrange
        final String login = "test@email.com";
        final User mockUser = new User();
        mockUser.setLogin(login);
        mockUser.setPassword("encodedPassword");

        when(userRepository.findByLogin(login)).thenReturn(mockUser);

        // Act
        final UserDetails userDetails = authorizationService.loadUserByUsername(login);

        // Assert
        assertNotNull(userDetails);
        assertEquals(login, userDetails.getUsername());
        verify(userRepository, times(1)).findByLogin(login);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user is not found")
    void loadByUsernameNotFound() {
        // Arrange
        final String login = "nonexistent@email.com";
        when(userRepository.findByLogin(login)).thenReturn(null);

        // Act & Assert
        // Spring Security expects either a UserDetails or this exception — never null.
        // Returning null used to cause a confusing internal error instead of a clean 401.
        final UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> authorizationService.loadUserByUsername(login)
        );

        assertEquals("User not found with login: " + login, exception.getMessage());
        verify(userRepository, times(1)).findByLogin(login);
    }
}
