import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;

public class VentanaRegistro extends JFrame {

    private JTextField usuario;
    private JTextField email;
    private JTextField tf;
    private JPasswordField contraseña;

    public VentanaRegistro() {
        setTitle("Registro de Sesion");
        setSize(400, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());
        JLabel labelIniciarSesion = new JLabel("REGISTRAR SESION", SwingConstants.CENTER);
        labelIniciarSesion.setFont(new Font("Arial", Font.BOLD, 20));
        labelIniciarSesion.setBorder(BorderFactory.createEmptyBorder(40, 10, 40, 10));
        panelPrincipal.add(labelIniciarSesion, BorderLayout.NORTH);

        JPanel panelCampos = new JPanel();
        panelCampos.setLayout(new GridLayout(4, 1, 20, 20));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(150, 50, 5, 50));

        usuario = new JTextField();
        email = new JTextField();
        tf = new JTextField();
        contraseña = new JPasswordField();

        addPlaceholder(usuario, "Nombre de Usuario");
        addPlaceholder(email, "Email");
        addPlaceholder(tf, "Telefono");
        addPlaceholder(contraseña, "Contraseña");

        panelCampos.add(usuario);
        panelCampos.add(email);
        panelCampos.add(tf);
        panelCampos.add(contraseña);
        panelPrincipal.add(panelCampos);

        // Panel para los botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(1, 2, 20, 20));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(5, 50, 100, 50));

        JButton botonAtras = new JButton("Atras");
        JButton botonRegistrarse = new JButton("Registrarse");

        panelBotones.add(botonAtras);
        panelBotones.add(botonRegistrarse);

        // Acción botón Registrarse
        botonRegistrarse.addActionListener((ActionEvent e) -> {
            String nombreUsuario = usuario.getText().trim();
            String correo = email.getText().trim();
            String telefono = tf.getText().trim();
            String pass = new String(contraseña.getPassword()).trim();

            // Validaciones
            if (nombreUsuario.isEmpty() || nombreUsuario.equals("Nombre de Usuario") ||
                correo.isEmpty() || correo.equals("Email") ||
                telefono.isEmpty() || telefono.equals("Telefono") ||
                pass.isEmpty() || pass.equals("Contraseña")) {
                
                JOptionPane.showMessageDialog(this, "Debes rellenar todos los campos",
                        "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (pass.length() < 8) {
                JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 8 caracteres",
                        "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean existe = GestorDatos.usuarios.values().stream()
                    .anyMatch(u -> u.getNombre().equalsIgnoreCase(nombreUsuario));

            if (existe) {
                JOptionPane.showMessageDialog(this, "El nombre de usuario ya existe",
                        "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Registrar el usuario con saldo inicial de 1.000.000
            GestorDatos.registrarUsuario(nombreUsuario, correo, telefono, pass, 1000000);

            JOptionPane.showMessageDialog(this, "Usuario registrado correctamente",
                    "Registro", JOptionPane.INFORMATION_MESSAGE);

            abrirVentanaInicio();
        });

        // Acción botón Atrás
        botonAtras.addActionListener((ActionEvent e) -> {
            abrirVentanaInicio();
        });

        add(panelPrincipal, BorderLayout.NORTH);
        add(panelBotones, BorderLayout.SOUTH);
    }

    // Placeholders para JTextField
    public static void addPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
    }

    // Placeholders para JPasswordField
    public static void addPlaceholder(JPasswordField passwordField, String placeholder) {
        passwordField.setEchoChar((char)0);
        passwordField.setText(placeholder);
        passwordField.setForeground(Color.GRAY);

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String pass = new String(passwordField.getPassword());
                if (pass.equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                    passwordField.setEchoChar('•');
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                String pass = new String(passwordField.getPassword());
                if (pass.isEmpty()) {
                    passwordField.setEchoChar((char)0);
                    passwordField.setText(placeholder);
                    passwordField.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void abrirVentanaInicio() {
        SwingUtilities.invokeLater(() -> {
            VentanaInicio ventanaI = new VentanaInicio();
            ventanaI.setVisible(true);
        });
        dispose();
    }
}
