package Modelo;

import java.io.Serializable;
import java.util.Objects; // Importar para equals y hashCode

/**
 * Representa al jugador, incluyendo su identidad y estado en el juego.
 * Implementa Serializable para poder guardarlo en archivos binarios.
 */
public class Jugador implements Serializable {
    // Necesario para la serialización, cámbialo si modificas campos incompatibles
    private static final long serialVersionUID = 1L;

    // --- Identidad del Jugador (guardado en /jugadores) ---
    private String nombre;
    private String email; // Opcional, según requerimiento

    // --- Estado en la Partida (guardado en /partidas) ---
    private int filaActual;
    private int columnaActual;
    private int puntos;
    // Podrías añadir más estado: nivelActual, inventario, etc.

    // Constructor para nuevo jugador (requiere nombre y email)
    public Jugador(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
        // Valores iniciales por defecto para una nueva partida
        this.filaActual = 1; // Posición inicial por defecto (ajustar)
        this.columnaActual = 1; // Posición inicial por defecto (ajustar)
        this.puntos = 0;
    }

    // Constructor completo (útil para cargar desde archivo)
    public Jugador(String nombre, String email, int fila, int columna, int puntos) {
        this.nombre = nombre;
        this.email = email;
        this.filaActual = fila;
        this.columnaActual = columna;
        this.puntos = puntos;
    }

    // --- Getters ---
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public int getFilaActual() { return filaActual; }
    public int getColumnaActual() { return columnaActual; }
    public int getPuntos() { return puntos; }

    // --- Setters (para estado de partida) ---
    public void setFilaActual(int fila) { this.filaActual = fila; }
    public void setColumnaActual(int columna) { this.columnaActual = columna; }
    public void setPuntos(int puntos) { this.puntos = puntos; }
    public void addPuntos(int cantidad) { this.puntos += cantidad; } // Método útil

    // --- Setters (para identidad - usualmente no se cambian después de crear) ---
    // public void setNombre(String nombre) { this.nombre = nombre; }
    // public void setEmail(String email) { this.email = email; }


    // --- equals y hashCode basados en el nombre (identificador único) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jugador jugador = (Jugador) o;
        return Objects.equals(nombre, jugador.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }

    @Override
    public String toString() {
        return "Jugador{" +
               "nombre='" + nombre + '\'' +
               ", email='" + email + '\'' +
               ", filaActual=" + filaActual +
               ", columnaActual=" + columnaActual +
               ", puntos=" + puntos +
               '}';
    }
}
