package Vista;

import java.io.IOException;

import Modelo.Escenario;
import Modelo.Jugador; // Importar Jugador
import Modelo.ObjetoEscenario;

public class VistaEscenario {

    // --- ATRIBUTOS ---
    private static final char CARACTER_VACIO = '.'; // Carácter para celdas vacías (si no hay objeto)
    private static final char CARACTER_JUGADOR = '@'; // Carácter para representar al jugador
    private static final char BORDE_HORIZONTAL = '-';
    private static final char BORDE_VERTICAL = '|';
    private static final char BORDE_ESQUINA = '+';

    public VistaEscenario() {
    }

    /**
     * Genera una representación en String del escenario, incluyendo bordes y al jugador.
     * @param escenario El modelo del escenario.
     * @param jugador El modelo del jugador (para obtener su posición). Puede ser null.
     * @return String con la vista del escenario.
     */
    public String vistaEscenario(Escenario escenario, Jugador jugador) {
        if (escenario == null || escenario.getMapa() == null) {
            return "Error: El escenario no es válido.";
        }

        StringBuilder sb = new StringBuilder();
        ObjetoEscenario[][] mapa = escenario.getMapa();
        int filas = escenario.getFilas();
        int columnas = escenario.getColumnas();

        // --- Dibujar Borde Superior ---
        sb.append(BORDE_ESQUINA);
        for (int j = 0; j < columnas; j++) {
            sb.append(BORDE_HORIZONTAL).append(BORDE_HORIZONTAL); // Doble para compensar el espacio entre celdas
        }
        sb.append(BORDE_ESQUINA).append("\n");

        // --- Dibujar Contenido y Bordes Laterales ---
        for (int i = 0; i < filas; i++) {
            sb.append(BORDE_VERTICAL); // Borde izquierdo
            for (int j = 0; j < columnas; j++) {
                char caracterCelda;
                // Comprobar si el jugador está en esta celda
                if (jugador != null && jugador.getFila() == i && jugador.getColumna() == j) {
                    caracterCelda = CARACTER_JUGADOR;
                } else {
                    // Si no está el jugador, dibujar el objeto del escenario
                    ObjetoEscenario obj = mapa[i][j];
                    if (obj != null) {
                        caracterCelda = obj.getObjetoChar(); // Usa el carácter del objeto
                    } else {
                        caracterCelda = CARACTER_VACIO; // Usa el carácter por defecto para vacío
                    }
                }
                sb.append(caracterCelda).append(" "); // Añade un espacio para mejor legibilidad
            }
            sb.append(BORDE_VERTICAL); // Borde derecho
            sb.append("\n"); // Salto de línea al final de cada fila
        }

        // --- Dibujar Borde Inferior ---
        sb.append(BORDE_ESQUINA);
        for (int j = 0; j < columnas; j++) {
            sb.append(BORDE_HORIZONTAL).append(BORDE_HORIZONTAL); // Doble
        }
        sb.append(BORDE_ESQUINA).append("\n");


        return sb.toString();
    }

    /**
     * Muestra un mensaje en la consola.
     * @param mensaje El mensaje a mostrar.
     */
    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

     /**
     * Limpia la pantalla de la consola (puede no funcionar en todos los entornos).
     */
    public void limpiarPantalla() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Para Windows
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // Para Unix/Linux/Mac
                 System.out.print("\033[H\033[2J");
                 System.out.flush();
                 // Alternativa: Runtime.getRuntime().exec("clear"); (menos fiable)
            }
        } catch (IOException | InterruptedException e) {
            // Si falla la limpieza, simplemente imprime líneas nuevas para simularla
            for (int i = 0; i < 50; ++i) System.out.println();
        }
    }
}
