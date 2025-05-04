package Controlador;

import Modelo.Puntuacion; // Importar Puntuacion
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona la interacción con la base de datos SQLite,
 * específicamente para la tabla de Puntuaciones.
 */
public class SQLite {

    private static final String URL_DB = "jdbc:sqlite:PuntuacionesJuego.db"; // Nombre de archivo para la BD

    /**
     * Inicializa la base de datos y crea la tabla Puntuaciones si no existe.
     * Se recomienda llamar a este método una vez al inicio de la aplicación.
     */
    public static void inicializarBaseDatos() {
        String sqlCreateTable = """
                                CREATE TABLE IF NOT EXISTS Puntuaciones (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    nombre TEXT NOT NULL,
                                    puntuacion INTEGER NOT NULL
                                );""";

        try (Connection conn = DriverManager.getConnection(URL_DB);
             Statement stmt = conn.createStatement()) {
            // Registrar driver (aunque con JDBC 4.0+ suele ser automático)
            // Class.forName("org.sqlite.JDBC");

            System.out.println("Conectado a SQLite. Verificando/Creando tabla Puntuaciones...");
            stmt.execute(sqlCreateTable);
            System.out.println("Tabla Puntuaciones lista.");

        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos SQLite: " + e.getMessage());
            e.printStackTrace();
        } /*catch (ClassNotFoundException e) {
            System.err.println("Error: Driver SQLite no encontrado.");
            e.printStackTrace();
        }*/
    }

    /**
     * Obtiene las 10 mejores puntuaciones de la base de datos.
     * @return Una lista de objetos Puntuacion, ordenada de mayor a menor.
     */
    public static List<Puntuacion> obtenerMejoresPuntuaciones() {
        List<Puntuacion> listaPuntuaciones = new ArrayList<>();
        // Consulta SQL para obtener las 10 mejores puntuaciones
        String sql = "SELECT nombre, puntuacion FROM Puntuaciones ORDER BY puntuacion DESC LIMIT 10";

        try (Connection conn = DriverManager.getConnection(URL_DB);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("Obteniendo mejores puntuaciones...");
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                int puntuacionVal = rs.getInt("puntuacion");
                listaPuntuaciones.add(new Puntuacion(nombre, puntuacionVal));
            }
            System.out.println("Se encontraron " + listaPuntuaciones.size() + " puntuaciones.");

        } catch (SQLException e) {
            System.err.println("Error al obtener puntuaciones de SQLite: " + e.getMessage());
            e.printStackTrace();
            // Podrías devolver una lista vacía o null para indicar el error
        }
        return listaPuntuaciones;
    }

    /**
     * Guarda una nueva puntuación en la base de datos.
     * @param nombre Nombre del jugador.
     * @param puntuacion Puntuación obtenida.
     * @return true si se guardó correctamente, false en caso contrario.
     */
    public static boolean guardarPuntuacion(String nombre, int puntuacion) {
        String sql = "INSERT INTO Puntuaciones(nombre, puntuacion) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(URL_DB);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setInt(2, puntuacion);
            pstmt.executeUpdate();
            System.out.println("Puntuación guardada para " + nombre + ": " + puntuacion);
            return true;

        } catch (SQLException e) {
            System.err.println("Error al guardar puntuación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Puedes añadir más métodos si los necesitas, como borrar puntuaciones, etc.
}
