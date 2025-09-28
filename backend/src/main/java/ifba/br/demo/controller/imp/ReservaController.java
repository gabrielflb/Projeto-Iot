package ifba.br.demo.controller.imp;

import ifba.br.demo.domain.dto.ReservaRequest;
import ifba.br.demo.domain.entity.Reservation;
import ifba.br.demo.service.imp.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reserva")
public class ReservaController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservaRequest request, @AuthenticationPrincipal Jwt jwt) {
        try {
            Long userId = Long.parseLong(jwt.getSubject());

            Reservation reservation = reservationService.createReservation(
                    request.resourceId(),
                    userId,
                    request.durationMinutes()
            );
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/history")
    public ResponseEntity<List<Reservation>> getMyHistory(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        List<Reservation> history = reservationService.findReservationsByUserId(userId);
        return ResponseEntity.ok(history);
    }
}