package Controlador;

import Modelo.Escenario;
import Modelo.Jugador;
import Modelo.ObjetoEscenario;
import Vista.VistaEscenario;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.BufferedWriter;

/**
 * Clase ControladorVistaEscenario
 *
 * Gestiona la lógica principal del juego:
 * - Carga/creación de Jugador (ahora en .bin).
 * - Carga/creación de Escenario (nuevo formato .txt).
 * - Bucle principal del juego con movimiento WASD (consola).
 * - Interacción con la Vista.
 *
 * @author Santiago
 * @author Juan
 * @version 0.2.0 (Jugador Binario, Escenario Nuevo Formato, Movimiento Básico)
 * @License GPL-3.0 license || ©2025
 */
public class ControladorVistaEscenario {

    private Escenario modeloEscenario;
    private Jugador modeloJugador;
    private VistaEscenario vista;
    private static Scanner scanner = new Scanner(System.in); // Scanner para entrada

    // Rutas y extensiones actualizadas
    private final String RUTA_BASE_JUGADORES = "jugadores";
    private final String RUTA_BASE_ESCENARIOS = "escenarios";
    private final String EXTENSION_JUGADOR = ".bin"; // Cambiado a .bin
    // Podrías tener una lista o pedir al usuario qué escenario cargar
    private final String[] NOMBRES_ESCENARIOS = {
            "escenario1.txt",
            "escenario2.txt",
            "escenario3.txt",
            "escenario4.txt"
    };
    private String escenarioActualNombre = NOMBRES_ESCENARIOS[0]; // Empezar con el primero por defecto

    /**
     * Constructor parametrizado
     *
     * @param vista VistaEscenario que se controla
     */
    public ControladorVistaEscenario(VistaEscenario vista) {
        if (vista == null)
            throw new IllegalArgumentException("La vista no puede ser null.");
        this.vista = vista;
    }

    /**
     * Inicia el juego: carga o crea jugador y escenario, luego entra al bucle principal.
     */
    public void iniciarOContinuarJuego() {
        vista.mostrarMensaje("Iniciando o continuando juego...");

        // 1. Gestionar Jugador (ahora con .bin)
        this.modeloJugador = gestionarCargaOCreacionJugador();
        if (this.modeloJugador == null) {
            vista.mostrarMensaje("Error crítico: No se pudo cargar ni crear el jugador. Saliendo.");
            return;
        }

        // 2. Seleccionar y Cargar Escenario (nuevo formato .txt)
        seleccionarEscenario(); // Permitir al usuario elegir
        this.modeloEscenario = gestionarCargaOCreacionEscenario(this.escenarioActualNombre);
        if (this.modeloEscenario == null) {
            vista.mostrarMensaje("Error crítico: No se pudo cargar ni generar un escenario. Saliendo.");
            // Opcional: ¿Intentar cargar otro escenario o salir?
            return;
        }

        // 3. Validar/Ajustar posición inicial del jugador si es necesario
        validarPosicionInicialJugador();


        vista.mostrarMensaje("\n¡Bienvenido, " + modeloJugador.getNombre() + "!");
        vista.mostrarMensaje("Escenario cargado: " + this.escenarioActualNombre);

        // 4. Iniciar Bucle Principal del Juego
        buclePrincipalJuego();

        // 5. (Opcional) Guardar estado al salir
        guardarEstadoJuego();

         vista.mostrarMensaje("\n¡Gracias por jugar!");
    }

    /**
     * Permite al usuario seleccionar qué escenario cargar de la lista NOMBRES_ESCENARIOS.
     */
    private void seleccionarEscenario() {
        vista.mostrarMensaje("\n--- Selección de Escenario ---");
        if (NOMBRES_ESCENARIOS == null || NOMBRES_ESCENARIOS.length == 0) {
            vista.mostrarMensaje("No hay escenarios definidos para seleccionar.");
            // Podrías asignar uno por defecto o manejar el error de otra forma
            this.escenarioActualNombre = "default_scenario.txt"; // Nombre genérico
            return;
        }

        for (int i = 0; i < NOMBRES_ESCENARIOS.length; i++) {
            vista.mostrarMensaje((i + 1) + ". " + NOMBRES_ESCENARIOS[i]);
        }

        int seleccion = -1;
        while (seleccion < 1 || seleccion > NOMBRES_ESCENARIOS.length) {
            vista.mostrarMensaje("Elige el número del escenario a cargar (1-" + NOMBRES_ESCENARIOS.length + "):");
            try {
                seleccion = scanner.nextInt();
                if (seleccion < 1 || seleccion > NOMBRES_ESCENARIOS.length) {
                    vista.mostrarMensaje("Selección inválida.");
                }
            } catch (InputMismatchException e) {
                vista.mostrarMensaje("Entrada inválida. Por favor, introduce un número.");
                scanner.next(); // Limpiar buffer del scanner
            }
        }
        scanner.nextLine(); // Consumir el salto de línea restante

        this.escenarioActualNombre = NOMBRES_ESCENARIOS[seleccion - 1];
        vista.mostrarMensaje("Has seleccionado: " + this.escenarioActualNombre);
    }


