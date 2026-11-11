package service;

import model.Perfil;
import model.SituacaoVaga;
import org.junit.jupiter.api.Test;
import repository.CandidaturaRepository;
import repository.UsuarioRepository;
import repository.VagaRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminServiceTest {
    @Test
    void mantemCategoriasEContagensDoResumoAdministrativo() {
        UsuarioRepository usuarios = mock(UsuarioRepository.class);
        VagaRepository vagas = mock(VagaRepository.class);
        CandidaturaRepository candidaturas = mock(CandidaturaRepository.class);
        when(usuarios.countByPerfil(Perfil.ESTUDANTE)).thenReturn(4L);
        when(usuarios.countByPerfil(Perfil.EMPRESA)).thenReturn(2L);
        when(vagas.countBySituacao(SituacaoVaga.ABERTA)).thenReturn(3L);
        when(vagas.countBySituacao(SituacaoVaga.ENCERRADA)).thenReturn(1L);
        when(candidaturas.count()).thenReturn(5L);

        var counts = new AdminService(usuarios, vagas, candidaturas).obterContagens();

        assertEquals(5, counts.size());
        assertEquals(4L, counts.get("Estudantes"));
        assertEquals(2L, counts.get("Empresas"));
        assertEquals(3L, counts.get("Vagas abertas"));
        assertEquals(1L, counts.get("Vagas encerradas"));
        assertEquals(5L, counts.get("Candidaturas"));
    }
}
