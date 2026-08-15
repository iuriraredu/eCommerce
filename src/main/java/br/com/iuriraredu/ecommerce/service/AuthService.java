package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.dto.AuthenticationDTO;
import br.com.iuriraredu.ecommerce.dto.LoginResponseDTO;
import br.com.iuriraredu.ecommerce.dto.RegisterDTO;
import br.com.iuriraredu.ecommerce.entity.User;
import br.com.iuriraredu.ecommerce.exception.BusinessException;
import br.com.iuriraredu.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(AuthenticationDTO data) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        String token = tokenService.generateToken((User) auth.getPrincipal());
        return new LoginResponseDTO(token);
    }

    public void register(RegisterDTO data) {
        if (this.userRepository.findByLogin(data.login()) != null) {
            throw new BusinessException("User already exists with this login!");
        }

        String encryptedPassword = passwordEncoder.encode(data.password());
        User user = new User();
        user.setLogin(data.login());
        user.setPassword(encryptedPassword);
        user.setRole(data.role());

        this.userRepository.save(user);
    }
}