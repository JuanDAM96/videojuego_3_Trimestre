package Controlador;

import Modelo.Puntuacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona la interacción con la base de datos SQLite, específicamente para la
 * tabla de Puntuaciones.
 * 
 * @author Santiago
 * @author Juan
 * @version 0.3.3
 */
public class SQLite {
    private static final String URL_DB = "jdbc:sqlite:PuntuacionesJuego.db";

    /**
     * Inicializa la base de datos y crea la tabla Puntuaciones si no existe. Se
     * recomienda llamar a este método una vez al inicio de la aplicación.
     */
    public static void inicializarBaseDatos() {
        String sqlCreateTable = """
                                CREATE TABLE IF NOT EXISTS Puntuaciones (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    nombre TEXT NOT NULL,
                                    puntuacion INTEGER NOT NULL
                                );""";

        try (Connection conexion = DriverManager.getConnection(URL_DB); Statement declaracion = conexion.createStatement()) {

            System.out.println("Conectado a SQLite. Verificando/Creando tabla Puntuaciones...");
            declaracion.execute(sqlCreateTable);
            System.out.println("Tabla Puntuaciones lista.");

        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos SQLite: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obtiene las 10 mejores puntuaciones de la base de datos.
     *
     * @return Una lista de objetos Puntuacion, ordenada de mayor a menor.
     */
    public static List<Puntuacion> obtenerMejoresPuntuaciones() {
        for (int i = 0; i < 20; i++) guardarPuntuacion("Defectos", i); // Esto son falsos positivos, para mostrar el rendimiento de la funcion

        List<Puntuacion> listaPuntuaciones = new ArrayList<>();
        String sql = "SELECT nombre, puntuacion FROM Puntuaciones ORDER BY puntuacion DESC LIMIT 10";

        try (Connection conexion = DriverManager.getConnection(URL_DB); Statement declaracion = conexion.createStatement(); ResultSet resultado = declaracion.executeQuery(sql)) {
            System.out.println("Obteniendo mejores puntuaciones...");
            while (resultado.next()) {
                String nombre = resultado.getString("nombre");
                int puntuacionVal = resultado.getInt("puntuacion");
                listaPuntuaciones.add(new Puntuacion(nombre, puntuacionVal));
            }
            System.out.println("Se encontraron " + listaPuntuaciones.size() + " puntuaciones.");

        } catch (SQLException e) {
            System.err.println("Error al obtener puntuaciones de SQLite: " + e.getMessage());
            e.printStackTrace();
        }
        return listaPuntuaciones;
    }

    /**
     * Guarda una nueva puntuación en la base de datos.
     *
     * @param nombre Nombre del jugador.
     * @param puntuacion Puntuación obtenida.
     * @return true si se guardó correctamente, false en caso contrario.
     */
    public static boolean guardarPuntuacion(String nombre, int puntuacion) {
        String sql = "INSERT INTO Puntuaciones(nombre, puntuacion) VALUES(?, ?)";

        try (Connection conexion = DriverManager.getConnection(URL_DB); PreparedStatement decPre = conexion.prepareStatement(sql)) {
            decPre.setString(1, nombre);
            decPre.setInt(2, puntuacion);
            decPre.executeUpdate();
            System.out.println("Puntuación guardada para " + nombre + ": " + puntuacion);
            return true;

        } catch (SQLException e) {
            System.err.println("Error al guardar puntuación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}