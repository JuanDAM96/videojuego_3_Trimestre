package Vista;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class PantallaDeInicio {
    @FXML
    public Button botonIni;

    @FXML
    Timeline tabla = new Timeline(new KeyFrame(Duration.millis(100), e -> {
        tabla();
    }));
    
/*     bucle.setCycleCount(Timeline.INDEFINITE); // Repetir infinitamente
    bucle.play(); */
    
    
    public static void tabla() {} // TODO Hacer la tabla cuando pasemos a SQLite

}
