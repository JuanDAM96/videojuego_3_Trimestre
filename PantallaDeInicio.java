

import Modelo.Puntuacion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.util.List;
import Controlador.SQLite;

/**
 * Clase Pantalla de inicio
 * Pantalla inicial que se muestra al jugador
 * 
 * @author Santiago
 * @author Juan
 * @version 0.3.3
 */
public class PantallaDeInicio {

    @FXML private TableView<Puntuacion> tablaPuntuaciones;
    @FXML private TableColumn<Puntuacion, String> columnaNombre;
    @FXML private TableColumn<Puntuacion, Integer> columnaPuntuacion;

    @FXML private Button btnIniciar;

    private ObservableList<Puntuacion> listaPuntuaciones = FXCollections.observableArrayList();

    /**
     * Metodo fxml para inicializar la escena
     */
    @FXML
    public void initialize() {
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaPuntuacion.setCellValueFactory(new PropertyValueFactory<>("puntuacion"));

        cargarPuntuaciones();

        tablaPuntuaciones.setItems(listaPuntuaciones);
    }

    /**
     * Carga las puntuaciones en la tabla
     */
    private void cargarPuntuaciones() {
        List<Puntuacion> topPuntuaciones = SQLite.obtenerMejoresPuntuaciones();
        if (topPuntuaciones != null) {
            listaPuntuaciones.setAll(topPuntuaciones);
            System.out.println("Puntuaciones cargadas en la tabla.");
        } else System.err.println("No se pudieron cargar las puntuaciones.");
    }

    /**
     * Boton para iniciar partida
     * @throws IOException No encuentra el archivo necesario
     */
    @FXML
    private void iniciarPartida() throws IOException {App.mostrarPantallaDeJuego();}
}
