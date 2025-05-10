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

/**
 * Gestiona la creacion y carga de Tiles
 * 
 * @author Santiago
 * @author Juan
 * @version 0.3.3
 */
public class GestorTile {
    private static final String RUTA_TILE = "file:./dungeon_tiles.png";
    private static final int TAMANO = 32;

    private static Image tile;
    private static Map<Character, Image> tileTemp = new HashMap<>();

    private static Map<Character, Point> coordenadas = new HashMap<>();

    // Conjunto estatico, sirve para agrupar
    static {
        coordenadas.put('E', new Point(8, 1));
        coordenadas.put('O', new Point(2, 1));
        coordenadas.put('P', new Point(5, 5));
    }

    // Sirve para una ejecucion de clase, destinado para depuracion
    static {
        try {
            System.out.println("Intentando cargar tileset desde: " + RUTA_TILE);
            tile = new Image(RUTA_TILE);
            if (tile.isError()) {
                System.err.println("**********************************************************");
                System.err.println("ERROR GRAVE: No se pudo cargar el tileset: " + RUTA_TILE);
                System.err.println("Verifica que el archivo dungeon_tiles.png esté en la raíz del proyecto.");
                System.err.println("Excepción: " + tile.getException());
                System.err.println("**********************************************************");
                tile = null;
            } else if (tile.getWidth() < TAMANO || tile.getHeight() < TAMANO) {
                System.err.println("**********************************************************");
                System.err.println("ERROR GRAVE: El tileset cargado es más pequeño (" + tile.getWidth() + "x" + tile.getHeight()
                        + ") que el TILE_SIZE definido (" + TAMANO + ").");
                System.err.println("Asegúrate de que TILE_SIZE sea correcto.");
                System.err.println("**********************************************************");
                tile = null;
            } else {
                System.out.println("Tileset cargado correctamente. Dimensiones: " + tile.getWidth() + "x" + tile.getHeight());
            }
        } catch (Exception e) {
            System.err.println("**********************************************************");
            System.err.println("Excepción inesperada al cargar el tileset: " + e.getMessage());
            e.printStackTrace();
            System.err.println("**********************************************************");
            tile = null;
        }
    }

    /**
     * Getter de Tile 
     * 
     * @param tipoTile Es el tipo del Tile que va a cargar, Personaje (P)
     * @return Devuelve la imagen según su tipo
     */
    public static Image getTileImage(char tipoTile) {
        if (tile == null) return null;

        if (tileTemp.containsKey(tipoTile)) return tileTemp.get(tipoTile);

        Point coords = coordenadas.get(tipoTile);
        boolean isFallback = false;
        if (coords == null) {
            System.err.println("Advertencia: Coordenadas no definidas para tile '" + tipoTile + "'. Usando fallback 'E'.");
            coords = coordenadas.get('E');
            isFallback = true;
            if (coords == null) {
                System.err.println("Error crítico: Coordenadas para tile por defecto 'E' no definidas.");
                return null;
            }
        }

        int x = coords.col * TAMANO;
        int y = coords.lin * TAMANO;

        if (x < 0 || y < 0 || x + TAMANO > tile.getWidth() || y + TAMANO > tile.getHeight()) {
            System.err.println("Error: Coordenadas (" + x + "," + y + ") para tile '" + tipoTile + "' están fuera de los límites del tileset. Usando fallback 'E'.");
            return getTileImage('E');
        }

        try {
            PixelReader reader = tile.getPixelReader();
            WritableImage tileImage = new WritableImage(reader, x, y, TAMANO, TAMANO);
            if (!isFallback || tipoTile == 'E') tileTemp.put(tipoTile, tileImage);
            
            return tileImage;
        } catch (Exception e) {
            System.err.println("Error al recortar el tile '" + tipoTile + "' en (" + x + "," + y + "): " + e.getMessage());
            return getTileImage('E');
        }
    }

    /**
     * Creacion del Tile
     * 
     * @param tipoTile Es el tipo del Tile que va a crear, Personaje (P)
     * @return Devuelve la vista de la imagen, es logico, no?
     */
    public static ImageView crearTileView(char tipoTile) {
        Image img = getTileImage(tipoTile);
        ImageView vista = new ImageView(img);
        vista.setFitWidth(TAMANO);
        vista.setFitHeight(TAMANO);
        vista.setPreserveRatio(true);
        if (img == null) vista.setStyle("-fx-background-color: magenta; -fx-border-color: black;");
        return vista;
    }

    /**
     * Añade Tiles a la matriz
     * 
     * @param gridPane Donde se añade 
     * @param escenario El escenario que pinta
     */
    public static void agregarTilesAlGrid(GridPane gridPane, Escenario escenario) {
        if (gridPane == null || escenario == null) {
            System.err.println("Error en agregarTilesAlGrid: GridPane o Escenario es null.");
            return;
        }
        gridPane.getChildren().clear();
        System.out.println("Dibujando mapa en GridPane (" + escenario.getFilas() + "x" + escenario.getColumnas() + ")...");
        for (int fila = 0; fila < escenario.getFilas(); fila++) {
            for (int columna = 0; columna < escenario.getColumnas(); columna++) {
                char tipoTile = escenario.getTile(fila, columna);
                ImageView vista = crearTileView(tipoTile);
                gridPane.add(vista, columna, fila);
            }
        }
        System.out.println("Tiles agregados al GridPane. Total hijos: " + gridPane.getChildren().size());
    }

    /**
     * Punto en el que se encuentra las cosas
     */
    private static class Point {
        int col, lin;

        /**
         * Constructor parametrizado
         * 
         * @param col la columna
         * @param lin la linea
         */
        Point(int col, int lin) {
            this.col = col;
            this.lin = lin;
        }
    }
}
