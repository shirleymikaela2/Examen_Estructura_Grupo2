package estructuras;

import java.util.function.Consumer;
import java.util.function.Predicate;

/** Cola FIFO: agrega al final y extrae del frente en tiempo constante. */
public final class Cola<T> {
    private static final class Nodo<T> {
        private final T dato;
        private Nodo<T> siguiente;

        private Nodo(T dato) {
            this.dato = dato;
        }
    }

    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private int cantidad;

    public int tamano() {
        return cantidad;
    }

    public boolean estaVacia() {
        return cantidad == 0;
    }

    public T frente() {
        return primero == null ? null : primero.dato;
    }

    public void encolar(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException("No se admiten datos nulos.");
        }

        Nodo<T> nuevo = new Nodo<>(dato);

        if (ultimo == null) {
            primero = nuevo;
        } else {
            ultimo.siguiente = nuevo;
        }

        ultimo = nuevo;
        cantidad++;
    }

    public T desencolar() {
        if (primero == null) {
            return null;
        }

        T dato = primero.dato;
        primero = primero.siguiente;

        if (primero == null) {
            ultimo = null;
        }

        cantidad--;
        return dato;
    }

    public T buscar(Predicate<T> criterio) {
        for (Nodo<T> nodo = primero; nodo != null; nodo = nodo.siguiente) {
            if (criterio.test(nodo.dato)) {
                return nodo.dato;
            }
        }

        return null;
    }

    public void recorrer(Consumer<T> accion) {
        for (Nodo<T> nodo = primero; nodo != null; nodo = nodo.siguiente) {
            accion.accept(nodo.dato);
        }
    }
}