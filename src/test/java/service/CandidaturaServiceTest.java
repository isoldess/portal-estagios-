package service;

import model.Perfil;
import model.SituacaoVaga;
import model.Usuario;
import model.Vaga;
import org.junit.jupiter.api.Test;
import repository.CandidaturaRepository;
import repository.UsuarioRepository;
import repository.VagaRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CandidaturaServiceTest {
    private final CandidaturaRepository candidaturas = mock(CandidaturaRepository.class);
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final VagaRepository vagas = mock(VagaRepository.class);
    private final CandidaturaService service =
            new CandidaturaService(candidaturas, usuarios, vagas);

    @Test
    void impedeCandidaturaDuplicada() {
        Usuario estudante = new Usuario(1L, "Ana", "ana@example.com",
                Perfil.ESTUDANTE, "Sistemas", null);
        Vaga vaga = new Vaga(2L, null, "Estágio", "Descrição", null,
                "São Paulo", model.Modalidade.REMOTA, 30, SituacaoVaga.ABERTA);
        when(usuarios.findById(1L)).thenReturn(Optional.of(estudante));
        when(vagas.findById(2L)).thenReturn(Optional.of(vaga));
        when(candidaturas.existsByEstudanteIdAndVagaId(1L, 2L)).thenReturn(true);

        ApiException exception = assertThrows(ApiException.class,
                () -> service.candidatar(1L, 2L));

        assertEquals(409, exception.getStatus().value());
        verify(candidaturas, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
