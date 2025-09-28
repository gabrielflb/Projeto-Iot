package ifba.br.demo.controller.imp;


import ifba.br.demo.domain.dto.LoginResponseDTO;
import ifba.br.demo.domain.dto.LoginResquestDTO;
import ifba.br.demo.domain.entity.Usuario;
import ifba.br.demo.exception.RegraNegocioException;
import ifba.br.demo.mapper.UsuarioMapper;
import ifba.br.demo.service.imp.UsuarioServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
public class TokenController {
    private final JwtEncoder jwtEncoder;
    @Autowired
    private  UsuarioMapper mapper;
    @Autowired
    private UsuarioServiceImp usuarioService;
;
    @Qualifier("passwordEncoder")
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public TokenController(JwtEncoder jwtEncoder, UsuarioMapper mapper, UsuarioServiceImp usuarioService, BCryptPasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.mapper = mapper;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginResquestDTO loginResquestDTO) {
         Usuario usuario = usuarioService.findEntityByEmail(loginResquestDTO.email());
         if (usuario == null || !usuario.senhaValida(loginResquestDTO, passwordEncoder)) {
             throw new RegraNegocioException("Usuário invalido! "+usuario.getEmail());
         }
        var expiresIn = 300L;
        var authorities = usuario.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

         var claims = JwtClaimsSet.builder()
                 .issuer("iotbackend")
                 .subject(usuario.getId().toString())
                 .claim("scope", authorities)
                 .expiresAt(Instant.now().plusSeconds(300L))
                 .issuedAt(Instant.now())
                 .build();

         var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new LoginResponseDTO(jwtValue, expiresIn));
    }

}
