package Controlador;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestiona la carga y el uso del tileset del juego.
 */
public class TileManager {

    private static final String TILESET_PATH = "file:./dungeon_tiles.png"; // Ruta al tileset
    private static final int TILE_SIZE = 16; // Tamaño de cada tile en el spritesheet (¡Ajustar!)
    private static Image tileset;
    private static Map<Character, Image> tileCache = new HashMap<>(); // Cache para tiles individuales

    // Mapeo de caracteres del escenario a coordenadas (col, fila) en el tileset
    // ¡¡¡ESTO ES UN EJEMPLO, DEBES AJUSTARLO A TU tileset.png!!!
    private static Map<Character, Point> tileCoordinates = new HashMap<>();
    static {
        // Formato: tileCoordinates.put('CARACTER', new Point(COLUMNA_EN_TILESET, FILA_EN_TILESET));
        tileCoordinates.put('E', new Point(1, 1));  // Ejemplo: Tile de suelo en (1, 1)
        tileCoordinates.put('O', new Point(0, 0));  // Ejemplo: Tile de pared en (0, 0)
        tileCoordinates.put('P', new Point(0, 6));  // Ejemplo: Tile de jugador en (0, 6) - El primer personaje
        // Añade más mapeos según necesites
    }

    // Carga el tileset completo una sola vez
    static {
        try {
            tileset = new Image(TILESET_PATH);
            if (tileset.isError()) {
                 System.err.println("Error grave: No se pudo cargar el tileset principal: " + TILESET_PATH);
                 tileset = null; // Marcar como nulo si falla
            } else {
                 System.out.println("Tileset cargado correctamente desde: " + TILESET_PATH);
            }
        } catch (Exception e) {
            System.err.println("Excepción al cargar el tileset: " + e.getMessage());
            e.printStackTrace();
            tileset = null;
        }
    }

    /**
     * Obtiene la imagen individual de un tile específico desde el tileset.
     * Usa una caché para evitar recortar la imagen repetidamente.
     * @param tileType Carácter que representa el tipo de tile ('E', 'O', 'P', etc.).
     * @return La imagen del tile, o null si no se puede obtener.
     */
    public static Image getTileImage(char tileType) {
        if (tileset == null) {
             System.err.println("Error: El tileset no está cargado.");
             return null; // No se puede obtener tile si el tileset falló
        }

        // Revisar caché primero
        if (tileCache.containsKey(tileType)) {
            return tileCache.get(tileType);
        }

        // Obtener coordenadas del mapa
        Point coords = tileCoordinates.get(tileType);
        if (coords == null) {
            System.err.println("Advertencia: No hay coordenadas definidas para el tile tipo '" + tileType + "'. Usando tile por defecto (espacio).");
            coords = tileCoordinates.get('E'); // Usar suelo por defecto si no se encuentra
            if (coords == null) return null; // Si ni siquiera hay suelo definido
        }

        // Calcular posición en píxeles dentro del tileset
        int x = coords.col * TILE_SIZE;
        int y = coords.row * TILE_SIZE;

        // Validar que las coordenadas estén dentro de la imagen
        if (x < 0 || y < 0 || x + TILE_SIZE > tileset.getWidth() || y + TILE_SIZE > tileset.getHeight()) {
             System.err.println("Error: Coordenadas (" + x + "," + y + ") para tile '" + tileType + "' están fuera de los límites del tileset ("+tileset.getWidth()+"x"+tileset.getHeight()+").");
             return null; // O devolver una imagen de error
        }


        // Recortar la imagen del tile individual
        try {
            PixelReader reader = tileset.getPixelReader();
            WritableImage tileImage = new WritableImage(reader, x, y, TILE_SIZE, TILE_SIZE);
            tileCache.put(tileType, tileImage); // Guardar en caché
            return tileImage;
        } catch (Exception e) {
             System.err.println("Error al recortar el tile '" + tileType + "' en ("+x+","+y+"): " + e.getMessage());
             return null; // O devolver imagen de error
        }
    }

     /**
     * Crea un ImageView para un tipo de tile específico.
     * @param tileType El carácter que representa el tile.
     * @return Un ImageView configurado o null si la imagen no se pudo cargar.
     */
    public static ImageView crearTileView(char tileType) {
        Image img = getTileImage(tileType);
        if (img != null) {
            ImageView imageView = new ImageView(img);
            // El tamaño ya está implícito en la imagen recortada, pero podemos asegurarlo
            imageView.setFitWidth(TILE_WIDTH);
            imageView.setFitHeight(TILE_HEIGHT);
            return imageView;
        } else {
             // Devolver un ImageView vacío o con una imagen placeholder si falla la carga
             ImageView errorView = new ImageView();
             errorView.setFitWidth(TILE_WIDTH);
             errorView.setFitHeight(TILE_HEIGHT);
             // Podrías ponerle un color o una imagen de error aquí
             return errorView;
        }
    }


    /**
     * Clase interna simple para almacenar coordenadas (columna, fila).
     */
    private static class Point {
        int col, row;
        Point(int col, int row) {
            this.col = col;
            this.row = row;
        }
    }

    // --- Método agregarTiles ahora usa el TileManager ---
    // (Este método podría estar aquí o en ExtObj, o incluso en PantallaDeJuego)
    public static void agregarTilesAlGrid(GridPane gridPane, Escenario escenario) {
        if (gridPane == null || escenario == null) {
            System.err.println("Error en agregarTilesAlGrid: GridPane o Escenario es null.");
            return;
        }
        gridPane.getChildren().clear();
        for (int fila = 0; fila < escenario.getFilas(); fila++) {
            for (int columna = 0; columna < escenario.getColumnas(); columna++) {
                char tipoTile = escenario.getTile(fila, columna);
                ImageView tileView = crearTileView(tipoTile); // Usa el método de esta clase
                if (tileView != null) {
                    gridPane.add(tileView, columna, fila);
                }
            }
        }
    }
}
