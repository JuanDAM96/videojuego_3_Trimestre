import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Clase App
 * 
 * La Clase Principal donde se llama y declara las clases y sus metodos, además
 * de ejecutarse el codigo
 * 
 * @author Santiago
 * @author Juan
 * @version 0.1
 * @License GPL-3.0 license || ©2025
 */
public class App {
    public static void main(String[] args) {
        String nombreArchivoConfiguracion = "configuracion.txt";

        File archivoConfiguracion = new File(nombreArchivoConfiguracion);

        if (archivoConfiguracion.exists()) {
            System.out.println(
                    "El archivo de configuración '" + nombreArchivoConfiguracion + "' ya existe. El programa termina.");
            return;
        } else {
            try {
                if (archivoConfiguracion.createNewFile()) {
                    System.out.println(
                            "El archivo de configuración '" + nombreArchivoConfiguracion + "' ha sido creado.");
                } else {
                    System.err.println(
                            "No se pudo crear el archivo de configuración '" + nombreArchivoConfiguracion + "'.");
                    return;
                }
            } catch (IOException e) {
                System.err.println("Error al crear el archivo de configuración: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }

        String[] directorios = { "escenarios", "jugadores", "partidas" };

        for (String nombreDirectorios : directorios) {
            Path rutaDirectorio = Paths.get(nombreDirectorios);
            if (!Files.exists(rutaDirectorio) || !Files.isDirectory(rutaDirectorio)) {
                try {
                    Files.createDirectories(rutaDirectorio);
                    System.out.println("Directorio '" + nombreDirectorios + "' creado.");
                } catch (IOException e) {
                    System.err.println("No se pudo crear el directorio '" + nombreDirectorios + "': " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("El directorio '" + nombreDirectorios + "' ya existe.");
            }
        }
        System.out.println("El programa ha finalizado su ejecución.");

    }
}
