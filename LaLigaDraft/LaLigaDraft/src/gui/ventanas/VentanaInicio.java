package gui.ventanas;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 * Ventana inicial de la aplicación.
 * Ofrece las opciones de iniciar sesión o registrarse.
 * Incluye motor de renderizado interactivo para botones.
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

        // Etiqueta de bienvenida
        JLabel labelBienvenida = new JLabel("¡BIENVENIDO A LALIGADRAFT!", SwingConstants.CENTER);
        labelBienvenida.setFont(new Font("Arial", Font.BOLD, 28));
        labelBienvenida.setForeground(Color.WHITE);
        labelBienvenida.setBorder(BorderFactory.createEmptyBorder(80, 10, 80, 10));
        panelPrincipal.add(labelBienvenida, BorderLayout.NORTH);

        // Panel para los botones
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 40, 40));
        panelBotones.setBackground(new Color(18, 18, 18));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(450, 100, 150, 100));

        // --- BOTÓN 1: INICIAR SESIÓN ---
        JButton botonIniciarSesion = new JButton("Iniciar Sesión");
        // Aquí llamamos a nuestro nuevo motor visual en lugar de poner colores simples
        configurarBotonInteractivo(botonIniciarSesion, new Color(231, 76, 60), Color.WHITE);

        // --- BOTÓN 2: REGISTRARME ---
        JButton botonRegistrarme = new JButton("Registrarme");
        // Lo mismo aquí, usamos el motor
        configurarBotonInteractivo(botonRegistrarme, new Color(231, 76, 60), Color.WHITE);

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

    // ==========================================================================
    // MOTOR DE INTERACTIVIDAD (UX/UI ENGINE) - COMMIT 1
    // Este método encapsula la lógica visual avanzada para los botones
    // ==========================================================================
    private void configurarBotonInteractivo(JButton boton, Color fondo, Color texto) {
        // 1. Configuración Base
        boton.setBackground(fondo);
        boton.setForeground(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Fuente un poco más moderna
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Pone la mano al pasar por encima
        
        // 2. Listener de Ratón Complejo (MouseAdapter)
        // Esto añade muchas líneas y hace que el botón reaccione
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Al entrar: Aclaramos el color (Efecto Highlight)
                Color base = boton.getBackground();
                int r = Math.min(255, base.getRed() + 20);
                int g = Math.min(255, base.getGreen() + 20);
                int b = Math.min(255, base.getBlue() + 20);
                boton.setBackground(new Color(r, g, b));
                // Añadimos un borde blanco sutil
                boton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                boton.setBorderPainted(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Al salir: Volvemos al estado original
                boton.setBackground(fondo);
                boton.setBorderPainted(false);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                // Al pulsar: Oscurecemos (Efecto Click)
                Color base = boton.getBackground();
                int r = Math.max(0, base.getRed() - 30);
                int g = Math.max(0, base.getGreen() - 30);
                int b = Math.max(0, base.getBlue() - 30);
                boton.setBackground(new Color(r, g, b));
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                // Al soltar: Recuperamos el color de "mouseEntered" si seguimos dentro
                if (boton.contains(evt.getPoint())) {
                    int r = Math.min(255, fondo.getRed() + 20);
                    int g = Math.min(255, fondo.getGreen() + 20);
                    int b = Math.min(255, fondo.getBlue() + 20);
                    boton.setBackground(new Color(r, g, b));
                } else {
                    boton.setBackground(fondo);
                }
            }
        });
    }
}