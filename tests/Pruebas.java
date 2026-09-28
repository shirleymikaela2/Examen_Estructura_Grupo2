package tests;
import modelo.Reserva;
import estructuras.ListaSimple;
import java.time.LocalDateTime;

public class Pruebas {
    public static void main(String[] args) {
        System.out.println("--- PRUEBA DE CANCELACIÓN DE RESERVAS ---");
        ListaSimple listaReservas = new ListaSimple();

        Reserva reservaPrueba = new Reserva(1, "PRO001", "DOC123", "Prof. Maigua", "Bloque A", LocalDateTime.now());
        listaReservas.registrarReserva(reservaPrueba);

        System.out.println("Intentando cancelar la reserva ID 1...");
        listaReservas.cancelarReserva("1"); 
    }
}