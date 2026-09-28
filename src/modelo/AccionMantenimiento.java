package modelo;

public record AccionMantenimiento(
        Proyector anterior,
        Proyector posterior,
        String motivo) {

    @Override
    public String toString() {
        return anterior.codigo()
                + " | "
                + anterior.estado()
                + " -> MANTENIMIENTO | "
                + motivo;
    }
}

