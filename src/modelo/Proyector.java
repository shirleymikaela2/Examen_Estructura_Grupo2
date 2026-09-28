package modelo;

/** Modelo inmutable: las actualizaciones reemplazan el objeto del inventario. */
public record Proyector(String codigo, String marca, int luminosidad, String aulaBase,
                        int horasLampara, Estado estado) {
    public Proyector {
        codigo = Validacion.identificador(codigo, "Codigo");
        marca = Validacion.texto(marca, "Marca");
        aulaBase = Validacion.texto(aulaBase, "Aula base");
        Validacion.rango(luminosidad, 1, 100000, "Luminosidad");
        Validacion.rango(horasLampara, 0, 1000000, "Horas de lampara");
        if (estado == null) throw new IllegalArgumentException("El estado es obligatorio.");
    }

    public Proyector conEstado(Estado nuevo) {
        return new Proyector(codigo, marca, luminosidad, aulaBase, horasLampara, nuevo);
    }

    public Proyector conHorasYEstado(int horas, Estado nuevo) {
        return new Proyector(codigo, marca, luminosidad, aulaBase, horas, nuevo);
    }

    @Override public String toString() {
        return String.format("%-8s | %-12s | %5d lm | %-10s | %5d h | %s",
                codigo, marca, luminosidad, aulaBase, horasLampara, estado);
    }
}
