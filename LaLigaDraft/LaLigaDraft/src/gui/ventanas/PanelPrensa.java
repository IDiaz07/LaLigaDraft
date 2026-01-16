package gui.ventanas;

import javax.swing.*;
import gui.clases.MotorNoticias;
import java.awt.*;

public class PanelPrensa extends JPanel {

    private MotorNoticias motorIA;
    private JTextArea areaNoticias;

    public PanelPrensa() {
        this.motorIA = new MotorNoticias(); // Instanciamos el cerebro
        
        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 18));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- TÍTULO ---
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(new Color(18, 18, 18));
        
        JLabel titulo = new JLabel("Diario LaLiga", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        
        JLabel sub = new JLabel("Rumores, Fichajes y Polémicas", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(Color.LIGHT_GRAY);
        
        header.add(titulo);
        header.add(sub);
        add(header, BorderLayout.NORTH);

        // --- ÁREA DE TEXTO (FEED) ---
        areaNoticias = new JTextArea();
        areaNoticias.setEditable(false);
        areaNoticias.setBackground(new Color(30, 30, 30));
        areaNoticias.setForeground(new Color(46, 204, 113)); // Verde Hacker
        areaNoticias.setFont(new Font("Consolas", Font.PLAIN, 14));
        areaNoticias.setLineWrap(true);
        areaNoticias.setWrapStyleWord(true);
        areaNoticias.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Cargar 5 noticias al iniciar
        cargarIniciales();

        JScrollPane scroll = new JScrollPane(areaNoticias);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        add(scroll, BorderLayout.CENTER);

        // --- BOTÓN ACTUALIZAR ---
        JButton btnUpdate = new JButton("LEER NUEVOS RUMORES");
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnUpdate.setBackground(new Color(230, 126, 34)); // Naranja prensa
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btnUpdate.addActionListener(e -> {
            String nueva = motorIA.generarNuevaNoticia();
            areaNoticias.setText(nueva + "\n\n" + areaNoticias.getText());
            areaNoticias.setCaretPosition(0);
        });
        
        JPanel pBtn = new JPanel();
        pBtn.setBackground(new Color(18, 18, 18));
        pBtn.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
        pBtn.add(btnUpdate);
        add(pBtn, BorderLayout.SOUTH);
    }

    private void cargarIniciales() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ÚLTIMAS NOTICIAS DEL DÍA ===\n\n");
        for(int i=0; i<5; i++) {
            sb.append(motorIA.generarNuevaNoticia()).append("\n\n");
        }
        areaNoticias.setText(sb.toString());
    }
}