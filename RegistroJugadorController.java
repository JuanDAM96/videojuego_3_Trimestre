
// TODO generar documentacion
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.IOException;
import Controlador.Sesion;
import Modelo.Jugador;

public class RegistroJugadorController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtEmail;
    @FXML private Button btnRegistrar;

    private String nombreInicial;

    public void setNombreInicial(String nombre) {
        this.nombreInicial = nombre;
        if (txtNombre != null) txtNombre.setText(nombre);
    }

    @FXML
    public void initialize() {
        if (nombreInicial != null) txtNombre.setText(nombreInicial);
        System.out.println("Inicializando RegistroJugadorController...");
    }

    @FXML
    private void registrarJugador() {
        System.out.println("Método registrarJugador() llamado.");
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("Error de Registro", "El nombre no puede estar vacío.");
            return;
        }

        Jugador nuevoJugador = new Jugador(nombre, email);

        boolean guardado = Sesion.guardarJugador(nuevoJugador);

        if (guardado) {
            System.out.println("Jugador '" + nombre + "' registrado y guardado.");
            App.setJugadorActual(nuevoJugador);
            App.mostrarPantallaDeInicio();
        } else mostrarAlerta("Error de Registro", "No se pudo guardar el jugador. Revisa la consola.");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}