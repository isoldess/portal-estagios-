package repository;

import model.Area;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AreaRepository extends JpaRepository<Area, Long> {
    boolean existsByNomeIgnoreCase(String nome);
    List<Area> findAllByOrderByNomeAsc();
}
