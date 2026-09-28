package servicio;

import estructuras.*;
import modelo.*;
import java.time.LocalDateTime;
import java.util.function.Consumer;

/** Reglas de negocio y coordinacion de las seis estructuras. Datos solo en memoria. */
public final class SistemaProyectores {
    // Politica academica elegida por el grupo, no especificacion de un fabricante.
    public static final int LIMITE_HORAS = 2000;

    private final ListaSecuencial<Proyector> inventario = new ListaSecuencial<>();
    private final ListaSimple<Reserva> reservas = new ListaSimple<>();
    private final Cola<Solicitud> espera = new Cola<>();
    private final ListaDoble<Movimiento> historial = new ListaDoble<>();
    private final Pila<AccionMantenimiento> deshacer = new Pila<>();
    private final ListaCircular<Expositor> turnos = new ListaCircular<>();
    private int siguienteReserva = 1;
    private int siguienteSolicitud = 1;
    private int siguienteMovimiento = 1;
    private int reservaEvento;
    private String nombreEvento;

    public int cantidadProyectores() { return inventario.tamano(); }
    public int cantidadReservas() { return reservas.tamano(); }
    public int cantidadSolicitudes() { return espera.tamano(); }
    public int cantidadMovimientos() { return historial.tamano(); }
    public int cantidadReversiones() { return deshacer.tamano(); }
    public int cantidadTurnos() { return turnos.tamano(); }
    public Solicitud frenteEspera() { return espera.frente(); }
    public AccionMantenimiento topeDeshacer() { return deshacer.tope(); }
    public Expositor turnoActual() { return turnos.actual(); }
    public void listarInventario(Consumer<Proyector> salida) { inventario.recorrer(salida); }
    public void listarReservas(Consumer<Reserva> salida) { reservas.recorrer(salida); }
    public void listarEspera(Consumer<Solicitud> salida) { espera.recorrer(salida); }
    public void listarTurnos(Consumer<Expositor> salida) { turnos.recorrer(salida); }
    public void listarHistorial(boolean inverso, Consumer<Movimiento> salida) {
        if (inverso) historial.recorrerAtras(salida);
        else historial.recorrerAdelante(salida);
    }

    public Proyector buscarProyector(String codigo) {
        String clave = Validacion.identificador(codigo, "Codigo");
        return inventario.buscar(p -> p.codigo().equals(clave));
    }

    public Reserva buscarReserva(int id) { return reservas.buscar(r -> r.id() == id); }

    public Reserva buscarReservaDocente(String idDocente) {
        String clave = Validacion.identificador(idDocente, "ID del docente");
        return reservas.buscar(r -> r.idDocente().equals(clave));
    }

    private Proyector exigirProyector(String codigo) {
        Proyector proyector = buscarProyector(codigo);
        if (proyector == null) throw new IllegalArgumentException("No existe el proyector indicado.");
        return proyector;
    }

    private void actualizar(Proyector proyector) {
        if (!inventario.modificar(p -> p.codigo().equals(proyector.codigo()), proyector)) {
            throw new IllegalStateException("No se pudo actualizar el inventario.");
        }
    }

    private void registrar(String tipo, String detalle) {
        historial.insertarAlFinal(new Movimiento(siguienteMovimiento++, LocalDateTime.now(), tipo, detalle));
    }

    public void registrarProyector(String codigo, String marca, int luminosidad, String aula,
                                  int horas, Estado estado) {
        Proyector nuevo = new Proyector(codigo, marca, luminosidad, aula, horas, estado);
        if (buscarProyector(nuevo.codigo()) != null) throw new IllegalArgumentException("Codigo duplicado.");
        if (estado == Estado.RESERVADO) {
            throw new IllegalArgumentException("Registre disponible o en mantenimiento y luego cree una reserva.");
        }
        if (horas > LIMITE_HORAS && estado != Estado.MANTENIMIENTO) {
            throw new IllegalArgumentException("Lampara sobre el limite: registre en mantenimiento.");
        }
        inventario.insertar(nuevo);
        registrar("ALTA_INVENTARIO", nuevo.toString());
    }

    public void eliminarProyector(String codigo) {
        Proyector p = exigirProyector(codigo);
        if (p.estado() == Estado.RESERVADO) throw new IllegalArgumentException("Tiene una reserva activa.");
        if (espera.buscar(s -> s.codigoProyector().equals(p.codigo())) != null) {
            throw new IllegalArgumentException("Tiene solicitudes en espera; atienda o cancele primero.");
        }
        inventario.eliminar(item -> item.codigo().equals(p.codigo()));
        registrar("BAJA_INVENTARIO", p.codigo());
    }

