package service;

import model.Perfil;
import model.SituacaoVaga;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.CandidaturaRepository;
import repository.UsuarioRepository;
import repository.VagaRepository;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AdminService {
    private final UsuarioRepository usuarios;
    private final VagaRepository vagas;
    private final CandidaturaRepository candidaturas;

    public AdminService(
            UsuarioRepository usuarios,
            VagaRepository vagas,
            CandidaturaRepository candidaturas) {
        this.usuarios = usuarios;
        this.vagas = vagas;
        this.candidaturas = candidaturas;
    }

    @Transactional(readOnly = true)
    public Map<String, Long> obterContagens() {
        Map<String, Long> contagens = new LinkedHashMap<>();
        contagens.put("Estudantes", usuarios.countByPerfil(Perfil.ESTUDANTE));
        contagens.put("Empresas", usuarios.countByPerfil(Perfil.EMPRESA));
        contagens.put("Vagas abertas", vagas.countBySituacao(SituacaoVaga.ABERTA));
        contagens.put("Vagas encerradas", vagas.countBySituacao(SituacaoVaga.ENCERRADA));
        contagens.put("Candidaturas", candidaturas.count());
        return contagens;
    }
}
