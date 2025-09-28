package ifba.br.demo.service.imp;

import ifba.br.demo.domain.entity.Reservation;
import ifba.br.demo.domain.entity.Resource;
import ifba.br.demo.mapper.UsuarioMapper;
import ifba.br.demo.repository.ReservationRepository;
import ifba.br.demo.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private UsuarioServiceImp usuarioServiceImp;
    @Autowired
    private UsuarioMapper usuarioMapper;
    @Autowired
    logAuditService logAuditService;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkReservationTimeouts() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> activeReservations = reservationRepository.findActiveReservations(now);

        for (Reservation reservation : activeReservations) {
            if (reservation.getTempoFinal().isBefore(now)) {
                reservation.setStatus("COMPLETO");
                reservationRepository.save(reservation);

                Resource resource = reservation.getResource();
                resource.setStatus("LIVRE");
                resource.setLastUpdated(now);
                resourceRepository.save(resource);

                logAuditService.logAction(
                        reservation.getUsuario().getNome(),
                        reservation.getStatus(),
                        resource.getId(),
                        "AUTO_RELEASE"
                );
            }
        }
    }

    @Transactional
    public Reservation createReservation(Long resourceId, Long userId, int durationMinutes) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusMinutes(durationMinutes);

        if (reservationRepository.existsConflictingReservation(resourceId, now, endTime)) {
            throw new RuntimeException("Recurso já reservado para este período");
        }

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Recurso não encontrado"));

        if (!"LIVRE".equals(resource.getStatus())) {
            throw new RuntimeException("Recurso não está disponível");
        }

        Reservation reservation = new Reservation();
        reservation.setUsuario(usuarioMapper.UsuarioDtoToUsuario(usuarioServiceImp.findById(userId)));
        reservation.setResource(resource);
        reservation.setTempoInicial(now);
        reservation.setTempoFinal(endTime);
        reservation.setStatus("ATIVO");

        Reservation savedReservation = reservationRepository.save(reservation);

        resource.setStatus("OCUPADO");
        resource.setLastUpdated(now);
        resourceRepository.save(resource);

        return savedReservation;
    }

    @Transactional
    public void releaseReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        if (!"ATIVO".equals(reservation.getStatus())) {
            throw new RuntimeException("Reserva não está ativa");
        }

        LocalDateTime now = LocalDateTime.now();
        reservation.setStatus("CANCELADO");
        reservation.setTempoFinal(now);
        reservationRepository.save(reservation);

        Resource resource = reservation.getResource();
        resource.setStatus("LIVRE");
        resource.setLastUpdated(now);
        resourceRepository.save(resource);
    }

    public List<Reservation> findReservationsByUserId(Long userId) {
        return reservationRepository.findByUsuarioIdOrderByTempoInicialDesc(userId);
    }
}