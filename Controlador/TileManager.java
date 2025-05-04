package Controlador;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import Modelo.Escenario;
import java.util.HashMap;
import java.util.Map;

public class TileManager {

    // --- ¡¡VERIFICA ESTAS CONSTANTES!! ---
    private static final String TILESET_PATH = "file:./dungeon_tiles.png"; // Ruta relativa al archivo tileset
    // ¿De cuántos píxeles de ancho y alto es CADA tile individual en tu imagen dungeon_tiles.png?
    // Cambia este valor si no es 16x16. ¡Debe ser exacto!
    private static final int TILE_SIZE = 32;
    // --- FIN VERIFICACIÓN ---

    private static Image tileset;
    private static Map<Character, Image> tileCache = new HashMap<>();

    // --- ¡¡CONFIGURA ESTAS COORDENADAS!! ---
    // Mapeo de caracteres del escenario a coordenadas (columna, fila) en el tileset (índice basado en 0)
    // Abre dungeon_tiles.png en un editor de imágenes que muestre coordenadas o cuenta los tiles.
    private static Map<Character, Point> tileCoordinates = new HashMap<>();
    static {
        // Formato: tileCoordinates.put('CARACTER_MAPA', new Point(COLUMNA_TILESET, FILA_TILESET));
        // Ejemplo (¡AJUSTA ESTOS NÚMEROS A TU IMAGEN!):
        // Si el suelo es el segundo tile en la segunda fila (contando desde 0):
        tileCoordinates.put('E', new Point(8, 1));  // Suelo (Espacio)
        // Si la pared es el primer tile en la primera fila:
        tileCoordinates.put('O', new Point(2, 1));  // Pared (Obstáculo)
        // Si el jugador es el primer personaje en la séptima fila:
        tileCoordinates.put('P', new Point(5, 5));  // Jugador
        // Añade aquí el resto de tiles que uses en tus archivos de escenario
        // tileCoordinates.put('D', new Point(col, fila)); // Puerta?
        // tileCoordinates.put('K', new Point(col, fila)); // Llave?
    }
    // --- FIN CONFIGURAR COORDENADAS ---

    // Carga el tileset
    static {
        try {
            System.out.println("Intentando cargar tileset desde: " + TILESET_PATH);
            tileset = new Image(TILESET_PATH);
            if (tileset.isError()) {
                 System.err.println("**********************************************************");
                 System.err.println("ERROR GRAVE: No se pudo cargar el tileset: " + TILESET_PATH);
                 System.err.println("Verifica que el archivo dungeon_tiles.png esté en la raíz del proyecto.");
                 System.err.println("Excepción: " + tileset.getException());
                 System.err.println("**********************************************************");
                 tileset = null;
            } else if (tileset.getWidth() < TILE_SIZE || tileset.getHeight() < TILE_SIZE) {
                 System.err.println("**********************************************************");
                 System.err.println("ERROR GRAVE: El tileset cargado es más pequeño (" + tileset.getWidth() + "x" + tileset.getHeight()
                                    + ") que el TILE_SIZE definido (" + TILE_SIZE + ").");
                 System.err.println("Asegúrate de que TILE_SIZE sea correcto.");
                 System.err.println("**********************************************************");
                 tileset = null; // Considerarlo inválido
            }
             else {
                 System.out.println("Tileset cargado correctamente. Dimensiones: " + tileset.getWidth() + "x" + tileset.getHeight());
            }
        } catch (Exception e) {
            System.err.println("**********************************************************");
            System.err.println("Excepción inesperada al cargar el tileset: " + e.getMessage());
            e.printStackTrace();
            System.err.println("**********************************************************");
            tileset = null;
        }
    }

    public static Image getTileImage(char tileType) {
        if (tileset == null) {
             // System.err.println("getTileImage: Tileset no cargado, devolviendo null para '" + tileType + "'");
             return null;
        }
        if (tileCache.containsKey(tileType)) {
            return tileCache.get(tileType);
        }

        Point coords = tileCoordinates.get(tileType);
        boolean isFallback = false;
        if (coords == null) {
            System.err.println("Advertencia: Coordenadas no definidas para tile '" + tileType + "'. Usando fallback 'E'.");
            coords = tileCoordinates.get('E'); // Fallback a 'E' (Espacio/Suelo)
            isFallback = true;
            if (coords == null) {
                 System.err.println("Error crítico: Coordenadas para tile por defecto 'E' no definidas.");
                 return null;
            }
        }

        int x = coords.col * TILE_SIZE;
        int y = coords.row * TILE_SIZE;

        if (x < 0 || y < 0 || x + TILE_SIZE > tileset.getWidth() || y + TILE_SIZE > tileset.getHeight()) {
             System.err.println("Error: Coordenadas (" + x + "," + y + ") para tile '" + tileType + "' están fuera de los límites del tileset. Usando fallback 'E'.");
             return getTileImage('E'); // Usar fallback si las coordenadas calculadas son inválidas
        }

        try {
            PixelReader reader = tileset.getPixelReader();
            // System.out.println("Recortando tile '" + tileType + "' de [" + x + "," + y + "] a [" + (x+TILE_SIZE) + "," + (y+TILE_SIZE) + "]"); // Log detallado
            WritableImage tileImage = new WritableImage(reader, x, y, TILE_SIZE, TILE_SIZE);
            if (!isFallback || tileType == 'E') { // Cachear solo si no es fallback (o si es E)
                 tileCache.put(tileType, tileImage);
            }
            return tileImage;
        } catch (Exception e) {
             System.err.println("Error al recortar el tile '" + tileType + "' en ("+x+","+y+"): " + e.getMessage());
             return getTileImage('E'); // Usar fallback en caso de error de recorte
        }
    }

    public static ImageView crearTileView(char tileType) {
        Image img = getTileImage(tileType);
        ImageView imageView = new ImageView(img);
        // El tamaño debería venir de la imagen recortada, pero aseguramos
        imageView.setFitWidth(TILE_SIZE);
        imageView.setFitHeight(TILE_SIZE);
        imageView.setPreserveRatio(true); // Ayuda a evitar distorsiones si hay errores leves
        if (img == null) {
             // System.err.println("Advertencia: Creando ImageView vacío para tile '" + tileType + "'");
             // Opcional: Poner un color para ver los tiles que fallan
             imageView.setStyle("-fx-background-color: magenta; -fx-border-color: black;");
        }
        return imageView;
    }

    public static void agregarTilesAlGrid(GridPane gridPane, Escenario escenario) {
        if (gridPane == null || escenario == null) {
            System.err.println("Error en agregarTilesAlGrid: GridPane o Escenario es null.");
            return;
        }
        gridPane.getChildren().clear();
        System.out.println("Dibujando mapa en GridPane ("+escenario.getFilas()+"x"+escenario.getColumnas()+")...");
        for (int fila = 0; fila < escenario.getFilas(); fila++) {
            for (int columna = 0; columna < escenario.getColumnas(); columna++) {
                char tipoTile = escenario.getTile(fila, columna);
                ImageView tileView = crearTileView(tipoTile);
                gridPane.add(tileView, columna, fila);
            }
        }
         System.out.println("Tiles agregados al GridPane. Total hijos: " + gridPane.getChildren().size());
    }

    private static class Point {
        int col, row;
        Point(int col, int row) { this.col = col; this.row = row; }
    }
}
