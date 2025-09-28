package ifba.br.demo.config;

import ifba.br.demo.exception.RegraNegocioException;
import ifba.br.demo.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfiguracao {
        private final UsuarioRepository usuarioRepository;

        public AppConfiguracao(UsuarioRepository userRepository) {
            this.usuarioRepository = userRepository;
        }

        @Bean
        UserDetailsService userDetailsService() {
            return username -> usuarioRepository.findByEmail(username)
                    .orElseThrow(() -> new RegraNegocioException("User not found"));
        }

        @Bean
        BCryptPasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
            return config.getAuthenticationManager();
        }

        @Bean
        AuthenticationProvider authenticationProvider() {
            DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

            authProvider.setUserDetailsService(userDetailsService());
            authProvider.setPasswordEncoder(passwordEncoder());

            return authProvider;
        }
}

