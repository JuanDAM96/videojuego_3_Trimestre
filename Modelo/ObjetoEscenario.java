package Modelo;

/**
 * Representa un objeto dentro del escenario.
 * Cada objeto tiene un carácter representativo y una propiedad de bloqueo.
 */
public class ObjetoEscenario {

    /** Indica si el objeto bloquea el paso. */
    private boolean bloqueo;
    /** Carácter que representa al objeto en el escenario. */
    private char objetoChar;

    /**
     * Constructor de la clase ObjetoEscenario.
     * 
     * @param objetoChar Carácter que representa el objeto.
     * @param bloqueo Indica si el objeto bloquea el paso (true) o no (false).
     */
    public ObjetoEscenario(char objetoChar, boolean bloqueo) {
        this.objetoChar = objetoChar;
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

    /**
     * Obtiene el carácter que representa el objeto.
     * 
     * @return Carácter representativo del objeto.
     */
    public char getObjetoChar() {
        return objetoChar;
    }

    /**
     * Establece el carácter que representa el objeto.
     * 
     * @param objetoChar Carácter que representará el objeto en el escenario.
     */
    public void setObjetoChar(char objetoChar) {
        this.objetoChar = objetoChar;
    }

    /**
     * Devuelve una representación en cadena del objeto.
     * 
     * @return Carácter representativo del objeto como cadena.
     */
    @Override
    public String toString() {
        return String.valueOf(objetoChar);
    }
}
