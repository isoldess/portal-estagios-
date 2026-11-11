package service;

import model.Perfil;
import model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import repository.UsuarioRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsuarioServiceTest {
    private final UsuarioRepository usuarios = org.mockito.Mockito.mock(UsuarioRepository.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UsuarioService service = new UsuarioService(usuarios, passwordEncoder);

    @Test
    void cadastroPublicoArmazenaSenhaHashEAplicaPerfilEstudante() {
        when(usuarios.existsByEmailIgnoreCase("ana@example.com")).thenReturn(false);
        when(usuarios.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuario = service.cadastrarEstudante(
                "Ana Silva", "ANA@example.com", "senha-segura-123", "Sistemas");

        assertEquals(Perfil.ESTUDANTE, usuario.getPerfil());
        assertNotEquals("senha-segura-123", usuario.getPasswordHash());
        assertTrue(passwordEncoder.matches("senha-segura-123", usuario.getPasswordHash()));
        assertEquals("ana@example.com", usuario.getEmail());
    }

    @Test
    void cadastroPublicoRejeitaEmailDuplicado() {
        when(usuarios.existsByEmailIgnoreCase("ana@example.com")).thenReturn(true);

        ApiException exception = assertThrows(ApiException.class, () ->
                service.cadastrarEstudante(
                        "Ana Silva", "ana@example.com", "senha-segura-123", "Sistemas"));

        assertEquals(409, exception.getStatus().value());
        verify(usuarios, org.mockito.Mockito.never()).save(any(Usuario.class));
    }
}
