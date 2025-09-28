package ifba.br.demo.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UsuarioDto {
    private Long id;

    @NotNull(message = "Nome não pode ser vazio")
    private String nome;

    @NotNull(message = "Senha não pode ser vazia")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;

    @NotNull(message = "Email não pode ser vazia")
    @Email(message = "Formato de email inválido")
    private String email;
}
