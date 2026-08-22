package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.dto.AuthenticationDTO;
import br.com.iuriraredu.ecommerce.dto.LoginResponseDTO;
import br.com.iuriraredu.ecommerce.dto.RegisterDTO;
import br.com.iuriraredu.ecommerce.entity.User;
import br.com.iuriraredu.ecommerce.entity.enums.UserRole;
import br.com.iuriraredu.ecommerce.exception.BusinessException;
import br.com.iuriraredu.ecommerce.repository.UserRepository;
import br.com.iuriraredu.ecommerce.security.UserDetailsImpl;
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
        final AuthenticationDTO data = new AuthenticationDTO("test@email.com", "password123");
        final Authentication authenticationMock = mock(Authentication.class);

        // The principal Spring Security hands back after authentication is a UserDetails
        // (UserDetailsImpl in our case), never the raw User entity — mirror that here.
        final User mockUser = new User();
        mockUser.setLogin("test@email.com");
        final UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(userDetails);
        when(tokenService.generateToken(userDetails)).thenReturn("fake-jwt-token");

        // Act
        final LoginResponseDTO response = authService.login(data);

        // Assert
        assertNotNull(response);
        assertEquals("fake-jwt-token", response.token());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, times(1)).generateToken(userDetails);
    }

    @Test
    @DisplayName("Should register new user successfully when login is not taken")
    void registerSuccess() {
        // Arrange
        final RegisterDTO data = new RegisterDTO("new@email.com", "password123", UserRole.USER);

        when(userRepository.existsByLogin(data.login())).thenReturn(false);
        when(passwordEncoder.encode(data.password())).thenReturn("encodedPassword");

        // Act & Assert (no longer returns a boolean, so we just execute it)
        assertDoesNotThrow(() -> authService.register(data));

        verify(userRepository, times(1)).existsByLogin(data.login());
        verify(passwordEncoder, times(1)).encode(data.password());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when trying to register with an already existing login")
    void registerUserAlreadyExists() {
        // Arrange
        final RegisterDTO data = new RegisterDTO("existing@email.com", "password123", UserRole.USER);

        when(userRepository.existsByLogin(data.login())).thenReturn(true);

        // Act & Assert (we expect BusinessException to be thrown)
        final BusinessException exception = assertThrows(BusinessException.class, () -> authService.register(data));

        assertEquals("User already exists with this login!", exception.getMessage());
        verify(userRepository, times(1)).existsByLogin(data.login());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }
}
