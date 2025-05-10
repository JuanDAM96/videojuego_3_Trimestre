package Modelo;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa al jugador, incluyendo su identidad y estado en el juego.
 * Implementa Serializable para poder guardarlo en archivos binarios.
 * 
 * @author Santiago
 * @author Juan
 * @version 0.3.3
 */
public class Jugador implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String email;

    private int filaActual;
    private int columnaActual;
    private int puntos;

    /**
     * Constructor parametrizado para registrar el jugador
     * @param nombre del jugador
     * @param email deñ jugador
     */
    public Jugador(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
        this.filaActual = 1; 
        this.columnaActual = 1; 
        this.puntos = 0;
    }

    /**
     * Constructor parametrizado para instaciar el jugador en el juego
     * @param nombre del jugador
     * @param email del jugador
     * @param fila Posicion que se encuentra (X)
     * @param columna Posicion que se encuentra (Y)
     * @param puntos Puntos adquiridos
     */
    public Jugador(String nombre, String email, int fila, int columna, int puntos) {
        this.nombre = nombre;
        this.email = email;
        this.filaActual = fila;
        this.columnaActual = columna;
        this.puntos = puntos;
    }

    /**
     * Getter del nombre
     * @return el nombre como String
     */
    public String getNombre() { return nombre; }

    /**
     * Getter de Email
     * @return email como String
     */
    public String getEmail() { return email; }

    /**
     * Getter de la fila
     * @return fila(X) que se encuentra el jugador como int
     */
    public int getFilaActual() { return filaActual; }

    /**
     * Getter de la columna
     * @return columna(Y) que se encuentra el jugador como int
     */
    public int getColumnaActual() { return columnaActual; }

    /**
     * Getter de puntos
     * @return Puntos acumulados por el jugador como int
     */
    public int getPuntos() { return puntos; }

    /**
     * Setter de Fila
     * @param fila fila(X) que se encuentra el jugador como int
     */
    public void setFilaActual(int fila) { this.filaActual = fila; }

    /**
     * Setter de la columna
     * @param columna columna(Y) que se encuentra el jugador como int
     */
    public void setColumnaActual(int columna) { this.columnaActual = columna; }

    /**
     * Setter introduce
     * @param puntos puntos al valor
     */
    public void setPuntos(int puntos) { this.puntos = puntos; }

    /**
     * Añade una cantidad de puntos
     * @param cantidad puntos acumuludados
     */
    public void addPuntos(int cantidad) { this.puntos += cantidad; }

    /**
     * Metodo sobreescrito para comparar objetos
     * @param o Objeto generico
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jugador jugador = (Jugador) o;
        return Objects.equals(nombre, jugador.nombre);
    }

    /**
     * Metodo sobreescrito para el nombre
     * @return numero de identificatorio
     */
    @Override
    public int hashCode() {return Objects.hash(nombre);}

    /**
     * Metodo sobreescrito para pasar un String
     * @return Devuelve una cadena de texto con toda la informacion
     */
    @Override
    public String toString() {return "Jugador{" + "nombre='" + nombre + '\'' +", email='" + email + '\'' +", filaActual=" + filaActual +", columnaActual=" + columnaActual +", puntos=" + puntos +'}';}
}