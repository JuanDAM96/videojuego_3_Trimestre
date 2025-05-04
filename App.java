


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL; // Importar URL
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

// Importa tus clases necesarias
import Controlador.SQLite;
import Controlador.Sesion;
import Modelo.Jugador;
import Modelo.Puntuacion; // Asegúrate de tener esta clase si la usas en otra parte

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
        crearDirectoriosSiNoExisten();
        SQLite.inicializarBaseDatos();
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Videojuego");

        TextInputDialog dialogoNombre = new TextInputDialog();
        dialogoNombre.setTitle("Inicio de Sesión");
        dialogoNombre.setHeaderText("Introduce tu nombre de jugador:");
        dialogoNombre.setContentText("Nombre:");

        Optional<String> resultado = dialogoNombre.showAndWait();

        if (resultado.isPresent() && !resultado.get().trim().isEmpty()) {
            nombreJugadorActual = resultado.get().trim();
            System.out.println("Intentando iniciar sesión como: " + nombreJugadorActual);

            jugadorActual = Sesion.cargarJugadorPorNombre(nombreJugadorActual);

            if (jugadorActual != null) {
                System.out.println("Jugador encontrado: " + jugadorActual.getNombre());
                mostrarPantallaDeInicio();
            } else {
                System.out.println("Jugador no encontrado. Mostrando pantalla de registro.");
                mostrarPantallaRegistro(); // Intenta mostrar el registro
            }
        } else {
            System.out.println("Inicio cancelado.");
            mostrarPantallaDeInicio(); // Muestra puntuaciones si se cancela
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
        try {
            System.out.println("Cargando PantallaDeInicio.fxml...");
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/Escenas/PantallaDeInicio.fxml"));
            if (loader.getLocation() == null) throw new IOException("FXML no encontrado: /Escenas/PantallaDeInicio.fxml");
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setTitle("Mejores Puntuaciones");
            primaryStage.setScene(scene);
            System.out.println("Mostrando PantallaDeInicio.");
            primaryStage.show(); // Asegurarse de que se muestre
        } catch (IOException | IllegalStateException e) {
            mostrarError("Error al cargar la pantalla de inicio", e);
        } catch (Exception e) {
             mostrarError("Error inesperado al mostrar pantalla de inicio", e);
        }
    }

    // --- MÉTODO ACTUALIZADO ---
    public static void mostrarPantallaRegistro() {
         String fxmlPath = "/Escenas/RegistroJugador.fxml"; // Ruta al FXML
         try {
            System.out.println("Intentando cargar FXML: " + fxmlPath);
            URL fxmlUrl = App.class.getResource(fxmlPath); // Obtener URL primero

            if (fxmlUrl == null) {
                 // Si no se encuentra, intentar sin la barra inicial (ruta relativa)
                 System.out.println("No encontrado con ruta absoluta, intentando ruta relativa: Escenas/RegistroJugador.fxml");
                 fxmlUrl = App.class.getResource("Escenas/RegistroJugador.fxml");
            }

            if (fxmlUrl == null) {
                 // Si sigue sin encontrarse, lanzar error claro
                 throw new IOException("No se pudo encontrar el archivo FXML en ninguna de las rutas probadas: " + fxmlPath + " o Escenas/RegistroJugador.fxml");
            }

            System.out.println("URL del FXML encontrada: " + fxmlUrl);
            FXMLLoader loader = new FXMLLoader(fxmlUrl); // Cargar desde la URL encontrada
            Parent root = loader.load();
            System.out.println("FXML de Registro cargado correctamente.");

            RegistroJugadorController controller = loader.getController();
            if (controller != null) {
                 System.out.println("Controlador RegistroJugadorController obtenido.");
                 controller.setNombreInicial(nombreJugadorActual);
            } else {
                 System.err.println("Advertencia: No se pudo obtener el controlador RegistroJugadorController desde el FXML.");
            }

            Scene scene = new Scene(root);
            primaryStage.setTitle("Registrar Nuevo Jugador");
            primaryStage.setScene(scene);
            System.out.println("Escena de Registro establecida en primaryStage.");
            primaryStage.show(); // Asegurarse de que se muestre

        } catch (IOException e) {
            System.err.println("IOException al cargar " + fxmlPath + ":");
            e.printStackTrace();
            mostrarError("Error fatal al cargar la pantalla de registro. Verifica que el archivo FXML exista en la ruta correcta y no tenga errores.", e);
        } catch (IllegalStateException e) {
            System.err.println("IllegalStateException (posiblemente problema en FXML o controlador de registro):");
            e.printStackTrace();
            mostrarError("Error interno al configurar la pantalla de registro.", e);
        } catch (Exception e) {
            System.err.println("Excepción inesperada al mostrar pantalla de registro:");
            e.printStackTrace();
            mostrarError("Error inesperado al mostrar el registro.", e);
        }
    }
    // --- FIN MÉTODO ACTUALIZADO ---

    public static void mostrarPantallaDeJuego() {
         String fxmlPath = "/Escenas/PantallaDeJuego.fxml";
        try {
            System.out.println("Cargando PantallaDeJuego.fxml...");
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
             if (loader.getLocation() == null) throw new IOException("FXML no encontrado: " + fxmlPath);
            Parent root = loader.load();
            PantallaDeJuego controller = loader.getController();

            Scene scene = new Scene(root);

            if (controller != null) {
                controller.setupInputHandlers(scene);
                scene.setUserData(controller);
                 System.out.println("Controlador PantallaDeJuego y manejadores de entrada configurados.");
            } else {
                 System.err.println("Error crítico: Controlador de PantallaDeJuego no encontrado.");
                 mostrarError("Error interno al iniciar el juego.", null);
                 return;
            }

            primaryStage.setTitle("Juego - " + (jugadorActual != null ? jugadorActual.getNombre() : "Invitado"));
            primaryStage.setScene(scene);
             System.out.println("Mostrando PantallaDeJuego.");
            // primaryStage.show(); // No es necesario si ya está visible

        } catch (IOException | IllegalStateException e) {
            mostrarError("Error al cargar la pantalla de juego", e);
        } catch (Exception e) {
             mostrarError("Error inesperado al mostrar pantalla de juego", e);
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
         System.out.println("Verificación de directorios completada.");
    }

     public static void mostrarError(String mensaje, Exception excepcion) {
         System.err.println("MOSTRANDO ERROR: " + mensaje); // Log para consola
         if (excepcion != null) {
             excepcion.printStackTrace();
         }
         // Intentar mostrar Alert en el hilo de JavaFX
         javafx.application.Platform.runLater(() -> {
              Alert alert = new Alert(AlertType.ERROR);
              alert.setTitle("Error");
              alert.setHeaderText(null);
              alert.setContentText(mensaje + (excepcion != null ? "\nDetalles: " + excepcion.getMessage() : ""));
              alert.showAndWait();
         });
     }

    public static void main(String[] args) {
        launch(args);
    }
}
