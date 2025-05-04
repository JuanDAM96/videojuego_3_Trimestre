// --- SIN "package com.videojuego;" ---
// Esta clase está en el paquete por defecto

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

// --- IMPORTS NECESARIOS para clases en otros paquetes ---
import Controlador.SQLite;
import Controlador.Sesion; // <<<--- IMPORT AÑADIDO/VERIFICADO
import Controlador.Control;
import Controlador.TileManager;
import Modelo.Jugador;
import Modelo.Puntuacion;
import Modelo.Escenario;
// Ya no se importan PantallaDeJuego ni RegistroJugadorController porque están en el mismo paquete (default)
// --- FIN IMPORTS ---


public class App extends Application {

    private static Stage primaryStage;
    private static String nombreJugadorActual = null;
    private static Jugador jugadorActual = null;

    private static final String DIRECTORIO_ESCENARIOS = "escenarios";
    private static final String DIRECTORIO_JUGADORES = "jugadores";
    private static final String DIRECTORIO_PARTIDAS = "partidas";
    private static final String[] DIRECTORIOS_NECESARIOS = { DIRECTORIO_ESCENARIOS, DIRECTORIO_JUGADORES, DIRECTORIO_PARTIDAS };


    @Override
    public void init() throws Exception {
        super.init();
        System.out.println("Ejecutando App.init()...");
        crearDirectoriosSiNoExisten();
        SQLite.inicializarBaseDatos();
        // Ahora Sesion.crearEscenarioPorDefectoSiNoExiste() debería ser encontrado
        Sesion.crearEscenarioPorDefectoSiNoExiste();
        System.out.println("App.init() completado.");
    }

    // ... (El resto de la clase App sin cambios respecto a la versión anterior) ...

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Videojuego");
        System.out.println("Ejecutando App.start()...");

        TextInputDialog dialogoNombre = new TextInputDialog();
        dialogoNombre.setTitle("Inicio de Sesión");
        dialogoNombre.setHeaderText("Introduce tu nombre de jugador:");
        dialogoNombre.setContentText("Nombre:");

        Optional<String> resultado = dialogoNombre.showAndWait();

        if (resultado.isPresent() && !resultado.get().trim().isEmpty()) {
            nombreJugadorActual = resultado.get().trim();
            System.out.println("Intentando iniciar sesión como: " + nombreJugadorActual);
            jugadorActual = Sesion.cargarJugadorPorNombre(nombreJugadorActual); // Usa Sesion importado

            if (jugadorActual != null) {
                System.out.println("Jugador encontrado: " + jugadorActual.getNombre());
                mostrarPantallaDeInicio();
            } else {
                System.out.println("Jugador no encontrado. Mostrando pantalla de registro.");
                mostrarPantallaRegistro();
            }
        } else {
            System.out.println("Inicio cancelado.");
            mostrarPantallaDeInicio();
        }

