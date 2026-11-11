package web;

import jakarta.validation.Valid;
import model.Modalidade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import security.PortalUserDetails;
import service.VagaService;
import web.dto.ApiDtos.VacancyRequest;
import web.dto.ApiDtos.VacancyResponse;

import java.util.List;

@RestController
@RequestMapping("/api")
public class VagaController {
    private final VagaService vagas;

    public VagaController(VagaService vagas) {
        this.vagas = vagas;
    }

    @GetMapping("/vagas")
    public List<VacancyResponse> abertas(
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) Modalidade modalidade) {
        return vagas.listarAbertas(areaId, modalidade)
                .stream().map(VacancyResponse::from).toList();
    }

    @GetMapping("/vagas/{id}")
    public VacancyResponse buscar(@PathVariable Long id) {
        return VacancyResponse.from(vagas.buscarAberta(id));
    }

    @GetMapping("/empresa/vagas")
    public List<VacancyResponse> minhasVagas(Authentication authentication) {
        return vagas.listarDaEmpresa(usuario(authentication).getId())
                .stream().map(VacancyResponse::from).toList();
    }

    @PostMapping("/empresa/vagas")
    public ResponseEntity<VacancyResponse> publicar(
            Authentication authentication, @Valid @RequestBody VacancyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(VacancyResponse.from(
                vagas.publicar(usuario(authentication).getId(), request)));
    }

    @PutMapping("/empresa/vagas/{id}")
    public VacancyResponse atualizar(
            Authentication authentication, @PathVariable Long id,
            @Valid @RequestBody VacancyRequest request) {
        return VacancyResponse.from(
                vagas.atualizar(id, usuario(authentication).getId(), request));
    }

    @PatchMapping("/empresa/vagas/{id}/encerramento")
    public ResponseEntity<Void> encerrar(Authentication authentication, @PathVariable Long id) {
        vagas.encerrar(id, usuario(authentication).getId());
        return ResponseEntity.noContent().build();
    }

    private PortalUserDetails usuario(Authentication authentication) {
        return (PortalUserDetails) authentication.getPrincipal();
    }
}