    /**
     * Gestiona la carga o creación del objeto Jugador (.bin).
     * Pide nombre de usuario, busca archivo .bin, carga o pide email y crea/guarda.
     * @return El objeto Jugador cargado o recién creado, o null si hay error.
     */
    private Jugador gestionarCargaOCreacionJugador() {
        String nombreUsuario = "";
        Jugador jugador = null;

        // Bucle para obtener un nombre de usuario válido
        while (nombreUsuario.trim().isEmpty()) {
            vista.mostrarMensaje("Introduce tu nombre de usuario:");
            try {
                nombreUsuario = scanner.nextLine().trim();
                if (nombreUsuario.isEmpty()) {
                    vista.mostrarMensaje("El nombre de usuario no puede estar vacío.");
                } else if (nombreUsuario.matches(".*[\\\\/:*?\"<>|].*")) { // Caracteres inválidos para nombres de archivo
                    vista.mostrarMensaje("El nombre de usuario contiene caracteres no válidos para nombres de archivo.");
                    nombreUsuario = ""; // Resetear para volver a pedir
                }
            } catch (Exception e) { // Captura genérica por si acaso
                vista.mostrarMensaje("Error al leer el nombre de usuario: " + e.getMessage());
                return null; // Salir si hay error de lectura
            }
        }

        // Intentar cargar jugador desde archivo binario
        String nombreArchivoJugador = nombreUsuario + EXTENSION_JUGADOR; // Ahora .bin
        jugador = cargarJugadorBinario(nombreArchivoJugador);

        if (jugador != null) {
            vista.mostrarMensaje("Jugador '" + jugador.getNombre() + "' cargado desde archivo binario.");
            // Aquí podrías cargar también la última posición guardada
        } else {
            // Si no se carga, crear uno nuevo
            vista.mostrarMensaje("No se encontró archivo para '" + nombreUsuario + "'. Creando nuevo jugador.");
            String correo = "";
            // Bucle para obtener un correo válido
            while (correo.trim().isEmpty()) {
                vista.mostrarMensaje("Introduce tu correo electrónico:");
                 try {
                    correo = scanner.nextLine().trim();
                    if (correo.isEmpty()) {
                        vista.mostrarMensaje("El correo no puede estar vacío.");
                    } else if (!correo.contains("@") || !correo.contains(".")) { // Validación muy básica de email
                        vista.mostrarMensaje("Formato de correo electrónico inválido.");
                        correo = ""; // Resetear para volver a pedir
                    }
                 } catch (Exception e) {
                    vista.mostrarMensaje("Error al leer el correo: " + e.getMessage());
                    return null; // Salir si hay error
                 }
            }

            // Crear nuevo jugador (posición inicial por defecto, p.ej., 1,1)
            // Se podría pedir o calcular una posición inicial más inteligente
            int filaInicial = 1;
            int columnaInicial = 1;
            try {
                jugador = new Jugador(nombreUsuario, correo, filaInicial, columnaInicial);
                vista.mostrarMensaje("Nuevo jugador creado: " + jugador.toString());
                // Guardar el nuevo jugador en archivo binario
                if (!guardarJugadorBinario(jugador, nombreArchivoJugador)) {
                    vista.mostrarMensaje("ADVERTENCIA: No se pudo guardar el nuevo jugador en archivo binario.");
                    // Considerar si esto es un error crítico o no
                } else {
                    vista.mostrarMensaje("Nuevo jugador guardado en '" + nombreArchivoJugador + "'.");
                }
            } catch (IllegalArgumentException e) {
                vista.mostrarMensaje("Error al crear jugador: " + e.getMessage());
                return null; // Error al crear la instancia
            }
        }
        return jugador;
    }

