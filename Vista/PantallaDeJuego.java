package Vista;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

public class PantallaDeJuego {
    @FXML
    GridPane gridPane;
    @FXML
    TextArea mensaje;
    @FXML
    public static void pintarEscenario(){
        gridPane.add(image,1,1);
    }
    @FXML
    public static void pintarJugador(int x, int y){
        gridPane.add(image,1,1);
    }

    public static void mensajeLateral(String mensaje) {
        
    }



}
