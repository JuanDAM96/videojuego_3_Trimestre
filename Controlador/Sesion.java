package Controlador;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Modelo.Escenario;
import Modelo.Jugador;
import Modelo.Objeto;

public class Sesion {

    /**
     * Maneja el inicio de sesión del jugador: Carga un jugador existente o crea uno
     * nuevo.
     * 
     * @param nombreUsuario El nombre de usuario (usado como nombre de archivo).
     * @return El objeto Jugador cargado o recién creado.
     */
    public static Jugador sesionJug(String nombreUsuario) {
        Path ruta = Paths.get("./Jugadores" + nombreUsuario.toLowerCase() + ".bin");
        Jugador jugador = null; // Inicializar a null

        if (Files.exists(ruta)) {
            jugador = Sesion.cargaJug(ruta); // Carga el jugador existente
        } else {
            System.out.println("Creando nuevo jugador: " + nombreUsuario);
            jugador = Sesion.creaJug(ruta, nombreUsuario); // Crea un nuevo jugador
        }
        Jugador.setNombre(jugador.getNombre());
        Jugador.setCorreo(jugador.getCorreo());

        return jugador;
    }

    /**
     * Crea un nuevo jugador, obtiene sus detalles y los guarda en un archivo.
     * 
     * @param ruta          La ruta del archivo para guardar los datos del jugador.
     * @param nombreUsuario El nombre de usuario para el nuevo jugador.
     * @return El objeto Jugador recién creado, o null si la creación falla.
     */
    public static Jugador creaJug(Path ruta, String nombreUsuario) {

        Scanner teclado = new Scanner(System.in); // Usa Scanner para leer la entrada del usuario
        System.out.print("Introduce el correo electrónico para " + nombreUsuario + ": ");
        String correo = teclado.nextLine();

        Jugador nuevoJugador = new Jugador(nombreUsuario, correo); // Constructor de ejemplo

        // Try-with-resources para cerrar automáticamente el stream
        try (OutputStream fos = Files.newOutputStream(ruta);
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            // Serializa y escribe el objeto Jugador en el archivo
            oos.writeObject(nuevoJugador);
            System.out.println("Jugador '" + nombreUsuario + "' guardado correctamente en " + ruta.toString());
            return nuevoJugador;

        } catch (IOException e) {
            System.err.println("Error al crear o guardar el jugador '" + nombreUsuario + "': " + e.getMessage());
            e.printStackTrace(); // Imprime el stack trace para depuración
            return null; // Devuelve null indicando fallo
        }
    }

