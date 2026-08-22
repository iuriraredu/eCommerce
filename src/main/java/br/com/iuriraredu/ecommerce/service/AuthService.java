package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.dto.AuthenticationDTO;
import br.com.iuriraredu.ecommerce.dto.LoginResponseDTO;
import br.com.iuriraredu.ecommerce.dto.RegisterDTO;
import br.com.iuriraredu.ecommerce.entity.User;
import br.com.iuriraredu.ecommerce.entity.enums.UserRole;
import br.com.iuriraredu.ecommerce.exception.BusinessException;
import br.com.iuriraredu.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(final AuthenticationDTO data) {
        final UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        final Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        // The principal here is whatever AuthorizationService.loadUserByUsername returned —
        // a UserDetailsImpl, not our User entity. TokenService only needs the UserDetails
        // contract, so no cast to a concrete type (and no entity leakage) is needed.
        final String token = tokenService.generateToken((UserDetails) auth.getPrincipal());
        return new LoginResponseDTO(token);
    }

    // Public endpoint (/auth/register): ignores any role coming from the request body.
    // Every public sign-up is born as USER — never trust a role sent by someone who isn't authenticated yet.
    public void register(final RegisterDTO data) {
        createUser(data, UserRole.USER);
    }

    // Restricted to the endpoint protected by ROLE_ADMIN. Only here is the role from the request body honored.
    public void registerWithRole(final RegisterDTO data) {
        createUser(data, data.role());
    }

    private void createUser(final RegisterDTO data, final UserRole role) {
        if (this.userRepository.existsByLogin(data.login())) {
            throw new BusinessException("User already exists with this login!");
        }

        final String encryptedPassword = passwordEncoder.encode(data.password());
        final User user = new User();
        user.setLogin(data.login());
        user.setPassword(encryptedPassword);
        user.setRole(role);

        this.userRepository.save(user);
    }
}