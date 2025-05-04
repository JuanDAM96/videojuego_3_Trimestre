package Controlador;

import Modelo.Escenario;
import Modelo.Jugador;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gestiona la carga y guardado de datos del juego (Escenarios, Jugadores, Partidas).
 */
public class Sesion {

    private static final String DIRECTORIO_ESCENARIOS = "escenarios";
    private static final String DIRECTORIO_JUGADORES = "jugadores";
    private static final String DIRECTORIO_PARTIDAS = "partidas"; // Para guardar estado de juego

    /**
     * Carga un escenario desde un archivo de texto con formato específico.
     * Formato esperado:
     * Linea 1: FilasXColumnas (ej. "30X30")
     * Lineas siguientes: Descripción de tiles por fila (ej. "10E 5O 15E")
     * @param rutaArchivo Ruta completa al archivo del escenario.
     * @return Un objeto Escenario, o null si hay error.
     */
    public static Escenario cargarEscenario(String rutaArchivo) {
        List<String> lineasMapa = new ArrayList<>();
        int filas = 0;
        int columnas = 0;

        try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
            // Leer dimensiones
            String primeraLinea = lector.readLine();
            if (primeraLinea == null || !primeraLinea.matches("\\d+X\\d+")) {
                System.err.println("Error: Formato incorrecto de dimensiones en la primera línea de " + rutaArchivo);
                return null;
            }
            String[] dims = primeraLinea.split("X");
            filas = Integer.parseInt(dims[0]);
            columnas = Integer.parseInt(dims[1]);

            if (filas <= 0 || columnas <= 0) {
                 System.err.println("Error: Dimensiones inválidas en " + rutaArchivo);
                 return null;
            }

            // Leer y decodificar el resto de líneas (RLE: Run-Length Encoding)
            String lineaRLE;
            Pattern pattern = Pattern.compile("(\\d+)([EO])"); // Patrón para "10E", "5O", etc.

            for (int i = 0; i < filas; i++) {
                lineaRLE = lector.readLine();
                if (lineaRLE == null) {
                    System.err.println("Error: Faltan líneas en el archivo de escenario: " + rutaArchivo);
                    return null; // Faltan filas
                }

                StringBuilder filaDecodificada = new StringBuilder(columnas);
                Matcher matcher = pattern.matcher(lineaRLE.trim());
                int colActual = 0;
                while (matcher.find()) {
                    int cantidad = Integer.parseInt(matcher.group(1));
                    char tipo = matcher.group(2).charAt(0); // 'E' o 'O'
                    for (int k = 0; k < cantidad && colActual < columnas; k++) {
                        filaDecodificada.append(tipo);
                        colActual++;
                    }
                }

                // Verificar si la fila decodificada tiene la longitud correcta
                if (filaDecodificada.length() != columnas) {
                     System.err.println("Error: La fila " + (i+1) + " decodificada no coincide con el ancho (" + columnas + ") en " + rutaArchivo);
                     System.err.println("Línea RLE: " + lineaRLE);
                     System.err.println("Decodificada ("+filaDecodificada.length()+"): " + filaDecodificada);
                     // Rellenar con espacios si es más corta (o manejar como error)
                     while(filaDecodificada.length() < columnas) {
                         filaDecodificada.append('E'); // Asumir espacio por defecto
                     }
                     // Truncar si es más larga
                     if (filaDecodificada.length() > columnas) {
                         filaDecodificada.setLength(columnas);
                     }
                     // return null; // Opcional: ser estricto con el formato
                }
                lineasMapa.add(filaDecodificada.toString());
            }

            // Crear el escenario con las filas decodificadas
            System.out.println("Escenario cargado desde: " + rutaArchivo + " (" + filas + "x" + columnas + ")");
            // Usar el constructor que convierte List<String> a char[][]
            return new Escenario(filas, columnas, lineasMapa);

        } catch (IOException e) {
            System.err.println("Error de E/S al cargar el escenario: " + e.getMessage());
            return null;
        } catch (NumberFormatException e) {
             System.err.println("Error al parsear números en el archivo de escenario: " + e.getMessage());
             return null;
        } catch (Exception e) { // Captura genérica para otros errores inesperados
            System.err.println("Error inesperado al cargar el escenario: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Guarda los datos de identidad de un jugador (nombre, email) en un archivo binario.
     * El archivo se guarda en la carpeta "jugadores" con el nombre del jugador.
     * @param jugador El objeto Jugador a guardar.
     * @return true si se guardó correctamente, false en caso contrario.
     */
    public static boolean guardarJugador(Jugador jugador) {
        if (jugador == null || jugador.getNombre() == null || jugador.getNombre().trim().isEmpty()) {
            System.err.println("Error: No se puede guardar un jugador nulo o sin nombre.");
            return false;
        }
        // Crear el directorio si no existe
        try {
            Files.createDirectories(Paths.get(DIRECTORIO_JUGADORES));
        } catch (IOException e) {
             System.err.println("Error al crear el directorio de jugadores: " + e.getMessage());
             return false;
        }

        String nombreArchivo = Paths.get(DIRECTORIO_JUGADORES, jugador.getNombre() + ".dat").toString();
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(nombreArchivo))) {
             // Guardamos solo los datos de identidad (o el objeto entero si es más simple)
             // Para guardar solo nombre/email, podrías crear un objeto específico o escribir los Strings.
             // Guardando el objeto entero es más fácil si Jugador es Serializable.
            salida.writeObject(jugador);
            System.out.println("Datos del jugador guardados en: " + nombreArchivo);
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar los datos del jugador '" + jugador.getNombre() + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

     /**
     * Carga los datos de identidad de un jugador desde un archivo binario.
     * Busca el archivo en la carpeta "jugadores".
     * @param nombre El nombre del jugador a cargar.
     * @return El objeto Jugador cargado, o null si no se encuentra o hay un error.
     */
    public static Jugador cargarJugadorPorNombre(String nombre) {
         if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }
        String nombreArchivo = Paths.get(DIRECTORIO_JUGADORES, nombre + ".dat").toString();
        File archivo = new File(nombreArchivo);

        if (!archivo.exists()) {
            System.out.println("No se encontró archivo para el jugador: " + nombre);
            return null;
        }

        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo))) {
            Jugador jugadorCargado = (Jugador) entrada.readObject();
            System.out.println("Datos del jugador '" + nombre + "' cargados desde: " + nombreArchivo);
            return jugadorCargado;
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.err.println("Error al cargar los datos del jugador '" + nombre + "': " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    // --- Métodos para guardar/cargar estado de la partida (opcional) ---

    /**
     * Guarda el estado actual de una partida (incluyendo jugador y escenario actual).
     * @param jugador El jugador con su estado actual.
     * @param escenario El escenario actual (puede ser solo la ruta o el objeto entero si es serializable).
     * @param nombrePartida Nombre para el archivo de guardado.
     */
    public static void guardarPartidaActual(Jugador jugador, Escenario escenario, String nombrePartida) {
         // Crear el directorio si no existe
        try {
            Files.createDirectories(Paths.get(DIRECTORIO_PARTIDAS));
        } catch (IOException e) {
             System.err.println("Error al crear el directorio de partidas: " + e.getMessage());
             return;
        }

        String rutaArchivo = Paths.get(DIRECTORIO_PARTIDAS, nombrePartida + ".sav").toString();
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            salida.writeObject(jugador);
            // Si Escenario es Serializable, puedes guardarlo también:
            // salida.writeObject(escenario);
            // Si no, guarda la ruta al archivo del escenario:
            // salida.writeUTF(rutaAlArchivoDelEscenarioActual);
            System.out.println("Partida guardada en: " + rutaArchivo);
        } catch (IOException e) {
            System.err.println("Error al guardar la partida: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Carga el estado de una partida guardada.
     * @param nombrePartida Nombre del archivo de guardado.
     * @return Un objeto Jugador con el estado cargado, o null si hay error.
     * (Podría devolver un objeto más complejo si guardas más cosas).
     */
     public static Jugador cargarPartidaActual(String nombrePartida) {
         String rutaArchivo = Paths.get(DIRECTORIO_PARTIDAS, nombrePartida + ".sav").toString();
         File archivo = new File(rutaArchivo);

         if (!archivo.exists()) {
             System.out.println("No se encontró archivo de partida guardada: " + nombrePartida);
             return null;
         }

         try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo))) {
             Jugador jugadorCargado = (Jugador) entrada.readObject();
             // Si guardaste más cosas, cárgalas aquí:
             // Escenario escenarioCargado = (Escenario) entrada.readObject();
             // String rutaEscenario = entrada.readUTF();
             System.out.println("Partida cargada desde: " + rutaArchivo);
             return jugadorCargado; // Devuelve solo el jugador por ahora
         } catch (IOException | ClassNotFoundException | ClassCastException e) {
             System.err.println("Error al cargar la partida '" + nombrePartida + "': " + e.getMessage());
             e.printStackTrace();
             return null;
         }
     }
}
