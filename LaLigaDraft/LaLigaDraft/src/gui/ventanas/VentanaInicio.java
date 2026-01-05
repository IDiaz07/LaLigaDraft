package gui.ventanas;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 * Ventana inicial de la aplicación.
 * Ofrece las opciones de iniciar sesión o registrarse.
 */
public class VentanaInicio extends JFrame {

    public VentanaInicio() {
        setTitle("LaLigaDraft - Inicio");
        setSize(600, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(18, 18, 18));

        // Etiqueta de bienvenida (NO MODIFICADA, solo proporcional)
        JLabel labelBienvenida = new JLabel("¡BIENVENIDO A LALIGADRAFT!", SwingConstants.CENTER);
        labelBienvenida.setFont(new Font("Arial", Font.BOLD, 28));
        labelBienvenida.setForeground(Color.WHITE);
        labelBienvenida.setBorder(BorderFactory.createEmptyBorder(80, 10, 80, 10));
        panelPrincipal.add(labelBienvenida, BorderLayout.NORTH);

        // Panel para los botones (idéntico estilo, sin deformaciones)
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 40, 40));
        panelBotones.setBackground(new Color(18, 18, 18));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(450, 100, 150, 100));

        JButton botonIniciarSesion = new JButton("Iniciar Sesión");
        botonIniciarSesion.setBackground(new Color(231, 76, 60));
        botonIniciarSesion.setForeground(Color.WHITE);
        botonIniciarSesion.setFont(new Font("Arial", Font.BOLD, 20));

        JButton botonRegistrarme = new JButton("Registrarme");
        botonRegistrarme.setBackground(new Color(231, 76, 60));
        botonRegistrarme.setForeground(Color.WHITE);
        botonRegistrarme.setFont(new Font("Arial", Font.BOLD, 20));

        panelBotones.add(botonIniciarSesion);
        panelBotones.add(botonRegistrarme);

        panelPrincipal.add(panelBotones, BorderLayout.CENTER);

        botonIniciarSesion.addActionListener((ActionEvent e) -> abrirVentanaIniciarSesion());
        botonRegistrarme.addActionListener((ActionEvent e) -> abrirVentanaRegistro());

        add(panelPrincipal);
    }

    private void abrirVentanaRegistro() {
        new VentanaRegistro().setVisible(true);
        dispose();
    }

    private void abrirVentanaIniciarSesion() {
        new VentanaIniciarSesion().setVisible(true);
        dispose();
    }
}
