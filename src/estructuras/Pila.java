package estructuras;

/** Pila LIFO implementada mediante nodos propios. */
public final class Pila<T> {
    private static final class Nodo<T> {
        private final T dato;
        private final Nodo<T> siguiente;
        private Nodo(T dato, Nodo<T> siguiente) { this.dato = dato; this.siguiente = siguiente; }
    }

    private Nodo<T> cima;
    private int cantidad;

    public int tamano() { return cantidad; }
    public boolean estaVacia() { return cantidad == 0; }
    public T tope() { return cima == null ? null : cima.dato; }

    public void apilar(T dato) {
        if (dato == null) throw new IllegalArgumentException("No se admiten datos nulos.");
        cima = new Nodo<>(dato, cima);
        cantidad++;
    }

    public T desapilar() {
        if (cima == null) return null;
        T dato = cima.dato;
        cima = cima.siguiente;
        cantidad--;
        return dato;
    }
}