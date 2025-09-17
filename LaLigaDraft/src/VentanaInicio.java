import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class VentanaInicio extends JFrame {
	public VentanaInicio() {
		setTitle("LaLigaDraft - Inicio");
		setSize(400, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Panel principal
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());

        // Etiqueta de bienvenida
        JLabel labelBienvenida = new JLabel("¡BIENVENIDO A LALIGADRAFT!", SwingConstants.CENTER);
        labelBienvenida.setFont(new Font("Arial", Font.BOLD, 20));
        labelBienvenida.setBorder(BorderFactory.createEmptyBorder(40, 10, 40, 10));
        panelPrincipal.add(labelBienvenida, BorderLayout.NORTH);

        // Panel para los botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(2, 1, 20, 20));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JButton botonIniciarSesion = new JButton("Iniciar Sesión");
        JButton botonRegistrarme = new JButton("Registrarme");

        panelBotones.add(botonIniciarSesion);
        panelBotones.add(botonRegistrarme);

        panelPrincipal.add(panelBotones, BorderLayout.CENTER);

        // Acciones de botones
        botonIniciarSesion.addActionListener((ActionEvent e) -> {
            abrirVentanaPrincipal();
        });

        botonRegistrarme.addActionListener((ActionEvent e) -> {
            abrirVentanaPrincipal();
        });

        add(panelPrincipal);
    }
	
	// Método para abrir la VentanaPrincipal y cerrar esta
    private void abrirVentanaPrincipal() {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventanaPrincipal = new VentanaPrincipal();
            ventanaPrincipal.setVisible(true);
        });
        dispose(); // Cierra la ventana de inicio
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaInicio ventanaInicio = new VentanaInicio();
            ventanaInicio.setVisible(true);
        });
    }
}
