package Vista;

import Modelo.Escenario;
import Modelo.ObjetoEscenario;

public class VistaEscenario {

    // --- ATRIBUTOS ---
    private static final char CARACTER_VACIO = '.'; // Carácter para celdas vacías

    public VistaEscenario() {
    }

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

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }
}