    /** Una solicitud especifica un proyector. La cola global respeta FIFO estricto. */
    public String solicitarReserva(String codigo, String idDocente, String docente, String bloque) {
        Proyector p = exigirProyector(codigo);
        String id = Validacion.identificador(idDocente, "ID del docente");
        String nombre = Validacion.texto(docente, "Docente");
        String horario = Validacion.texto(bloque, "Bloque");
        if (buscarReservaDocente(id) != null || espera.buscar(s -> s.idDocente().equals(id)) != null) {
            throw new IllegalArgumentException("El docente ya tiene una reserva o solicitud pendiente.");
        }
        if (p.horasLampara() > LIMITE_HORAS) {
            throw new IllegalArgumentException("Lampara con " + p.horasLampara() + " h: supera el limite de " + LIMITE_HORAS + " h.");
        }
        if (p.estado() == Estado.MANTENIMIENTO) throw new IllegalArgumentException("Proyector en mantenimiento.");
        if (p.estado() == Estado.RESERVADO || !espera.estaVacia()) {
            Solicitud solicitud = new Solicitud(siguienteSolicitud++, p.codigo(), id, nombre, horario, LocalDateTime.now());
            espera.encolar(solicitud);
            registrar("SOLICITUD_EN_COLA", solicitud.toString());
            return "Solicitud S" + solicitud.id() + " en cola. Posicion: " + espera.tamano() + ".";
        }
        Reserva reserva = crearReserva(p, id, nombre, horario);
        return "Reserva creada: " + reserva;
    }

    private Reserva crearReserva(Proyector p, String id, String docente, String bloque) {
        Reserva reserva = new Reserva(siguienteReserva++, p.codigo(), id, docente, bloque, LocalDateTime.now());
        reservas.insertar(reserva);
        actualizar(p.conEstado(Estado.RESERVADO));
        registrar("RESERVA", reserva.toString());
        return reserva;
    }

    public Reserva atenderSiguiente() {
        Solicitud solicitud = espera.frente();
        if (solicitud == null) throw new IllegalArgumentException("La cola esta vacia.");
        Proyector p = exigirProyector(solicitud.codigoProyector());
        if (p.estado() != Estado.DISPONIBLE || p.horasLampara() > LIMITE_HORAS) {
            throw new IllegalArgumentException("El proyector de S" + solicitud.id()
                    + " no esta habilitado. La solicitud conserva el frente de la cola.");
        }
        Reserva reserva = crearReserva(p, solicitud.idDocente(), solicitud.docente(), solicitud.bloque());
        espera.desencolar();
        registrar("ATENCION_FIFO", "S" + solicitud.id() + " -> R" + reserva.id());
        return reserva;
    }

    public Solicitud cancelarFrente() {
        if (espera.estaVacia()) throw new IllegalArgumentException("La cola esta vacia.");
        Solicitud solicitud = espera.desencolar();
        registrar("CANCELACION_COLA", solicitud.toString());
        return solicitud;
    }

    public void devolver(int idReserva, int horasUso) {
        Reserva reserva = buscarReserva(idReserva);
        if (reserva == null) throw new IllegalArgumentException("No existe esa reserva activa.");
        if (reservaEvento == idReserva) throw new IllegalArgumentException("Cierre primero el evento vinculado a esta reserva.");
        Validacion.rango(horasUso, 0, 10000, "Horas de uso");
        Proyector p = exigirProyector(reserva.codigoProyector());
        int nuevasHoras = p.horasLampara() + horasUso;
        Estado estado = nuevasHoras > LIMITE_HORAS ? Estado.MANTENIMIENTO : Estado.DISPONIBLE;
        Proyector devuelto = p.conHorasYEstado(nuevasHoras, estado);
        reservas.eliminar(r -> r.id() == idReserva);
        actualizar(devuelto);
        registrar("DEVOLUCION", "R" + idReserva + " | " + p.codigo() + " | +" + horasUso + " h | " + estado);
        if (estado == Estado.MANTENIMIENTO) {
            registrar("LIMITE_LAMPARA", p.codigo() + ": requiere cambio de lampara; " + nuevasHoras + " h.");
        }
    }

    public void enviarMantenimiento(String codigo, String motivo) {
        Proyector p = exigirProyector(codigo);
        String razon = Validacion.texto(motivo, "Motivo");
        if (p.estado() != Estado.DISPONIBLE) {
            throw new IllegalArgumentException("Solo se envia a mantenimiento un proyector disponible.");
        }
        Proyector posterior = p.conEstado(Estado.MANTENIMIENTO);
        actualizar(posterior);
        deshacer.apilar(new AccionMantenimiento(p, posterior, razon));
        registrar("MANTENIMIENTO", p.codigo() + " | " + razon);
    }

    public void cambiarLampara(String codigo) {
        Proyector p = exigirProyector(codigo);
        if (p.estado() != Estado.MANTENIMIENTO) throw new IllegalArgumentException("El equipo debe estar en mantenimiento.");
        actualizar(p.conHorasYEstado(0, Estado.MANTENIMIENTO));
        registrar("CAMBIO_LAMPARA", p.codigo() + " | " + p.horasLampara() + " h -> 0 h; sigue en mantenimiento.");
    }

