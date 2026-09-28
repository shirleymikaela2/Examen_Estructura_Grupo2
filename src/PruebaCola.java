import estructuras.Cola;
import modelo.Solicitud;

public class PruebaCola {

    public static void main(String[] args) {

        Cola<Solicitud> cola = new Cola<>();

        Solicitud solicitud1 =
                new Solicitud("1801111111", "Docente A", "PROY-001");

        Solicitud solicitud2 =
                new Solicitud("1802222222", "Docente B", "PROY-002");

        Solicitud solicitud3 =
                new Solicitud("1803333333", "Docente C", "PROY-003");

        // Se agregan las solicitudes respetando el orden FIFO.
        cola.encolar(solicitud1);
        cola.encolar(solicitud2);
        cola.encolar(solicitud3);

        System.out.println("=== COLA ORIGINAL ===");
        cola.recorrer(System.out::println);

        String cedulaBuscada = "1802222222";

        int posicion = cola.posicion(
                solicitud -> solicitud.getCedulaDocente().equals(cedulaBuscada)
        );

        Solicitud encontrada = cola.buscar(
                solicitud -> solicitud.getCedulaDocente().equals(cedulaBuscada)
        );

        System.out.println();
        System.out.println("=== CONSULTA POR DOCENTE ===");

        if (encontrada != null) {
            System.out.println("Docente: " + encontrada.getNombreDocente());
            System.out.println("Cedula: " + encontrada.getCedulaDocente());
            System.out.println("Posicion en la cola: " + posicion);
        } else {
            System.out.println("El docente no tiene una solicitud en espera.");
        }

        System.out.println();
        System.out.println("=== COLA DESPUES DE CONSULTAR ===");
        cola.recorrer(System.out::println);
    }
}