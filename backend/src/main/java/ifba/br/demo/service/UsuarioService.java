package ifba.br.demo.service;

import ifba.br.demo.domain.dto.UsuarioDto;

import java.util.List;

public interface UsuarioService {
    UsuarioDto create(UsuarioDto usuarioDto);

    UsuarioDto update(UsuarioDto user);

    void remove(Long id);

    List<UsuarioDto> findAll();

    UsuarioDto findById(Long id);

    UsuarioDto findByEmail(String email);

    Boolean isAdmin(String username);
}
