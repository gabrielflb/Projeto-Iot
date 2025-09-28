package ifba.br.demo.repository;

import ifba.br.demo.domain.entity.Resource;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    @Override
    Optional<Resource> findById(Long aLong);
    List<Resource> findByUsuarioId(Long usuarioId);

    @Override
    <S extends Resource> List<S> findAll(Example<S> example);
}
