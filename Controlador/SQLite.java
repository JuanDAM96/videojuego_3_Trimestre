

import java.sql.*;
import java.util.Scanner;

public class SQLite{
    public static void comenzar(){
        Connection conexion = null;
        try {
            Scanner teclado = new Scanner(System.in);
            // Registrar el controlador JDBC de SQLite
            Class.forName("org.sqlite.JDBC");
            
            // Conectar a la base de datos (en este caso, una base de datos en memoria)
            conexion = DriverManager.getConnection("jdbc:sqlite:Prueba.db");
            
            System.out.println("Conectado a la base de datos SQLite.");
            
            // Crear una declaración SQL para interactuar con la base de datos
            Statement declarar = conexion.createStatement();
            declarar.setQueryTimeout(30);  // Configura un tiempo de espera de 30 segundos
            
            // Ejecutar una consulta SQL
            ResultSet resultado = null;
            
            // Procesar los resultados de la consulta
/*             String tnombre = teclado.nextLine();
            int tnota = teclado.nextInt();

            declarar.executeUpdate("INSERT INTO Usuario (nombre, nota) VALUES ('" + tnombre + "', '" + tnota + "');"); */
            
            declarar.executeUpdate("DELETE FROM Usuario WHERE nombre is null or nota is null;");
            /* declarar.executeUpdate("INSERT INTO Usuario (nombre, nota) VALUES ('Lucas', 5);"); */
            declarar.executeUpdate("INSERT INTO Tabla1 (fecha) VALUES ('2025-04-25 10:30:00');");
            // Procesar los resultados de la consulta
            resultado = declarar.getGeneratedKeys();
            while (resultado.next()) {
                System.out.println("Clave: " + resultado.getInt(1));
            }











            /*             resultado = declarar.executeQuery("SELECT * FROM Usuario;");
            while (resultado.next()) {
                System.out.println("Nombre: " + resultado.getString("nombre") + " Nota: " + resultado.getString("nota"));
            } */
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conexion != null) {
                    conexion.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}