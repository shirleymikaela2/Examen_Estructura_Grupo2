package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record Movimiento(int id, LocalDateTime fecha, String tipo, String detalle) {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Override public String toString() {
        return String.format("#%03d | %s | %-20s | %s", id, fecha.format(FORMATO), tipo, detalle);
    }
}
