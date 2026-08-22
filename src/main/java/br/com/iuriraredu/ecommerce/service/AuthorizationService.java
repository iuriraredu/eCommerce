package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.entity.User;
import br.com.iuriraredu.ecommerce.repository.UserRepository;
import br.com.iuriraredu.ecommerce.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String login) throws UsernameNotFoundException {
        final User user = userRepository.findByLogin(login);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with login: " + login);
        }
        return new UserDetailsImpl(user);
    }
}
