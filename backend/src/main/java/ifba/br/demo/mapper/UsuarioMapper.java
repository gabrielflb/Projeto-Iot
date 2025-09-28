package ifba.br.demo.mapper;

import ifba.br.demo.domain.dto.UsuarioDto;
import ifba.br.demo.domain.entity.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class UsuarioMapper {
    public abstract UsuarioDto usuarioToUsuarioDto(Usuario usuario);
    public abstract Usuario UsuarioDtoToUsuario(UsuarioDto usuarioDto);
}
