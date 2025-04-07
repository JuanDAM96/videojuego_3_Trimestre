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
import java.util.Objects;

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
public class Jugador implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String correo;
    private int fila; // Posición actual del jugador
    private int columna; // Posición actual del jugador

    /**
     * Constructor parametrizado
     *
     * @param nombre         Nombre del jugador.
     * @param correo         Correo del jugador.
     * @param filaInicial    Fila inicial del jugador.
     * @param columnaInicial Columna inicial del jugador.
     * @throws IllegalArgumentException Si el nombre o el correo están vacíos.
     */
    public Jugador(String nombre, String correo, int filaInicial, int columnaInicial) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del jugador no puede estar vacío.");
        }
        if (correo == null || correo.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo del jugador no puede estar vacío.");
        }
        this.nombre = nombre.trim();
        this.correo = correo.trim();
        this.fila = filaInicial >= 0 ? filaInicial : 0;
        this.columna = columnaInicial >= 0 ? columnaInicial : 0;
    }

    // --- GETTERS ---

    /**
     * Obtiene el nombre del jugador.
     * 
     * @return El nombre del jugador.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el correo del jugador.
     * 
     * @return El correo del jugador.
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Obtiene la fila actual del jugador.
     * 
     * @return La fila actual del jugador.
     */
    public int getFila() {
        return fila;
    }

    /**
     * Obtiene la columna actual del jugador.
     * 
     * @return La columna actual del jugador.
     */
    public int getColumna() {
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

    // --- MÉTODOS PARA GUARDAR Y CARGAR ---

    /**
     * Guarda los datos del jugador actual en un archivo binario.
     * 
     * @param rutaArchivo La ruta completa del archivo .bin donde guardar los datos.
     * @return true si se guardó correctamente, false en caso de error.
     */
    public boolean guardarEnArchivoBinario(String rutaArchivo) {
        Path ruta = Paths.get(rutaArchivo);
        try {
            Path directorioPadre = ruta.getParent();
            if (directorioPadre != null && !Files.exists(directorioPadre)) {
                Files.createDirectories(directorioPadre);
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta.toFile()))) {
                oos.writeObject(this);
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar el jugador en archivo binario '" + rutaArchivo + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Carga los datos de un jugador desde un archivo binario.
     * 
     * @param rutaArchivo La ruta completa del archivo .bin desde donde cargar los
     *                    datos.
     * @return Una instancia de Jugador si la carga fue exitosa, null en caso de
     *         error.
     */
    public static Jugador cargarDesdeArchivoBinario(String rutaArchivo) {
        Path ruta = Paths.get(rutaArchivo);
        if (!Files.exists(ruta) || !Files.isReadable(ruta)) {
            System.out.println("Archivo binario de jugador no encontrado o sin permisos: " + rutaArchivo);
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ruta.toFile()))) {
            Object obj = ois.readObject();
            if (obj instanceof Jugador) {
                return (Jugador) obj;
            } else {
                System.err.println("Error: El archivo binario '" + rutaArchivo + "' no contiene un objeto Jugador válido.");
                return null;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar el jugador desde archivo binario '" + rutaArchivo + "': " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // --- MÉTODOS OVERRIDE ---

    /**
     * Devuelve una representación en String de la información del jugador.
     * 
     * @return Una cadena con la información del jugador.
     */
    @Override
    public String toString() {
        return "Jugador [nombre=" + nombre + ", correo=" + correo + ", fila=" + fila + ", columna=" + columna + "]";
    }

    /**
     * Compara este jugador con otro objeto.
     * 
     * @param objeto Objeto a comparar.
     * @return true si los objetos son iguales, false en caso contrario.
     */
    @Override
    public boolean equals(Object objeto) {
        if (this == objeto)
            return true;
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        Jugador jugador = (Jugador) objeto;
        return fila == jugador.fila &&
               columna == jugador.columna &&
               Objects.equals(nombre, jugador.nombre) &&
               Objects.equals(correo, jugador.correo);
    }

    /**
     * Calcula el código hash del jugador.
     * 
     * @return Un entero que representa el código hash del jugador.
     */
    @Override
    public int hashCode() {
        return Objects.hash(nombre, correo, fila, columna);
    }
}
