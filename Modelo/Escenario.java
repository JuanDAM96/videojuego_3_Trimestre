package Modelo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Escenario {

    //Atributos
    private ObjetoEscenario[][] mapa;
    private static final char SEPARADOR_CELDA = ' '; // Espacio entre celdas en el archivo
    private static final char SEPARADOR_CHARS = ':'; // Separa carácter de booleano en archivo

    public Escenario(int filas, int columnas) {
        if (filas <= 0 || columnas <= 0) {
            throw new IllegalArgumentException("Las dimensiones del escenario deben ser positivas.");
        }
        this.mapa = new ObjetoEscenario[filas][columnas];
    }

    // --- GETTERS ---
    public ObjetoEscenario[][] getMapa() { 
        return mapa; 
    }

    public int getFilas() { 
        return (this.mapa != null) ? this.mapa.length : 0; 
    }

    public int getColumnas() { 
        return (this.mapa != null && this.mapa.length > 0) ? this.mapa[0].length : 0; 
    }

    // --- MÉTODOS PARA MANIPULAR EL MAPA ---
    public boolean colocarObjeto(int fila, int columna, ObjetoEscenario objeto) {
        if (esPosicionValida(fila, columna)) {
            this.mapa[fila][columna] = objeto;
            return true;
        }
        return false;
    }

    public ObjetoEscenario getObjetoEn(int fila, int columna) {
        if (esPosicionValida(fila, columna)) {
            return this.mapa[fila][columna];
        }
        return null;
    }

    public boolean esPosicionValida(int fila, int columna) {
        return fila >= 0 && fila < getFilas() && columna >= 0 && columna < getColumnas();
    }

    // GUARDAR/CARGAR

    public boolean guardarEnArchivo(String rutaArchivo) {
        Path ruta = Paths.get(rutaArchivo);
         try {
            // Asegurarse de que el directorio padre exista
            Path directorioPadre = ruta.getParent();
            if (directorioPadre != null && !Files.exists(directorioPadre)) {
                Files.createDirectories(directorioPadre);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(ruta)) {
                // Guardar dimensiones
                writer.write("filas=" + getFilas());
                writer.newLine();
                writer.write("columnas=" + getColumnas());
                writer.newLine();

                // Guardar el mapa
                for (int i = 0; i < getFilas(); i++) {
                    StringBuilder linea = new StringBuilder();
                    for (int j = 0; j < getColumnas(); j++) {
                        ObjetoEscenario obj = this.mapa[i][j];
                        if (obj != null) {
                            linea.append(obj.getObjetoChar());
                            linea.append(SEPARADOR_CHARS);
                            linea.append(obj.isBloqueo() ? 'T' : 'F');
                        } else {
                            linea.append('.'); // Carácter especial para representar null
                            linea.append(SEPARADOR_CHARS);
                            linea.append('F'); 
                        }
                        if (j < getColumnas() - 1) {
                            linea.append(SEPARADOR_CELDA); // Añadir separador entre celdas
                        }
                    }
                    writer.write(linea.toString());
                    writer.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar el escenario en '" + rutaArchivo + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static Escenario cargarDesdeArchivo(String rutaArchivo) {
        Path ruta = Paths.get(rutaArchivo);
        if (!Files.exists(ruta) || !Files.isReadable(ruta)) {
            System.out.println("Archivo de escenario no encontrado o sin permisos: " + rutaArchivo);
            return null;
        }

        int filas = -1;
        int columnas = -1;
        Escenario escenario = null;

        try (BufferedReader reader = Files.newBufferedReader(ruta)) {
            String linea;

            // Leer dimensiones
            while ((linea = reader.readLine()) != null) {
                 linea = linea.trim();
                 if (linea.startsWith("filas=")) {
                     filas = Integer.parseInt(linea.substring("filas=".length()));
                 } else if (linea.startsWith("columnas=")) {
                     columnas = Integer.parseInt(linea.substring("columnas=".length()));
                 }
                 // Si ya tenemos ambas dimensiones, salimos del bucle
                 if (filas > 0 && columnas > 0) {
                     break;
                 }
            }

            if (filas <= 0 || columnas <= 0) {
                 System.err.println("Error: Dimensiones inválidas o no encontradas en '" + rutaArchivo + "'.");
                 return null;
            }

            escenario = new Escenario(filas, columnas);

            // Leer el mapa
            for (int i = 0; i < filas; i++) {
                linea = reader.readLine();
                if (linea == null) {
                    System.err.println("Error: Faltan filas en el mapa del archivo '" + rutaArchivo + "'. Fila esperada: " + (i+1));
                    return null; 
                }
                // Dividir la línea en celdas usando el separador de celda
                String[] celdasStr = linea.trim().split(String.valueOf(SEPARADOR_CELDA)); 
                if (celdasStr.length != columnas) {
                     System.err.println("Error: Número incorrecto de columnas en la fila " + (i+1) + " del archivo '" + rutaArchivo + "'. Esperadas: " + columnas + ", encontradas: " + celdasStr.length);
                     return null; 
                }

                for (int j = 0; j < columnas; j++) {
                    String celda = celdasStr[j];
                    // Dividir la celda en carácter y booleano usando el separador de propiedades
                    String[] partes = celda.split(String.valueOf(SEPARADOR_CHARS)); 
                    if (partes.length != 2 || partes[0].length() != 1 || partes[1].length() != 1) {
                         System.err.println("Error: Formato de celda inválido '" + celda + "' en fila " + (i+1) + ", columna " + (j+1) + " del archivo '" + rutaArchivo + "'.");
                         return null; 
                    }
                    
                    char objetoChar = partes[0].charAt(0);
                    char bloqueoChar = partes[1].charAt(0);

                    if (objetoChar == '.') { // '.' representa null
                        escenario.colocarObjeto(i, j, null);
                    } else {
                        boolean bloqueo = (bloqueoChar == 'T');
                        escenario.colocarObjeto(i, j, new ObjetoEscenario(objetoChar, bloqueo));
                    }
                }
            }
             // Verificar si se leyeron más líneas de las esperadas (podría indicar un error)
             if (reader.readLine() != null) {
                 System.err.println("Advertencia: Hay datos adicionales inesperados al final del archivo '" + rutaArchivo + "'.");
             }

        } catch (IOException e) {
            System.err.println("Error de E/S al cargar el escenario desde '" + rutaArchivo + "': " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (NumberFormatException e) {
            System.err.println("Error: Formato numérico inválido para dimensiones en '" + rutaArchivo + "': " + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            System.err.println("Error al crear escenario desde archivo '" + rutaArchivo + "': " + e.getMessage());
            return null;
        } catch (Exception e) { 
             System.err.println("Error inesperado durante la carga del escenario desde '" + rutaArchivo + "': " + e.getMessage());
             e.printStackTrace();
             return null;
        }

        return escenario;
    }
}
