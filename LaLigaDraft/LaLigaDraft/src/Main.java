import javax.swing.SwingUtilities;

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
