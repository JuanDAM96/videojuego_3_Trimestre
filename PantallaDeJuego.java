// --- SIN "package Vista;" ---

// --- IMPORTS NECESARIOS ---
import Modelo.Escenario;
import Modelo.Jugador;
import Controlador.Control;
import Controlador.Sesion;
import Controlador.TileManager;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane; // Import necesario
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
// --- FIN IMPORTS ---

public class PantallaDeJuego {

    @FXML private AnchorPane rootPane;
    @FXML private GridPane gridPaneMapa; // Asegúrate que el fx:id en FXML sea este
    @FXML private ImageView playerImageView; // Asegúrate que el fx:id en FXML sea este
    @FXML private TextArea txtComentarios;

    private Jugador jugadorActual;
    private Escenario escenarioActual;
    private AnimationTimer gameLoop;

    // --- ¡¡ASEGÚRATE QUE ESTE VALOR COINCIDA CON TileManager.TILE_SIZE!! ---
    private final int TILE_SIZE = 32; // O 32 si tus tiles son de ese tamaño

    @FXML
    public void initialize() {
        System.out.println("Inicializando PantallaDeJuego...");
        cargarDatosJuego();
        if (escenarioActual == null || jugadorActual == null) {
             txtComentarios.setText("Error crítico: No se pudo cargar el escenario o el jugador.");
             return;
        }

        // Ahora TileManager.agregarTilesAlGrid debería funcionar
        TileManager.agregarTilesAlGrid(gridPaneMapa, escenarioActual);
        txtComentarios.appendText("Escenario dibujado.\n");

        setupPlayerVisuals();
        if (jugadorActual != null) {
            txtComentarios.appendText("Jugador '" + jugadorActual.getNombre() + "' listo.\n");
        }

        startGameLoop();
        rootPane.setFocusTraversable(true);
    }

    // --- Métodos cargarDatosJuego, setupPlayerVisuals, setupInputHandlers, startGameLoop, updatePlayerPositionVisual, stopGame (sin cambios respecto a la versión anterior, pero ahora los imports deberían funcionar) ---
    private void cargarDatosJuego() {
        escenarioActual = Sesion.cargarEscenario("escenarios/nivel1.txt");
        if (escenarioActual == null) {
             System.err.println("Fallo al cargar escenario.");
             return;
        }
        String nombreJugador = App.getNombreJugadorActual();
        if (nombreJugador != null) {
             jugadorActual = Sesion.cargarJugadorPorNombre(nombreJugador);
        }
        if (jugadorActual == null) {
            System.out.println("Creando jugador 'Invitado'.");
            jugadorActual = new Jugador("Invitado", "", 1, 1, 0);
        }
    }

    private void setupPlayerVisuals() {
        if (playerImageView == null) {
            System.err.println("Error: playerImageView es null.");
            return;
        }
        Image playerImg = TileManager.getTileImage('P');
        if (playerImg != null) {
            playerImageView.setImage(playerImg);
            playerImageView.setFitWidth(TILE_SIZE);
            playerImageView.setFitHeight(TILE_SIZE);
            playerImageView.setManaged(false);
            playerImageView.setViewOrder(-1.0);
            updatePlayerPositionVisual();
        } else {
            System.err.println("Error: No se pudo obtener imagen del jugador ('P').");
            txtComentarios.appendText("Error: Falta imagen del jugador.\n");
            playerImageView.setVisible(false);
        }
    }

    public void setupInputHandlers(Scene scene) {
         if (scene == null) return;
         System.out.println("Configurando manejadores de entrada...");
         scene.setOnKeyPressed((KeyEvent event) -> {
             Control.agregarTecla(event.getCode());
         });
         scene.setOnKeyReleased((KeyEvent event) -> {
             Control.eliminarTecla(event.getCode());
         });
         javafx.application.Platform.runLater(() -> rootPane.requestFocus());

         scene.getWindow().focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                Control.limpiarTeclas(); // Llama al método corregido
            }
         });
         txtComentarios.appendText("Controles WASD activados.\n");
    }

    private void startGameLoop() {
         if (gameLoop != null) gameLoop.stop();
         System.out.println("Iniciando bucle del juego...");
         gameLoop = new AnimationTimer() {
             @Override
             public void handle(long now) {
                 // Llama al método mov con la firma correcta
                 boolean seMovio = Control.mov(jugadorActual, escenarioActual);
                 if (seMovio) {
                     updatePlayerPositionVisual();
                     txtComentarios.appendText("Movido a: ["+jugadorActual.getFilaActual()+","+jugadorActual.getColumnaActual()+"] Pts: " + jugadorActual.getPuntos() + "\n");
                     txtComentarios.setScrollTop(Double.MAX_VALUE);
                 }
             }
         };
         gameLoop.start();
    }

    private void updatePlayerPositionVisual() {
        if (jugadorActual == null || playerImageView == null || gridPaneMapa == null) return;
        double pixelX = jugadorActual.getColumnaActual() * TILE_SIZE + gridPaneMapa.getLayoutX();
        double pixelY = jugadorActual.getFilaActual() * TILE_SIZE + gridPaneMapa.getLayoutY();
        playerImageView.setTranslateX(pixelX);
        playerImageView.setTranslateY(pixelY);
    }

    public void stopGame() {
        if (gameLoop != null) {
            gameLoop.stop();
            System.out.println("Bucle del juego detenido.");
        }
        Control.limpiarTeclas(); // Llama al método corregido
        // Guardar...
    }
}
