import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class VentanaIniciarSesion extends JFrame {
	
	private JTextField usuario;
    private JPasswordField contraseña;

	public VentanaIniciarSesion() {
		setTitle("Inicio de Sesion");
	    setSize(400, 600);
	    setLocationRelativeTo(null);
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setResizable(false);
	    
	    JPanel panelPrincipal = new JPanel();
	    panelPrincipal.setLayout(new BorderLayout());
	    JLabel labelIniciarSesion = new JLabel("INICIO DE SESION", SwingConstants.CENTER);
	    labelIniciarSesion.setFont(new Font("Arial", Font.BOLD, 20));
	    labelIniciarSesion.setBorder(BorderFactory.createEmptyBorder(40, 10, 40, 10));
        panelPrincipal.add(labelIniciarSesion, BorderLayout.NORTH);
        
        JPanel panelCampos = new JPanel();
        panelCampos.setLayout(new GridLayout(4, 1, 20, 20));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(150, 50, 5, 50));
        
        usuario = new JTextField();
        contraseña = new JPasswordField();
        
        addPlaceholder(usuario, "Nombre de Usuario");
        addPlaceholder(contraseña, "Contraseña");
	    
        panelCampos.add(usuario);
        panelCampos.add(contraseña);
        panelPrincipal.add(panelCampos);
        
     // Panel para los botones
        
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(1, 2, 20, 20));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(5, 50, 100, 50));

        JButton botonAtras = new JButton("Atras");
        JButton botonIniciarSesion = new JButton("Iniciar Sesion");

        panelBotones.add(botonAtras);
        panelBotones.add(botonIniciarSesion);
        
        botonIniciarSesion.addActionListener((ActionEvent e) -> {
            String nombreUsuario = usuario.getText().trim();
            String pass = new String(contraseña.getPassword()).trim();

            // Validación campos vacíos
            if (nombreUsuario.isEmpty() || nombreUsuario.equals("Nombre de Usuario") ||
                pass.isEmpty() || pass.equals("Contraseña")) {
                JOptionPane.showMessageDialog(this, "Debes rellenar todos los campos",
                        "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Buscar usuario en el diccionario
            Usuario usuarioEncontrado = GestorDatos.usuarios.values().stream()
                    .filter(u -> u.getNombre().equalsIgnoreCase(nombreUsuario))
                    .findFirst()
                    .orElse(null);

            if (usuarioEncontrado == null) {
                JOptionPane.showMessageDialog(this, "El usuario no existe",
                        "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Verificar contraseña
            if (!usuarioEncontrado.getContrasena().equals(pass)) {
                JOptionPane.showMessageDialog(this, "Contraseña incorrecta",
                        "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Login correcto
            JOptionPane.showMessageDialog(this, "Bienvenido " + usuarioEncontrado.getNombre(),
                    "Login correcto", JOptionPane.INFORMATION_MESSAGE);

            abrirVentanaPrincipal();
        });

        botonAtras.addActionListener((ActionEvent e) -> {
            abrirVentanaInicio();
        });
        
        
	    add(panelPrincipal, BorderLayout.NORTH);
	    add(panelBotones, BorderLayout.SOUTH);
	}
	
	
	
	// Método genérico para JTextField
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

    // Versión especial para JPasswordField
    public static void addPlaceholder(JPasswordField passwordField, String placeholder) {
        passwordField.setEchoChar((char)0); // Mostrar el texto en claro
        passwordField.setText(placeholder);
        passwordField.setForeground(Color.GRAY);

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String pass = new String(passwordField.getPassword());
                if (pass.equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                    passwordField.setEchoChar('•'); // Restaurar bullets
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
    
    
    private void abrirVentanaPrincipal() {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventanaP = new VentanaPrincipal();
            ventanaP.setVisible(true);
        });
        dispose();
    }
    
    private void abrirVentanaInicio() {
        SwingUtilities.invokeLater(() -> {
            VentanaInicio ventanaI = new VentanaInicio();
            ventanaI.setVisible(true);
        });
        dispose();
    }
    
}
