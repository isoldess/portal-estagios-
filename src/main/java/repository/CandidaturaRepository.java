package repository;

import model.Candidatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {
    boolean existsByEstudanteIdAndVagaId(Long estudanteId, Long vagaId);
    List<Candidatura> findByEstudanteIdOrderByDataInscricaoDesc(Long estudanteId);
    List<Candidatura> findByVagaIdOrderByDataInscricaoDesc(Long vagaId);
}
