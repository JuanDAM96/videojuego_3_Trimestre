

import Modelo.Escenario;
import Modelo.Jugador;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Point2D; // Para posición del jugador
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane; // Usar AnchorPane como raíz en FXML
import javafx.scene.layout.GridPane;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Controlador.Control;
import Controlador.Sesion;
import Controlador.TileManager; // Usar el nuevo TileManager

public class PantallaDeJuego {

    @FXML private AnchorPane rootPane; // Nodo raíz (debe ser AnchorPane en FXML)
    @FXML private GridPane gridPaneMapa; // GridPane para el fondo del mapa
    @FXML private ImageView playerImageView; // ImageView para el jugador (en AnchorPane)
    @FXML private TextArea txtComentarios;

    private Jugador jugadorActual;
    private Escenario escenarioActual;
    private AnimationTimer gameLoop;

    // Tamaño de los tiles (debe coincidir con TileManager)
    private final int TILE_SIZE = 16; // O 32 si tus tiles son de ese tamaño

    // Mapa para asociar KeyCode con dirección (opcional pero útil)
    private final Map<KeyCode, Point2D> movementVectors = new HashMap<>();

    @FXML
    public void initialize() {
        // --- 1. Cargar / Inicializar ---
        cargarDatosJuego(); // Carga escenario y jugador
        if (escenarioActual == null || jugadorActual == null) {
             txtComentarios.setText("Error crítico: No se pudo cargar el escenario o el jugador.");
             // Podrías deshabilitar controles o mostrar un error más formal
             return;
        }

        // --- 2. Dibujar Mapa ---
        TileManager.agregarTilesAlGrid(gridPaneMapa, escenarioActual);
        txtComentarios.appendText("Escenario dibujado.\n");

        // --- 3. Configurar Jugador Visual ---
        setupPlayerVisuals();
        txtComentarios.appendText("Jugador '" + jugadorActual.getNombre() + "' listo.\n");

        // --- 4. Iniciar Bucle ---
        startGameLoop();

        // El setup de input se hará cuando la escena esté lista (ver App.java)
    }

    // Carga los datos necesarios para empezar a jugar
    private void cargarDatosJuego() {
        // TODO: Implementar lógica para elegir/cargar escenario y jugador
        // Por ahora, cargamos uno por defecto:
        escenarioActual = Sesion.cargarEscenario( "escenarios/nivel1.txt"); // Asegúrate que exista
        // Deberías tener el nombre del jugador que inició sesión
        String nombreJugadorActual = App.getNombreJugadorActual(); // Necesitas este método en App
        if (nombreJugadorActual != null) {
             jugadorActual = Sesion.cargarJugadorPorNombre(nombreJugadorActual);
             // Si se cargó, podrías querer cargar el estado de su última partida
             // Jugador partidaGuardada = Sesion.cargarPartidaActual(nombreJugadorActual + "_partida");
             // if (partidaGuardada != null) jugadorActual = partidaGuardada;
        }

        // Si algo falla, crear jugador por defecto
        if (jugadorActual == null) {
            System.out.println("Creando jugador por defecto para la partida.");
            jugadorActual = new Jugador("Invitado", "", 1, 1, 0); // Posición inicial (1,1)
        }
    }

    // Configura la apariencia inicial del jugador
    private void setupPlayerVisuals() {
        if (playerImageView == null) {
            System.err.println("Error: playerImageView no inyectado desde FXML.");
            return;
        }
        Image playerImg = TileManager.getTileImage('P'); // 'P' u otro char para el jugador
        if (playerImg != null) {
            playerImageView.setImage(playerImg);
            playerImageView.setFitWidth(TILE_SIZE);
            playerImageView.setFitHeight(TILE_SIZE);
            playerImageView.setManaged(false); // Importante para posicionar en AnchorPane
            playerImageView.setViewOrder(-1.0); // Para que esté encima del GridPane
            updatePlayerPositionVisual(); // Colocar en la posición inicial
        } else {
            System.err.println("Error: No se pudo obtener la imagen del jugador desde TileManager.");
            txtComentarios.appendText("Error: Falta imagen del jugador.\n");
            // Podrías ocultar el ImageView o ponerle un placeholder
            playerImageView.setVisible(false);
        }
    }

    // Configura los manejadores de eventos de teclado
    public void setupInputHandlers(Scene scene) {
        if (scene == null) return;

        scene.setOnKeyPressed((KeyEvent event) -> {
            Control.agregarTecla(event.getCode());
            // System.out.println("Tecla presionada: " + event.getCode()); // Para depuración
        });

        scene.setOnKeyReleased((KeyEvent event) -> {
            Control.eliminarTecla(event.getCode());
            // System.out.println("Tecla liberada: " + event.getCode()); // Para depuración
        });

        scene.getWindow().focusedProperty().addListener((obs, oldVal, newVal) -> {
           if (!newVal) {
               Control.limpiarTeclas(); // Limpiar teclas si la ventana pierde foco
           }
        });
        txtComentarios.appendText("Controles WASD activados.\n");
         // Asegurarse que el AnchorPane (o el nodo que contiene todo) tenga el foco
         rootPane.requestFocus();
    }

    // Inicia el bucle principal del juego
    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Llama a la lógica de movimiento del controlador
                boolean seMovio = Control.mov(jugadorActual, escenarioActual);

                if (seMovio) {
                    updatePlayerPositionVisual(); // Actualiza la posición en pantalla
                    // Añadir lógica de juego aquí (recoger objetos, enemigos, etc.)
                    // checkCollisionsWithObjects();
                    // updateGameStatus();
                    txtComentarios.appendText("Movido a: ["+jugadorActual.getFilaActual()+","+jugadorActual.getColumnaActual()+"] Pts: " + jugadorActual.getPuntos() + "\n");
                    // Auto-scroll del TextArea
                    txtComentarios.setScrollTop(Double.MAX_VALUE);
                }
            }
        };
        gameLoop.start();
    }

    // Actualiza la posición VISUAL del ImageView del jugador en el AnchorPane
    private void updatePlayerPositionVisual() {
        if (jugadorActual == null || playerImageView == null || gridPaneMapa == null) return;

        // Calcular coordenadas en píxeles relativas al AnchorPane
        // Asumiendo que el GridPane está en (0,0) dentro del AnchorPane
        double pixelX = jugadorActual.getColumnaActual() * TILE_SIZE + gridPaneMapa.getLayoutX();
        double pixelY = jugadorActual.getFilaActual() * TILE_SIZE + gridPaneMapa.getLayoutY();

        playerImageView.setTranslateX(pixelX);
        playerImageView.setTranslateY(pixelY);
    }

    // Detiene el bucle del juego y guarda el estado si es necesario
    public void stopGame() {
        if (gameLoop != null) {
            gameLoop.stop();
            System.out.println("Bucle del juego detenido.");
        }
        Control.limpiarTeclas();

        // Guardar puntuación si es relevante (ej. si es mayor que la mínima en top 10)
        // if (jugadorActual.getPuntos() > puntuacionMinimaTop10) {
        //      SQLite.guardarPuntuacion(jugadorActual.getNombre(), jugadorActual.getPuntos());
        // }

        // Guardar estado de la partida
        // Sesion.guardarPartidaActual(jugadorActual, escenarioActual, jugadorActual.getNombre() + "_partida");
    }
}
