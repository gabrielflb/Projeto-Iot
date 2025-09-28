package ifba.br.demo.repository;

import ifba.br.demo.domain.entity.LogAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface logAuditoRepository extends JpaRepository<LogAudit, Long> {
}
