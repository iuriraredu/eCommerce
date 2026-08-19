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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static br.com.iuriraredu.ecommerce.entity.enums.UserRole.USER;

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

    // Endpoint público (/auth/register): ignora qualquer role vinda do corpo da requisição.
    // Todo cadastro público nasce como USER — nunca confie em role enviada por quem ainda não está autenticado.
    public void register(RegisterDTO data) {
        createUser(data, USER);
    }

    // Uso restrito a endpoint protegido por ROLE_ADMIN. Só aqui a role do corpo da requisição é respeitada.
    public void registerWithRole(RegisterDTO data) {
        createUser(data, data.role());
    }

    private void createUser(RegisterDTO data, UserRole role) {
        if (this.userRepository.findByLogin(data.login()) != null) {
            throw new BusinessException("User already exists with this login!"); // Se eu quiser fazer i18n, como você faria para deixar essa mensagem em diversas línguas diferentes? Além disso, se eu quiser retornar códigos customizados de Http (400, 404 etc) como eu faria isso?
        }

        String encryptedPassword = passwordEncoder.encode(data.password());
        User user = new User();
        user.setLogin(data.login());
        user.setPassword(encryptedPassword);
        user.setRole(role);

        this.userRepository.save(user);
    }
}