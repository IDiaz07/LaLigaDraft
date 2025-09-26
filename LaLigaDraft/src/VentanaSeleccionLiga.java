// =============================================
// NUEVO: VentanaSeleccionLiga.java
// =============================================
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class VentanaSeleccionLiga extends JFrame {
    private final Usuario usuario;

    public VentanaSeleccionLiga(Usuario usuario) {
        this.usuario = usuario;
        setTitle("Elegir liga");
        setSize(420, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        JLabel titulo = new JLabel("¡Bienvenido, " + usuario.getNombre() + "!");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        root.add(titulo, BorderLayout.NORTH);

        JPanel opciones = new JPanel(new GridLayout(2, 1, 12, 12));
        JButton btnPublica = new JButton("Unirme a una liga pública");
        JButton btnPrivada = new JButton("Crear liga privada para mis amigos");
        opciones.add(btnPublica);
        opciones.add(btnPrivada);
        root.add(opciones, BorderLayout.CENTER);

        JButton btnSalir = new JButton("Salir");
        root.add(btnSalir, BorderLayout.SOUTH);

        setContentPane(root);

        btnPublica.addActionListener(e -> unirseALigaPublica());
        btnPrivada.addActionListener(e -> crearLigaPrivada());
        btnSalir.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                VentanaInicio v = new VentanaInicio();
                v.setVisible(true);
            });
        });
    }

    private void unirseALigaPublica() {
        // Busca una liga pública existente; si no hay, crea una por defecto
        Liga liga = GestorDatos.buscarLigaPublicaDisponible();
        if (liga == null) {
            liga = GestorDatos.registrarLiga("Liga Pública", true, null);
        }
        GestorDatos.agregarUsuarioALiga(usuario.getId(), liga.getId());
        JOptionPane.showMessageDialog(this, "Te uniste a: " + liga.getNombre());
        abrirPrincipal();
    }

    private void crearLigaPrivada() {
        JTextField nombre = new JTextField();
        JTextField codigo = new JTextField();
        JPanel form = new JPanel(new GridLayout(0,1,8,8));
        form.add(new JLabel("Nombre de la liga"));
        form.add(nombre);
        form.add(new JLabel("Código de invitación (opcional)"));
        form.add(codigo);
        int ok = JOptionPane.showConfirmDialog(this, form, "Crear liga privada",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok == JOptionPane.OK_OPTION) {
            String nom = nombre.getText().trim();
            String cod = codigo.getText().trim();
            if (nom.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.");
                return;
            }
            Liga liga = GestorDatos.registrarLiga(nom, false, cod.isEmpty()? null: cod);
            GestorDatos.agregarUsuarioALiga(usuario.getId(), liga.getId());
            JOptionPane.showMessageDialog(this, "Liga creada: " + liga.getNombre());
            abrirPrincipal();
        }
    }

    private void abrirPrincipal() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            // Si tu VentanaPrincipal llega a necesitar el usuario/liga, añade un constructor que los reciba
            new VentanaPrincipal().setVisible(true);
        });
    }
}