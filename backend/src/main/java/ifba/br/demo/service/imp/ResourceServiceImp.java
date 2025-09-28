package ifba.br.demo.service.imp;

import ifba.br.demo.domain.entity.Resource;
import ifba.br.demo.domain.entity.Reservation;
import ifba.br.demo.domain.entity.Usuario;
import ifba.br.demo.exception.RegraNegocioException;
import ifba.br.demo.repository.ResourceRepository;
import ifba.br.demo.repository.ReservationRepository;
import ifba.br.demo.repository.UsuarioRepository;
import ifba.br.demo.service.imp.logAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceServiceImp {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private logAuditService auditService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public ResponseEntity<?> releaseResource(Long resourceId, String email) {
        try {
            Resource resource = resourceRepository.findById(resourceId)
                    .orElseThrow(() -> new RuntimeException("Recurso não encontrado: " + resourceId));

            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            if (!resource.getUsuario().getId().equals(usuario.getId()) && !usuario.getIsAdmin()) {
                auditService.logFailedAttempt(email, "RELEASE_RESOURCE", resourceId,
                        "Tentativa de liberar recurso que não pertence ao usuário.");
                return ResponseEntity.status(403).body("Acesso negado: você não tem permissão para liberar este recurso.");
            }

            LocalDateTime now = LocalDateTime.now();
            Optional<Reservation> activeReservationOpt = reservationRepository
                    .findActiveReservationsByResource(resourceId, now)
                    .stream()
                    .findFirst();

            if (activeReservationOpt.isEmpty()) {
                resource.setStatus("LIVRE");
                resource.setLastUpdated(now);
                resourceRepository.save(resource);

                auditService.logAction(email, "RELEASE_RESOURCE", resourceId,
                        "Recurso liberado sem reserva ativa encontrada (possível inconsistência)");

                return ResponseEntity.ok()
                        .body("Recurso liberado com sucesso (sem reserva ativa encontrada)");
            }

            Reservation activeReservation = activeReservationOpt.get();
            activeReservation.setStatus("COMPLETO");
            activeReservation.setTempoFinal(now);
            reservationRepository.save(activeReservation);

            resource.setStatus("LIVRE");
            resource.setLastUpdated(now);
            resourceRepository.save(resource);

            auditService.logAction(email, "RELEASE_RESOURCE", resourceId,
                    "Recurso liberado com sucesso. Reserva ID: " + activeReservation.getId() +
                            " finalizada antecipadamente. Duração real: " +
                            calculateDurationMinutes(activeReservation.getTempoInicial(), now) + " minutos");

            return ResponseEntity.ok()
                    .body("Recurso liberado com sucesso. Reserva finalizada.");

        } catch (Exception e) {
            auditService.logActionWithResult(email, "RELEASE_RESOURCE", resourceId,
                    "Erro ao liberar recurso: " + e.getMessage(), "ERROR");

            return ResponseEntity.internalServerError()
                    .body("Erro ao liberar recurso: " + e.getMessage());
        }
    }

    @Transactional
    public void autoReleaseResource(Long resourceId, Long reservationId) {
        try {
            Resource resource = resourceRepository.findById(resourceId)
                    .orElseThrow(() -> new RuntimeException("Recurso não encontrado para liberação automática"));

            Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);

            if (reservationOpt.isPresent()) {
                Reservation reservation = reservationOpt.get();
                reservation.setStatus("COMPLETO");
                reservationRepository.save(reservation);
            }

            resource.setStatus("LIVRE");
            resource.setLastUpdated(LocalDateTime.now());
            resourceRepository.save(resource);

            auditService.logAction("SYSTEM", "AUTO_RELEASE", resourceId,
                    "Liberação automática por timeout. Reserva ID: " + reservationId);

        } catch (Exception e) {
            auditService.logActionWithResult("SYSTEM", "AUTO_RELEASE", resourceId,
                    "Erro na liberação automática: " + e.getMessage(), "ERROR");
        }
    }

    @Transactional
    public ResponseEntity<?> forceReleaseResource(Long resourceId, String adminUsername, String reason) {
        try {
            Resource resource = resourceRepository.findById(resourceId)
                    .orElseThrow(() -> new RuntimeException("Recurso não encontrado"));

            LocalDateTime now = LocalDateTime.now();
            reservationRepository.findActiveReservationsByResource(resourceId, now)
                    .forEach(reservation -> {
                        reservation.setStatus("CANCELADO");
                        reservation.setTempoFinal(now);
                        reservationRepository.save(reservation);
                    });

            String previousStatus = resource.getStatus();
            resource.setStatus("LIVRE");
            resource.setLastUpdated(now);
            resourceRepository.save(resource);

            auditService.logAction(adminUsername, "FORCE_RELEASE", resourceId,
                    "Liberação forçada por administrador. Status anterior: " + previousStatus +
                            ". Motivo: " + reason);

            return ResponseEntity.ok()
                    .body("Recurso liberado forçadamente com sucesso. Status anterior: " + previousStatus);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro na liberação forçada: " + e.getMessage());
        }
    }

    private long calculateDurationMinutes(LocalDateTime start, LocalDateTime end) {
        return java.time.Duration.between(start, end).toMinutes();
    }
    public List<Resource> getResourcesByUserId(Long userId) {
        return resourceRepository.findByUsuarioId(userId);
    }

    @Transactional
    public void deleteResource(Long resourceId) {
        if (!resourceRepository.existsById(resourceId)) {
            throw new RegraNegocioException("Recurso não encontrado com o ID: " + resourceId);
        }
        resourceRepository.deleteById(resourceId);
    }

    @Transactional
    public Resource createResource(Resource resource) {
        resource.setStatus("LIVRE");
        resource.setLastUpdated(LocalDateTime.now());
        return resourceRepository.save(resource);
    }

    @Transactional
    public List<Resource> findAll() {
        return resourceRepository.findAll();
    }
}