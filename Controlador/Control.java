package Controlador; // Debe estar en el paquete Controlador

// --- IMPORTS NECESARIOS ---
import Modelo.Escenario;
import Modelo.Jugador;
import javafx.scene.input.KeyCode;
import java.util.HashSet; // Importar HashSet
import java.util.Set;
// --- FIN IMPORTS ---

public class Control {

    private static final Set<KeyCode> teclasPresionadas = new HashSet<>();

    /**
     * Intenta mover al jugador basado en las teclas presionadas y valida colisiones.
     * @param jugador La instancia del jugador a mover.
     * @param escenario La instancia del escenario actual.
     * @return true si el jugador se movió, false si no hubo movimiento o hubo colisión.
     */
    // --- ASEGÚRATE QUE LA FIRMA SEA EXACTAMENTE ESTA ---
    public static boolean mov(Jugador jugador, Escenario escenario) {
        if (jugador == null || escenario == null) {
            System.err.println("Error en Control.mov: Jugador o Escenario es null.");
            return false;
        }

        int cambioFila = 0;
        int cambioColumna = 0;

        if (teclasPresionadas.contains(KeyCode.W)) cambioFila = -1;
        if (teclasPresionadas.contains(KeyCode.S)) cambioFila = 1;
        if (teclasPresionadas.contains(KeyCode.A)) cambioColumna = -1;
        if (teclasPresionadas.contains(KeyCode.D)) cambioColumna = 1;

        if (cambioFila == 0 && cambioColumna == 0) {
            return false; // No hay movimiento
        }

        int nuevaFila = jugador.getFilaActual() + cambioFila;
        int nuevaColumna = jugador.getColumnaActual() + cambioColumna;

        // Validar límites
        if (nuevaFila < 0 || nuevaFila >= escenario.getFilas() ||
            nuevaColumna < 0 || nuevaColumna >= escenario.getColumnas()) {
            return false;
        }

        // Validar colisión (asumiendo 'O' es obstáculo)
        char tileDestino = escenario.getTile(nuevaFila, nuevaColumna);
        if (tileDestino == 'O') {
             return false;
        }

        // Movimiento válido
        jugador.setFilaActual(nuevaFila);
        jugador.setColumnaActual(nuevaColumna);
        return true;
    }

    public static void agregarTecla(KeyCode tecla) {
        teclasPresionadas.add(tecla);
    }

    public static void eliminarTecla(KeyCode tecla) {
        teclasPresionadas.remove(tecla);
    }

    /**
     * Limpia el conjunto de teclas presionadas actualmente.
     */
     // --- ASEGÚRATE QUE ESTE MÉTODO EXISTA Y SEA ASÍ ---
    public static void limpiarTeclas() {
        teclasPresionadas.clear();
    }
}
