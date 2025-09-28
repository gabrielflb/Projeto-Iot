package ifba.br.demo.repository;

import ifba.br.demo.domain.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.status = 'ACTIVE' AND r.tempoFinal > :currentTime")
    List<Reservation> findActiveReservations(@Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT r FROM Reservation r WHERE r.resource.id = :resourceId AND r.status = 'ACTIVE' AND r.tempoFinal > :currentTime")
    List<Reservation> findActiveReservationsByResource(@Param("resourceId") Long resourceId, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.resource.id = :resourceId AND r.status = 'ACTIVE' " +
            "AND (:startTime < r.tempoFinal AND :endTime > r.tempoInicial)")
    boolean existsConflictingReservation(@Param("resourceId") Long resourceId,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime);

    @Query("SELECT r FROM Reservation r WHERE r.status = 'ACTIVE' AND r.tempoFinal BETWEEN :now AND :threshold")
    List<Reservation> findExpiringReservations(@Param("now") LocalDateTime now,
                                               @Param("threshold") LocalDateTime threshold);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.tempoInicial BETWEEN :startDate AND :endDate")
    Long countReservationsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    List<Reservation> findByUsuarioIdOrderByTempoInicialDesc(Long usuarioId);
}