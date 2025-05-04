package Controlador;

import Modelo.Jugador;
import javafx.scene.input.KeyCode;

public class Control {
    
    // Conjunto de teclas presionadas (ejemplo: W, S, A, D)
    private static final java.util.Set<KeyCode> teclasPresionadas = new java.util.HashSet<>();

    // Método para manejar el movimiento del jugador
    public static void mov() {
        int cambioFila = 0;   // Cambio en la fila (eje Y)
        int cambioColumna = 0; // Cambio en la columna (eje X)

        // Verificar qué teclas están presionadas y ajustar el movimiento
        if (teclasPresionadas.contains(KeyCode.W)) {
            cambioFila = -1; // Moverse hacia arriba
        }
        if (teclasPresionadas.contains(KeyCode.S)) {
            cambioFila = 1;  // Moverse hacia abajo
        }
        if (teclasPresionadas.contains(KeyCode.A)) {
            cambioColumna = -1; // Moverse hacia la izquierda
        }
        if (teclasPresionadas.contains(KeyCode.D)) {
            cambioColumna = 1;  // Moverse hacia la derecha
        }

        // Calcular nueva posición
        int nuevaFila = Jugador.getFilaActual() + cambioFila;
        int nuevaColumna = Jugador.getColumnaActual() + cambioColumna;

        // Validar que la nueva posición esté dentro del rango del mapa
        if (nuevaFila >= 0 && nuevaFila < Escenario.getFilas() && 
            nuevaColumna >= 0 && nuevaColumna < Escenario.getColumnas()) {
            Jugador.setFilaActual(nuevaFila);
            Jugador.setColumnaActual(nuevaColumna);
        }
    }

    // Método para agregar una tecla presionada
    public static void agregarTecla(KeyCode tecla) {
        teclasPresionadas.add(tecla);
    }

    // Método para eliminar una tecla presionada
    public static void eliminarTecla(KeyCode tecla) {
        teclasPresionadas.remove(tecla);
    }
}
