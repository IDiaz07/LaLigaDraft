package gui.ventanas;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import bd.GestorDatos;
import gui.clases.Usuario;
import gui.clases.PanelLogo; // Importamos tu nuevo logo

/**
 * Ventana para el inicio de sesión.
 * Diseño visual mejorado + Lógica original intacta.
 */
public class VentanaIniciarSesion extends JFrame {

    private JTextField usuario;
    private JPasswordField contraseña;

    public VentanaIniciarSesion() {
        setTitle("Inicio de Sesion");
        // Ajustamos tamaño para que sea igual que la ventana de Registro
        setSize(450, 750); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(18, 18, 18));

        // 1. LOGO (Sustituye al JLabel de texto)
        // Lo metemos en un panel contenedor para darle margen superior
        JPanel panelLogo = new JPanel();
        panelLogo.setBackground(new Color(18, 18, 18));
        panelLogo.setBorder(new EmptyBorder(40, 0, 20, 0));
        panelLogo.add(new PanelLogo());
        
        panelPrincipal.add(panelLogo, BorderLayout.NORTH);

        // 2. CAMPOS DE TEXTO
        JPanel panelCampos = new JPanel();
        panelCampos.setLayout(new BoxLayout(panelCampos, BoxLayout.Y_AXIS));
        panelCampos.setBackground(new Color(18, 18, 18));
        // Ajustamos márgenes para centrar visualmente
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        usuario = new JTextField();
        contraseña = new JPasswordField();

        // Estilizado moderno
        estiloCampo(usuario);
        estiloCampo(contraseña);

        // Tus placeholders originales
        addPlaceholder(usuario, "Nombre de Usuario");
        addPlaceholder(contraseña, "Contraseña");

        usuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        contraseña.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelCampos.add(usuario);
        panelCampos.add(Box.createVerticalStrut(30)); // Más espacio entre campos
        panelCampos.add(contraseña);

        panelPrincipal.add(panelCampos, BorderLayout.CENTER);

        // 3. BOTONES
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 20, 0));
        // Márgenes ajustados para que quede pegado abajo pero con aire
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 40, 60, 40)); 
        panelBotones.setBackground(new Color(18, 18, 18));
        panelBotones.setPreferredSize(new Dimension(450, 130)); // Altura fija zona inferior

        JButton botonAtras = crearBoton("Atrás", new Color(90, 90, 90)); // Gris
        JButton botonIniciarSesion = crearBoton("Entrar", new Color(52, 152, 219)); // Azul

        panelBotones.add(botonAtras);
        panelBotones.add(botonIniciarSesion);

        // --- LÓGICA ORIGINAL EXACTA ---
        botonIniciarSesion.addActionListener((ActionEvent e) -> {
            String nombreUsuario = usuario.getText().trim();
            // Cuidado con el placeholder: si el texto es el placeholder, tratamos como vacío
            if(nombreUsuario.equals("Nombre de Usuario")) nombreUsuario = "";
            
            String pass = new String(contraseña.getPassword()).trim();
            if(pass.equals("Contraseña")) pass = "";

            if (nombreUsuario.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debes rellenar todos los campos");
                return;
            }

            // Tu búsqueda con Streams
            String finalNombre = nombreUsuario; // Variable efectiva final para lambda
            Usuario usuarioEncontrado = GestorDatos.usuarios.values().stream()
                    .filter(u -> u.getNombre().equalsIgnoreCase(finalNombre))
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

            // Lógica de navegación original
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

   
    // --- ESTILOS VISUALES MEJORADOS ---
    private void estiloCampo(JTextField field) {
        field.setBackground(new Color(28, 28, 28));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        // Borde sutil y padding interno
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Altura fija cómoda
    }

    private JButton crearBoton(String txt, Color bg) {
        JButton b = new JButton(txt);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 18));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // --- TUS PLACEHOLDERS ORIGINALES (INTACTOS) ---
    
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
        passwordField.setEchoChar((char) 0);
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
                    passwordField.setEchoChar((char) 0);
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