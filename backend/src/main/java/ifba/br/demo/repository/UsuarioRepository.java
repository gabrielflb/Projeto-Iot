package ifba.br.demo.repository;

import ifba.br.demo.domain.entity.Usuario;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @Override
    Optional<Usuario> findById(Long aLong);
    Optional<Usuario> findByEmail(String email);
    //boolean isAdmin(String email);

}
