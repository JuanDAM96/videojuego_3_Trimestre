package Controlador; // Este código es un visor de tiles para un tileset de un juego 2D, como el de un RPG o un juego de plataformas.
// El código está diseñado para ser ejecutado en JavaFX y permite cargar un tileset, recortar tiles individuales y mostrarlos en una interfaz gráfica.

// TODO: Cambiar la ruta del tileset a una ruta relativa o usar un recurso de clase si es necesario.
// TODO: Añadir más ejemplos de tiles para mostrar en la interfaz gráfica.
// TODO @T4C30: Cambiar nombres de variables y métodos para que sean más descriptivos y a español(Menos Crtl-C y Crtl-V para paginas randoms de internet), si es necesario.
// TODO @JuanDAM96: Organiza el codigo en paquetes y clases según el patrón MVC (Modelo-Vista-Controlador) si es necesario, segun las modificaciones pertinentes para JavaFX.
// TODO @JuanDAM96: Añadir comentarios y documentación para facilitar la comprensión del código.

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane; // Usaremos GridPane para mostrar algunos tiles
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TileExtractor extends Application {

    // --- Parámetros Configurables ---
    private static final String TILESET_PATH = "dungeon_tiles.png"; // Ruta a tu archivo de tileset
    private static final int TILE_WIDTH = 16;   // Ancho de un tile individual en píxeles
    private static final int TILE_HEIGHT = 16;  // Alto de un tile individual en píxeles
    // ---------------------------------

    private Image tileset; // La imagen completa cargada

    @Override
    public void start(Stage primaryStage) {
        // 1. Cargar la imagen completa del tileset
        try {
            // Intenta cargar desde el sistema de archivos.
            // Si está en resources, usa getClass().getResourceAsStream(...).
            tileset = new Image(new FileInputStream(TILESET_PATH));
        } catch (FileNotFoundException e) {
            System.err.println("Error: No se pudo encontrar el archivo del tileset en: " + TILESET_PATH);
            e.printStackTrace();
            // Considera mostrar un mensaje de error al usuario en la UI
            return; // Salir si no se puede cargar la imagen
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen: " + TILESET_PATH);
            e.printStackTrace();
            return;
        }

        // 2. Crear un layout para mostrar algunos tiles recortados (ejemplo)
        GridPane grid = new GridPane();
        grid.setHgap(5); // Espacio horizontal entre tiles
        grid.setVgap(5); // Espacio vertical entre tiles

        // 3. Obtener y mostrar algunos tiles de ejemplo
        // Nota: Las coordenadas (tileX, tileY) son índices de tile (columna, fila), empezando en 0

        // Ejemplo 1: Tile en la esquina superior izquierda (0, 0)
        ImageView tile0_0 = getTile(0, 0);
        if (tile0_0 != null) grid.add(tile0_0, 0, 0); // Añadir al grid en posición (0,0)

        // Ejemplo 2: Tile a su derecha (1, 0) - parece ser borde superior de muro
        ImageView tile1_0 = getTile(1, 0);
        if (tile1_0 != null) grid.add(tile1_0, 1, 0);

        // Ejemplo 3: Tile debajo del primero (0, 1) - parece ser borde izquierdo de muro
        ImageView tile0_1 = getTile(0, 1);
        if (tile0_1 != null) grid.add(tile0_1, 0, 1);

        // Ejemplo 4: Tile de suelo más oscuro (ej. en columna 8, fila 1)
        ImageView tile8_1 = getTile(8, 1);
        if (tile8_1 != null) grid.add(tile8_1, 1, 1); // Ponerlo al lado del anterior

        // Ejemplo 5: Un personaje (ej. el slime verde, aprox. columna 15, fila 7)
        ImageView slimeVerde = getTile(15, 7);
        if (slimeVerde != null) grid.add(slimeVerde, 0, 2);

        // Ejemplo 6: La antorcha (aprox. columna 11, fila 5)
        ImageView antorcha = getTile(11, 5);
         if (antorcha != null) grid.add(antorcha, 1, 2);

        // Configurar la escena y mostrarla
        Scene scene = new Scene(grid);
        primaryStage.setTitle("Visor de Tiles Recortados");
        primaryStage.setScene(scene);
        primaryStage.sizeToScene(); // Ajustar tamaño de ventana al contenido
        primaryStage.show();
    }

    /**
     * Crea y devuelve un ImageView que muestra un tile específico del tileset.
     *
     * @param tileX Índice de la columna del tile (empezando en 0).
     * @param tileY Índice de la fila del tile (empezando en 0).
     * @return Un ImageView configurado para mostrar el tile, o null si las coordenadas son inválidas.
     */
    public ImageView getTile(int tileX, int tileY) {
        if (tileset == null) {
            System.err.println("Error: El tileset no ha sido cargado.");
            return null;
        }

        // Calcular las coordenadas en píxeles dentro de la imagen completa
        int pixelX = tileX * TILE_WIDTH;
        int pixelY = tileY * TILE_HEIGHT;

        // Validar que las coordenadas estén dentro de los límites de la imagen
        if (pixelX < 0 || pixelY < 0 ||
            pixelX + TILE_WIDTH > tileset.getWidth() ||
            pixelY + TILE_HEIGHT > tileset.getHeight()) {
            System.err.println("Advertencia: Coordenadas de tile (" + tileX + ", " + tileY +
                               ") están fuera de los límites de la imagen.");
            return null; // O devolver un tile por defecto/vacío
        }

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