    /**
     * Gestiona la carga o creación del objeto Escenario usando el nuevo formato.
     * @param nombreArchivo El nombre del archivo de escenario a cargar/crear.
     * @return El objeto Escenario cargado o recién generado, o null si ocurre un error.
     */
    private Escenario gestionarCargaOCreacionEscenario(String nombreArchivo) {
        vista.mostrarMensaje("Intentando cargar escenario desde '" + nombreArchivo + "' (nuevo formato)...");
        // Usar el nuevo método de carga
        Escenario escenario = Escenario.cargarDesdeArchivoNuevoFormato(Paths.get(RUTA_BASE_ESCENARIOS, nombreArchivo).toString());

        if (escenario == null) {
            vista.mostrarMensaje("No se pudo cargar '" + nombreArchivo + "' o el archivo tiene errores.");
            vista.mostrarMensaje("Generando y guardando un escenario por defecto en '" + nombreArchivo + "'.");
            try {
                // Crear un escenario por defecto (p.ej., 20x10)
                escenario = generarEscenarioPorDefecto(20, 10); // Ancho 20, Alto 10
                // Guardarlo con el nombre esperado para futuros usos
                if (!escenario.guardarEnArchivoNuevoFormato(Paths.get(RUTA_BASE_ESCENARIOS, nombreArchivo).toString())) {
                     vista.mostrarMensaje("Advertencia: No se pudo guardar el escenario por defecto generado.");
                }
            } catch (IllegalArgumentException e) {
                vista.mostrarMensaje("Error al generar escenario por defecto: " + e.getMessage());
                return null; // Error crítico si no se puede generar
            }
        } else {
            vista.mostrarMensaje("Escenario cargado correctamente desde '" + nombreArchivo + "'.");
        }
        return escenario;
    }

     /**
     * Verifica si la posición actual del jugador es válida dentro del escenario cargado.
     * Si no es válida (fuera de límites o bloqueada), intenta encontrar una posición inicial válida.
     */
    private void validarPosicionInicialJugador() {
        if (modeloEscenario == null || modeloJugador == null) return;

        int fila = modeloJugador.getFila();
        int col = modeloJugador.getColumna();

        if (!modeloEscenario.esPosicionValida(fila, col) || modeloEscenario.esPosicionBloqueada(fila, col)) {
            vista.mostrarMensaje("Posición inicial del jugador (" + fila + "," + col + ") inválida o bloqueada.");
            // Buscar una posición inicial alternativa (ej: la primera celda no bloqueada)
            boolean encontrada = false;
            for (int i = 0; i < modeloEscenario.getFilas(); i++) {
                for (int j = 0; j < modeloEscenario.getColumnas(); j++) {
                    if (!modeloEscenario.esPosicionBloqueada(i, j)) {
                        modeloJugador.setPosicion(i, j);
                        vista.mostrarMensaje("Jugador reubicado en la primera posición válida: (" + i + "," + j + ")");
                        encontrada = true;
                        break;
                    }
                }
                if (encontrada) break;
            }
            if (!encontrada) {
                vista.mostrarMensaje("¡ADVERTENCIA! No se encontró ninguna posición válida para el jugador en este escenario.");
                // Aquí el juego podría no ser jugable. Considerar terminar o manejar de otra forma.
            }
        }
    }


    /**
     * Bucle principal del juego donde el usuario introduce comandos (WASD).
     */
    private void buclePrincipalJuego() {
        String comando = "";
        boolean salir = false;

        while (!salir) {
            vista.limpiarPantalla(); // Limpiar la consola antes de mostrar
            mostrarEscenarioActual(); // Mostrar estado actual
            vista.mostrarMensaje("Mover: [W] Arriba [A] Izquierda [S] Abajo [D] Derecha | [Q] Salir");
            vista.mostrarMensaje("Introduce tu movimiento: ");

            try {
                 comando = scanner.nextLine().trim().toUpperCase(); // Leer línea y convertir a mayúsculas
            } catch (Exception e) {
                 vista.mostrarMensaje("Error leyendo entrada. Intenta de nuevo.");
                 continue; // Saltar al siguiente ciclo
            }


            if (comando.length() != 1) {
                vista.mostrarMensaje("Comando inválido. Usa W, A, S, D o Q.");
                pausaBreve(); // Pequeña pausa para que el usuario vea el mensaje
                continue; // Volver a pedir comando
            }

            char accion = comando.charAt(0);

            switch (accion) {
                case 'W':
                    moverJugador(-1, 0); // Mover arriba (fila - 1)
                    break;
                case 'A':
                    moverJugador(0, -1); // Mover izquierda (columna - 1)
                    break;
                case 'S':
                    moverJugador(1, 0); // Mover abajo (fila + 1)
                    break;
                case 'D':
                    moverJugador(0, 1); // Mover derecha (columna + 1)
                    break;
                case 'Q':
                    salir = true;
                    vista.mostrarMensaje("Saliendo del juego...");
                    break;
                default:
                    vista.mostrarMensaje("Comando desconocido: '" + accion + "'. Usa W, A, S, D o Q.");
                    pausaBreve();
                    break;
            }
            // Podrías añadir una pequeña pausa aquí si el juego va muy rápido
            // pausaBreve();
        }
    }

