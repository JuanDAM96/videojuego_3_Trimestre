package Vista;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.util.Duration;

public class PantallaDeInicio {
    @FXML
    public Button botonIni;

    @FXML
    public static void cambio(){
        botonIni.setOnAction(e ->{
            App.pdj(PantallaDeJuego);
        })
    }

    @FXML
    TableView tabla;
    
    @FXML
    public static void tabla() {
        // TODO Hacer un resultset para cambiar los datos cuando el jugador cambie a las pantalla de inicio
    } // TODO Hacer la tabla cuando pasemos a SQLite

}
