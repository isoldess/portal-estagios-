package service;

import model.Area;
import model.Modalidade;
import model.Perfil;
import model.SituacaoVaga;
import model.Usuario;
import model.Vaga;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.AreaRepository;
import repository.UsuarioRepository;
import repository.VagaRepository;
import web.dto.ApiDtos.VacancyRequest;

import java.util.List;

@Service
public class VagaService {
    private final VagaRepository vagas;
    private final AreaRepository areas;
    private final UsuarioRepository usuarios;

    public VagaService(
            VagaRepository vagas, AreaRepository areas, UsuarioRepository usuarios) {
        this.vagas = vagas;
        this.areas = areas;
        this.usuarios = usuarios;
    }

    @Transactional(readOnly = true)
    public List<Vaga> listarAbertas(Long areaId, Modalidade modalidade) {
        if (areaId != null && !areas.existsById(areaId)) {
            throw ApiException.notFound("Área não encontrada.");
        }
        return vagas.buscarAbertasFiltradas(SituacaoVaga.ABERTA, areaId, modalidade);
    }

    @Transactional(readOnly = true)
    public Vaga buscarAberta(Long id) {
        Vaga vaga = vagas.findById(id)
                .orElseThrow(() -> ApiException.notFound("Vaga não encontrada."));
        if (vaga.getSituacao() != SituacaoVaga.ABERTA) {
            throw ApiException.notFound("Vaga não encontrada.");
        }
        return vaga;
    }

    @Transactional(readOnly = true)
    public List<Vaga> listarDaEmpresa(Long empresaId) {
        return vagas.findByEmpresaResponsavelIdOrderByIdDesc(empresaId);
    }

    @Transactional
    public Vaga publicar(Long empresaId, VacancyRequest dados) {
        Usuario empresa = buscarEmpresa(empresaId);
        Area area = buscarArea(dados.areaId());
        return vagas.save(new Vaga(null, empresa, limpar(dados.titulo()),
                limpar(dados.descricao()), area, limpar(dados.localizacao()),
                dados.modalidade(), dados.cargaHorariaSemanal(), SituacaoVaga.ABERTA));
    }

    @Transactional
    public Vaga atualizar(Long vagaId, Long empresaId, VacancyRequest dados) {
        Vaga vaga = buscarDaEmpresa(vagaId, empresaId);
        if (vaga.getSituacao() != SituacaoVaga.ABERTA) {
            throw ApiException.conflict("Não é possível editar uma vaga encerrada.");
        }
        vaga.setTitulo(limpar(dados.titulo()));
        vaga.setDescricao(limpar(dados.descricao()));
        vaga.setArea(buscarArea(dados.areaId()));
        vaga.setLocalizacao(limpar(dados.localizacao()));
        vaga.setModalidade(dados.modalidade());
        vaga.setCargaHorariaSemanal(dados.cargaHorariaSemanal());
        return vaga;
    }

    @Transactional
    public void encerrar(Long vagaId, Long empresaId) {
        Vaga vaga = buscarDaEmpresa(vagaId, empresaId);
        if (vaga.getSituacao() == SituacaoVaga.ENCERRADA) {
            throw ApiException.conflict("Esta vaga já está encerrada.");
        }
        vaga.setSituacao(SituacaoVaga.ENCERRADA);
    }

    private Vaga buscarDaEmpresa(Long vagaId, Long empresaId) {
        return vagas.findByIdAndEmpresaResponsavelId(vagaId, empresaId)
                .orElseThrow(() -> ApiException.notFound("Vaga não encontrada."));
    }

    private Usuario buscarEmpresa(Long empresaId) {
        Usuario empresa = usuarios.findById(empresaId)
                .orElseThrow(() -> ApiException.notFound("Empresa não encontrada."));
        if (empresa.getPerfil() != Perfil.EMPRESA) {
            throw ApiException.forbidden("Somente empresas podem publicar vagas.");
        }
        return empresa;
    }

    private Area buscarArea(Long areaId) {
        return areas.findById(areaId)
                .orElseThrow(() -> ApiException.notFound("Área não encontrada."));
    }

    private String limpar(String value) {
        if (value == null || value.isBlank()) {
            throw ApiException.badRequest("Os dados obrigatórios da vaga devem ser informados.");
        }
        return value.trim();
    }
}
