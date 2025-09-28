package ifba.br.demo.service.imp;

import ifba.br.demo.domain.dto.UsuarioDto;
import ifba.br.demo.domain.entity.Usuario;
import ifba.br.demo.exception.RegraNegocioException;
import ifba.br.demo.mapper.UsuarioMapper;
import ifba.br.demo.repository.UsuarioRepository;
import ifba.br.demo.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImp implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;
    @Qualifier("passwordEncoder")
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UsuarioDto create(UsuarioDto usuarioDto) {
        Usuario usuarioNovo = usuarioMapper.UsuarioDtoToUsuario(usuarioDto);
        usuarioNovo.setIsAdmin(false);
        usuarioNovo.setSenha(passwordEncoder.encode(usuarioNovo.getSenha()));
        List<Usuario> usuarioExiste = usuarioRepository.findAll();
        for (Usuario usuario : usuarioExiste) {
            if (usuarioNovo.getEmail().equals(usuario.getEmail())) {
                throw new RegraNegocioException("Já existe usuário cadastrado com o email: " + usuarioDto.getEmail());
            }
        }
        usuarioRepository.save(usuarioNovo);
        return usuarioMapper.usuarioToUsuarioDto(usuarioNovo);
    }

    @Override
    public List<UsuarioDto> findAll() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<UsuarioDto> usuariosDtos = new ArrayList<>();

        usuarios.forEach(p-> usuariosDtos.add(usuarioMapper.usuarioToUsuarioDto(p)));
        return usuariosDtos;
    }

    @Override
    public UsuarioDto findById(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (!usuario.isPresent()) {
            throw new RegraNegocioException("Usuário não encontrado!");
        }
        return usuarioMapper.usuarioToUsuarioDto(usuario.get());
    }

    @Override
    public UsuarioDto findByEmail(String email) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (!usuario.isPresent()) {
            throw new RegraNegocioException("Usuário não encontrado!");
        }
        return usuarioMapper.usuarioToUsuarioDto(usuario.get());
    }

    @Override
    public Boolean isAdmin(String email) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (!usuario.isPresent() && !usuario.get().getIsAdmin()){
            throw new RegraNegocioException("Usuário não encontrado!");
        }
        return (usuario.get().getIsAdmin());
    }

    @Override
    public void remove(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RegraNegocioException("Usuário não encontrada com id " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public UsuarioDto update(UsuarioDto p) {
        if (!usuarioRepository.existsById(p.getId())) {
            throw new RegraNegocioException("Usuário não encontrada com id " + p.getId());
        }
        return create(p);
    }
    public Usuario findEntityByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado!"));
    }

}
