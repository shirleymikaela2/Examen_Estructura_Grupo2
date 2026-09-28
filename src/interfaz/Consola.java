package interfaz;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import modelo.*;
import servicio.SistemaProyectores;

/** Interfaz de texto; todas las reglas permanecen en el servicio. */
public final class Consola {
    private static final class FinEntrada extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private final SistemaProyectores sistema;
    private final Scanner entrada;
    private final PrintStream salida;

    public Consola(SistemaProyectores sistema, InputStream entrada, PrintStream salida) {
        this.sistema = sistema;
        this.entrada = new Scanner(entrada);
        this.salida = salida;
    }

    public void ejecutar() {
        salida.println("\nGRUPO 2 | RESERVA Y ROTACION DE PROYECTORES");
        salida.println("Limite de lampara: " + SistemaProyectores.LIMITE_HORAS + " h. Datos solo en memoria.");
        try {
            boolean continuar = true;
            while (continuar) {
                titulo("MENU PRINCIPAL");
                salida.println("1. Inventario\n2. Reservas y devoluciones\n3. Cola de espera\n4. Historial");
                salida.println("5. Mantenimiento\n6. Turnos de expositores\n7. Deshacer mantenimiento\n0. Salir");
                try {
                    switch (numero("Opcion", 0, 7)) {
                        case 1 -> inventario();
                        case 2 -> reservas();
                        case 3 -> cola();
                        case 4 -> historial();
                        case 5 -> mantenimiento();
                        case 6 -> turnos();
                        case 7 -> deshacer();
                        case 0 -> continuar = false;
                        default -> throw new IllegalStateException("Opcion fuera de rango.");
                    }
                } catch (IllegalArgumentException e) {
                    salida.println("AVISO: " + e.getMessage());
                }
            }
            salida.println("Sesion finalizada. Los datos en memoria se descartan.");
        } catch (FinEntrada e) {
            salida.println("\nFin de entrada. Sesion finalizada sin errores.");
        }
    }

    private void titulo(String texto) { salida.println("\n=== " + texto + " ==="); }

    private String texto(String pregunta) {
        while (true) {
            salida.print(pregunta + ": ");
            if (!entrada.hasNextLine()) throw new FinEntrada();
            String valor = entrada.nextLine();
            try { return Validacion.texto(valor, pregunta); }
            catch (IllegalArgumentException e) { salida.println("AVISO: " + e.getMessage()); }
        }
    }

    private int numero(String pregunta, int minimo, int maximo) {
        while (true) {
            String valor = texto(pregunta);
            try {
                return Validacion.rango(Integer.parseInt(valor), minimo, maximo, pregunta);
            } catch (NumberFormatException e) {
                salida.println("AVISO: escriba un numero entero.");
            } catch (IllegalArgumentException e) {
                salida.println("AVISO: " + e.getMessage());
            }
        }
    }

    private boolean confirmar(String accion) { return texto(accion + " (s/n)").equalsIgnoreCase("s"); }

    private void inventario() {
        titulo("INVENTARIO");
        salida.println("1. Mostrar\n2. Buscar\n3. Registrar\n4. Eliminar\n0. Volver");
        switch (numero("Opcion", 0, 4)) {
            case 1 -> {
                salida.println("Codigo   | Marca        | Lumenes  | Aula       | Horas   | Estado");
                sistema.listarInventario(salida::println);
                salida.println("Total: " + sistema.cantidadProyectores());
            }
            case 2 -> {
                Proyector p = sistema.buscarProyector(texto("Codigo"));
                salida.println(p == null ? "No encontrado." : p);
            }
            case 3 -> {
                String codigo = texto("Codigo");
                String marca = texto("Marca");
                int lumenes = numero("Luminosidad (lumenes)", 1, 100000);
                String aula = texto("Aula base");
                int horas = numero("Horas de lampara", 0, 1000000);
                int estado = numero("Estado (1=Disponible, 2=Mantenimiento)", 1, 2);
                sistema.registrarProyector(codigo, marca, lumenes, aula, horas,
                        estado == 1 ? Estado.DISPONIBLE : Estado.MANTENIMIENTO);
                salida.println("Proyector registrado.");
            }
            case 4 -> {
                String codigo = texto("Codigo a eliminar");
                if (confirmar("Confirmar eliminacion")) {
                    sistema.eliminarProyector(codigo);
                    salida.println("Proyector eliminado.");
                }
            }
            default -> { }
        }
    }

    private void reservas() {
        titulo("RESERVAS Y DEVOLUCIONES");
        salida.println("1. Solicitar reserva\n2. Mostrar activas\n3. Buscar por docente\n4. Devolver\n0. Volver");
        switch (numero("Opcion", 0, 4)) {
            case 1 -> salida.println(sistema.solicitarReserva(texto("Codigo de proyector"),
                    texto("ID del docente"), texto("Nombre del docente"), texto("Bloque horario descriptivo")));
            case 2 -> {
                sistema.listarReservas(salida::println);
                salida.println("Reservas activas: " + sistema.cantidadReservas());
            }
            case 3 -> {
                Reserva r = sistema.buscarReservaDocente(texto("ID del docente"));
                salida.println(r == null ? "Sin reserva activa." : r);
            }
            case 4 -> {
                int id = numero("Numero de reserva (sin R)", 1, Integer.MAX_VALUE);
                int horas = numero("Horas utilizadas", 0, 10000);
                if (confirmar("Confirmar devolucion")) {
                    sistema.devolver(id, horas);
                    salida.println("Devolucion registrada. Consulte la cola para atender la siguiente solicitud.");
                }
            }
            default -> { }
        }
    }

