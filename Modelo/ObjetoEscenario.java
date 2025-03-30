package Modelo;

public class ObjetoEscenario {

    private boolean bloqueo;
    private char objetoChar;

    public ObjetoEscenario(char objetoChar, boolean bloqueo) {
        this.objetoChar = objetoChar;
        this.bloqueo = bloqueo;
    }

    public boolean isBloqueo() {
        return bloqueo;
    }

    public void setBloqueo(boolean bloqueo) {
        this.bloqueo = bloqueo;
    }

    public char getObjetoChar() {
        return objetoChar;
    }

    public void setObjetoChar(char objetoChar) {
        this.objetoChar = objetoChar;
    }

    @Override
    public String toString() {
        return String.valueOf(objetoChar);
    }
}