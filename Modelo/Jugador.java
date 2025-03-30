package Modelo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Clase Jugador
 * 
 * La Clase Jugador Representa al jugador dentro del juego, Guarda/Carga datos
 * en formato de texto (.txt).
 * 
 * @author Santiago
 * @author Juan
 * @version 0.1.1
 * @License GPL-3.0 license || ©2025
 */
public class Jugador {
    private String nombre;
    private String correo;

    /**
     * Constructor parametrizado
     * 
     * @param nombre Nombre del jugador
     * @param correo Correo del jugador
     **/
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

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    /**
     * Guarda los datos del jugador actual en el archivo de texto especificado.
     * Formato: una propiedad por línea (nombre=valor).
     * 
     * @param rutaArchivo La ruta completa del archivo .txt donde guardar los datos.
     * @return true si se guardó correctamente, false en caso de error.
     **/
    public boolean guardarEnArchivo(String rutaArchivo) {
        Path ruta = Paths.get(rutaArchivo);
        try {
            Path directorioPadre = ruta.getParent();
            if (directorioPadre != null && !Files.exists(directorioPadre)) {
                Files.createDirectories(directorioPadre);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(ruta)) {
                writer.write("nombre=" + this.nombre);
                writer.newLine();
                writer.write("correo=" + this.correo);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar el jugador en '" + rutaArchivo + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Carga los datos de un jugador desde el archivo de texto especificado.
     * Espera el formato: una propiedad por línea (nombre=valor).
     * 
     * @param rutaArchivo La ruta completa del archivo .txt desde donde cargar los
     *                    datos.
     * @return Una instancia de Jugador si la carga fue exitosa, null en caso de
     *         error o archivo no encontrado.
     **/
    public static Jugador cargarDesdeArchivo(String rutaArchivo) {
        Path ruta = Paths.get(rutaArchivo);
        if (!Files.exists(ruta) || !Files.isReadable(ruta)) {
            return null;
        }

        String nombre = null;
        String correo = null;

        try (BufferedReader reader = Files.newBufferedReader(ruta)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.startsWith("nombre=")) {
                    nombre = linea.substring("nombre=".length());
                } else if (linea.startsWith("correo=")) {
                    correo = linea.substring("correo=".length());
                }

            }

            if (nombre != null && correo != null) {
                return new Jugador(nombre, correo);
            } else {
                System.err.println(
                        "Error: Formato de archivo de jugador inválido o incompleto en '" + rutaArchivo + "'.");
                return null;
            }

        } catch (IOException e) {
            System.err.println("Error al cargar el jugador desde '" + rutaArchivo + "': " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (IllegalArgumentException e) {
            System.err.println("Error al crear jugador desde archivo '" + rutaArchivo + "': " + e.getMessage());
            return null;
        }
    }

    /**
     * Pone en String toda la informacion del jugador
     * 
     * @return String. devuelve toda la informacion del jugador
     */
    @Override
    public String toString() {
        return "Jugador [nombre=" + nombre + ", correo=" + correo + "]";
    }

    /**
     * Comparador de objetos. Compara todos los parametros que se le pasan.
     * 
     * @param objeto Objeto que se le pasa como parametro
     * @return boolean. Confirma con un true/false si son iguales
     **/
    @Override
    public boolean equals(Object objeto) {
        if (this == objeto)
            return true;
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        Jugador jugador = (Jugador) objeto;
        return Objects.equals(nombre, jugador.nombre) && Objects.equals(correo, jugador.correo);
    }

    /**
     * Numero que devuelve el hash
     * 
     * @return int. Numero del hash a traves de su numero y correo
     */
    @Override
    public int hashCode() {
        return Objects.hash(nombre, correo);
    }
}
