package ifba.br.demo.controller.imp;

import ifba.br.demo.domain.entity.Resource;
import ifba.br.demo.domain.entity.Usuario;
import ifba.br.demo.exception.RegraNegocioException;
import ifba.br.demo.service.imp.ResourceServiceImp;
import ifba.br.demo.service.imp.UsuarioServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static ifba.br.demo.constants.ResourceConstants.API_RESOURCES;

@RestController
@RequestMapping(API_RESOURCES)
public class ResourceController {


    private final ResourceServiceImp resourceService;

    private final UsuarioServiceImp usuarioServiceImp;

    public ResourceController(ResourceServiceImp resourceService, UsuarioServiceImp usuarioServiceImp) {
        this.resourceService = resourceService;
        this.usuarioServiceImp = usuarioServiceImp;
    }


    @PostMapping("/{resourceId}/release")
    public ResponseEntity<?> releaseResource(@PathVariable Long resourceId,
                                             @RequestHeader("Email") String email) {
        return resourceService.releaseResource(resourceId, email);
    }

    @PostMapping("/{resourceId}/force-release")
    public ResponseEntity<?> forceReleaseResource(@PathVariable Long resourceId,
                                                  @RequestParam String reason,
                                                  @RequestHeader("Email") String email) {
        if (!usuarioServiceImp.isAdmin(email)) {
            return ResponseEntity.status(403).body("Acesso negado. Apenas administradores.");
        }
        return resourceService.forceReleaseResource(resourceId, email, reason);
    }
    @GetMapping
    public ResponseEntity<List<Resource>> getResources(Authentication authentication) {
        return ResponseEntity.ok(resourceService.findAll());
    }

    @DeleteMapping("/{resourceId}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<Void> deleteResource(@PathVariable Long resourceId) {
        try {
            resourceService.deleteResource(resourceId);
            return ResponseEntity.noContent().build();
        } catch (RegraNegocioException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<Resource> createResource(@RequestBody Resource resource) {
        Resource newResource = resourceService.createResource(resource);
        return ResponseEntity.ok(newResource);
    }
}