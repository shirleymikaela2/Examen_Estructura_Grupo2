package estructuras;

import java.util.function.Consumer;
import java.util.function.Predicate;

/** El ultimo turno enlaza al actual. No hay un enlace nulo en una ronda no vacia. */
public final class ListaCircular<T> {
    private static final class Nodo<T> {
        private final T dato;
        private Nodo<T> siguiente;
        private Nodo(T dato) { this.dato = dato; }
    }

    private Nodo<T> actual;
    private Nodo<T> anteriorActual;
    private int cantidad;

    public int tamano() { return cantidad; }
    public boolean estaVacia() { return actual == null; }
    public T actual() { return actual == null ? null : actual.dato; }

    /** Inserta al final de la ronda sin quitar el turno de quien la ocupa. */
    public void insertar(T dato) {
        if (dato == null) throw new IllegalArgumentException("No se admiten datos nulos.");
        Nodo<T> nuevo = new Nodo<>(dato);
        if (actual == null) {
            actual = nuevo;
            nuevo.siguiente = nuevo;
        } else {
            nuevo.siguiente = actual;
            anteriorActual.siguiente = nuevo;
        }
        anteriorActual = nuevo;
        cantidad++;
    }

    public T avanzar() {
        if (actual == null) return null;
        anteriorActual = actual;
        actual = actual.siguiente;
        return actual.dato;
    }

    public T eliminarActual() {
        if (actual == null) return null;
        T dato = actual.dato;
        if (cantidad == 1) {
            actual = null;
            anteriorActual = null;
        } else {
            anteriorActual.siguiente = actual.siguiente;
            actual = actual.siguiente;
        }
        cantidad--;
        return dato;
    }

    public T buscar(Predicate<T> criterio) {
        Nodo<T> nodo = actual;
        for (int i = 0; i < cantidad; i++) {
            if (criterio.test(nodo.dato)) return nodo.dato;
            nodo = nodo.siguiente;
        }
        return null;
    }

    public void recorrer(Consumer<T> accion) {
        Nodo<T> nodo = actual;
        for (int i = 0; i < cantidad; i++) {
            accion.accept(nodo.dato);
            nodo = nodo.siguiente;
        }
    }
}
