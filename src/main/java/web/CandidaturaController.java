package web;

import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import security.PortalUserDetails;
import service.CandidaturaService;
import web.dto.ApiDtos.ApplicantResponse;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CandidaturaController {
    private final CandidaturaService candidaturas;

    public CandidaturaController(CandidaturaService candidaturas) {
        this.candidaturas = candidaturas;
    }

    @PostMapping("/estudante/vagas/{vagaId}/candidaturas")
    public ResponseEntity<ApplicantResponse> candidatar(
            Authentication authentication, @PathVariable @Positive Long vagaId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApplicantResponse.from(
                candidaturas.candidatar(usuario(authentication).getId(), vagaId)));
    }

    @GetMapping("/estudante/candidaturas")
    public List<ApplicantResponse> minhasCandidaturas(Authentication authentication) {
        return candidaturas.listarDoEstudante(usuario(authentication).getId())
                .stream().map(ApplicantResponse::from).toList();
    }

    @GetMapping("/empresa/vagas/{vagaId}/candidaturas")
    public List<ApplicantResponse> candidatos(
            Authentication authentication, @PathVariable Long vagaId) {
        return candidaturas.listarDaVaga(vagaId, usuario(authentication).getId())
                .stream().map(ApplicantResponse::from).toList();
    }

    private PortalUserDetails usuario(Authentication authentication) {
        return (PortalUserDetails) authentication.getPrincipal();
    }
}
