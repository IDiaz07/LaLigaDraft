package gui.ventanas;

import javax.swing.*;
import java.awt.*;
import gui.clases.PanelLogo;

public class VentanaInicio extends JFrame {

    public VentanaInicio() {
        setTitle("LaLiga Draft - Inicio");
        setSize(450, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Panel principal con fondo oscuro
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(18, 18, 18));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // 1. LOGO VECTORIAL
        mainPanel.add(new PanelLogo());
        
        mainPanel.add(Box.createVerticalGlue());

        // 2. BOTONES
        JButton btnLogin = crearBotonEstilizado("INICIAR SESIÓN", new Color(52, 152, 219));
        btnLogin.addActionListener(e -> {
            // CORREGIDO: Ahora llama a la clase con el nombre exacto
            new VentanaIniciarSesion().setVisible(true);
            dispose();
        });

        JButton btnRegistro = crearBotonEstilizado("CREAR CUENTA", new Color(46, 204, 113));
        btnRegistro.addActionListener(e -> {
            new VentanaRegistro().setVisible(true);
            dispose();
        });

        JButton btnSalir = crearBotonEstilizado("SALIR", new Color(192, 57, 43));
        btnSalir.setPreferredSize(new Dimension(200, 40));
        btnSalir.addActionListener(e -> System.exit(0));

        mainPanel.add(btnLogin);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(btnRegistro);
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(btnSalir);

        add(mainPanel);
    }

    private JButton crearBotonEstilizado(String texto, Color colorFondo) {
        JButton btn = new JButton(texto);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(colorFondo);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(300, 50));
        btn.setPreferredSize(new Dimension(300, 50));
        return btn;
    }
}