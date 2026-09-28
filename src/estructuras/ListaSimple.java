package estructuras;

import java.util.function.Consumer;
import java.util.function.Predicate;

/** Lista simplemente enlazada con nodos propios y referencias al inicio y fin. */
public final class ListaSimple<T> {
    private static final class Nodo<T> {
        private final T dato;
        private Nodo<T> siguiente;
        private Nodo(T dato) { this.dato = dato; }
    }

    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private int cantidad;

    public int tamano() { return cantidad; }
    public boolean estaVacia() { return cantidad == 0; }

    public void insertar(T dato) {
        if (dato == null) throw new IllegalArgumentException("No se admiten datos nulos.");
        Nodo<T> nuevo = new Nodo<>(dato);
        if (ultimo == null) primero = nuevo;
        else ultimo.siguiente = nuevo;
        ultimo = nuevo;
        cantidad++;
    }

    public T buscar(Predicate<T> criterio) {
        for (Nodo<T> nodo = primero; nodo != null; nodo = nodo.siguiente) {
            if (criterio.test(nodo.dato)) return nodo.dato;
        }
        return null;
    }

    public T eliminar(Predicate<T> criterio) {
        Nodo<T> anterior = null;
        Nodo<T> actual = primero;
        while (actual != null) {
            if (criterio.test(actual.dato)) {
                if (anterior == null) primero = actual.siguiente;
                else anterior.siguiente = actual.siguiente;
                if (actual == ultimo) ultimo = anterior;
                cantidad--;
                return actual.dato;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return null;
    }

    public void recorrer(Consumer<T> accion) {
        for (Nodo<T> nodo = primero; nodo != null; nodo = nodo.siguiente) accion.accept(nodo.dato);
    }
}
