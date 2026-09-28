package modelo;

public record Expositor(String identificacion, String nombre) {
    public Expositor {
        identificacion = Validacion.identificador(identificacion, "ID de expositor");
        nombre = Validacion.texto(nombre, "Nombre de expositor");
    }
    @Override public String toString() { return identificacion + " - " + nombre; }
}
