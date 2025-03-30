package Modelo;

import java.io.BufferedReader; 
import java.io.BufferedWriter; 
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;


public class Jugador {

 
    private String nombre;
    private String correo;

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


    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }

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
                System.err.println("Error: Formato de archivo de jugador inválido o incompleto en '" + rutaArchivo + "'.");
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


    @Override
    public String toString() {
        return "Jugador [nombre=" + nombre + ", correo=" + correo + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jugador jugador = (Jugador) o;
        return Objects.equals(nombre, jugador.nombre) && Objects.equals(correo, jugador.correo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, correo);
    }
}
