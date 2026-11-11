package web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.AdminService;
import service.UsuarioService;
import web.dto.ApiDtos.UserResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService admin;
    private final UsuarioService usuarios;

    public AdminController(AdminService admin, UsuarioService usuarios) {
        this.admin = admin;
        this.usuarios = usuarios;
    }

    @GetMapping("/resumo")
    public Map<String, Long> resumo() {
        return admin.obterContagens();
    }

    @GetMapping("/usuarios")
    public List<UserResponse> usuarios() {
        return usuarios.listarTodos().stream().map(UserResponse::from).toList();
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> excluirUsuario(@PathVariable Long id) {
        usuarios.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
