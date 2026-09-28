import estructuras.*;
import interfaz.Consola;
import modelo.*;
import servicio.SistemaProyectores;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/** Pruebas sin dependencias externas. Un fallo produce codigo de salida distinto de cero. */
public final class Pruebas {
    private static int aprobadas;
    private static int fallidas;

    private Pruebas() { }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }

    private static void rechaza(Runnable accion) {
        try { accion.run(); }
        catch (IllegalArgumentException e) { return; }
        throw new AssertionError("Se esperaba una validacion.");
    }

    private static void caso(String nombre, Runnable prueba) {
        try {
            prueba.run();
            aprobadas++;
            System.out.println("[OK] " + nombre);
        } catch (RuntimeException | AssertionError e) {
            fallidas++;
            System.out.println("[FALLO] " + nombre + ": " + e.getMessage());
        }
    }

    private static SistemaProyectores base() {
        SistemaProyectores s = new SistemaProyectores();
        s.registrarProyector("P1", "Epson", 3500, "A1", 100, Estado.DISPONIBLE);
        s.registrarProyector("P2", "BenQ", 3000, "A2", 100, Estado.DISPONIBLE);
        return s;
    }

    private static void reservar(SistemaProyectores s, String codigo, String docente) {
        s.solicitarReserva(codigo, docente, "Docente " + docente, "08:00-10:00");
    }

    private static void coherente(SistemaProyectores s) {
        s.listarInventario(p -> {
            int[] activas = {0};
            s.listarReservas(r -> { if (r.codigoProyector().equals(p.codigo())) activas[0]++; });
            verificar(activas[0] == (p.estado() == Estado.RESERVADO ? 1 : 0), "Inventario y reservas desincronizados");
        });
        s.listarReservas(r -> verificar(s.buscarProyector(r.codigoProyector()) != null, "Reserva huerfana"));
    }

    public static void main(String[] args) {
        caso("Lista secuencial: crecimiento, busqueda y modificacion", () -> {
            ListaSecuencial<Integer> l = new ListaSecuencial<>(1);
            for (int i = 0; i < 30; i++) l.insertar(i);
            verificar(l.tamano() == 30 && l.obtener(29) == 29, "Crecimiento");
            verificar(l.buscar(n -> n == 15) == 15, "Busqueda");
            verificar(l.modificar(n -> n == 15, 99) && l.obtener(15) == 99, "Modificar");
            verificar(!l.modificar(n -> n == -1, 7), "No existente");
            rechaza(() -> l.insertar(null));
        });
        caso("Lista secuencial: eliminacion y desplazamiento", () -> {
            ListaSecuencial<Integer> l = new ListaSecuencial<>();
            for (int i = 1; i <= 4; i++) l.insertar(i);
            verificar(l.eliminar(n -> n == 1) == 1, "Primero");
            verificar(l.eliminar(n -> n == 3) == 3, "Medio");
            verificar(l.eliminar(n -> n == 4) == 4, "Ultimo");
            verificar(l.obtener(0) == 2 && l.tamano() == 1, "Compactacion");
            l.eliminar(n -> n == 2);
            verificar(l.estaVacia() && l.eliminar(n -> true) == null, "Vacia");
        });
        caso("Lista simple: elimina cabeza, medio, cola y nodo unico", () -> {
            ListaSimple<Integer> l = new ListaSimple<>();
            verificar(l.eliminar(n -> true) == null, "Vacia");
            for (int i = 1; i <= 4; i++) l.insertar(i);
            verificar(l.buscar(n -> n == 3) == 3, "Buscar");
            l.eliminar(n -> n == 1); l.eliminar(n -> n == 3); l.eliminar(n -> n == 4);
            StringBuilder b = new StringBuilder(); l.recorrer(b::append);
            verificar(b.toString().equals("2"), "Enlaces");
            l.eliminar(n -> n == 2); l.insertar(8);
            verificar(l.tamano() == 1 && l.buscar(n -> true) == 8, "Reutilizacion");
        });
        caso("Lista doble: recorridos opuestos y lista vacia", () -> {
            ListaDoble<String> l = new ListaDoble<>();
            StringBuilder a = new StringBuilder(), b = new StringBuilder();
            l.recorrerAtras(b::append);
            l.insertarAlFinal("A"); l.insertarAlFinal("B"); l.insertarAlFinal("C");
            l.recorrerAdelante(a::append); l.recorrerAtras(b::append);
            verificar(a.toString().equals("ABC") && b.toString().equals("CBA") && l.tamano() == 3, "Bidireccionalidad");
        });
        caso("Cola: FIFO, frente, vaciado y reutilizacion", () -> {
            Cola<Integer> c = new Cola<>();
            verificar(c.frente() == null && c.desencolar() == null, "Vacia");
            c.encolar(1); c.encolar(2);
            verificar(c.frente() == 1 && c.tamano() == 2, "Frente no extrae");
            verificar(c.desencolar() == 1 && c.desencolar() == 2, "FIFO");
            c.encolar(3); verificar(c.desencolar() == 3 && c.estaVacia(), "Reutilizacion");
        });
        caso("Pila: LIFO y operaciones sobre pila vacia", () -> {
            Pila<Integer> p = new Pila<>();
            verificar(p.tope() == null && p.desapilar() == null, "Vacia");
            p.apilar(1); p.apilar(2);
            verificar(p.tope() == 2 && p.tamano() == 2, "Tope no extrae");
            verificar(p.desapilar() == 2 && p.desapilar() == 1 && p.estaVacia(), "LIFO");
        });
        caso("Circular: vacia, nodo unico y reinsercion", () -> {
            ListaCircular<String> c = new ListaCircular<>();
            verificar(c.avanzar() == null && c.eliminarActual() == null, "Vacia");
            c.insertar("A"); verificar(c.avanzar().equals("A"), "Autorreferencia");
            verificar(c.eliminarActual().equals("A") && c.estaVacia(), "Eliminar unico");
            c.insertar("B"); verificar(c.actual().equals("B") && c.tamano() == 1, "Reinsertar");
        });
        caso("Circular: vuelta completa, insercion rotada y eliminacion", () -> {
            ListaCircular<String> c = new ListaCircular<>();
            c.insertar("A"); c.insertar("B"); c.insertar("C");
            c.avanzar(); c.insertar("D");
            StringBuilder b = new StringBuilder(); c.recorrer(b::append);
            verificar(b.toString().equals("BCAD"), "Insercion al final de ronda actual");
            for (int i = 0; i < 4; i++) c.avanzar();
            verificar(c.actual().equals("B"), "Circularidad");
            c.eliminarActual(); verificar(c.actual().equals("C") && c.tamano() == 3, "Eliminar actual");
            c.eliminarActual(); c.eliminarActual(); c.eliminarActual();
            verificar(c.estaVacia(), "Vaciado completo");
        });
        caso("Datos iniciales consistentes y reserva real para PRO003", () -> {
            SistemaProyectores s = SistemaProyectores.conDatosEjemplo();
            verificar(s.cantidadProyectores() == 4 && s.cantidadReservas() == 1, "Datos");
            verificar(s.buscarReserva(1).codigoProyector().equals("PRO003"), "Reserva inicial");
            coherente(s);
        });
        caso("Inventario: duplicados normalizados y entradas invalidas", () -> {
            SistemaProyectores s = base();
            rechaza(() -> s.registrarProyector(" p1 ", "X", 1, "A", 0, Estado.DISPONIBLE));
            rechaza(() -> s.registrarProyector("P3", " ", 1, "A", 0, Estado.DISPONIBLE));
            rechaza(() -> s.registrarProyector("P3", "X", -1, "A", 0, Estado.DISPONIBLE));
            rechaza(() -> s.registrarProyector("P3", "X", 1, "A", -1, Estado.DISPONIBLE));
            rechaza(() -> s.registrarProyector("P3", "X", 1, "A", 0, Estado.RESERVADO));
            verificar(s.cantidadProyectores() == 2, "No se insertaron registros invalidos");
        });
        caso("Reserva valida, busqueda docente y estado sincronizado", () -> {
            SistemaProyectores s = base(); reservar(s, "p1", "d1");
            verificar(s.buscarReservaDocente("D1").id() == 1, "Busqueda");
            verificar(s.buscarProyector("P1").estado() == Estado.RESERVADO, "Estado"); coherente(s);
        });
        caso("Limite de lampara: 2000 permitido; 2001 bloqueado", () -> {
            SistemaProyectores s = new SistemaProyectores();
            s.registrarProyector("P1", "A", 1, "A", 2000, Estado.DISPONIBLE);
            s.registrarProyector("P2", "A", 1, "A", 2001, Estado.MANTENIMIENTO);
            reservar(s, "P1", "D1"); rechaza(() -> reservar(s, "P2", "D2"));
            rechaza(() -> s.registrarProyector("P3", "A", 1, "A", 2001, Estado.DISPONIBLE));
            verificar(s.cantidadReservas() == 1 && s.cantidadSolicitudes() == 0, "Regla limite");
        });
        caso("No se reserva un equipo en mantenimiento", () -> {
            SistemaProyectores s = base(); s.enviarMantenimiento("P1", "Revision");
            rechaza(() -> reservar(s, "P1", "D1"));
            verificar(s.cantidadSolicitudes() == 0, "No encolar un equipo no habilitado"); coherente(s);
        });
        caso("Docente no duplica reserva ni solicitud pendiente", () -> {
            SistemaProyectores s = base(); reservar(s, "P1", "D1");
            rechaza(() -> reservar(s, "P2", "d1"));
            reservar(s, "P1", "D2"); rechaza(() -> reservar(s, "P2", "D2"));
            verificar(s.cantidadReservas() == 1 && s.cantidadSolicitudes() == 1, "Sin duplicados");
        });
        caso("FIFO: no pierde el frente bloqueado y atiende en orden", () -> {
            SistemaProyectores s = base(); reservar(s, "P1", "D1");
            reservar(s, "P1", "D2"); reservar(s, "P1", "D3");
            rechaza(() -> s.atenderSiguiente());
            verificar(s.frenteEspera().idDocente().equals("D2") && s.cantidadSolicitudes() == 2, "Frente conservado");
            s.devolver(1, 2); Reserva r = s.atenderSiguiente();
            verificar(r.idDocente().equals("D2"), "Primero");
            s.devolver(r.id(), 1); verificar(s.atenderSiguiente().idDocente().equals("D3"), "Segundo"); coherente(s);
        });
        caso("FIFO global: solicitud nueva no adelanta a las anteriores", () -> {
            SistemaProyectores s = base(); reservar(s, "P1", "D1");
            reservar(s, "P1", "D2"); reservar(s, "P2", "D3");
            verificar(s.cantidadReservas() == 1 && s.cantidadSolicitudes() == 2, "Encolado global");
            rechaza(() -> s.atenderSiguiente());
            verificar(s.cancelarFrente().idDocente().equals("D2"), "Cancelar frente");
            verificar(s.atenderSiguiente().idDocente().equals("D3"), "Atender siguiente"); coherente(s);
        });
        caso("Cola vacia y solicitud invalidada sin efectos parciales", () -> {
            SistemaProyectores s = base();
            rechaza(() -> s.atenderSiguiente()); rechaza(() -> s.cancelarFrente());
            rechaza(() -> s.solicitarReserva("P1", "D1", " ", "08:00"));
            verificar(s.cantidadReservas() == 0 && s.cantidadSolicitudes() == 0, "Atomicidad");
        });
        caso("Devolucion suma horas y libera inventario", () -> {
            SistemaProyectores s = base(); reservar(s, "P1", "D1"); s.devolver(1, 3);
            verificar(s.buscarProyector("P1").horasLampara() == 103 && s.cantidadReservas() == 0, "Devolucion"); coherente(s);
        });
        caso("Devolucion sobre limite exige mantenimiento y nueva lampara", () -> {
            SistemaProyectores s = base(); reservar(s, "P1", "D1"); reservar(s, "P1", "D2");
            s.devolver(1, 1901);
            verificar(s.buscarProyector("P1").estado() == Estado.MANTENIMIENTO, "Mantenimiento automatico");
            rechaza(() -> s.atenderSiguiente()); rechaza(() -> s.finalizarMantenimiento("P1"));
            s.cambiarLampara("P1"); s.finalizarMantenimiento("P1");
            verificar(s.atenderSiguiente().idDocente().equals("D2") && s.buscarProyector("P1").horasLampara() == 0, "Recuperacion");
            coherente(s);
        });
        caso("Devolucion invalida conserva la reserva y el historial", () -> {
            SistemaProyectores s = base(); reservar(s, "P1", "D1"); int h = s.cantidadMovimientos();
            rechaza(() -> s.devolver(1, -1)); rechaza(() -> s.devolver(999, 1));
            verificar(s.cantidadReservas() == 1 && s.cantidadMovimientos() == h, "Sin efectos parciales"); coherente(s);
        });
        caso("Eliminacion bloqueada por reserva y por espera", () -> {
            SistemaProyectores s = base(); reservar(s, "P1", "D1"); reservar(s, "P1", "D2");
            rechaza(() -> s.eliminarProyector("P1")); s.devolver(1, 0);
            rechaza(() -> s.eliminarProyector("P1")); s.cancelarFrente(); s.eliminarProyector("P1");
            verificar(s.buscarProyector("P1") == null && s.cantidadProyectores() == 1, "Baja validada");
        });
        caso("No envia equipo reservado a mantenimiento", () -> {
            SistemaProyectores s = base(); reservar(s, "P1", "D1");
            rechaza(() -> s.enviarMantenimiento("P1", "Revision"));
            rechaza(() -> s.cambiarLampara("P1")); coherente(s);
        });
        caso("Deshacer mantenimiento restaura en orden LIFO", () -> {
            SistemaProyectores s = base(); s.enviarMantenimiento("P1", "Error 1"); s.enviarMantenimiento("P2", "Error 2");
            verificar(s.topeDeshacer().anterior().codigo().equals("P2"), "Tope");
            s.deshacerUltimoMantenimiento();
            verificar(s.buscarProyector("P2").estado() == Estado.DISPONIBLE && s.buscarProyector("P1").estado() == Estado.MANTENIMIENTO, "LIFO");
            s.deshacerUltimoMantenimiento(); rechaza(() -> s.deshacerUltimoMantenimiento());
            verificar(s.cantidadReversiones() == 0, "Pila vacia"); coherente(s);
        });
        caso("Reversion obsoleta no sobrescribe una lampara reemplazada", () -> {
            SistemaProyectores s = base(); s.enviarMantenimiento("P1", "Revision"); s.cambiarLampara("P1");
            rechaza(() -> s.deshacerUltimoMantenimiento());
            verificar(s.buscarProyector("P1").horasLampara() == 0 && s.cantidadReversiones() == 1, "No sobrescribir");
            s.descartarReversionObsoleta(); verificar(s.cantidadReversiones() == 0, "Descartar");
        });
        caso("Reversion obsoleta no afecta un equipo recreado con igual codigo", () -> {
            SistemaProyectores s = base(); s.enviarMantenimiento("P1", "Revision"); s.eliminarProyector("P1");
            s.registrarProyector("P1", "Nuevo", 1, "B", 10, Estado.MANTENIMIENTO);
            rechaza(() -> s.deshacerUltimoMantenimiento());
            verificar(s.buscarProyector("P1").marca().equals("Nuevo"), "Identidad protegida");
        });
        caso("Solo se descarta una reversion obsoleta", () -> {
            SistemaProyectores s = base(); rechaza(() -> s.descartarReversionObsoleta());
            s.enviarMantenimiento("P1", "Revision"); rechaza(() -> s.descartarReversionObsoleta());
            verificar(s.cantidadReversiones() == 1, "Accion valida conservada");
        });
        caso("Evento requiere reserva activa y expositores sin duplicar", () -> {
            SistemaProyectores s = base(); rechaza(() -> s.iniciarEvento(99, "Evento"));
            rechaza(() -> s.agregarExpositor("E1", "A")); reservar(s, "P1", "D1"); s.iniciarEvento(1, "Evento");
            rechaza(() -> s.iniciarEvento(1, "Otro")); s.agregarExpositor("E1", "A");
            rechaza(() -> s.agregarExpositor("e1", "B")); verificar(s.cantidadTurnos() == 1, "Sin duplicados");
        });
        caso("Evento: ronda circular, baja actual y cierre antes de devolver", () -> {
            SistemaProyectores s = base(); reservar(s, "P1", "D1"); s.iniciarEvento(1, "Evento");
            s.agregarExpositor("E1", "A"); s.agregarExpositor("E2", "B"); s.agregarExpositor("E3", "C");
            for (int i = 0; i < 3; i++) s.avanzarTurno();
            verificar(s.turnoActual().identificacion().equals("E1"), "Vuelta completa");
            s.eliminarTurnoActual(); verificar(s.turnoActual().identificacion().equals("E2"), "Sucesor");
            rechaza(() -> s.devolver(1, 1)); s.cerrarEvento(); s.devolver(1, 1);
            verificar(s.cantidadTurnos() == 0 && s.cantidadReservas() == 0, "Cierre"); coherente(s);
        });
        caso("Historial conserva todos los movimientos en ambos sentidos", () -> {
            SistemaProyectores s = base(); reservar(s, "P1", "D1"); s.devolver(1, 1);
            s.enviarMantenimiento("P1", "Error"); s.deshacerUltimoMantenimiento();
            int[] asc = {0}, desc = {s.cantidadMovimientos() + 1};
            s.listarHistorial(false, m -> verificar(m.id() == ++asc[0], "Orden ascendente"));
            s.listarHistorial(true, m -> verificar(m.id() == --desc[0], "Orden descendente"));
            verificar(asc[0] == 6 && desc[0] == 1, "Registro de operaciones");
        });
        caso("Consola: numeros invalidos, operaciones, error y salida", () -> {
            String entrada = "abc\n9\n2\n1\nP1\nD1\nAna\n08:00\n2\n2\n1\n4\nP1\ns\n0\n";
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            SistemaProyectores s = base();
            new Consola(s, new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)),
                    new PrintStream(bytes, true, StandardCharsets.UTF_8)).ejecutar();
            String texto = bytes.toString(StandardCharsets.UTF_8);
            verificar(texto.contains("numero entero") && texto.contains("entre 0 y 7"), "Validacion numerica");
            verificar(texto.contains("Reserva creada") && texto.contains("reserva activa") && texto.contains("Sesion finalizada"), "Flujo completo");
            coherente(s);
        });
        caso("Consola: fin de entrada sin bucle infinito", () -> {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            new Consola(base(), new ByteArrayInputStream(new byte[0]),
                    new PrintStream(bytes, true, StandardCharsets.UTF_8)).ejecutar();
            verificar(bytes.toString(StandardCharsets.UTF_8).contains("Fin de entrada"), "EOF");
        });

        System.out.println("\nRESULTADO: " + aprobadas + " aprobadas, " + fallidas + " fallidas; total " + (aprobadas + fallidas) + ".");
        if (fallidas > 0) System.exit(1);
    }
}
