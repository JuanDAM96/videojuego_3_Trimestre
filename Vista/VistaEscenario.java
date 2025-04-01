package Vista;

import Modelo.Escenario;
import Modelo.ObjetoEscenario;

/**
 * Clase que proporciona una representación visual del escenario en formato de texto.
 */
public class VistaEscenario {

    /** Carácter utilizado para representar celdas vacías en la vista del escenario. */
    private static final char CARACTER_VACIO = '.';

    /**
     * Constructor de la clase VistaEscenario.
     */
    public VistaEscenario() {
    }

    /**
     * Genera una representación visual en forma de texto del escenario.
     * 
     * @param escenario Escenario a representar visualmente.
     * @return Cadena de texto que representa el escenario.
     */
    public String vistaEscenario(Escenario escenario) {
        if (escenario == null || escenario.getMapa() == null) {
            return "Error: El escenario no es válido.";
        }

        StringBuilder sb = new StringBuilder();
        ObjetoEscenario[][] mapa = escenario.getMapa();
        int filas = escenario.getFilas();
        int columnas = escenario.getColumnas();

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                ObjetoEscenario obj = mapa[i][j];
                if (obj != null) {
                    sb.append(obj.getObjetoChar()); // Usa el carácter del objeto
                } else {
                    sb.append(CARACTER_VACIO); // Usa el carácter por defecto para vacío
                }
                sb.append(" "); // Añade un espacio para mejor legibilidad
            }
            sb.append("\n"); // Salto de línea al final de cada fila
        }

        return sb.toString();
    }

    /**
     * Muestra un mensaje en la consola.
     * 
     * @param mensaje Mensaje a mostrar en la consola.
     */
    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }
}
