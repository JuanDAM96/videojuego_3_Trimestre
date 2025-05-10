

import Modelo.Escenario;
import Modelo.Jugador;
import Controlador.Control;
import Controlador.Sesion;
import Controlador.GestorTile;
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
import javafx.scene.layout.GridPane;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Pantaalla de juego
 * Lugar donde supuestamente tiene que jugar el jugador
 * 
 * @author Santiago
 * @author Juan
 * @version 0.3.3
 */
public class PantallaDeJuego {

    @FXML
    private AnchorPane paneRaiz;
    @FXML
    private GridPane mapaJuego;
    @FXML
    private ImageView jugador;
    @FXML
    private TextArea txtComentarios;

    private Jugador jugadorActual;
    private Escenario escenarioActual;
    private AnimationTimer bucleJuego;

    private final int TAMANO_TILE = 32;

    /**
     * Incializa panatalla de juego
     */
    @FXML
    public void initialize() {
        System.out.println("Inicializando PantallaDeJuego...");
        cargarDatosJuego();
        if (escenarioActual == null || jugadorActual == null) {
            txtComentarios.setText("Error crítico: No se pudo cargar el escenario o el jugador.");
            return;
        }

        GestorTile.agregarTilesAlGrid(mapaJuego, escenarioActual);
        txtComentarios.appendText("Escenario dibujado.\n");

        visualizacionJug();
        if (jugadorActual != null) txtComentarios.appendText("Jugador '" + jugadorActual.getNombre() + "' listo.\n");

        bucleJuego();
        paneRaiz.setFocusTraversable(true);
    }

    /**
     * Carga los datos de los archivos
     */
    private void cargarDatosJuego() {
        escenarioActual = Sesion.cargarEscenario("escenarios/nivel1.txt");
        if (escenarioActual == null) {
            System.err.println("Fallo al cargar escenario.");
            return;
        }
        String nombreJugador = App.getNombreJugadorActual();
        if (nombreJugador != null) jugadorActual = Sesion.cargarJugadorPorNombre(nombreJugador);

        if (jugadorActual == null) {
            System.out.println("Creando jugador 'Invitado'.");
            jugadorActual = new Jugador("Invitado", "", 1, 1, 0);
        }
    }

    /**
     * Muestra el jugador
     */
    private void visualizacionJug() {
        if (jugador == null) {
            System.err.println("Error: playerImageView es null.");
            return;
        }
        Image jugadorImg = GestorTile.getTileImage('P');
        if (jugadorImg != null) {
            jugador.setImage(jugadorImg);
            jugador.setFitWidth(TAMANO_TILE);
            jugador.setFitHeight(TAMANO_TILE);
            jugador.setManaged(false);
            jugador.setViewOrder(-1.0);
            actJugPos();
        } else {
            System.err.println("Error: No se pudo obtener imagen del jugador ('P').");
            txtComentarios.appendText("Error: Falta imagen del jugador.\n");
            jugador.setVisible(false);
        }
    }

    /**
     * Captura las entradas
     * 
     * @param escena La escena que se tiene que capturar las acciones del jugador
     */
    public void setCapInp(Scene escena) {
        if (escena == null) return;
        
        System.out.println("Configurando manejadores de entrada...");

        escena.setOnKeyPressed((KeyEvent evento) -> {Control.agregarTecla(evento.getCode());});
        escena.setOnKeyReleased((KeyEvent evento) -> {Control.eliminarTecla(evento.getCode());});
        javafx.application.Platform.runLater(() -> paneRaiz.requestFocus());

        escena.getWindow().focusedProperty().addListener((obs, viejoVal, nuevoVal) -> {if (!nuevoVal) Control.limpiarTeclas();});

        txtComentarios.appendText("Controles WASD activados.\n");
    }

    /**
     * Polling del juego
     */
    private void bucleJuego() {
        if (bucleJuego != null) bucleJuego.stop();
        System.out.println("Iniciando bucle del juego...");
        bucleJuego = new AnimationTimer() {
            @Override
            public void handle(long ahora) {
                boolean seMovio = Control.mov(jugadorActual, escenarioActual);
                if (seMovio) {
                    actJugPos();
                    txtComentarios.appendText("Movido a: [" + jugadorActual.getFilaActual() + "," + jugadorActual.getColumnaActual() + "] Pts: " + jugadorActual.getPuntos() + "\n");
                    txtComentarios.setScrollTop(Double.MAX_VALUE);
                }
            }
        };
        bucleJuego.start();
    }

    /**
     * Actualiza la posicion del jugador
     */
    private void actJugPos() {
        if (jugadorActual == null || jugador == null || mapaJuego == null) return;
        
        double pixelX = jugadorActual.getColumnaActual() * TAMANO_TILE + mapaJuego.getLayoutX();
        double pixelY = jugadorActual.getFilaActual() * TAMANO_TILE + mapaJuego.getLayoutY();
        jugador.setTranslateX(pixelX);
        jugador.setTranslateY(pixelY);
    }

    /**
     * Detiene el polling del juego
     */
    public void paraJuego() {
        if (bucleJuego != null) {
            bucleJuego.stop();
            System.out.println("Bucle del juego detenido.");
        }
        Control.limpiarTeclas();
    }
}