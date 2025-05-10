

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.IOException;
import Controlador.Sesion;
import Modelo.Jugador;

/**
 * Clase Registro
 * Se encarga de registrar nuevos jugadores
 * 
 * @author Santiago
 * @author Juan
 * @version 0.3.3
 */
public class RegistroJugadorController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtEmail;
    @FXML private Button btnRegistrar;

    private String nombreInicial;

    /**
     * Introduce el nombre del jugador
     * @param nombre
     */
    public void setNombreInicial(String nombre) {
        this.nombreInicial = nombre;
        if (txtNombre != null) txtNombre.setText(nombre);
    }

    /**
     * Esto solo inicializa la clase por asi decirlo
     */
    @FXML
    public void initialize() {
        if (nombreInicial != null) txtNombre.setText(nombreInicial);
        System.out.println("Inicializando RegistroJugadorController...");
    }

    /**
     * Registra al nuevo jugador
     */
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

    /**
     * Muestra la Alerta visualmente
     * 
     * @param titulo Titulo de la alerta
     * @param mensaje Mensaje de la alerta
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}