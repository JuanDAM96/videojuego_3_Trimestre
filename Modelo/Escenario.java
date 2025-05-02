//Los 4 esenarios son el mismo aun. Y hay que añadir mas obstaculos

package Modelo;

/**
 * Clase que representa un escenario en un videojuego.
 * El escenario está compuesto por una matriz de objetos que representan celdas del mapa.
 * Proporciona métodos para manipular el mapa y guardar/cargar su estado en un archivo.
 */
public class Escenario {

    // Atributos
    private Objeto[][] mapa;
    private int filas;
    private int columnas;

    // Constantes para el formato del escenario
    private static final char ESPACIO_VACIO = 'E';
    private static final char OBJETO_BLOQUEANTE = 'O';

    /**
     * Constructor privado para inicializar un escenario con las dimensiones especificadas.
     * 
     * @param filas    Número de filas del escenario.
     * @param columnas Número de columnas del escenario.
     * @throws IllegalArgumentException Si las dimensiones son menores o iguales a 0.
     */
    public Escenario(int filas, int columnas) {
        if (filas <= 0 || columnas <= 0) {
            throw new IllegalArgumentException("Las dimensiones del escenario deben ser positivas.");
        }
        this.filas = filas;
        this.columnas = columnas;
        this.mapa = new Objeto[filas][columnas];
    }

    // --- GETTERS ---

    /**
     * Obtiene la matriz que representa el mapa del escenario.
     * 
     * @return La matriz de objetos del escenario.
     */
    public Objeto[][] getMapa() {
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

    public void setMapa(Objeto[][] mapa) {
        this.mapa = mapa;
    }

    public void setFilas(int filas) {
        this.filas = filas;
    }

    public void setColumnas(int columnas) {
        this.columnas = columnas;
    }

    public static char getEspacioVacio() {
        return ESPACIO_VACIO;
    }

    public static char getObjetoBloqueante() {
        return OBJETO_BLOQUEANTE;
    }
    

}