    public void finalizarMantenimiento(String codigo) {
        Proyector p = exigirProyector(codigo);
        if (p.estado() != Estado.MANTENIMIENTO) throw new IllegalArgumentException("El equipo no esta en mantenimiento.");
        if (p.horasLampara() > LIMITE_HORAS) throw new IllegalArgumentException("Cambie primero la lampara que supera el limite.");
        actualizar(p.conEstado(Estado.DISPONIBLE));
        registrar("ALTA_MANTENIMIENTO", p.codigo());
    }

    public void deshacerUltimoMantenimiento() {
        AccionMantenimiento accion = deshacer.tope();
        if (accion == null) throw new IllegalArgumentException("No hay cambios para deshacer.");
        // La identidad detecta incluso una baja y posterior alta con el mismo codigo.
        if (buscarProyector(accion.anterior().codigo()) != accion.posterior()) {
            throw new IllegalArgumentException("Reversion obsoleta: hubo cambios posteriores. Use 'Descartar tope obsoleto'.");
        }
        actualizar(accion.anterior());
        deshacer.desapilar();
        registrar("DESHACER", accion.anterior().codigo() + " | restaurado a " + accion.anterior().estado());
    }

    public void descartarReversionObsoleta() {
        AccionMantenimiento accion = deshacer.tope();
        if (accion == null) throw new IllegalArgumentException("La pila esta vacia.");
        if (buscarProyector(accion.anterior().codigo()) == accion.posterior()) {
            throw new IllegalArgumentException("La accion todavia es reversible; use deshacer.");
        }
        deshacer.desapilar();
        registrar("DESCARTE_REVERSION", accion.anterior().codigo() + " | cambio posterior impide restauracion segura.");
    }

    public void iniciarEvento(int idReserva, String nombre) {
        if (reservaEvento != 0) throw new IllegalArgumentException("Ya existe un evento activo. Cierrelo primero.");
        Reserva reserva = buscarReserva(idReserva);
        if (reserva == null) throw new IllegalArgumentException("El evento requiere una reserva activa.");
        nombreEvento = Validacion.texto(nombre, "Nombre del evento");
        reservaEvento = idReserva;
        registrar("INICIO_EVENTO", nombreEvento + " | " + reserva);
    }

    public String descripcionEvento() {
        return reservaEvento == 0 ? "Sin evento activo." : nombreEvento + " | " + buscarReserva(reservaEvento);
    }

    private void exigirEvento() {
        if (reservaEvento == 0) throw new IllegalArgumentException("Inicie un evento con una reserva activa.");
    }

    public void agregarExpositor(String id, String nombre) {
        exigirEvento();
        Expositor expositor = new Expositor(id, nombre);
        if (turnos.buscar(e -> e.identificacion().equals(expositor.identificacion())) != null) {
            throw new IllegalArgumentException("Expositor duplicado en la ronda.");
        }
        turnos.insertar(expositor);
        registrar("ALTA_TURNO", expositor.toString());
    }

    public Expositor avanzarTurno() {
        exigirEvento();
        if (turnos.estaVacia()) throw new IllegalArgumentException("La ronda esta vacia.");
        Expositor expositor = turnos.avanzar();
        registrar("ROTACION", "Turno de " + expositor);
        return expositor;
    }

    public Expositor eliminarTurnoActual() {
        exigirEvento();
        if (turnos.estaVacia()) throw new IllegalArgumentException("La ronda esta vacia.");
        Expositor retirado = turnos.eliminarActual();
        registrar("BAJA_TURNO", retirado.toString());
        return retirado;
    }

    public void cerrarEvento() {
        exigirEvento();
        registrar("CIERRE_EVENTO", nombreEvento + " | R" + reservaEvento + " | " + turnos.tamano() + " turnos retirados.");
        while (!turnos.estaVacia()) turnos.eliminarActual();
        reservaEvento = 0;
        nombreEvento = null;
    }

    public static SistemaProyectores conDatosEjemplo() {
        SistemaProyectores s = new SistemaProyectores();
        s.registrarProyector("PRO001", "Epson", 3500, "B201", 1200, Estado.DISPONIBLE);
        s.registrarProyector("PRO002", "BenQ", 3200, "A102", 800, Estado.MANTENIMIENTO);
        s.registrarProyector("PRO003", "ViewSonic", 3600, "C301", 500, Estado.DISPONIBLE);
        s.registrarProyector("PRO004", "Optoma", 4000, "D101", 2001, Estado.MANTENIMIENTO);
        s.solicitarReserva("PRO003", "DOC001", "Ana Torres", "09:00-11:00");
        return s;
    }
}