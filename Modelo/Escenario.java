//Los 4 esenarios son el mismo aun. Y hay que añadir mas obstaculos

package Modelo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase que representa un escenario en un videojuego.
 * El escenario está compuesto por una matriz de objetos que representan celdas del mapa.
 * Proporciona métodos para manipular el mapa y guardar/cargar su estado en un archivo.
 */
public class Escenario {

    // Atributos
    private ObjetoEscenario[][] mapa;
    private int filas;
    private int columnas;

    // Constantes para el formato del escenario
    private static final char ESPACIO_VACIO = 'E';
    private static final char OBJETO_BLOQUEANTE = 'O';
    private static final char OBJETO_NO_BLOQUEANTE_DEFAULT = '.'; // Para otros caracteres no definidos como E u O

    /**
     * Constructor privado para inicializar un escenario con las dimensiones especificadas.
     * 
     * @param filas    Número de filas del escenario.
     * @param columnas Número de columnas del escenario.
     * @throws IllegalArgumentException Si las dimensiones son menores o iguales a 0.
     */
    private Escenario(int filas, int columnas) {
        if (filas <= 0 || columnas <= 0) {
            throw new IllegalArgumentException("Las dimensiones del escenario deben ser positivas.");
        }
        this.filas = filas;
        this.columnas = columnas;
        this.mapa = new ObjetoEscenario[filas][columnas];
    }

    // --- GETTERS ---

    /**
     * Obtiene la matriz que representa el mapa del escenario.
     * 
     * @return La matriz de objetos del escenario.
     */
    public ObjetoEscenario[][] getMapa() {
        return mapa;
    }

    /**
     * Obtiene el número de filas del escenario.
     * 
     * @return Número de filas.
     */
    public int getFilas() {
        return filas;
    }

    /**
     * Obtiene el número de columnas del escenario.
     * 
     * @return Número de columnas.
     */
    public int getColumnas() {
        return columnas;
    }

    // --- MÉTODOS PARA MANIPULAR EL MAPA ---

    /**
     * Coloca un objeto en una posición específica del mapa.
     * 
     * @param fila    Fila donde se colocará el objeto.
     * @param columna Columna donde se colocará el objeto.
     * @param objeto  El objeto a colocar.
     * @return true si el objeto se colocó correctamente, false si la posición no es válida.
     */
    public boolean colocarObjeto(int fila, int columna, ObjetoEscenario objeto) {
        if (esPosicionValida(fila, columna)) {
            this.mapa[fila][columna] = objeto;
            return true;
        }
        return false;
    }

    /**
     * Obtiene el objeto en una posición específica del mapa.
     * 
     * @param fila    Fila de la posición.
     * @param columna Columna de la posición.
     * @return El objeto en la posición especificada, o null si no hay objeto o la posición no es válida.
     */
    public ObjetoEscenario getObjetoEn(int fila, int columna) {
        if (esPosicionValida(fila, columna)) {
            return this.mapa[fila][columna];
        }
        return null;
    }

    /**
     * Verifica si una posición es válida dentro del mapa.
     * 
     * @param fila    Fila de la posición.
     * @param columna Columna de la posición.
     * @return true si la posición es válida, false en caso contrario.
     */
    public boolean esPosicionValida(int fila, int columna) {
        return fila >= 0 && fila < getFilas() && columna >= 0 && columna < getColumnas();
    }

    /**
     * Verifica si una posición está bloqueada.
     * Una posición está bloqueada si está fuera de los límites del mapa o si contiene un objeto bloqueante.
     * 
     * @param fila    Fila de la posición.
     * @param columna Columna de la posición.
     * @return true si la posición está bloqueada, false en caso contrario.
     */
    public boolean esPosicionBloqueada(int fila, int columna) {
        if (!esPosicionValida(fila, columna)) {
            return true;
        }
        ObjetoEscenario obj = getObjetoEn(fila, columna);
        return obj != null && obj.isBloqueo();
    }

    // --- GUARDAR/CARGAR con NUEVO FORMATO ---