    private void cola() {
        titulo("COLA FIFO");
        salida.println("1. Listar\n2. Consultar frente\n3. Atender siguiente\n4. Cancelar solicitud del frente\n0. Volver");
        switch (numero("Opcion", 0, 4)) {
            case 1 -> {
                sistema.listarEspera(salida::println);
                salida.println("Solicitudes pendientes: " + sistema.cantidadSolicitudes());
            }
            case 2 -> salida.println(sistema.frenteEspera() == null ? "Cola vacia." : sistema.frenteEspera());
            case 3 -> salida.println("Atendida: " + sistema.atenderSiguiente());
            case 4 -> {
                salida.println(sistema.frenteEspera() == null ? "Cola vacia." : sistema.frenteEspera());
                if (confirmar("Cancelar el frente")) salida.println("Cancelada: " + sistema.cancelarFrente());
            }
            default -> { }
        }
    }

    private void historial() {
        titulo("HISTORIAL DOBLEMENTE ENLAZADO");
        int orden = numero("Orden (1=Antiguo a reciente, 2=Reciente a antiguo, 0=Volver)", 0, 2);
        if (orden != 0) {
            sistema.listarHistorial(orden == 2, salida::println);
            salida.println("Movimientos: " + sistema.cantidadMovimientos());
        }
    }

    private void mantenimiento() {
        titulo("MANTENIMIENTO");
        salida.println("1. Enviar a mantenimiento (reversible)\n2. Registrar cambio de lampara\n3. Finalizar mantenimiento\n0. Volver");
        switch (numero("Opcion", 0, 3)) {
            case 1 -> {
                sistema.enviarMantenimiento(texto("Codigo"), texto("Motivo"));
                salida.println("Enviado a mantenimiento. Accion guardada en la pila.");
            }
            case 2 -> {
                String codigo = texto("Codigo");
                if (confirmar("Registrar reemplazo fisico de la lampara y reiniciar horas")) {
                    sistema.cambiarLampara(codigo);
                    salida.println("Lampara en 0 h. Finalice el mantenimiento para habilitar el equipo.");
                }
            }
            case 3 -> {
                sistema.finalizarMantenimiento(texto("Codigo"));
                salida.println("Equipo disponible. Puede atender la cola.");
            }
            default -> { }
        }
    }

    private void turnos() {
        titulo("TURNOS CIRCULARES");
        salida.println(sistema.descripcionEvento());
        salida.println("1. Iniciar evento con una reserva\n2. Agregar expositor\n3. Mostrar ronda\n4. Consultar actual");
        salida.println("5. Avanzar\n6. Eliminar turno actual\n7. Cerrar evento\n0. Volver");
        switch (numero("Opcion", 0, 7)) {
            case 1 -> {
                sistema.iniciarEvento(numero("Numero de reserva (sin R)", 1, Integer.MAX_VALUE), texto("Nombre del evento"));
                salida.println("Evento iniciado.");
            }
            case 2 -> {
                sistema.agregarExpositor(texto("ID de expositor"), texto("Nombre"));
                salida.println("Expositor agregado al final de la ronda.");
            }
            case 3 -> {
                salida.println("Ronda desde el turno actual:");
                sistema.listarTurnos(salida::println);
                salida.println("Expositores: " + sistema.cantidadTurnos());
            }
            case 4 -> salida.println(sistema.turnoActual() == null ? "Ronda vacia." : sistema.turnoActual());
            case 5 -> salida.println("Turno actual: " + sistema.avanzarTurno());
            case 6 -> {
                if (confirmar("Eliminar turno actual")) salida.println("Retirado: " + sistema.eliminarTurnoActual());
            }
            case 7 -> {
                if (confirmar("Cerrar evento y retirar sus turnos")) {
                    sistema.cerrarEvento();
                    salida.println("Evento cerrado. La reserva sigue activa hasta registrar su devolucion.");
                }
            }
            default -> { }
        }
    }

    private void deshacer() {
        titulo("PILA DE MANTENIMIENTOS");
        salida.println("1. Consultar tope\n2. Deshacer ultimo envio\n3. Descartar tope obsoleto\n0. Volver");
        switch (numero("Opcion", 0, 3)) {
            case 1 -> {
                salida.println(sistema.topeDeshacer() == null ? "Pila vacia." : sistema.topeDeshacer());
                salida.println("Acciones: " + sistema.cantidadReversiones());
            }
            case 2 -> {
                sistema.deshacerUltimoMantenimiento();
                salida.println("Estado anterior restaurado.");
            }
            case 3 -> {
                sistema.descartarReversionObsoleta();
                salida.println("Tope obsoleto descartado; el estado del equipo se conserva.");
            }
            default -> { }
        }
    }
}
