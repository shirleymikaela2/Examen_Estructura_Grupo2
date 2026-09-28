package estructuras;

import java.util.function.Consumer;

/** Historial con enlaces anterior/siguiente para consultar ambos sentidos. */
public final class ListaDoble<T> {
    private static final class Nodo<T> {
        private final T dato;
        private Nodo<T> anterior;
        private Nodo<T> siguiente;
        private Nodo(T dato) { this.dato = dato; }
    }

    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private int cantidad;

    public int tamano() { return cantidad; }

    public void insertarAlFinal(T dato) {
        if (dato == null) throw new IllegalArgumentException("No se admiten datos nulos.");
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.anterior = ultimo;
        if (ultimo == null) primero = nuevo;
        else ultimo.siguiente = nuevo;
        ultimo = nuevo;
        cantidad++;
    }

    public void recorrerAdelante(Consumer<T> accion) {
        for (Nodo<T> nodo = primero; nodo != null; nodo = nodo.siguiente) accion.accept(nodo.dato);
    }

    public void recorrerAtras(Consumer<T> accion) {
        for (Nodo<T> nodo = ultimo; nodo != null; nodo = nodo.anterior) accion.accept(nodo.dato);
    }
}
