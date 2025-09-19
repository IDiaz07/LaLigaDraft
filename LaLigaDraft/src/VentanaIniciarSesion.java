import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class VentanaIniciarSesion extends JFrame {

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
        panelCampos.setBorder(BorderFactory.createEmptyBorder(150, 50, 100, 50));
        
        JTextField usuario = new JTextField();
        JTextField email = new JTextField();
        JTextField tf = new JTextField();
        JPasswordField contraseña = new JPasswordField();
        
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
        panelBotones.setBorder(BorderFactory.createEmptyBorder(325, 50, 100, 50));

        JButton botonAtras = new JButton("Atras");
        JButton botonIniciarSesion = new JButton("Iniciar Sesion");

        panelBotones.add(botonAtras);
        panelBotones.add(botonIniciarSesion);
        
        botonIniciarSesion.addActionListener((ActionEvent e) -> {
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
        dispose(); // Cierra la ventana de inicio
    }
    
    private void abrirVentanaInicio() {
        SwingUtilities.invokeLater(() -> {
            VentanaInicio ventanaI = new VentanaInicio();
            ventanaI.setVisible(true);
        });
        dispose(); // Cierra la ventana de inicio
    }
    
}
