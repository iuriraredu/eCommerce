package br.com.iuriraredu.ecommerce.controller;

import br.com.iuriraredu.ecommerce.dto.AuthenticationDTO;
import br.com.iuriraredu.ecommerce.dto.RegisterDTO;
import br.com.iuriraredu.ecommerce.entity.Usuario;
import br.com.iuriraredu.ecommerce.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthenticationDTO data) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(
                data.login(), data.password());

        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        return ResponseEntity.ok("Login efetuado com sucesso! (O Token JWT será implementado na V5)");
    }

    @PostMapping("/registrar")
    public ResponseEntity<Void> registrar(@RequestBody RegisterDTO data) {
        if (this.usuarioRepository.findByLogin(data.login()) != null) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        Usuario usuario = new Usuario();
        usuario.setLogin(data.login());
        usuario.setPassword(encryptedPassword);
        usuario.setRole(data.role());

        this.usuarioRepository.save(usuario);

        return ResponseEntity.status(CREATED).build();
    }
}