         primaryStage.setOnCloseRequest(event -> {
             Scene currentScene = primaryStage.getScene();
             if (currentScene != null && currentScene.getUserData() instanceof PantallaDeJuego) {
                 ((PantallaDeJuego) currentScene.getUserData()).stopGame();
             }
             System.out.println("Cerrando aplicación.");
         });
    }

    public static void mostrarPantallaDeInicio() {
        String fxmlPath = "/Escenas/PantallaDeInicio.fxml";
        try {
            System.out.println("Cargando " + fxmlPath + "...");
            URL fxmlUrl = App.class.getResource(fxmlPath);
            if (fxmlUrl == null) fxmlUrl = App.class.getResource("Escenas/PantallaDeInicio.fxml");
            if (fxmlUrl == null) throw new IOException("FXML no encontrado: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setTitle("Mejores Puntuaciones");
            primaryStage.setScene(scene);
            System.out.println("Mostrando PantallaDeInicio.");
            primaryStage.show();
        } catch (Exception e) {
            mostrarError("Error al cargar la pantalla de inicio", e);
        }
    }

     public static void mostrarPantallaRegistro() {
         String fxmlPath = "/Escenas/RegistroJugador.fxml";
         try {
            System.out.println("Intentando cargar FXML: " + fxmlPath);
            URL fxmlUrl = App.class.getResource(fxmlPath);
            if (fxmlUrl == null) fxmlUrl = App.class.getResource("Escenas/RegistroJugador.fxml");
            if (fxmlUrl == null) throw new IOException("No se pudo encontrar FXML: " + fxmlPath);

            System.out.println("URL del FXML encontrada: " + fxmlUrl);
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            System.out.println("FXML de Registro cargado.");

            RegistroJugadorController controller = loader.getController();
            if (controller != null) {
                 System.out.println("Controlador RegistroJugadorController obtenido.");
                 controller.setNombreInicial(nombreJugadorActual);
            } else {
                 System.err.println("Advertencia: Controlador RegistroJugadorController es null.");
            }

            Scene scene = new Scene(root);
            primaryStage.setTitle("Registrar Nuevo Jugador");
            primaryStage.setScene(scene);
            System.out.println("Escena de Registro establecida.");
            primaryStage.show();

        } catch (Exception e) {
            mostrarError("Error al mostrar la pantalla de registro", e);
        }
    }

    public static void mostrarPantallaDeJuego() {
         String fxmlPath = "/Escenas/PantallaDeJuego.fxml";
        try {
            System.out.println("Cargando " + fxmlPath + "...");
            URL fxmlUrl = App.class.getResource(fxmlPath);
             if (fxmlUrl == null) throw new IOException("FXML no encontrado: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            PantallaDeJuego controller = loader.getController();

            if (controller == null) {
                 System.err.println("Error crítico: Controlador de PantallaDeJuego no encontrado.");
                 mostrarError("Error interno al iniciar el juego (controlador nulo).", null);
                 return;
            }
             System.out.println("Controlador PantallaDeJuego obtenido.");

            Scene scene = new Scene(root);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Juego - " + (jugadorActual != null ? jugadorActual.getNombre() : "Invitado"));
            System.out.println("Escena de Juego establecida.");

            controller.setupInputHandlers(scene);
            scene.setUserData(controller);

             System.out.println("Mostrando PantallaDeJuego.");
            // primaryStage.show();

        } catch (Exception e) {
            mostrarError("Error al cargar la pantalla de juego", e);
        }
    }

     public static String getNombreJugadorActual() { return nombreJugadorActual; }
     public static Jugador getJugadorActual() { return jugadorActual; }
     public static void setJugadorActual(Jugador jugador) {
         jugadorActual = jugador;
         nombreJugadorActual = (jugador != null) ? jugador.getNombre() : null;
     }

    private static void crearDirectoriosSiNoExisten() {
        System.out.println("Verificando/creando directorios...");
        for (String nombreDirectorio : DIRECTORIOS_NECESARIOS) {
            Path rutaDirectorio = Paths.get(nombreDirectorio);
            try {
                if (Files.notExists(rutaDirectorio)) {
                    Files.createDirectories(rutaDirectorio);
                    System.out.println("Directorio '" + nombreDirectorio + "' creado.");
                } else if (!Files.isDirectory(rutaDirectorio)) {
                     System.err.println("Error: La ruta '" + nombreDirectorio + "' existe pero no es un directorio.");
                }
            } catch (IOException e) {
                System.err.println("No se pudo crear o verificar el directorio '" + nombreDirectorio + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
         // System.out.println("Verificación de directorios completada.");
    }

     public static void mostrarError(String mensaje, Exception excepcion) {
         System.err.println("MOSTRANDO ERROR: " + mensaje);
         if (excepcion != null) {
             excepcion.printStackTrace();
         }
         javafx.application.Platform.runLater(() -> {
              Alert alert = new Alert(AlertType.ERROR);
              alert.setTitle("Error");
              alert.setHeaderText(null);
              alert.setContentText(mensaje + (excepcion != null ? "\nDetalles: " + excepcion.getClass().getSimpleName() + " - " + excepcion.getMessage() : ""));
              alert.showAndWait();
         });
     }

    public static void main(String[] args) {
        launch(args);
    }
}
