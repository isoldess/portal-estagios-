package service;

import model.Perfil;
import model.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.UsuarioRepository;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarios, PasswordEncoder passwordEncoder) {
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario cadastrarEstudante(String nome, String email, String senha, String curso) {
        validarConta(nome, email, senha);
        if (curso == null || curso.isBlank()) {
            throw ApiException.badRequest("Informe o curso do estudante.");
        }
        return salvarConta(nome, email, senha, Perfil.ESTUDANTE, curso.trim(), null);
    }

    @Transactional
    public Usuario cadastrarEmpresa(
            String nome, String email, String senha, String nomeEmpresa) {
        validarConta(nome, email, senha);
        if (nomeEmpresa == null || nomeEmpresa.isBlank()) {
            throw ApiException.badRequest("Informe o nome da empresa.");
        }
        return salvarConta(nome, email, senha, Perfil.EMPRESA, null, nomeEmpresa.trim());
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarios.findAllByOrderByNomeAsc();
    }

    @Transactional
    public void excluir(Long id) {
        if (!usuarios.existsById(id)) {
            throw ApiException.notFound("Usuário não encontrado.");
        }
        usuarios.deleteById(id);
    }

    private Usuario salvarConta(
            String nome, String email, String senha, Perfil perfil,
            String curso, String nomeEmpresa) {
        String normalizedEmail = email.trim().toLowerCase();
        if (usuarios.existsByEmailIgnoreCase(normalizedEmail)) {
            throw ApiException.conflict("Este e-mail já está cadastrado.");
        }
        Usuario usuario = new Usuario(null, nome.trim(), normalizedEmail,
                perfil, curso, nomeEmpresa);
        usuario.setPasswordHash(passwordEncoder.encode(senha));
        return usuarios.save(usuario);
    }

    private void validarConta(String nome, String email, String senha) {
        if (nome == null || nome.isBlank() || email == null || email.isBlank()
                || senha == null || senha.length() < 8
                || senha.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw ApiException.badRequest("Nome, e-mail e senha válida são obrigatórios.");
        }
    }
}