    /**
     * Intenta mover al jugador en la dirección indicada.
     * Verifica límites y colisiones con objetos bloqueantes.
     * @param deltaFila Cambio en la fila (-1, 0, 1)
     * @param deltaColumna Cambio en la columna (-1, 0, 1)
     */
    private void moverJugador(int deltaFila, int deltaColumna) {
        if (modeloJugador == null || modeloEscenario == null) return;

        int filaActual = modeloJugador.getFila();
        int columnaActual = modeloJugador.getColumna();

        int nuevaFila = filaActual + deltaFila;
        int nuevaColumna = columnaActual + deltaColumna;

        // 1. Verificar límites del escenario
        if (!modeloEscenario.esPosicionValida(nuevaFila, nuevaColumna)) {
            vista.mostrarMensaje("¡Auch! Te chocaste con el borde.");
            pausaBreve();
            return; // No mover, está fuera de los límites
        }

        // 2. Verificar si la nueva posición está bloqueada
        if (modeloEscenario.esPosicionBloqueada(nuevaFila, nuevaColumna)) {
             ObjetoEscenario obj = modeloEscenario.getObjetoEn(nuevaFila, nuevaColumna);
             vista.mostrarMensaje("¡Bloqueado por un objeto! (" + (obj != null ? obj.getObjetoChar() : '?') + ")");
             pausaBreve();
             return; // No mover, hay un objeto bloqueante
        }

        // 3. Si es válida y no está bloqueada, actualizar posición del jugador
        modeloJugador.setPosicion(nuevaFila, nuevaColumna);
        // No es necesario redibujar aquí, el bucle principal lo hará
    }


    /**
     * Muestra el escenario actual usando la vista.
     */
    public void mostrarEscenarioActual() {
        if (vista != null && modeloEscenario != null) {
            // Pasar el jugador a la vista para que sepa dónde dibujarlo
            String representacion = vista.vistaEscenario(modeloEscenario, modeloJugador);
           // vista.mostrarMensaje("\n--- Escenario Actual ---"); // El borde ya lo indica
            vista.mostrarMensaje(representacion);
           // vista.mostrarMensaje("------------------------\n"); // El borde ya lo indica
        } else {
            vista.mostrarMensaje("Error: No se puede mostrar el escenario (vista, modeloEscenario o modeloJugador no disponibles).");
        }
    }

    /**
     * Genera un mapa de escenario por defecto con bordes y un obstáculo.
     * @param ancho Ancho deseado.
     * @param alto Alto deseado.
     * @return Un nuevo objeto Escenario.
     */
    private Escenario generarEscenarioPorDefecto(int ancho, int alto) {
         if (alto <= 0 || ancho <= 0) {
             throw new IllegalArgumentException("Dimensiones deben ser positivas para generar escenario.");
         }

         Escenario escenario;
         try {
             Path tempPath = Paths.get(RUTA_BASE_ESCENARIOS, "___temp_default.txt");
             try (BufferedWriter writer = Files.newBufferedWriter(tempPath)) {
                 writer.write(ancho + "X" + alto);
                 writer.newLine();
                 // Crear línea de mapa simple: Bordes 'O', interior 'E'
                 StringBuilder mapLine = new StringBuilder();
                 mapLine.append(ancho).append('O'); // Borde superior
                 for(int i = 1; i < alto - 1; i++) {
                     mapLine.append(" 1O "); // Borde izquierdo
                     mapLine.append(ancho - 2).append('E'); // Espacio interior
                     mapLine.append(" 1O"); // Borde derecho
                 }
                  mapLine.append(" ").append(ancho).append('O'); // Borde inferior
                 writer.write(mapLine.toString().replaceAll(" +", " ").trim()); // Limpiar espacios extra
                 writer.newLine();
             }
             escenario = Escenario.cargarDesdeArchivoNuevoFormato(tempPath.toString());
             Files.deleteIfExists(tempPath); // Borrar archivo temporal

             if (escenario == null) throw new RuntimeException("Fallo al crear escenario por defecto");

             // Añadir un obstáculo extra si hay espacio
             if (alto > 2 && ancho > 2) {
                  escenario.colocarObjeto(alto / 2, ancho / 2, new ObjetoEscenario('X', true));
             }


         } catch (Exception e) {
              System.err.println("Error crítico generando escenario por defecto: " + e.getMessage());
              throw new RuntimeException("No se pudo generar el escenario por defecto.", e);
         }


        return escenario;
    }

