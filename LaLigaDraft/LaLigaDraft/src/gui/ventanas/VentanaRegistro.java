package gui.ventanas;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import bd.GestorDatos;
import gui.clases.PanelLogo; // Importamos tu logo

public class VentanaRegistro extends JFrame {

    private JTextField usuario;
    private JTextField email;
    private JTextField telefono;
    private JPasswordField contraseña;

    public VentanaRegistro() {
        setTitle("Registro");
        // Ajustamos tamaño para que sea coherente con Login (450 ancho)
        // pero más alto (750) porque tiene más campos.
        setSize(450, 750); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Panel principal con BoxLayout vertical para apilar cosas
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(18, 18, 18));
        panel.setBorder(new EmptyBorder(20, 40, 20, 40)); // Márgenes laterales

        // 1. AÑADIMOS EL LOGO (Sustituye al texto "REGISTRO")
        PanelLogo logo = new PanelLogo();
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(logo);
        
        panel.add(Box.createVerticalStrut(20)); // Espacio

        // 2. CAMPOS (Misma lógica de placeholders, pero estilizados)
        usuario = crearCampo("Nombre de Usuario");
        panel.add(usuario);
        panel.add(Box.createVerticalStrut(15));

        email = crearCampo("Email");
        panel.add(email);
        panel.add(Box.createVerticalStrut(15));

        telefono = crearCampo("Teléfono");
        panel.add(telefono);
        panel.add(Box.createVerticalStrut(15));

        contraseña = crearCampoPassword("Contraseña");
        panel.add(contraseña);
        panel.add(Box.createVerticalStrut(30));

        // 3. BOTONES
        JPanel botones = new JPanel(new GridLayout(1, 2, 20, 0)); // Grid 1 fila, 2 columnas
        botones.setBackground(new Color(18, 18, 18));
        botones.setMaximumSize(new Dimension(500, 50)); // Altura fija botones

        JButton atras = crearBoton("Atrás", new Color(90, 90, 90));
        JButton registrar = crearBoton("Registrarse", new Color(46, 204, 113)); // Verde

        atras.addActionListener(e -> volver());
        registrar.addActionListener(e -> registrarUsuario());

        botones.add(atras);
        botones.add(registrar);
        
        panel.add(botones);
        panel.add(Box.createVerticalGlue()); // Relleno final

        add(panel);
    }

    // --- MÉTODOS DE CREACIÓN VISUAL ---

    private JTextField crearCampo(String placeholder) {
        JTextField tf = new JTextField(placeholder);
        estilizarInput(tf);
        
        // Mantenemos TU lógica de FocusListener para el placeholder
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText("");
                    tf.setForeground(Color.WHITE); // Al escribir, blanco
                }
            }
            public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setText(placeholder);
                    tf.setForeground(Color.GRAY); // Placeholder gris
                }
            }
        });
        return tf;
    }

    private JPasswordField crearCampoPassword(String placeholder) {
        JPasswordField pf = new JPasswordField(placeholder);
        estilizarInput(pf);
        pf.setEchoChar((char) 0); // Al principio se ve el texto "Contraseña"

        pf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (new String(pf.getPassword()).equals(placeholder)) {
                    pf.setText("");
                    pf.setEchoChar('•'); // Pone los puntitos
                    pf.setForeground(Color.WHITE);
                }
            }
            public void focusLost(FocusEvent e) {
                if (new String(pf.getPassword()).isEmpty()) {
                    pf.setEchoChar((char) 0); // Quita puntitos para ver "Contraseña"
                    pf.setText(placeholder);
                    pf.setForeground(Color.GRAY);
                }
            }
        });
        return pf;
    }

    private void estilizarInput(JTextField tf) {
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Altura cómoda
        tf.setBackground(new Color(28, 28, 28));
        tf.setForeground(Color.GRAY); // Color inicial placeholder
        tf.setCaretColor(Color.WHITE);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        tf.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private JButton crearBoton(String txt, Color bg) {
        JButton b = new JButton(txt);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // --- LÓGICA DE NEGOCIO (INTACTA) ---

    private void volver() {
        new VentanaInicio().setVisible(true);
        dispose();
    }

    private void registrarUsuario() {
        // Tu lógica original exacta
        String nombre = usuario.getText().trim();
        String mail = email.getText().trim();
        String tel = telefono.getText().trim();
        String pass = new String(contraseña.getPassword());

        // Ajuste: verificamos también contra los placeholders
        if (nombre.isEmpty() || mail.isEmpty() || tel.isEmpty() || pass.isEmpty() ||
            nombre.equals("Nombre de Usuario") || mail.equals("Email") || 
            tel.equals("Teléfono") || pass.equals("Contraseña")) {
            
            JOptionPane.showMessageDialog(this, "Rellena todos los campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validación de email original
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!mail.matches(emailRegex)) {
            JOptionPane.showMessageDialog(this, "Por favor, introduce un email válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // LLAMADA A TU BD ORIGINAL
        GestorDatos.registrarUsuario(nombre, mail, tel, pass, 1000000);

        JOptionPane.showMessageDialog(this, "Usuario registrado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        
        // Ir a selección de liga (o volver al inicio, según tu flujo original lo tenías en volver)
        volver(); 
        // Si prefieres que vaya a SeleccionLiga, cambia la linea de arriba por:
        // new VentanaSeleccionLiga(GestorDatos.getUsuario(mail)).setVisible(true); dispose();
    }
}