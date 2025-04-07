import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import Vista.VistaEscenario;
import Controlador.ControladorVistaEscenario;

/**
 * Clase App
 * Punto de entrada principal de la aplicación.
 * Realiza la configuración inicial (directorios) e inicia el controlador del
 * juego.
 * 
 * @author Santiago
 * @author Juan
 * @version 0.1.1 (Inicia Controlador con lógica Carga/Guardado)
 * @License GPL-3.0 license || ©2025
 */
public class App {

    // Constantes para configuración inicial
    private static final String NOMBRE_ARCHIVO_CONFIG = "configuracion.txt";
    // Asegurarse de que los directorios base para guardar/cargar existan
    private static final String[] DIRECTORIOS_NECESARIOS = { "escenarios", "jugadores", "partidas" };

    public static void main(String[] args) {

        System.out.println("Iniciando configuración del juego...");

        crearArchivoConfiguracionSiNoExiste();
        crearDirectoriosSiNoExisten();

        System.out.println("Configuración inicial completada.");
        System.out.println("-----------------------------------");

        // Crear instancias de Vista y Controlador
        try {
            VistaEscenario vista = new VistaEscenario();
            ControladorVistaEscenario controlador = new ControladorVistaEscenario(vista);

            // Iniciar el juego a través del controlador
            // El controlador se encargará de cargar o crear Jugador y Escenario
            controlador.iniciarOContinuarJuego();

        } catch (IllegalArgumentException e) {
            System.err.println("Error al inicializar el controlador/vista: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ocurrió un error inesperado al iniciar el juego: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\nEl programa ha finalizado su ejecución.");
        }
    }

    /**
     * Creacion de un archivo de configuracion si no existe
     */
    private static void crearArchivoConfiguracionSiNoExiste() {
        File archivoConfiguracion = new File(NOMBRE_ARCHIVO_CONFIG);
        if (!archivoConfiguracion.exists()) {
            try {
                if (archivoConfiguracion.createNewFile()) {
                    System.out.println("Archivo de configuración '" + NOMBRE_ARCHIVO_CONFIG + "' creado.");
                } else {
                    System.err.println("No se pudo crear el archivo de configuración '" + NOMBRE_ARCHIVO_CONFIG + "'.");
                }
            } catch (IOException e) {
                System.err.println("Error al crear el archivo de configuración: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Archivo de configuración '" + NOMBRE_ARCHIVO_CONFIG + "' ya existe.");
        }
    }

    /**
     * Creacion de un directorio si no existe
     */
    private static void crearDirectoriosSiNoExisten() {
        System.out.println("Verificando/creando directorios necesarios...");
        for (String nombreDirectorio : DIRECTORIOS_NECESARIOS) {
            Path rutaDirectorio = Paths.get(nombreDirectorio);
            if (!Files.exists(rutaDirectorio)) {
                try {
                    Files.createDirectories(rutaDirectorio);
                    System.out.println(
                            "Directorio '" + nombreDirectorio + "' creado en: " + rutaDirectorio.toAbsolutePath());
                } catch (IOException e) {
                    System.err.println("No se pudo crear el directorio '" + nombreDirectorio + "': " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                if (!Files.isDirectory(rutaDirectorio)) {
                    System.err.println("Error: La ruta '" + nombreDirectorio + "' existe pero no es un directorio.");
                } else {
                    System.out.println("Directorio '" + nombreDirectorio + "' ya existe.");
                }
            }
        }
    }
}