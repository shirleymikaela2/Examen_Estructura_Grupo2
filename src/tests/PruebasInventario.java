package tests;

import estructuras.ListaSecuencial;
import modelo.Estado;
import modelo.Proyector;

/** Pruebas independientes del inventario asignado a Cris. */
public final class PruebasInventario {
    private static int aprobadas;

    private PruebasInventario() { }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }

    private static void rechazar(Runnable accion) {
        try {
            accion.run();
        } catch (IllegalArgumentException e) {
            return;
        }
        throw new AssertionError("La operacion invalida fue aceptada.");
    }

    private static void caso(String nombre, Runnable prueba) {
        prueba.run();
        aprobadas++;
        System.out.println("[OK] " + nombre);
    }

    public static void main(String[] args) {
        caso("Registro y busqueda en lista secuencial", () -> {
            ListaSecuencial<Proyector> inventario = new ListaSecuencial<>();
            Proyector proyector = new Proyector("PRO001", "Epson", 3500,
                    "B201", 1200, Estado.DISPONIBLE);
            inventario.insertar(proyector);
            verificar(inventario.tamano() == 1, "Cantidad incorrecta");
            verificar(inventario.buscar(p -> p.codigo().equals("PRO001")) == proyector,
                    "No se encontro el proyector");
        });

        caso("Crecimiento del arreglo sin perder elementos", () -> {
            ListaSecuencial<Integer> lista = new ListaSecuencial<>(1);
            for (int i = 0; i < 30; i++) lista.insertar(i);
            verificar(lista.tamano() == 30, "Cantidad incorrecta");
            for (int i = 0; i < 30; i++) {
                verificar(lista.obtener(i) == i, "Dato perdido al crecer");
            }
        });

        caso("Modificacion y eliminacion con desplazamiento", () -> {
            ListaSecuencial<Integer> lista = new ListaSecuencial<>();
            lista.insertar(1);
            lista.insertar(2);
            lista.insertar(3);
            verificar(lista.modificar(n -> n == 2, 20), "No se modifico el dato");
            verificar(lista.eliminar(n -> n == 1) == 1, "No se elimino el primero");
            verificar(lista.tamano() == 2 && lista.obtener(0) == 20
                    && lista.obtener(1) == 3, "No se desplazaron los datos");
            verificar(!lista.modificar(n -> n == 99, 100),
                    "Se modifico un dato inexistente");
        });

        caso("Validacion de datos del proyector", () -> {
            rechazar(() -> new Proyector(" ", "Epson", 3500,
                    "B201", 1200, Estado.DISPONIBLE));
            rechazar(() -> new Proyector("PRO001", " ", 3500,
                    "B201", 1200, Estado.DISPONIBLE));
            rechazar(() -> new Proyector("PRO001", "Epson", 0,
                    "B201", 1200, Estado.DISPONIBLE));
            rechazar(() -> new Proyector("PRO001", "Epson", 3500,
                    "B201", -1, Estado.DISPONIBLE));
            rechazar(() -> new Proyector("PRO001", "Epson", 3500,
                    "B201", 1200, null));
        });

        caso("Actualizacion de estado conserva los otros datos", () -> {
            Proyector original = new Proyector("PRO001", "Epson", 3500,
                    "B201", 1200, Estado.DISPONIBLE);
            Proyector reservado = original.conEstado(Estado.RESERVADO);
            verificar(original.estado() == Estado.DISPONIBLE,
                    "Se altero el modelo original");
            verificar(reservado.estado() == Estado.RESERVADO
                    && reservado.codigo().equals("PRO001")
                    && reservado.marca().equals("Epson")
                    && reservado.horasLampara() == 1200,
                    "La actualizacion perdio datos");
        });

        caso("Cambio de horas conserva codigo, marca y aula", () -> {
            Proyector original = new Proyector("PRO001", "Epson", 3500,
                    "B201", 1200, Estado.RESERVADO);
            Proyector devuelto = original.conHorasYEstado(1202, Estado.DISPONIBLE);
            verificar(devuelto.horasLampara() == 1202
                    && devuelto.estado() == Estado.DISPONIBLE
                    && devuelto.codigo().equals(original.codigo())
                    && devuelto.marca().equals(original.marca())
                    && devuelto.aulaBase().equals(original.aulaBase()),
                    "El cambio de horas perdio datos");
        });

        System.out.println("RESULTADO INVENTARIO: " + aprobadas + " pruebas aprobadas.");
    }
}
