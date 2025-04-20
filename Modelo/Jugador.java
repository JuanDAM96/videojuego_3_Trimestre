package Modelo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Clase Jugador
 *
 * Representa al jugador dentro del juego. Guarda/Carga datos en formato binario
 * (.bin). Incluye la posición del jugador en el escenario.
 * 
 * @author Santiago
 * @author Juan
 * @version 0.2.0 (Guardado Binario y Posición)
 * @license GPL-3.0 license || ©2025
 */
public class Jugador{

    private static String nombre;
    private static String correo;
    private static int fila; // Posición actual del jugador
    private static int columna; // Posición actual del jugador

    /**
     * Constructor parametrizado
     *
     * @param nombre         Nombre del jugador.
     * @param correo         Correo del jugador.
     * @param filaInicial    Fila inicial del jugador.
     * @param columnaInicial Columna inicial del jugador.
     * @throws IllegalArgumentException Si el nombre o el correo están vacíos.
     */
    public Jugador(String nombre, String correo) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del jugador no puede estar vacío.");
        }
        if (correo == null || correo.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo del jugador no puede estar vacío.");
        }
        this.nombre = nombre.trim();
        this.correo = correo.trim();
    }

    // --- GETTERS ---

    public static void setFila(int fila) {
        Jugador.fila = fila;
    }

    public static void setColumna(int columna) {
        Jugador.columna = columna;
    }

    /**
     * Obtiene el nombre del jugador.
     * 
     * @return El nombre del jugador.
     */
    public static String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el correo del jugador.
     * 
     * @return El correo del jugador.
     */
    public static String getCorreo() {
        return correo;
    }

    /**
     * Obtiene la fila actual del jugador.
     * 
     * @return La fila actual del jugador.
     */
    public static int getFila() {
        return fila;
    }

    /**
     * Obtiene la columna actual del jugador.
     * 
     * @return La columna actual del jugador.
     */
    public static int getColumna() {
        return columna;
    }

    // --- SETTERS ---

    /**
     * Establece la posición del jugador en el escenario.
     * 
     * @param fila    Nueva fila del jugador.
     * @param columna Nueva columna del jugador.
     */
    public void setPosicion(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public static void setNombre(String nombre) {
        Jugador.nombre = nombre;
    }

    public static void setCorreo(String correo) {
        Jugador.correo = correo;
    }



}
