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
	    panelPrincipal.setBackground(new Color(18, 18, 18));
	    
	    JLabel labelIniciarSesion = new JLabel("INICIO DE SESION", SwingConstants.CENTER);
	    labelIniciarSesion.setFont(new Font("Arial", Font.BOLD, 20));
	    labelIniciarSesion.setBorder(BorderFactory.createEmptyBorder(40, 10, 40, 10));
	    labelIniciarSesion.setForeground(Color.WHITE);
        panelPrincipal.add(labelIniciarSesion, BorderLayout.NORTH);
        
        JPanel panelCampos = new JPanel();
        panelCampos.setLayout(new GridLayout(4, 1, 20, 20));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(150, 50, 5, 50));
        panelCampos.setBackground(new Color(18, 18, 18));
        
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
        panelBotones.setBackground(new Color(18, 18, 18));

        JButton botonAtras = new JButton("Atras");
        botonAtras.setBackground(new Color(231, 76, 60));
        botonAtras.setForeground(Color.WHITE);
        
        JButton botonIniciarSesion = new JButton("Iniciar Sesion");
        botonIniciarSesion.setBackground(new Color(231, 76, 60));
        botonIniciarSesion.setForeground(Color.WHITE);

        panelBotones.add(botonAtras);
        panelBotones.add(botonIniciarSesion);
        
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

            // 🔹 Asignar equipo inicial si no tiene jugadores
            GestorDatos.asignarEquipoInicial(usuarioEncontrado);

            // 🔹 Determinar flujo según ligaActualId
            if (usuarioEncontrado.getLigas() != null && !usuarioEncontrado.getLigas().isEmpty()) {
                if (usuarioEncontrado.getLigaActualId() == -1) {
                    // Asignar la primera liga que tenga
                    usuarioEncontrado.setLigaActualId(usuarioEncontrado.getLigas().get(0));
                }
                // Abrir VentanaPrincipal directamente
                dispose();
                SwingUtilities.invokeLater(() -> new VentanaPrincipal(usuarioEncontrado).setVisible(true));
            } else {
                // No tiene ligas -> abrir selector
                dispose();
                SwingUtilities.invokeLater(() -> new VentanaSeleccionLiga(usuarioEncontrado).setVisible(true));
            }
        });


        botonAtras.addActionListener((ActionEvent e) -> {
            abrirVentanaInicio();
        });
        
        
	    add(panelPrincipal, BorderLayout.CENTER);
	    add(panelBotones, BorderLayout.SOUTH);
	}
	
	
	
	// Método genérico para JTextField
	public static void addPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.WHITE);
        textField.setBackground(new Color(28, 28, 28));

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.WHITE);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.WHITE);
                }
            }
        });
    }

    // Versión especial para JPasswordField
    public static void addPlaceholder(JPasswordField passwordField, String placeholder) {
        passwordField.setEchoChar((char)0); // Mostrar el texto en claro
        passwordField.setText(placeholder);
        passwordField.setForeground(Color.WHITE);
        passwordField.setBackground(new Color(28, 28, 28));
        

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String pass = new String(passwordField.getPassword());
                if (pass.equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.WHITE);
                    passwordField.setEchoChar('•'); // Restaurar bullets
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                String pass = new String(passwordField.getPassword());
                if (pass.isEmpty()) {
                    passwordField.setEchoChar((char)0);
                    passwordField.setText(placeholder);
                    passwordField.setForeground(Color.WHITE);
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
