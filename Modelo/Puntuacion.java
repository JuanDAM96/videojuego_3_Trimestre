package Modelo;

/**
 * Clase simple para representar una entrada en la tabla de puntuaciones.
 */
public class Puntuacion {
    private String nombre;
    private int puntuacion;

    // Constructor vacío necesario para PropertyValueFactory si se usa
    public Puntuacion() {
    }

    public Puntuacion(String nombre, int puntuacion) {
        this.nombre = nombre;
        this.puntuacion = puntuacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    @Override
    public String toString() {
        return nombre + ": " + puntuacion;
    }
}
