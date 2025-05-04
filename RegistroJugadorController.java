

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.IOException;

// --- IMPORT CORREGIDO ---
// Importa la clase App usando su paquete completo

// --- FIN IMPORT CORREGIDO ---

import Controlador.Sesion;
import Modelo.Jugador;

public class RegistroJugadorController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtEmail;
    @FXML private Button btnRegistrar;

    private String nombreInicial;

    public void setNombreInicial(String nombre) {
        this.nombreInicial = nombre;
        if (txtNombre != null) {
            txtNombre.setText(nombre);
        }
    }

    @FXML
    public void initialize() {
        if (nombreInicial != null) {
            txtNombre.setText(nombreInicial);
        }
         System.out.println("Inicializando RegistroJugadorController..."); // Mensaje de depuración
    }

    @FXML
    private void registrarJugador() {
        System.out.println("Método registrarJugador() llamado."); // Mensaje de depuración
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("Error de Registro", "El nombre no puede estar vacío.");
            return;
        }
        // Añadir más validaciones si es necesario

        Jugador nuevoJugador = new Jugador(nombre, email);

        boolean guardado = Sesion.guardarJugador(nuevoJugador);

        if (guardado) {
            System.out.println("Jugador '" + nombre + "' registrado y guardado.");
            // --- Usa la clase App importada ---
            App.setJugadorActual(nuevoJugador);
            App.mostrarPantallaDeInicio();
            // --- Fin uso App ---
        } else {
            mostrarAlerta("Error de Registro", "No se pudo guardar el jugador. Revisa la consola.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
