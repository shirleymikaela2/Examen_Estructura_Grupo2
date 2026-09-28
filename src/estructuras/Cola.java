package estructuras;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Cola genérica que trabaja con el principio FIFO:
 * el primero en entrar es el primero en salir.
 */
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

    /**
     * Devuelve la cantidad de elementos almacenados en la cola.
     */
    public int tamano() {
        return cantidad;
    }

    /**
     * Indica si la cola se encuentra vacía.
     */
    public boolean estaVacia() {
        return cantidad == 0;
    }

    /**
     * Devuelve el primer elemento sin eliminarlo.
     */
    public T frente() {
        return primero == null ? null : primero.dato;
    }

    /**
     * Agrega un elemento al final de la cola.
     */
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

    /**
     * Elimina y devuelve el primer elemento de la cola.
     */
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

    /**
     * Busca un elemento utilizando un criterio.
     * La búsqueda no modifica el orden de la cola.
     */
    public T buscar(Predicate<T> criterio) {
        for (Nodo<T> nodo = primero; nodo != null; nodo = nodo.siguiente) {
            if (criterio.test(nodo.dato)) {
                return nodo.dato;
            }
        }

        return null;
    }

    /**
     * Devuelve la posición de un elemento dentro de la cola.
     *
     * La primera posición es 1.
     * Si el elemento no existe, devuelve -1.
     *
     * Este método no desencola elementos, por lo que mantiene
     * completamente el orden FIFO.
     */
    public int posicion(Predicate<T> criterio) {
        int posicionActual = 1;

        for (Nodo<T> nodo = primero; nodo != null; nodo = nodo.siguiente) {

            if (criterio.test(nodo.dato)) {
                return posicionActual;
            }

            posicionActual++;
        }

        return -1;
    }

    /**
     * Recorre todos los elementos de la cola sin modificarla.
     */
    public void recorrer(Consumer<T> accion) {
        for (Nodo<T> nodo = primero; nodo != null; nodo = nodo.siguiente) {
            accion.accept(nodo.dato);
        }
    }
}