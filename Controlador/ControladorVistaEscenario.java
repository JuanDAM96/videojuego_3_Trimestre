package Controlador;

import Modelo.Escenario;
import Modelo.Jugador;
import Modelo.ObjetoEscenario;
import Vista.VistaEscenario;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;


public class ControladorVistaEscenario {


    private Escenario modeloEscenario;
    private Jugador modeloJugador;
    private VistaEscenario vista; 
    private static Scanner scanner = new Scanner(System.in);


    private final String RUTA_BASE_JUGADORES = "jugadores";
    private final String RUTA_BASE_ESCENARIOS = "escenarios";
    private final String EXTENSION_JUGADOR = ".txt";
    private final String ARCHIVO_ESCENARIO_A_CARGAR = "escenario_predeterminado.txt";

    public ControladorVistaEscenario(VistaEscenario vista) {
        if (vista == null) throw new IllegalArgumentException("La vista no puede ser null.");
        this.vista = vista;
    }


    public void iniciarOContinuarJuego() {
        vista.mostrarMensaje("Iniciando o continuando juego...");

        this.modeloJugador = gestionarCargaOCreacionJugador();
        if (this.modeloJugador == null) {
             vista.mostrarMensaje("Error crítico: No se pudo cargar ni crear el jugador. Saliendo.");
             return;
        }

        this.modeloEscenario = gestionarCargaOCreacionEscenario();
        if (this.modeloEscenario == null) {
             vista.mostrarMensaje("Error crítico: No se pudo cargar ni generar un escenario. Saliendo.");
             return;
        }

        vista.mostrarMensaje("\n¡Bienvenido, " + modeloJugador.getNombre() + "!");
        mostrarEscenarioActual();
    }


    private Jugador gestionarCargaOCreacionJugador() {
        String nombreUsuario = "";
        Jugador jugador = null;

        while (nombreUsuario.trim().isEmpty()) {
            vista.mostrarMensaje("Introduce tu nombre de usuario:");
            try {
                 nombreUsuario = scanner.nextLine().trim();
                 if (nombreUsuario.isEmpty()) {
                     vista.mostrarMensaje("El nombre de usuario no puede estar vacío.");
                 } else if (nombreUsuario.matches(".*[\\\\/:*?\"<>|].*")) {
                     vista.mostrarMensaje("El nombre de usuario contiene caracteres no válidos.");
                     nombreUsuario = "";
                 }
            } catch (Exception e) {
                 vista.mostrarMensaje("Error al leer el nombre de usuario: " + e.getMessage());
                 return null;
            }
        }

        String nombreArchivoJugador = nombreUsuario + EXTENSION_JUGADOR;
        jugador = cargarJugadorTexto(nombreArchivoJugador);

        if (jugador != null) {
            vista.mostrarMensaje("Jugador '" + jugador.getNombre() + "' cargado desde archivo de texto.");
        } else {
            vista.mostrarMensaje("No se encontró archivo para '" + nombreUsuario + "'. Creando nuevo jugador.");
            String correo = "";
            while (correo.trim().isEmpty()) {
                 vista.mostrarMensaje("Introduce tu correo electrónico:");
                 try {
                      correo = scanner.nextLine().trim();
                      if (correo.isEmpty()) {
                          vista.mostrarMensaje("El correo no puede estar vacío.");
                      } else if (!correo.contains("@") || !correo.contains(".")) {
                           vista.mostrarMensaje("Formato de correo electrónico inválido.");
                           correo = "";
                      }
                 } catch (Exception e) {
                      vista.mostrarMensaje("Error al leer el correo: " + e.getMessage());
                      return null;
                 }
            }

            try {
                 jugador = new Jugador(nombreUsuario, correo);
                 vista.mostrarMensaje("Nuevo jugador creado: " + jugador.toString());
                 if (!guardarJugadorTexto(jugador, nombreArchivoJugador)) {
                      vista.mostrarMensaje("ADVERTENCIA: No se pudo guardar el nuevo jugador en archivo de texto.");
                 } else {
                      vista.mostrarMensaje("Nuevo jugador guardado en '" + nombreArchivoJugador + "'.");
                 }
            } catch (IllegalArgumentException e) {
                 vista.mostrarMensaje("Error al crear jugador: " + e.getMessage());
                 return null;
            }
        }
        return jugador;
    }


