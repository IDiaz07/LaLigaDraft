package main;
import javax.swing.SwingUtilities;

import bd.GestorDatos;
import gui.ventanas.VentanaInicio;

public class Main {
    public static void main(String[] args) {
        GestorDatos.cargarTodo();
        GestorDatos.rellenarMercadoSiVacio();
        System.out.println("Usuarios cargados: " + GestorDatos.usuarios.size());

        SwingUtilities.invokeLater(() -> {
            new VentanaInicio().setVisible(true);
        });
    }
}
