// TODO generar documentacio

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
import Controlador.SQLite;
import Controlador.Sesion;
import Controlador.Control;
import Controlador.TileManager;
import Modelo.Jugador;
import Modelo.Puntuacion;
import Modelo.Escenario;

public class App extends Application {

    private static Stage primeraEsc;
    private static String nombreJugadorActual = null;
    private static Jugador jugadorActual = null;

    private static final String DIRECTORIO_ESCENARIOS = "escenarios";
    private static final String DIRECTORIO_JUGADORES = "jugadores";
    private static final String DIRECTORIO_PARTIDAS = "partidas";
    private static final String[] DIRECTORIOS_NECESARIOS = {DIRECTORIO_ESCENARIOS, DIRECTORIO_JUGADORES, DIRECTORIO_PARTIDAS};

    @Override
    public void init() throws Exception {
        super.init();
        System.out.println("Ejecutando App.init()...");
        crearDirectoriosSiNoExisten();
        SQLite.inicializarBaseDatos();
        Sesion.crearEscenarioPorDefectoSiNoExiste();
        System.out.println("App.init() completado.");
    }

    @Override
    public void start(Stage stage) {
        primeraEsc = stage;
        primeraEsc.setTitle("Videojuego");
        System.out.println("Ejecutando App.start()...");

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
                mostrarPantallaRegistro();
            }
        } else {
            System.out.println("Inicio cancelado.");
            mostrarPantallaDeInicio();
        }

        primeraEsc.setOnCloseRequest(_ -> {
            Scene escesnaActual = primeraEsc.getScene();
            if (escesnaActual != null && escesnaActual.getUserData() instanceof PantallaDeJuego)
                ((PantallaDeJuego) escesnaActual.getUserData()).paraJuego();
            System.out.println("Cerrando aplicación.");
        });
    }

    public static void mostrarPantallaDeInicio() {
        String rutaFXML = "/Escenas/PantallaDeInicio.fxml";
        try {
            System.out.println("Cargando " + rutaFXML + "...");
            URL fxmlUrl = App.class.getResource(rutaFXML);
            if (fxmlUrl == null) fxmlUrl = App.class.getResource("Escenas/PantallaDeInicio.fxml");

            if (fxmlUrl == null) {throw new IOException("FXML no encontrado: " + rutaFXML);}

            FXMLLoader cargar = new FXMLLoader(fxmlUrl);
            Parent raiz = cargar.load();
            Scene escena = new Scene(raiz);
            primeraEsc.setTitle("Mejores Puntuaciones");
            primeraEsc.setScene(escena);
            System.out.println("Mostrando PantallaDeInicio.");
            primeraEsc.show();
        } catch (Exception e) {mostrarError("Error al cargar la pantalla de inicio", e);}
    }

    public static void mostrarPantallaRegistro() {
        String rutaFXML = "/Escenas/RegistroJugador.fxml";
        try {
            System.out.println("Intentando cargar FXML: " + rutaFXML);
            URL fxmlUrl = App.class.getResource(rutaFXML);
            if (fxmlUrl == null) fxmlUrl = App.class.getResource("Escenas/RegistroJugador.fxml");
            if (fxmlUrl == null) {throw new IOException("No se pudo encontrar FXML: " + rutaFXML);}

            System.out.println("URL del FXML encontrada: " + fxmlUrl);
            FXMLLoader cargar = new FXMLLoader(fxmlUrl);
            Parent raiz = cargar.load();
            System.out.println("FXML de Registro cargado.");

            RegistroJugadorController controlador = cargar.getController();
            if (controlador != null) {
                System.out.println("Controlador RegistroJugadorController obtenido.");
                controlador.setNombreInicial(nombreJugadorActual);
            } else
                System.err.println("Advertencia: Controlador RegistroJugadorController es null.");

            Scene escena = new Scene(raiz);
            primeraEsc.setTitle("Registrar Nuevo Jugador");
            primeraEsc.setScene(escena);
            System.out.println("Escena de Registro establecida.");
            primeraEsc.show();

        } catch (Exception e) {mostrarError("Error al mostrar la pantalla de registro", e);}
    }

    public static void mostrarPantallaDeJuego() {
        String rutaFXML = "/Escenas/PantallaDeJuego.fxml";
        try {
            System.out.println("Cargando " + rutaFXML + "...");
            URL fxmlUrl = App.class.getResource(rutaFXML);
            if (fxmlUrl == null) {throw new IOException("FXML no encontrado: " + rutaFXML);}

            FXMLLoader cargar = new FXMLLoader(fxmlUrl);
            Parent raiz = cargar.load();
            PantallaDeJuego control = cargar.getController();

            if (control == null) {
                System.err.println("Error crítico: Controlador de PantallaDeJuego no encontrado.");
                mostrarError("Error interno al iniciar el juego (controlador nulo).", null);
                return;
            }
            System.out.println("Controlador PantallaDeJuego obtenido.");

            Scene escena = new Scene(raiz);

            primeraEsc.setScene(escena);
            primeraEsc.setTitle("Juego - " + (jugadorActual != null ? jugadorActual.getNombre() : "Invitado"));
            System.out.println("Escena de Juego establecida.");

            control.setCapInp(escena);
            escena.setUserData(control);

            System.out.println("Mostrando PantallaDeJuego.");

        } catch (Exception e) {mostrarError("Error al cargar la pantalla de juego", e);}
    }

    public static String getNombreJugadorActual() {return nombreJugadorActual;}

    public static Jugador getJugadorActual() {return jugadorActual;}

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
                } else if (!Files.isDirectory(rutaDirectorio))
                    System.err.println("Error: La ruta '" + nombreDirectorio + "' existe pero no es un directorio.");
            } catch (IOException e) {
                System.err.println("No se pudo crear o verificar el directorio '" + nombreDirectorio + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void mostrarError(String mensaje, Exception excepcion) {
        System.err.println("MOSTRANDO ERROR: " + mensaje);

        if (excepcion != null) excepcion.printStackTrace();

        javafx.application.Platform.runLater(() -> {
            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText(null);
            alerta.setContentText(mensaje + (excepcion != null ? "\nDetalles: " + excepcion.getClass().getSimpleName() + " - " + excepcion.getMessage() : ""));
            alerta.showAndWait();
        });
    }

    public static void main(String[] args) {launch(args);}
}