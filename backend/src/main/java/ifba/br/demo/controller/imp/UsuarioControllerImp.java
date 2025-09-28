package ifba.br.demo.controller.imp;

import ifba.br.demo.controller.UsuarioControllerApi;
import ifba.br.demo.domain.dto.UsuarioDto;
import ifba.br.demo.domain.entity.Usuario;
import ifba.br.demo.exception.RegraNegocioException;
import ifba.br.demo.repository.UsuarioRepository;
import ifba.br.demo.service.imp.UsuarioServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UsuarioControllerImp implements UsuarioControllerApi {

    private final UsuarioServiceImp service;

    public UsuarioDto create(UsuarioDto usuarioDto) {
        return service.create(usuarioDto);
    }

    public UsuarioDto update(Long id, UsuarioDto usuarioDto) {
        usuarioDto.setId(id);
        return service.update(usuarioDto);
    }


    public ResponseEntity<Void> delete(Long id) {
        service.remove(id);
        return ResponseEntity.noContent().build();
    }

    public List<UsuarioDto> getAll() {
        return service.findAll();
    }

    public ResponseEntity<UsuarioDto> getById(Long id) {
        UsuarioDto p = service.findById(id);
        if (p != null) {
            return ResponseEntity.ok(p);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
