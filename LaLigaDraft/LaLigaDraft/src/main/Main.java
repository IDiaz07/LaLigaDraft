package main;

import javax.swing.*;
import java.awt.*;
import bd.GestorDatos;
import gui.ventanas.VentanaInicio;

public class Main {
    public static void main(String[] args) {
        // --- 1. PANTALLA DE CARGA ---
        JWindow ventanaCarga = new JWindow();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(18, 18, 18));
        panel.setBorder(BorderFactory.createLineBorder(new Color(231, 76, 60), 2));

        JLabel titulo = new JLabel("LALIGADRAFT 25", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 40));
        titulo.setForeground(Color.WHITE);
        panel.add(titulo, BorderLayout.CENTER);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(new Color(18, 18, 18));
        
        JLabel estado = new JLabel("Iniciando...");
        estado.setForeground(Color.GRAY);
        
        JProgressBar barra = new JProgressBar(0, 100);
        barra.setForeground(new Color(231, 76, 60));
        barra.setBackground(new Color(40, 40, 40));
        barra.setBorderPainted(false);

        panelSur.add(estado, BorderLayout.NORTH);
        panelSur.add(barra, BorderLayout.SOUTH);
        panel.add(panelSur, BorderLayout.SOUTH);

        ventanaCarga.setContentPane(panel);
        ventanaCarga.setSize(500, 300);
        ventanaCarga.setLocationRelativeTo(null);
        ventanaCarga.setVisible(true);

        // --- 2. CARGA DE DATOS ---
        try {
            for(int i=0; i<30; i++) { Thread.sleep(10); barra.setValue(i); }
            
            estado.setText("Conectando base de datos...");
            GestorDatos.inicializar(); 
            
            barra.setValue(70);
            estado.setText("Cargando sistema...");
            // Si tienes el método rellenarMercadoSiVacio úsalo, si no, borra esta línea:
            try { GestorDatos.rellenarMercadoSiVacio(); } catch(Exception e){}
            
            for(int i=70; i<=100; i++) { Thread.sleep(10); barra.setValue(i); }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // --- 3. ABRIR VENTANA ---
        ventanaCarga.dispose();
        SwingUtilities.invokeLater(() -> new VentanaInicio().setVisible(true));
    }
}