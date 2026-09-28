package modelo;

import java.util.Locale;

public final class Validacion {
    private Validacion() { }

    public static String texto(String valor, String campo) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException(campo + " es obligatorio.");
        String limpio = valor.strip();
        if (limpio.length() > 120) throw new IllegalArgumentException(campo + " admite hasta 120 caracteres.");
        for (int i = 0; i < limpio.length(); i++) {
            if (Character.isISOControl(limpio.charAt(i))) {
                throw new IllegalArgumentException(campo + " contiene caracteres de control.");
            }
        }
        return limpio;
    }

    public static String identificador(String valor, String campo) {
        String limpio = texto(valor, campo).toUpperCase(Locale.ROOT);
        if (!limpio.matches("[A-Z0-9][A-Z0-9_-]{0,19}")) {
            throw new IllegalArgumentException(campo + ": use de 1 a 20 letras, numeros, guion o guion bajo.");
        }
        return limpio;
    }

    public static int rango(int valor, int minimo, int maximo, String campo) {
        if (valor < minimo || valor > maximo) {
            throw new IllegalArgumentException(campo + " debe estar entre " + minimo + " y " + maximo + ".");
        }
        return valor;
    }
}
