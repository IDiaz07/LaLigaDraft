import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class VentanaIniciarSesion extends JFrame {
    
    private JTextField usuario;
    private JPasswordField contraseña;

    public VentanaIniciarSesion() {

        setTitle("Inicio de Sesion");
        setSize(600, 900); // ⬅ tamaño grande
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(18, 18, 18));

        // =============================
        //        TÍTULO
        // =============================
        JLabel labelIniciarSesion = new JLabel("INICIO DE SESIÓN", SwingConstants.CENTER);
        labelIniciarSesion.setFont(new Font("Arial", Font.BOLD, 32));
        labelIniciarSesion.setBorder(BorderFactory.createEmptyBorder(80, 10, 40, 10));
        labelIniciarSesion.setForeground(Color.WHITE);
        panelPrincipal.add(labelIniciarSesion, BorderLayout.NORTH);

        // =============================
        //        CAMPOS CENTRALES
        // =============================
        JPanel panelCampos = new JPanel();
        panelCampos.setLayout(new BoxLayout(panelCampos, BoxLayout.Y_AXIS));
        panelCampos.setBackground(new Color(18, 18, 18));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));

        usuario = new JTextField();
        contraseña = new JPasswordField();

        // Tamaño proporcional
        usuario.setMaximumSize(new Dimension(400, 45));
        contraseña.setMaximumSize(new Dimension(400, 45));

        estiloCampo(usuario);
        estiloCampo(contraseña);

        addPlaceholder(usuario, "Nombre de Usuario");
        addPlaceholder(contraseña, "Contraseña");

        usuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        contraseña.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelCampos.add(usuario);
        panelCampos.add(Box.createVerticalStrut(20));
        panelCampos.add(contraseña);

        panelPrincipal.add(panelCampos, BorderLayout.CENTER);

        // =============================
        //        BOTONES ABAJO
        // =============================
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 40, 40));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 100, 150, 100));
        panelBotones.setBackground(new Color(18, 18, 18));

        JButton botonAtras = crearBoton("Atrás");
        JButton botonIniciarSesion = crearBoton("Iniciar Sesión");

        panelBotones.add(botonAtras);
        panelBotones.add(botonIniciarSesion);

        // Acción botones
        botonIniciarSesion.addActionListener((ActionEvent e) -> {
            String nombreUsuario = usuario.getText().trim();
            String pass = new String(contraseña.getPassword()).trim();

            if (nombreUsuario.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debes rellenar todos los campos");
                return;
            }

            Usuario usuarioEncontrado = GestorDatos.usuarios.values().stream()
                    .filter(u -> u.getNombre().equalsIgnoreCase(nombreUsuario))
                    .findFirst()
                    .orElse(null);

            if (usuarioEncontrado == null) {
                JOptionPane.showMessageDialog(this, "El usuario no existe");
                return;
            }

            if (!usuarioEncontrado.getContrasena().equals(pass)) {
                JOptionPane.showMessageDialog(this, "Contraseña incorrecta");
                return;
            }

            GestorDatos.asignarEquipoInicial(usuarioEncontrado);

            if (usuarioEncontrado.getLigas() != null && !usuarioEncontrado.getLigas().isEmpty()) {
                if (usuarioEncontrado.getLigaActualId() == -1) {
                    usuarioEncontrado.setLigaActualId(usuarioEncontrado.getLigas().get(0));
                }
                dispose();
                SwingUtilities.invokeLater(() -> new VentanaPrincipal(usuarioEncontrado).setVisible(true));
            } else {
                dispose();
                SwingUtilities.invokeLater(() -> new VentanaSeleccionLiga(usuarioEncontrado).setVisible(true));
            }
        });

        botonAtras.addActionListener(e -> abrirVentanaInicio());

        add(panelPrincipal, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    // -------------------- ESTILO CAMPOS --------------------
    private void estiloCampo(JTextField field) {
        field.setBackground(new Color(28, 28, 28));
        field.setForeground(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
    }

    private JButton crearBoton(String txt) {
        JButton b = new JButton(txt);
        b.setBackground(new Color(231, 76, 60));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 22));
        b.setFocusPainted(false);
        return b;
    }

    // -------------------- PLACEHOLDERS --------------------
    public static void addPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);

        textField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.WHITE);
                }
            }

            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
    }

    public static void addPlaceholder(JPasswordField passwordField, String placeholder) {
        passwordField.setEchoChar((char)0);
        passwordField.setText(placeholder);
        passwordField.setForeground(Color.GRAY);

        passwordField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (new String(passwordField.getPassword()).equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.WHITE);
                    passwordField.setEchoChar('•');
                }
            }

            public void focusLost(FocusEvent e) {
                if (new String(passwordField.getPassword()).isEmpty()) {
                    passwordField.setEchoChar((char)0);
                    passwordField.setText(placeholder);
                    passwordField.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void abrirVentanaInicio() {
        new VentanaInicio().setVisible(true);
        dispose();
    }
}
