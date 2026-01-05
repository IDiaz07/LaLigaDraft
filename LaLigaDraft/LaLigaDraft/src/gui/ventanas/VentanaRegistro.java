package gui.ventanas;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import bd.GestorDatos;

/**
 * Ventana para el registro de nuevos usuarios.
 * Recoge datos personales y realiza validaciones antes de guardar en la base de
 * datos.
 */
public class VentanaRegistro extends JFrame {

    private JTextField usuario;
    private JTextField email;
    private JTextField telefono;
    private JPasswordField contraseña;

    public VentanaRegistro() {
        setTitle("Registro");
        setSize(600, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(18, 18, 18));

        JLabel titulo = new JLabel("REGISTRO", SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 32));
        titulo.setBorder(BorderFactory.createEmptyBorder(80, 10, 60, 10));

        panel.add(titulo, BorderLayout.NORTH);

        JPanel campos = new JPanel(new GridLayout(4, 1, 30, 30));
        campos.setBackground(new Color(18, 18, 18));
        campos.setBorder(BorderFactory.createEmptyBorder(100, 80, 80, 80));

        usuario = crearCampo("Nombre de Usuario");
        email = crearCampo("Email");
        telefono = crearCampo("Teléfono");
        contraseña = crearCampoPassword("Contraseña");

        campos.add(usuario);
        campos.add(email);
        campos.add(telefono);
        campos.add(contraseña);

        panel.add(campos, BorderLayout.CENTER);

        JPanel botones = new JPanel(new GridLayout(1, 2, 40, 40));
        botones.setBackground(new Color(18, 18, 18));
        botones.setBorder(BorderFactory.createEmptyBorder(40, 80, 150, 80));

        JButton atras = crearBoton("Atrás");
        JButton registrar = crearBoton("Registrarse");

        atras.addActionListener(e -> volver());
        registrar.addActionListener(e -> registrarUsuario());

        botones.add(atras);
        botones.add(registrar);

        add(panel, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    private JTextField crearCampo(String placeholder) {
        JTextField tf = new JTextField(placeholder);
        tf.setBackground(new Color(28, 28, 28));
        tf.setForeground(Color.WHITE);
        tf.setFont(new Font("Arial", Font.PLAIN, 20));
        tf.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder))
                    tf.setText("");
            }

            public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty())
                    tf.setText(placeholder);
            }
        });

        return tf;
    }

    private JPasswordField crearCampoPassword(String placeholder) {
        JPasswordField pf = new JPasswordField(placeholder);
        pf.setBackground(new Color(28, 28, 28));
        pf.setForeground(Color.WHITE);
        pf.setFont(new Font("Arial", Font.PLAIN, 20));
        pf.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        pf.setEchoChar((char) 0);

        pf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (new String(pf.getPassword()).equals(placeholder)) {
                    pf.setText("");
                    pf.setEchoChar('•');
                }
            }

            public void focusLost(FocusEvent e) {
                if (new String(pf.getPassword()).isEmpty()) {
                    pf.setEchoChar((char) 0);
                    pf.setText(placeholder);
                }
            }
        });

        return pf;
    }

    private JButton crearBoton(String txt) {
        JButton b = new JButton(txt);
        b.setBackground(new Color(231, 76, 60));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 22));
        return b;
    }

    private void volver() {
        new VentanaInicio().setVisible(true);
        dispose();
    }

    private void registrarUsuario() {
        String nombre = usuario.getText().trim();
        String mail = email.getText().trim();
        String tel = telefono.getText().trim();
        String pass = new String(contraseña.getPassword());

        if (nombre.isEmpty() || mail.isEmpty() || tel.isEmpty() || pass.isEmpty() ||
                nombre.equals("Nombre de Usuario")) {
            JOptionPane.showMessageDialog(this, "Rellena todos los campos.");
            return;
        }

        // Validación de email
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!mail.matches(emailRegex)) {
            JOptionPane.showMessageDialog(this, "Por favor, introduce un email válido.");
            return;
        }

        GestorDatos.registrarUsuario(nombre, mail, tel, pass, 1000000);

        JOptionPane.showMessageDialog(this, "Usuario registrado correctamente.");
        volver();
    }
}
