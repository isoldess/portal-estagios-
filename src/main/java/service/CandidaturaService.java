package service;

import model.Candidatura;
import model.Perfil;
import model.SituacaoVaga;
import model.Usuario;
import model.Vaga;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.CandidaturaRepository;
import repository.UsuarioRepository;
import repository.VagaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CandidaturaService {
    private final CandidaturaRepository candidaturas;
    private final UsuarioRepository usuarios;
    private final VagaRepository vagas;

    public CandidaturaService(
            CandidaturaRepository candidaturas,
            UsuarioRepository usuarios,
            VagaRepository vagas) {
        this.candidaturas = candidaturas;
        this.usuarios = usuarios;
        this.vagas = vagas;
    }

    @Transactional
    public Candidatura candidatar(Long estudanteId, Long vagaId) {
        Usuario estudante = usuarios.findById(estudanteId)
                .orElseThrow(() -> ApiException.notFound("Estudante não encontrado."));
        if (estudante.getPerfil() != Perfil.ESTUDANTE) {
            throw ApiException.forbidden("Somente estudantes podem se candidatar.");
        }
        Vaga vaga = vagas.findById(vagaId)
                .orElseThrow(() -> ApiException.notFound("Vaga não encontrada."));
        if (vaga.getSituacao() != SituacaoVaga.ABERTA) {
            throw ApiException.conflict("Não é possível se candidatar a uma vaga encerrada.");
        }
        if (candidaturas.existsByEstudanteIdAndVagaId(estudanteId, vagaId)) {
            throw ApiException.conflict("Você já se candidatou a esta vaga.");
        }
        return candidaturas.save(new Candidatura(null, estudante, vaga, LocalDateTime.now()));
    }

    @Transactional(readOnly = true)
    public List<Candidatura> listarDoEstudante(Long estudanteId) {
        return candidaturas.findByEstudanteIdOrderByDataInscricaoDesc(estudanteId);
    }

    @Transactional(readOnly = true)
    public List<Candidatura> listarDaVaga(Long vagaId, Long empresaId) {
        Vaga vaga = vagas.findById(vagaId)
                .orElseThrow(() -> ApiException.notFound("Vaga não encontrada."));
        if (!vaga.getEmpresaResponsavel().getId().equals(empresaId)) {
            throw ApiException.forbidden(
                    "Você só pode consultar candidatos das suas próprias vagas.");
        }
        return candidaturas.findByVagaIdOrderByDataInscricaoDesc(vagaId);
    }

    @Transactional(readOnly = true)
    public long contarTodas() {
        return candidaturas.count();
    }
}