    /**
     * Guarda el escenario en un archivo de texto con el nuevo formato.
     * 
     * Formato del archivo:
     * Línea 1: Dimensiones del mapa en el formato "AnchoXAlto" (ej: "80X20")
     * Línea 2: Descripción comprimida del mapa (ej: "25E 35O 25E")
     * @param rutaArchivo La ruta del archivo .txt donde guardar.
     * @return true si se guardó correctamente, false en caso contrario.
     */
    public boolean guardarEnArchivoNuevoFormato(String rutaArchivo) {
        Path ruta = Paths.get(rutaArchivo);
        try {
            // Asegurarse de que el directorio padre exista
            Path directorioPadre = ruta.getParent();
            if (directorioPadre != null && !Files.exists(directorioPadre)) {
                Files.createDirectories(directorioPadre);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(ruta)) {
                // Guardar dimensiones
                writer.write(getColumnas() + "X" + getFilas()); // Formato AnchoXAlto
                writer.newLine();

                // Guardar el mapa en el formato "NE NO NE..."
                StringBuilder lineaMapa = new StringBuilder();
                int contador = 0;
                char tipoActual = ' '; // Inicializar con un valor no válido

                for (int i = 0; i < getFilas(); i++) {
                    for (int j = 0; j < getColumnas(); j++) {
                        ObjetoEscenario obj = this.mapa[i][j];
                        char tipoCelda;
                        boolean bloqueoCelda;

                        if (obj == null) {
                            tipoCelda = ESPACIO_VACIO; // Asumir null como espacio vacío
                            bloqueoCelda = false;
                        } else {
                            tipoCelda = obj.getObjetoChar();
                            bloqueoCelda = obj.isBloqueo();
                        }

                        // Determinar el carácter representativo (E o O)
                        char representacion = (bloqueoCelda) ? OBJETO_BLOQUEANTE : ESPACIO_VACIO;

                        if (contador == 0) {
                            // Primer elemento
                            tipoActual = representacion;
                            contador = 1;
                        } else if (representacion == tipoActual) {
                            // Mismo tipo, incrementar contador
                            contador++;
                        } else {
                            // Diferente tipo, escribir el anterior y empezar nuevo conteo
                            if (lineaMapa.length() > 0) {
                                lineaMapa.append(" "); // Separador
                            }
                            lineaMapa.append(contador).append(tipoActual);
                            tipoActual = representacion;
                            contador = 1;
                        }
                    }
                }
                // Escribir el último grupo contado
                if (contador > 0) {
                     if (lineaMapa.length() > 0) {
                        lineaMapa.append(" "); // Separador
                    }
                    lineaMapa.append(contador).append(tipoActual);
                }

                writer.write(lineaMapa.toString());
                writer.newLine(); // Añadir una nueva línea al final si se prefiere
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar el escenario (nuevo formato) en '" + rutaArchivo + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Carga un escenario desde un archivo de texto con el nuevo formato.
     * Línea 1: Dimensiones (ej: "80X20")
     * Línea 2: Descripción del mapa (ej: "25E 35O 25E")
     * 
     * @param rutaArchivo La ruta del archivo .txt desde donde cargar.
     * @return El Escenario cargado, o null si hay errores.
     */
    public static Escenario cargarDesdeArchivoNuevoFormato(String rutaArchivo) {
        Path ruta = Paths.get(rutaArchivo);
        if (!Files.exists(ruta) || !Files.isReadable(ruta)) {
            System.out.println("Archivo de escenario (nuevo formato) no encontrado o sin permisos: " + rutaArchivo);
            return null;
        }

        Escenario escenario = null;
        int filas = -1;
        int columnas = -1;

        try (BufferedReader reader = Files.newBufferedReader(ruta)) {
            // --- Leer Línea 1: Dimensiones ---
            String lineaDimensiones = reader.readLine();
            if (lineaDimensiones == null || lineaDimensiones.trim().isEmpty()) {
                System.err.println("Error: Falta la línea de dimensiones en '" + rutaArchivo + "'.");
                return null;
            }
            lineaDimensiones = lineaDimensiones.trim().toUpperCase(); // Convertir a mayúsculas por si acaso X está en minúscula
            String[] partesDimensiones = lineaDimensiones.split("X");
            if (partesDimensiones.length != 2) {
                System.err.println("Error: Formato de dimensiones inválido ('" + lineaDimensiones + "'). Se esperaba 'AnchoXAlto' en '" + rutaArchivo + "'.");
                return null;
            }
            try {
                columnas = Integer.parseInt(partesDimensiones[0]);
                filas = Integer.parseInt(partesDimensiones[1]);
            } catch (NumberFormatException e) {
                System.err.println("Error: Dimensiones no numéricas ('" + lineaDimensiones + "') en '" + rutaArchivo + "'.");
                return null;
            }

            if (filas <= 0 || columnas <= 0) {
                System.err.println("Error: Dimensiones inválidas (<= 0) en '" + rutaArchivo + "'.");
                return null;
            }

            // Crear el escenario con las dimensiones leídas
            escenario = new Escenario(filas, columnas);

            // --- Leer Línea 2: Definición del Mapa ---
            String lineaMapa = reader.readLine();
            if (lineaMapa == null || lineaMapa.trim().isEmpty()) {
                System.err.println("Error: Falta la línea de definición del mapa en '" + rutaArchivo + "'.");
                return null;
            }
            lineaMapa = lineaMapa.trim();

            // Patrón para extraer "NE" o "NO" (Número y Tipo)
            Pattern pattern = Pattern.compile("(\\d+)([EO])"); // Grupo 1: número, Grupo 2: E o O
            Matcher matcher = pattern.matcher(lineaMapa);

            int filaActual = 0;
            int columnaActual = 0;
            int celdasProcesadas = 0;

            while (matcher.find()) {
                int cantidad = Integer.parseInt(matcher.group(1));
                char tipo = matcher.group(2).charAt(0); // E o O

                boolean esBloqueante = (tipo == OBJETO_BLOQUEANTE);
                char caracterObjeto = (tipo == OBJETO_BLOQUEANTE) ? '#' : '.'; // Carácter visual (puedes cambiarlo)

                for (int k = 0; k < cantidad; k++) {
                    if (filaActual >= filas || columnaActual >= columnas) {
                        System.err.println("Error: La definición del mapa excede las dimensiones especificadas en '" + rutaArchivo + "'.");
                        return null; // Demasiados elementos definidos
                    }

                    // Colocar el objeto en el mapa
                    escenario.colocarObjeto(filaActual, columnaActual, new ObjetoEscenario(caracterObjeto, esBloqueante));
                    celdasProcesadas++;

                    // Avanzar a la siguiente celda
                    columnaActual++;
                    if (columnaActual >= columnas) {
                        columnaActual = 0;
                        filaActual++;
                    }
                }
            }

             // Verificar si se procesaron todas las celdas esperadas
            if (celdasProcesadas != filas * columnas) {
                 System.err.println("Error: La definición del mapa no coincide con las dimensiones especificadas en '" + rutaArchivo + "'. " +
                                    "Esperadas: " + (filas * columnas) + ", Procesadas: " + celdasProcesadas);
                 return null;
            }

             // Verificar si hay contenido extra no esperado en la línea del mapa
             int endLastMatch = -1;
             matcher.reset(); // Reiniciar el matcher para buscar de nuevo
             while (matcher.find()) {
                 endLastMatch = matcher.end();
             }
             // Buscar si hay caracteres después del último match que no sean espacios
             if (endLastMatch != -1 && endLastMatch < lineaMapa.length() && !lineaMapa.substring(endLastMatch).trim().isEmpty()) {
                  System.err.println("Advertencia: Caracteres extra encontrados al final de la línea de definición del mapa en '" + rutaArchivo + "': '" + lineaMapa.substring(endLastMatch).trim() + "'");
             }


            // Verificar si hay más líneas inesperadas en el archivo
            if (reader.readLine() != null) {
                System.err.println("Advertencia: Hay datos adicionales inesperados al final del archivo '" + rutaArchivo + "'.");
            }

        } catch (IOException e) {
            System.err.println("Error de E/S al cargar el escenario (nuevo formato) desde '" + rutaArchivo + "': " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (NumberFormatException e) {
            // Captura errores al parsear números en la definición del mapa (ej. "A5E")
            System.err.println("Error: Formato numérico inválido en la definición del mapa en '" + rutaArchivo + "': " + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            System.err.println("Error al crear escenario desde archivo (nuevo formato) '" + rutaArchivo + "': " + e.getMessage());
            return null;
        } catch (Exception e) { // Captura general para otros errores inesperados
            System.err.println("Error inesperado durante la carga del escenario (nuevo formato) desde '" + rutaArchivo + "': " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return escenario;
    }
}
