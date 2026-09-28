package estructuras;

import java.util.function.Consumer;
import java.util.function.Predicate;

/** Lista sobre un arreglo redimensionable; no utiliza colecciones de Java. */
public final class ListaSecuencial<T> {
    private Object[] elementos;
    private int cantidad;

    public ListaSecuencial() { this(8); }

    public ListaSecuencial(int capacidad) {
        if (capacidad < 1) throw new IllegalArgumentException("La capacidad debe ser positiva.");
        elementos = new Object[capacidad];
    }

    public int tamano() { return cantidad; }
    public boolean estaVacia() { return cantidad == 0; }

    public void insertar(T dato) {
        if (dato == null) throw new IllegalArgumentException("No se admiten datos nulos.");
        if (cantidad == elementos.length) {
            Object[] nuevo = new Object[elementos.length * 2];
            for (int i = 0; i < cantidad; i++) nuevo[i] = elementos[i];
            elementos = nuevo;
        }
        elementos[cantidad++] = dato;
    }

    @SuppressWarnings("unchecked")
    public T obtener(int indice) {
        if (indice < 0 || indice >= cantidad) throw new IndexOutOfBoundsException("Indice invalido.");
        return (T) elementos[indice];
    }

    public T buscar(Predicate<T> criterio) {
        for (int i = 0; i < cantidad; i++) if (criterio.test(obtener(i))) return obtener(i);
        return null;
    }

    public T eliminar(Predicate<T> criterio) {
        for (int i = 0; i < cantidad; i++) {
            T dato = obtener(i);
            if (criterio.test(dato)) {
                for (int j = i; j < cantidad - 1; j++) elementos[j] = elementos[j + 1];
                elementos[--cantidad] = null;
                return dato;
            }
        }
        return null;
    }

    public boolean modificar(Predicate<T> criterio, T nuevo) {
        if (nuevo == null) throw new IllegalArgumentException("No se admiten datos nulos.");
        for (int i = 0; i < cantidad; i++) {
            if (criterio.test(obtener(i))) {
                elementos[i] = nuevo;
                return true;
            }
        }
        return false;
    }

    public void recorrer(Consumer<T> accion) {
        for (int i = 0; i < cantidad; i++) accion.accept(obtener(i));
    }
}
