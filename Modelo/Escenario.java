package Modelo;
// TODO generar documentacion

import java.util.List;

/**
 * Representa el mapa del juego, cargado desde un archivo.
 */
public class Escenario {

    private int filas;
    private int columnas;
    private char[][] mapaTiles;

    /**
     * Constructor principal.
     *
     * @param filas Número de filas.
     * @param columnas Número de columnas.
     * @param mapaTiles Matriz 2D con los caracteres de cada tile.
     */
    public Escenario(int filas, int columnas, char[][] mapaTiles) {
        this.filas = filas;
        this.columnas = columnas;
        this.mapaTiles = mapaTiles;
    }

    /**
     * Constructor alternativo que toma List<String> y lo convierte a char[][].
     * Útil si Sesion.cargarEscenario prefiere devolver List<String>
     * inicialmente.
     *
     * @param filas Número de filas.
     * @param columnas Número de columnas.
     * @param mapaLista Lista de Strings representando las filas del mapa.
     */
    public Escenario(int filas, int columnas, List<String> mapaLista) {
        this.filas = filas;
        this.columnas = columnas;
        if (mapaLista != null && !mapaLista.isEmpty()) {
            this.mapaTiles = new char[filas][columnas];
            for (int i = 0; i < filas; i++) {
                String filaStr = (i < mapaLista.size()) ? mapaLista.get(i) : "";
                for (int j = 0; j < columnas; j++) {
                    this.mapaTiles[i][j] = (j < filaStr.length()) ? filaStr.charAt(j) : ' ';
                }
            }
        } else {
            this.mapaTiles = new char[filas][columnas];
            System.err.println("Advertencia: Se recibió una lista de mapa vacía o nula para Escenario.");
        }
    }

    public int getFilas() {return filas;}

    public int getColumnas() {return columnas;}

    /**
     * Obtiene el carácter (tile) en una posición específica del mapa.
     *
     * @param fila Fila deseada.
     * @param columna Columna deseada.
     * @return El carácter en esa posición, o '\0' si está fuera de los límites.
     */
    public char getTile(int fila, int columna) {
        if (mapaTiles != null && fila >= 0 && fila < filas && columna >= 0 && columna < columnas) 
            return mapaTiles[fila][columna];
        return '\0';
    }

    public void setTile(int fila, int columna, char tipo) {
        if (mapaTiles != null && fila >= 0 && fila < filas && columna >= 0 && columna < columnas)
            mapaTiles[fila][columna] = tipo;
    }
}