    private Escenario gestionarCargaOCreacionEscenario() {
        vista.mostrarMensaje("Intentando cargar escenario desde '" + ARCHIVO_ESCENARIO_A_CARGAR + "'...");
        Escenario escenario = cargarEscenarioTexto(ARCHIVO_ESCENARIO_A_CARGAR);

        if (escenario == null) {
             vista.mostrarMensaje("No se pudo cargar el escenario desde archivo o el archivo no existe.");
             vista.mostrarMensaje("Generando escenario por defecto (no se guardará en formato de texto).");
             try {
                escenario = new Escenario(10, 20);
                generarMapaPorDefecto(escenario);
             } catch (IllegalArgumentException e) {
                 vista.mostrarMensaje("Error al generar escenario por defecto: " + e.getMessage());
                 return null;
             }
        } else {
             vista.mostrarMensaje("Escenario cargado correctamente desde '" + ARCHIVO_ESCENARIO_A_CARGAR + "'.");
        }
        return escenario;
    }


    public void mostrarEscenarioActual() {
        if (vista != null && modeloEscenario != null) {
            String representacion = vista.vistaEscenario(modeloEscenario);
            vista.mostrarMensaje("\n--- Escenario Actual ---");
            vista.mostrarMensaje(representacion);
            vista.mostrarMensaje("------------------------\n");
        } else {
             vista.mostrarMensaje("Error: No se puede mostrar el escenario (vista o modelo no disponibles).");
        }
    }

    private void generarMapaPorDefecto(Escenario escenario) {
        if (escenario == null) return;
        int filas = escenario.getFilas();
        int columnas = escenario.getColumnas();
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (i == 0 || i == filas - 1 || j == 0 || j == columnas - 1) {
                    escenario.colocarObjeto(i, j, new ObjetoEscenario('#', true));
                } else {
                    escenario.colocarObjeto(i, j, new ObjetoEscenario('.', false));
                }
            }
        }
        if (filas > 2 && columnas > 2) {
             escenario.colocarObjeto(filas / 2, columnas / 2, new ObjetoEscenario('X', false));
        }
    }



    private Jugador cargarJugadorTexto(String nombreArchivo) {
        Path rutaCompleta = Paths.get(RUTA_BASE_JUGADORES, nombreArchivo);
        return Jugador.cargarDesdeArchivo(rutaCompleta.toString());
    }


    private boolean guardarJugadorTexto(Jugador jugador, String nombreArchivo) {
        if (jugador == null) return false;
        Path rutaCompleta = Paths.get(RUTA_BASE_JUGADORES, nombreArchivo);
         vista.mostrarMensaje("Guardando jugador en: " + rutaCompleta.toAbsolutePath());
        return jugador.guardarEnArchivo(rutaCompleta.toString());
    }


    private Escenario cargarEscenarioTexto(String nombreArchivo) {
        Path rutaCompleta = Paths.get(RUTA_BASE_ESCENARIOS, nombreArchivo);
        return Escenario.cargarDesdeArchivo(rutaCompleta.toString());
    }


    private boolean guardarEscenarioTexto(Escenario escenario, String nombreArchivo) {
        if (escenario == null) return false;
        Path rutaCompleta = Paths.get(RUTA_BASE_ESCENARIOS, nombreArchivo);
        vista.mostrarMensaje("Guardando escenario en formato texto en: " + rutaCompleta.toAbsolutePath());
        return escenario.guardarEnArchivo(rutaCompleta.toString());
    }

}