package Modelo;

/**
 * Clase simple para representar una entrada en la tabla de puntuaciones.
 * 
 * @author Santiago
 * @author Juan
 * @version 0.3.3
 */
public class Puntuacion {
    private String nombre;
    private int puntuacion;

    /**
     * Contructor defectuoso
     */
    public Puntuacion() {}

    /**
     * Constuctor parametrizado
     * 
     * @param nombre Nombre del jugador
     * @param puntuacion Puntuacion del jugador
     */
    public Puntuacion(String nombre, int puntuacion) {
        this.nombre = nombre;
        this.puntuacion = puntuacion;
    }

    /**
     * Getter de Nombre
     * @return Nombre{String}
     */
    public String getNombre() {return nombre;}

    /**
     * Setter de nombre
     * @param nombre Nombre del jugador{String}
     */
    public void setNombre(String nombre) {this.nombre = nombre;}

    /**
     * Getter de Puntuacion
     * @return Puntuacion{int}
     */
    public int getPuntuacion() {return puntuacion;}

    /**
     * Setter de puntuacion
     * @param puntuacion {int}
     */
    public void setPuntuacion(int puntuacion) {this.puntuacion = puntuacion;}

    /**
     * Informacion completa
     * 
     * @return Cadena de texto del jugador y la puntuacion
     */
    @Override
    public String toString() {return nombre + ": " + puntuacion;}
}