    /**
     * Guarda el estado actual del juego (Jugador).
     * El escenario se guarda cada vez que se genera/carga uno por defecto,
     * o podrías añadir una opción para guardarlo explícitamente.
     */
    private void guardarEstadoJuego() {
        if (modeloJugador != null) {
            String nombreArchivo = modeloJugador.getNombre() + EXTENSION_JUGADOR;
            vista.mostrarMensaje("Guardando datos del jugador en " + nombreArchivo + "...");
            if (guardarJugadorBinario(modeloJugador, nombreArchivo)) {
                vista.mostrarMensaje("Datos guardados.");
            } else {
                vista.mostrarMensaje("Error al guardar los datos del jugador.");
            }
        }
        // Opcional: Guardar el escenario actual si se hicieron cambios
        if (modeloEscenario != null && this.escenarioActualNombre != null) {
            vista.mostrarMensaje("Guardando escenario actual...");
            if (guardarEscenarioNuevoFormato(modeloEscenario, this.escenarioActualNombre)) {
                vista.mostrarMensaje("Escenario guardado.");
            } else {
                 vista.mostrarMensaje("Error al guardar el escenario.");
            }
        }
    }


    /**
     * Intenta cargar un jugador desde un archivo binario.
     * @param nombreArchivo El nombre del archivo (ej. "usuario1.bin").
     * @return El Jugador cargado o null si no se encontró o hubo un error.
     */
    private Jugador cargarJugadorBinario(String nombreArchivo) {
        Path rutaCompleta = Paths.get(RUTA_BASE_JUGADORES, nombreArchivo);
        // Usar el método estático de Jugador para cargar binario
        return Jugador.cargarDesdeArchivoBinario(rutaCompleta.toString());
    }

    /**
     * Guarda un jugador en un archivo binario.
     * @param jugador El objeto Jugador a guardar.
     * @param nombreArchivo El nombre del archivo (ej. "usuario1.bin").
     * @return true si se guardó correctamente, false en caso contrario.
     */
    private boolean guardarJugadorBinario(Jugador jugador, String nombreArchivo) {
        if (jugador == null)
            return false;
        Path rutaCompleta = Paths.get(RUTA_BASE_JUGADORES, nombreArchivo);
        vista.mostrarMensaje("Guardando jugador en (binario): " + rutaCompleta.toAbsolutePath());
        // Usar el método de instancia de Jugador para guardar binario
        return jugador.guardarEnArchivoBinario(rutaCompleta.toString());
    }

    /**
     * Pausa la ejecución por un corto período de tiempo (milisegundos).
     * Útil para que los mensajes de error/estado sean visibles antes de limpiar la pantalla.
     */
    private void pausaBreve() {
        try {
            Thread.sleep(1500); // Pausa de 1.5 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restablecer estado de interrupción
            vista.mostrarMensaje("Pausa interrumpida.");
        }
    }

    // Métodos para guardar/cargar escenario (ya estaban usando los correctos,
    // pero los renombro por claridad y uso el nuevo formato)

    /**
     * Intenta cargar un escenario desde un archivo de texto con el nuevo formato.
     * @param nombreArchivo El nombre del archivo.
     * @return El Escenario cargado o null si hubo error.
     */
    private Escenario cargarEscenarioNuevoFormato(String nombreArchivo) {
        Path rutaCompleta = Paths.get(RUTA_BASE_ESCENARIOS, nombreArchivo);
        return Escenario.cargarDesdeArchivoNuevoFormato(rutaCompleta.toString());
    }

    /**
     * Guarda un escenario en un archivo de texto usando el nuevo formato.
     * @param escenario El objeto Escenario a guardar.
     * @param nombreArchivo El nombre del archivo.
     * @return true si se guardó correctamente, false en caso contrario.
     */
    private boolean guardarEscenarioNuevoFormato(Escenario escenario, String nombreArchivo) {
        if (escenario == null)
            return false;
        Path rutaCompleta = Paths.get(RUTA_BASE_ESCENARIOS, nombreArchivo);
        vista.mostrarMensaje("Guardando escenario (nuevo formato) en: " + rutaCompleta.toAbsolutePath());
        return escenario.guardarEnArchivoNuevoFormato(rutaCompleta.toString());
    }
}