    /**
     * Carga los datos del jugador desde un archivo especificado.
     * 
     * @param ruta La ruta del archivo desde donde cargar los datos del jugador.
     * @return El objeto Jugador cargado, o null si la carga falla.
     */
    public static Jugador cargaJug(Path ruta) {
        // Comprueba si el archivo existe realmente antes de intentar cargarlo
        if (!Files.exists(ruta)) {
            System.err.println("Error: El archivo del jugador no existe en " + ruta.toString());
            return null;
        }

        // Try-with-resources para cerrar automáticamente el stream
        try (InputStream fis = Files.newInputStream(ruta);
                ObjectInputStream ois = new ObjectInputStream(fis)) {

            // Lee el objeto serializado y lo castea a Jugador
            Object obj = ois.readObject();
            if (obj instanceof Jugador) {
                System.out.println("Jugador cargado correctamente desde " + ruta.toString());
                return (Jugador) obj;
            } else {
                System.err.println("Error: El archivo no contiene un objeto Jugador válido.");
                return null;
            }

        } catch (IOException e) {
            System.err.println("Error de E/S al cargar el jugador desde " + ruta.toString() + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            System.err.println(
                    "Error: La clase Jugador no se encontró durante la deserialización. Asegúrate de que esté en el classpath y sea Serializable.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Maneja la selección y carga del escenario.
     * 
     * @return El objeto Escenario cargado, o null si la selección/carga falla.
     */
    public static Escenario sesionEsc(String arch) {
        Escenario escenario = null;

        // Construye la ruta completa al archivo .txt
        Path ruta = Paths.get("./Escenarios" + arch.toLowerCase() + ".txt"); // Añade extensión y convierte a minúsculas

        // Comprueba si el archivo existe antes de intentar cargarlo
        if (Files.exists(ruta)) {
            System.out.println("Cargando escenario desde: " + ruta.toString());
            escenario = Sesion.cargaEsc(ruta); // Llama a la función que carga y procesa el archivo
            if (escenario == null) {
                System.err.println("No se pudo cargar el escenario desde: " + ruta);
            } else {
                System.out.println("Escenario '" + arch + "' cargado correctamente.");
            }
        } else {
            System.err.println("Error: El archivo del escenario '" + ruta.getFileName() + "' no existe en Escenarios");
        }

        // Considera añadir un bucle o comportamiento por defecto si la carga falla
        return escenario;
    }

    /**
     * Carga los datos del escenario desde un archivo de texto especificado.
     * El formato del archivo de texto determina cómo se interpreta.
     * 
     * @param ruta La ruta al archivo de texto del escenario.
     * @return El objeto Escenario cargado, o null si la carga falla.
     * @throws IOException 
     */
    public static Escenario cargaEsc(Path ruta) throws IOException {
        Escenario escenario;
        int filas;
        int columnas;
        Objeto[][] mapa;
        List<String> archivo = new ArrayList<>();
        String[] segmentos; 

        // Constantes para los tipos de tile (puedes usar las de Escenario si son
        // públicas)
        final char ESPACIO_VACIO = Escenario.getEspacioVacio(); // Espacio vacío
        final char OBJETO_BLOQUEANTE = Escenario.getObjetoBloqueante(); // Objeto bloqueante


        // Leer fichero
        archivo = Files.readAllLines(ruta);
        for (int i = 0; i < archivo.size(); i++) {
            if (i == 0) {
                String[] dimensiones = archivo.get(0).split("X");
        
                filas = Integer.parseInt(dimensiones[0].trim());
                columnas = Integer.parseInt(dimensiones[1].trim());
                escenario = new Escenario(filas, columnas); 
            }else{
                String linea = archivo.get(i).trim();
                segmentos = linea.split("\\s+");
            }

            // insertar objeto
            for (int x = 0; x < filas; x++) {
                for (String segmento : segmentos) {
                    // Extraer cantidad y tipo
                    int cantidad = Integer.parseInt(segmento.substring(0, segmento.length() - 1));
                    char tipo = segmento.charAt(segmento.length() - 1);
                    // Llenar el mapa con los objetos correspondientes
                    for (int j = 0; j < columnas; j++) {
                        for (int k ; cantidad < cantidad+columnas; cantidad++) {
                            if (tipo == 'O') {
                                //TODO @JuanDAM96 mapa[x][j] = ;
                            } else {
                                //TODO @JuanDAM96 mapa[x][j] = ;
                            }
                        }
            
                    }
                }
            }


        }

        escenario.setMapa(mapa);
        return escenario;

/*         // Expresión regular para parsear segmentos como "20O", "39E"
        // Captura: Grupo 1 -> número (\d+), Grupo 2 -> tipo ([EO])
        final Pattern PATRON_SEGMENTO = Pattern.compile("(\\d+)([EO])");

        try (BufferedReader reader = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {

            // 1. Leer y parsear la línea de dimensiones
            String lineaDimensiones = reader.readLine();
            if (lineaDimensiones == null || lineaDimensiones.trim().isEmpty()) {
                System.err.println("Error: Archivo vacío o sin línea de dimensiones en " + ruta);
                return null;
            }

            String[] partesDimension = lineaDimensiones.trim().split("X", 2);
            if (partesDimension.length != 2) {
                System.err.println("Error: Formato de dimensiones inválido en " + ruta
                        + ". Se esperaba 'filasXcolumnas', se encontró: " + lineaDimensiones);
                return null;
            }

            int filas;
            int columnas;
            try {
                filas = Integer.parseInt(partesDimension[0]);
                columnas = Integer.parseInt(partesDimension[1]);
                if (filas <= 0 || columnas <= 0) {
                    throw new NumberFormatException("Las dimensiones deben ser números positivos.");
                }
            } catch (NumberFormatException e) {
                System.err.println(
                        "Error: Dimensiones inválidas en " + ruta + ": " + lineaDimensiones + " - " + e.getMessage());
                return null;
            }

            // 2. Preparar la estructura del mapa
            ObjetoEscenario[][] mapa = new ObjetoEscenario[filas][columnas];

            // 3. Leer y procesar las líneas de datos de las filas
            String lineaDatosFila;
            int filaActual = 0;
            while ((lineaDatosFila = reader.readLine()) != null && filaActual < filas) {
                lineaDatosFila = lineaDatosFila.trim();
                if (lineaDatosFila.isEmpty()) {
                    // Podrías decidir si ignorar líneas vacías o considerarlo un error
                    System.err.println("Advertencia: Línea vacía encontrada en " + ruta + " en la fila de datos "
                            + (filaActual + 1));
                    continue; // Opcional: Saltar líneas vacías entre datos de filas
                    // return null; // Opcional: Considerar línea vacía como error
                }

                int columnaActual = 0;
                // Dividir la línea por espacios (uno o más)
                String[] segmentos = lineaDatosFila.split("\\s+");

                for (String segmento : segmentos) {
                    if (segmento.isEmpty())
                        continue; // Ignorar posibles espacios dobles

                    Matcher matcher = PATRON_SEGMENTO.matcher(segmento);
                    if (!matcher.matches()) {
                        System.err.println("Error: Formato de segmento inválido en " + ruta + ", fila "
                                + (filaActual + 1) + ", segmento: '" + segmento + "'");
                        return null; // Error en el formato de un segmento
                    }

                    int cantidad = Integer.parseInt(matcher.group(1)); // Ya validado por regex como número
                    char tipo = matcher.group(2).charAt(0); // Ya validado por regex como 'E' o 'O'

                    // Verificar si el segmento cabe en la fila actual
                    if (columnaActual + cantidad > columnas) {
                        System.err.println("Error: Los datos de la fila " + (filaActual + 1)
                                + " exceden el número de columnas (" + columnas + ") definido en " + ruta);
                        return null;
                    }

                    // Crear los objetos correspondientes en el mapa
                    boolean esBloqueante = (tipo == OBJETO_BLOQUEANTE);
                    for (int i = 0; i < cantidad; i++) {
                        mapa[filaActual][columnaActual] = new ObjetoEscenario(tipo, esBloqueante);
                        columnaActual++;
                    }
                } // Fin del bucle de segmentos

                // Verificar si se llenó exactamente la fila
                if (columnaActual != columnas) {
                    System.err.println("Error: Los datos de la fila " + (filaActual + 1)
                            + " no completan el número de columnas (" + columnas + ") definido en " + ruta
                            + ". Se procesaron " + columnaActual + " tiles.");
                    return null;
                }

                filaActual++; // Pasar a la siguiente fila
            } // Fin del bucle de lectura de líneas

            // 4. Verificar si se leyeron todas las filas esperadas
            if (filaActual != filas) {
                System.err.println("Error: Número incorrecto de filas de datos en " + ruta + ". Se esperaban " + filas
                        + ", se encontraron " + filaActual);
                return null;
            }

            // 5. Crear y devolver el objeto Escenario
            // **NECESITARÁS AJUSTAR ESTA PARTE SEGÚN LA OPCIÓN ELEGIDA PARA LA CLASE
            // Escenario**

            // Ejemplo usando Opción A (Método Factory Estático en Escenario)
            // return Escenario.crearDesdeDatos(filas, columnas, mapa);

            // Ejemplo usando Opción B (Constructor público/package-private y setter)
            try {
                Escenario escenario = new Escenario(filas, columnas); // Asume constructor accesible
                // escenario.setMapa(mapa); // Asume método setter accesible
                // Necesitas implementar el método setMapa o similar en Escenario.java
                System.err.println(
                        "ADVERTENCIA: El mapa leído necesita ser asignado al objeto Escenario. Implementa un constructor o setter adecuado en Escenario.java.");
                // Temporalmente devolvemos un escenario sin mapa asignado para demostrar la
                // lectura.
                // ¡ESTO DEBE SER CORREGIDO EN LA CLASE Escenario!
                return escenario; // Devuelve el escenario con dimensiones, pero sin mapa interno poblado.
            } catch (Exception e) {
                System.err.println("Error: No se pudo instanciar Escenario. ¿Es accesible el constructor?");
                return null;
            }

        } catch (IOException e) {
            System.err.println("Error de E/S al leer el archivo de escenario " + ruta + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) { // Captura otros errores inesperados (ej. OutOfMemoryError)
            System.err.println("Error inesperado al procesar el archivo de escenario " + ruta + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        } */
    }

}
