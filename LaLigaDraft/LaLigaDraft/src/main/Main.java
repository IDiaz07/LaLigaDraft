package main;

import javax.swing.SwingUtilities;

import bd.GestorDatos;
import gui.ventanas.VentanaInicio;

/**
 * Clase principal que actúa como punto de entrada de la aplicación.
 * Se encarga de cargar los datos necesarios y lanzar la interfaz gráfica.
 */
public class Main {
    /**
     * Método principal que inicia la ejecución.
     * 
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        // Carga los datos de usuarios, jugadores y ligas desde la base de datos
        GestorDatos.cargarTodo();

        // Comprueba si el mercado está vacío y, si es así, genera jugadores aleatorios
        GestorDatos.rellenarMercadoSiVacio();

        System.out.println("Usuarios cargados: " + GestorDatos.usuarios.size());

        // Inicia la interfaz gráfica 
        SwingUtilities.invokeLater(() -> {
            new VentanaInicio().setVisible(true);
        });
    }
}
