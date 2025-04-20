package Controlador;

import Modelo.Jugador;

public class Control {
    
    public static void mov() {
        int dx = Jugador.getFila(); // Cambio en X
        int dy = Jugador.getColumna(); // Cambio en Y

        // Comprobar qué teclas están presionadas y ajustar el cambio de posición
        if (activeKeys.contains(KeyCode.W)) {
            dy -= Jugador.getColumna();
        }
        if (activeKeys.contains(KeyCode.S)) {
            dy += Jugador.getColumna();
        }
        if (activeKeys.contains(KeyCode.A)) {
            dx -= Jugador.getFila();
        }
        if (activeKeys.contains(KeyCode.D)) {
            dx += Jugador.getFila();
        }

        // Calcular la nueva posición potencial
        int newX = Jugador.getFila() + dx;
        int newY = Jugador.getColumna() + dy;

        Jugador.setFila(newX);
        Jugador.setColumna(newY);
    }
}
