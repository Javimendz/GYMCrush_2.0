package com.backend.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.backend.domain.Reserva;
import com.backend.domain.enums.TipoNotificacion;
import com.backend.event.ReservaEvent;
import com.backend.repository.ReservaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificacionesScheduler {

    private final ReservaRepository reservaRepo;
    private final ApplicationEventPublisher eventPublisher;

    //@Scheduled(cron = "0 */30 * * * *") // Cada 30 minutos
    @Scheduled(cron = "*/10 * * * * *") // Para pruebas: cada 10 segundos
    @Transactional
    public void recordatorioProximasClases() {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();
        LocalTime limite = ahora.plusHours(2);

        log.info("Job Recordatorios: Buscando clases para hoy {} entre {} y {}", hoy, ahora, limite);

        List<Reserva> proximas = reservaRepo.findReservasParaRecordatorio(hoy, ahora, limite);

        for (Reserva r : proximas) {
            if (r.getConfirmado() == null || !r.getConfirmado()) {
                eventPublisher.publishEvent(new ReservaEvent(
                    r.getId(), 
                    "¡Recordatorio! Tu clase de " + r.getHorario().getActividad().getNombre() + " empieza pronto.",
                    "Próximo entrenamiento",
                    TipoNotificacion.RECORDATORIO
                ));
                
                r.setConfirmado(true);
                reservaRepo.save(r);
            }
        }
    }
}