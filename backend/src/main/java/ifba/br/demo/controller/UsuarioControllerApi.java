package ifba.br.demo.controller;

import ifba.br.demo.domain.dto.UsuarioDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static ifba.br.demo.constants.UsuarioConstants.API_USUARIO;

@RequestMapping(API_USUARIO)
public interface UsuarioControllerApi {

    @PostMapping
    UsuarioDto create(@RequestBody @Valid UsuarioDto pessoaDTO);

    @PutMapping("/{id}")
    UsuarioDto update(@PathVariable("id") Long id,
                      @RequestBody @Valid UsuarioDto pessoaDTO);

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    ResponseEntity<Void> delete(@PathVariable("id") Long id);

    @GetMapping
    List<UsuarioDto> getAll();

    @GetMapping("/{id}")
    ResponseEntity<UsuarioDto> getById(@PathVariable("id") Long id);

}
