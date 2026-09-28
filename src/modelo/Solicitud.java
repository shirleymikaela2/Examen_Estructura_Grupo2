package modelo;

public final class Solicitud {
    private final String cedulaDocente;
    private final String nombreDocente;
    private final String codigoProyector;

    public Solicitud(String cedulaDocente, String nombreDocente, String codigoProyector) {
        this.cedulaDocente = cedulaDocente;
        this.nombreDocente = nombreDocente;
        this.codigoProyector = codigoProyector;
    }

    public String getCedulaDocente() {
        return cedulaDocente;
    }

    public String getNombreDocente() {
        return nombreDocente;
    }

    public String getCodigoProyector() {
        return codigoProyector;
    }

    @Override
    public String toString() {
        return "Solicitud{" +
                "cedulaDocente='" + cedulaDocente + '\'' +
                ", nombreDocente='" + nombreDocente + '\'' +
                ", codigoProyector='" + codigoProyector + '\'' +
                '}';
    }
}