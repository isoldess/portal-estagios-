package security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import repository.UsuarioRepository;

@Service
public class PortalUserDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarios;

    public PortalUserDetailsService(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        return usuarios.findByEmailIgnoreCase(email.trim())
                .filter(usuario -> usuario.getPasswordHash() != null)
                .map(PortalUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas."));
    }
}
