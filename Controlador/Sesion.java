package Controlador;


import Modelo.Escenario;
import Modelo.Jugador;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase sesion
 * Gestiona la actual sesion del jugador y su partida
 * 
 * @author Santiago
 * @author Juan
 * @version 0.3.3
 */
public class Sesion {

    private static final String DIRECTORIO_ESCENARIOS = "escenarios";
    private static final String DIRECTORIO_JUGADORES = "jugadores";
    private static final String DIRECTORIO_PARTIDAS = "partidas";
    private static final String NOMBRE_ESCENARIO_DEFAULT = "nivel_default0.txt";


    /**
     * Creo otros Escenarios extra
     * @param veces las veces que se repite el bucle
     */
    public static void crearEscenarios(int veces) {
        Path rutaEscenarioDefault = Paths.get(DIRECTORIO_ESCENARIOS, "nivel_default" +veces+ ".txt");

        if (Files.exists(rutaEscenarioDefault)) return;

        System.out.println("Creando archivo de escenario por defecto en: " + rutaEscenarioDefault);
        int filas = 8;
        int columnas = 10;
        List<String> contenidoMapa = new ArrayList<>();
        contenidoMapa.add(filas + "X" + columnas);
        contenidoMapa.add(columnas + "O");
        String filaIntermedia = "1O" + (columnas - 2) + "E" + "1O";
        for (int i = 1; i < filas - 1; i++)
            contenidoMapa.add(filaIntermedia);
        contenidoMapa.add(columnas + "O");

        try (BufferedWriter escritor = Files.newBufferedWriter(rutaEscenarioDefault)) {
            for (String linea : contenidoMapa) {
                escritor.write(linea);
                escritor.newLine();
            }
            System.out.println("Archivo de escenario por defecto creado.");
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo de escenario por defecto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Carga el escenario dependiedo del nombre del archivo que desee carga.
     * Se ejecuta un maldito interprete de escenario propio.
     * 
     * @param nombreArchivo Nombre del Archivo que se va a guardar
     * @return Un escenario que se mostrara por pantalla
     */
    public static Escenario cargarEscenario(String nombreArchivo) {
        Path rutaArchivo = Paths.get(DIRECTORIO_ESCENARIOS, nombreArchivo);
        List<String> lineasMapa = new ArrayList<>();
        int filas = 0;
        int columnas = 0;

        if (!Files.exists(rutaArchivo)) {
            System.err.println("Archivo de escenario no encontrado: " + rutaArchivo);
            Path rutaDefault = Paths.get(DIRECTORIO_ESCENARIOS, NOMBRE_ESCENARIO_DEFAULT);
            if (Files.exists(rutaDefault)) {
                System.out.println("Cargando escenario por defecto: " + rutaDefault);
                rutaArchivo = rutaDefault;
            } else {
                System.err.println("Tampoco se encontró el escenario por defecto.");
                return null;
            }
        }

        try (BufferedReader lector = Files.newBufferedReader(rutaArchivo)) {
            String primeraLinea = lector.readLine();
            if (primeraLinea == null || !primeraLinea.matches("\\d+X\\d+")) return null;

            String[] dims = primeraLinea.split("X");
            filas = Integer.parseInt(dims[0]);
            columnas = Integer.parseInt(dims[1]);
            if (filas <= 0 || columnas <= 0) return null;

            String lineaRLE;
            Pattern pattern = Pattern.compile("(\\d+)([EO])");
            for (int i = 0; i < filas; i++) {
                lineaRLE = lector.readLine();
                if (lineaRLE == null) return null;

                String filaDecodificada = "";
                Matcher matcher = pattern.matcher(lineaRLE.trim());
                int colActual = 0;
                while (matcher.find() && colActual < columnas) {
                    int cantidad = Integer.parseInt(matcher.group(1));
                    char tipo = matcher.group(2).charAt(0);
                    for (int k = 0; k < cantidad && colActual < columnas; k++) {
                        filaDecodificada += tipo;
                        colActual++;
                    }
                }
                while (filaDecodificada.length() < columnas) filaDecodificada += "E";

                if (filaDecodificada.length() > columnas) filaDecodificada = filaDecodificada.substring(0, columnas);

                lineasMapa.add(filaDecodificada.toString());
            }
            System.out.println("Escenario cargado desde: " + rutaArchivo);
            return new Escenario(filas, columnas, lineasMapa);
        } catch (Exception e) {
            System.err.println("Error al cargar escenario '" + rutaArchivo + "': " + e.getMessage());
            return null;
        }
    }

    /**
     * El guardado del jugador recien registrado.
     * 
     * @param jugador El guardado como binario
     * @return Comprobar si ha sido posible
     */
    public static boolean guardarJugador(Jugador jugador) {
        if (jugador == null || jugador.getNombre() == null || jugador.getNombre().trim().isEmpty()) return false;

        try {Files.createDirectories(Paths.get(DIRECTORIO_JUGADORES));} 
        catch (IOException e) {return false;}

        String nombreArchivo = Paths.get(DIRECTORIO_JUGADORES, jugador.getNombre() + ".dat").toString();

        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(nombreArchivo))) {
            salida.writeObject(jugador);
            return true;
        } catch (IOException e) {return false;}
    }

    /**
     * Carga el jugador en la sesion
     * 
     * @param nombre Nombre del jugador
     * @return Devuelve el jugador cargado
     */
    public static Jugador cargarJugadorPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return null;

        String nombreArchivo = Paths.get(DIRECTORIO_JUGADORES, nombre + ".dat").toString();
        File archivo = new File(nombreArchivo);
        if (!archivo.exists()) return null;

        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo))) {return (Jugador) entrada.readObject();}
        catch (Exception e) {return null;}
    }
}