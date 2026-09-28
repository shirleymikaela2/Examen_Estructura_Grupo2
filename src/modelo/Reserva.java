package modelo;

import java.time.LocalDateTime;

public record Reserva(int id, String codigoProyector, String idDocente, String docente,
                      String bloque, LocalDateTime fecha) {
    @Override public String toString() {
        return "R" + id + " | " + codigoProyector + " | " + idDocente + " - " + docente + " | " + bloque;
    }
}
