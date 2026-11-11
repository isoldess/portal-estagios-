package repository;

import model.Modalidade;
import model.SituacaoVaga;
import model.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VagaRepository extends JpaRepository<Vaga, Long> {
    List<Vaga> findBySituacaoOrderByIdDesc(SituacaoVaga situacao);

    @Query("""
            select v from Vaga v
            where v.situacao = :situacao
              and (:areaId is null or v.area.id = :areaId)
              and (:modalidade is null or v.modalidade = :modalidade)
            order by v.id desc
            """)
    List<Vaga> buscarAbertasFiltradas(
            @Param("situacao") SituacaoVaga situacao,
            @Param("areaId") Long areaId,
            @Param("modalidade") Modalidade modalidade);

    List<Vaga> findByEmpresaResponsavelIdOrderByIdDesc(Long empresaId);
    Optional<Vaga> findByIdAndEmpresaResponsavelId(Long id, Long empresaId);
    boolean existsByAreaId(Long areaId);
    long countBySituacao(SituacaoVaga situacao);
}
