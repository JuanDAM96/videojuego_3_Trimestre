package Controlador; // Este código es un visor de tiles para un tileset de un juego 2D, como el de un RPG o un juego de plataformas.
// El código está diseñado para ser ejecutado en JavaFX y permite cargar un tileset, recortar tiles individuales y mostrarlos en una interfaz gráfica.


import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane; // Usaremos GridPane para mostrar algunos tiles
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ExtObj {

    // --- Parámetros Configurables ---
    private static final String TILESET_PATH = "dungeon_tiles.png"; // Ruta a tu archivo de tileset
    private static final int TILE_WIDTH = 16;   // Ancho de un tile individual en píxeles
    private static final int TILE_HEIGHT = 16;  // Alto de un tile individual en píxeles


    /**
     * Crea y devuelve un ImageView que muestra un tile específico del tileset.
     *
     * @param tileX Índice de la columna del tile (empezando en 0).
     * @param tileY Índice de la fila del tile (empezando en 0).
     * @return Un ImageView configurado para mostrar el tile, o null si las coordenadas son inválidas.
     */
    public static ImageView Ext(int tileX, int tileY) {

        // Calcular las coordenadas en píxeles dentro de la imagen completa
        int pixelX = tileX * TILE_WIDTH;
        int pixelY = tileY * TILE_HEIGHT;

        // Crear el rectángulo (Viewport) que define el área a recortar
        Rectangle2D viewport = new Rectangle2D(pixelX, pixelY, TILE_WIDTH, TILE_HEIGHT);

        // Crear un ImageView que use la imagen completa del tileset
        ImageView tileView = new ImageView(tileset);

        // Aplicar el viewport para que solo se muestre el tile deseado
        tileView.setViewport(viewport);

        // Opcional: Ajustar el tamaño del ImageView si quieres escalar el tile
        // tileView.setFitWidth(TILE_WIDTH * 2); // Escalar al doble
        // tileView.setFitHeight(TILE_HEIGHT * 2);
        // tileView.setPreserveRatio(true); // Mantener proporción si escalas

        return tileView;
    }

    

}
