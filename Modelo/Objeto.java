package Modelo;



import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;


/**
 * Representa un objeto dentro del escenario.
 * Cada objeto tiene un carácter representativo y una propiedad de bloqueo.
 */
public class Objeto {


    private static ArrayList<ImageView>;
    /** Indica si el objeto bloquea el paso. */
    private boolean bloqueo;

    @FXML
    private ImageView rec;

    /**
     * Constructor de la clase ObjetoEscenario.
     * 
     * @param objetoChar Carácter que representa el objeto.
     * @param bloqueo Indica si el objeto bloquea el paso (true) o no (false).
     */
    public Objeto(ImageView rec, boolean bloqueo) {
        this.rec = rec;
        this.bloqueo = bloqueo;
    }

    /**
     * Verifica si el objeto bloquea el paso.
     * 
     * @return true si el objeto bloquea, false en caso contrario.
     */
    public boolean isBloqueo() {
        return bloqueo;
    }

    /**
     * Establece si el objeto bloquea el paso.
     * 
     * @param bloqueo true para bloquear el paso, false para permitirlo.
     */
    public void setBloqueo(boolean bloqueo) {
        this.bloqueo = bloqueo;
    }

    public ImageView getRec() {
        return rec;
    }

    public void setRec(ImageView rec) {
        this.rec = rec;
    }

    public static void almacenImagen() {
        
    }


}
