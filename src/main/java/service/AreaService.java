package service;

import model.Area;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.AreaRepository;
import repository.VagaRepository;

import java.util.List;

@Service
public class AreaService {
    private final AreaRepository areas;
    private final VagaRepository vagas;

    public AreaService(AreaRepository areas, VagaRepository vagas) {
        this.areas = areas;
        this.vagas = vagas;
    }

    @Transactional(readOnly = true)
    public List<Area> listarTodas() {
        return areas.findAllByOrderByNomeAsc();
    }

    @Transactional
    public Area cadastrar(String nome) {
        String normalizedName = validarNome(nome);
        if (areas.existsByNomeIgnoreCase(normalizedName)) {
            throw ApiException.conflict("Esta área já está cadastrada.");
        }
        return areas.save(new Area(null, normalizedName));
    }

    @Transactional
    public Area atualizar(Long id, String nome) {
        String normalizedName = validarNome(nome);
        Area area = areas.findById(id)
                .orElseThrow(() -> ApiException.notFound("Área não encontrada."));
        if (areas.existsByNomeIgnoreCase(normalizedName)
                && !area.getNome().equalsIgnoreCase(normalizedName)) {
            throw ApiException.conflict("Já existe outra área com esse nome.");
        }
        area.setNome(normalizedName);
        return area;
    }

    @Transactional
    public void excluir(Long id) {
        if (!areas.existsById(id)) {
            throw ApiException.notFound("Área não encontrada.");
        }
        if (vagas.existsByAreaId(id)) {
            throw ApiException.conflict("Não é possível excluir uma área vinculada a vagas.");
        }
        areas.deleteById(id);
    }

    private String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw ApiException.badRequest("O nome da área é obrigatório.");
        }
        return nome.trim();
    }
}
