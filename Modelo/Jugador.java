package Modelo;
// TODO generar documentacion
import java.io.Serializable;
import java.util.Objects;

/**
 * Representa al jugador, incluyendo su identidad y estado en el juego.
 * Implementa Serializable para poder guardarlo en archivos binarios.
 */
public class Jugador implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String email;

    private int filaActual;
    private int columnaActual;
    private int puntos;

    public Jugador(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
        this.filaActual = 1; 
        this.columnaActual = 1; 
        this.puntos = 0;
    }


    public Jugador(String nombre, String email, int fila, int columna, int puntos) {
        this.nombre = nombre;
        this.email = email;
        this.filaActual = fila;
        this.columnaActual = columna;
        this.puntos = puntos;
    }


    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public int getFilaActual() { return filaActual; }
    public int getColumnaActual() { return columnaActual; }
    public int getPuntos() { return puntos; }


    public void setFilaActual(int fila) { this.filaActual = fila; }
    public void setColumnaActual(int columna) { this.columnaActual = columna; }
    public void setPuntos(int puntos) { this.puntos = puntos; }
    public void addPuntos(int cantidad) { this.puntos += cantidad; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jugador jugador = (Jugador) o;
        return Objects.equals(nombre, jugador.nombre);
    }

    @Override
    public int hashCode() {return Objects.hash(nombre);}

    @Override
    public String toString() {return "Jugador{" + "nombre='" + nombre + '\'' +", email='" + email + '\'' +", filaActual=" + filaActual +", columnaActual=" + columnaActual +", puntos=" + puntos +'}';}
}
