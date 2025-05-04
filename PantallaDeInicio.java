

// Importar Puntuacion en lugar de Jugador para la tabla
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
import Controlador.SQLite; // Para obtener puntuaciones

public class PantallaDeInicio {

    // Inyectar la TableView y sus columnas desde el FXML
    @FXML private TableView<Puntuacion> tablaPuntuaciones;
    @FXML private TableColumn<Puntuacion, String> columnaNombre;
    @FXML private TableColumn<Puntuacion, Integer> columnaPuntuacion;

    @FXML private Button btnIniciar;

    private ObservableList<Puntuacion> listaPuntuaciones = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar las columnas para que usen las propiedades de Puntuacion
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaPuntuacion.setCellValueFactory(new PropertyValueFactory<>("puntuacion"));

        // Cargar las puntuaciones desde SQLite
        cargarPuntuaciones();

        // Asignar la lista observable a la tabla
        tablaPuntuaciones.setItems(listaPuntuaciones);
    }

    // Carga las 10 mejores puntuaciones desde la base de datos
    private void cargarPuntuaciones() {
        List<Puntuacion> topPuntuaciones = SQLite.obtenerMejoresPuntuaciones();
        if (topPuntuaciones != null) {
            listaPuntuaciones.setAll(topPuntuaciones);
            System.out.println("Puntuaciones cargadas en la tabla.");
        } else {
            System.err.println("No se pudieron cargar las puntuaciones.");
            // Opcional: Mostrar mensaje en la UI
        }
    }

    // Manejador del botón "Iniciar Partida"
    @FXML
    private void iniciarPartida() throws IOException {
        // Llama al método en App para cambiar a la pantalla de juego
        App.mostrarPantallaDeJuego();
    }
}
