package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.dto.AuthenticationDTO;
import br.com.iuriraredu.ecommerce.dto.LoginResponseDTO;
import br.com.iuriraredu.ecommerce.dto.RegisterDTO;
import br.com.iuriraredu.ecommerce.entity.User;
import br.com.iuriraredu.ecommerce.entity.enums.UserRole;
import br.com.iuriraredu.ecommerce.exception.BusinessException;
import br.com.iuriraredu.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Should authenticate user and return token successfully")
    void loginSuccess() {
        // Arrange
        AuthenticationDTO data = new AuthenticationDTO("test@email.com", "password123");
        Authentication authenticationMock = mock(Authentication.class);
        User mockUser = new User();
        mockUser.setLogin("test@email.com");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(mockUser);
        when(tokenService.generateToken(mockUser)).thenReturn("fake-jwt-token");

        // Act
        LoginResponseDTO response = authService.login(data);

        // Assert
        assertNotNull(response);
        assertEquals("fake-jwt-token", response.token());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, times(1)).generateToken(mockUser);
    }

    @Test
    @DisplayName("Should register new user successfully when login is not taken")
    void registerSuccess() {
        // Arrange
        RegisterDTO data = new RegisterDTO("new@email.com", "password123", UserRole.USER);

        when(userRepository.findByLogin(data.login())).thenReturn(null);
        when(passwordEncoder.encode(data.password())).thenReturn("encodedPassword");

        // Act & Assert (não retorna mais boolean, então apenas executamos)
        assertDoesNotThrow(() -> authService.register(data));

        verify(userRepository, times(1)).findByLogin(data.login());
        verify(passwordEncoder, times(1)).encode(data.password());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when trying to register with an already existing login")
    void registerUserAlreadyExists() {
        // Arrange
        RegisterDTO data = new RegisterDTO("existing@email.com", "password123", UserRole.USER);
        User existingUser = new User();
        existingUser.setLogin(data.login());

        when(userRepository.findByLogin(data.login())).thenReturn(existingUser);

        // Act & Assert (esperamos que lance a BusinessException)
        BusinessException exception = assertThrows(BusinessException.class, () -> authService.register(data));

        assertEquals("User already exists with this login!", exception.getMessage());
        verify(userRepository, times(1)).findByLogin(data.login());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }
}
