import interfaz.Consola;
import interfaz.Demostracion;
import servicio.SistemaProyectores;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        if (args.length > 1 || (args.length == 1 && !args[0].equals("--demo")
                && !args[0].equals("--vacio") && !args[0].equals("--ayuda"))) {
            System.err.println("Uso: java -cp build Main [--demo | --vacio | --ayuda]");
            System.exit(1);
        }
        if (args.length == 1 && args[0].equals("--ayuda")) {
            System.out.println("Sin opciones: menu con datos de ejemplo. --vacio: sin datos. --demo: demostracion automatica.");
        } else if (args.length == 1 && args[0].equals("--demo")) {
            Demostracion.ejecutar();
        } else {
            SistemaProyectores sistema = args.length == 1
                    ? new SistemaProyectores() : SistemaProyectores.conDatosEjemplo();
            new Consola(sistema, System.in, System.out).ejecutar();
        }
    }
}
