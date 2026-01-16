package gui.ventanas;

import javax.swing.*;
import java.awt.*;
import gui.clases.Usuario;

public class PanelAjustes extends JPanel {

    // Guardo el usuario por si mas adelante quiero guardar sus preferencias en la BD
    private Usuario usuario;
    
    // Componentes de la interfaz
    private JCheckBox chkNotificaciones;
    private JCheckBox chkSonido;
    private JCheckBox chkModoOscuro;
    private JSlider sliderVolumen;
    private JComboBox<String> comboIdioma;
    private JButton btnGuardar;
    private JButton btnBorrarCache;

    public PanelAjustes(Usuario u) {
        this.usuario = u;
        
        // Configuro el layout principal
        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 18));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- TITULO DE LA PANTALLA ---
        JLabel titulo = new JLabel("Configuración del Sistema");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        add(titulo, BorderLayout.NORTH);

        // --- PANEL CENTRAL CON LAS OPCIONES ---
        // Uso GridBagLayout porque es el que mejor me deja colocar las cosas centradas
        JPanel panelOpciones = new JPanel(new GridBagLayout());
        panelOpciones.setBackground(new Color(18, 18, 18));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Margen entre elementos
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // 1. SECCION GENERAL
        JLabel lblGeneral = new JLabel("--- GENERAL ---");
        lblGeneral.setForeground(Color.ORANGE);
        panelOpciones.add(lblGeneral, gbc);
        
        gbc.gridy++;
        chkNotificaciones = new JCheckBox("Activar notificaciones de fichajes");
        estilizarCheckbox(chkNotificaciones);
        panelOpciones.add(chkNotificaciones, gbc);

        gbc.gridy++;
        chkSonido = new JCheckBox("Reproducir sonidos al hacer clic");
        estilizarCheckbox(chkSonido);
        panelOpciones.add(chkSonido, gbc);
        
        // 2. SECCION VISUAL
        gbc.gridy++;
        panelOpciones.add(Box.createVerticalStrut(20), gbc); // Espacio vacio
        
        gbc.gridy++;
        JLabel lblVisual = new JLabel("--- APARIENCIA ---");
        lblVisual.setForeground(Color.CYAN);
        panelOpciones.add(lblVisual, gbc);
        
        gbc.gridy++;
        chkModoOscuro = new JCheckBox("Forzar Modo Oscuro (Experimental)");
        estilizarCheckbox(chkModoOscuro);
        chkModoOscuro.setSelected(true); // Por defecto activado
        panelOpciones.add(chkModoOscuro, gbc);
        
        gbc.gridy++;
        JPanel pIdioma = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pIdioma.setBackground(new Color(18, 18, 18));
        JLabel lblIdioma = new JLabel("Idioma de la interfaz: ");
        lblIdioma.setForeground(Color.WHITE);
        
        String[] idiomas = {"Español (España)", "English (UK)", "Deutsch", "Català"};
        comboIdioma = new JComboBox<>(idiomas);
        
        pIdioma.add(lblIdioma);
        pIdioma.add(comboIdioma);
        panelOpciones.add(pIdioma, gbc);

        // 3. SECCION AVANZADA
        gbc.gridy++;
        panelOpciones.add(Box.createVerticalStrut(20), gbc);
        
        gbc.gridy++;
        JLabel lblAv = new JLabel("--- AVANZADO ---");
        lblAv.setForeground(Color.RED);
        panelOpciones.add(lblAv, gbc);
        
        gbc.gridy++;
        JLabel lblVol = new JLabel("Volumen de efectos:");
        lblVol.setForeground(Color.GRAY);
        panelOpciones.add(lblVol, gbc);
        
        gbc.gridy++;
        sliderVolumen = new JSlider(0, 100, 75);
        sliderVolumen.setBackground(new Color(18, 18, 18));
        panelOpciones.add(sliderVolumen, gbc);
        
        gbc.gridy++;
        btnBorrarCache = new JButton("Borrar caché temporal");
        btnBorrarCache.setBackground(new Color(100, 0, 0));
        btnBorrarCache.setForeground(Color.WHITE);
        btnBorrarCache.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Caché borrada correctamente.");
        });
        panelOpciones.add(btnBorrarCache, gbc);

        add(panelOpciones, BorderLayout.CENTER);

        // --- BOTONERA INFERIOR ---
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(new Color(18, 18, 18));
        
        btnGuardar = new JButton("GUARDAR CAMBIOS");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setBackground(new Color(46, 204, 113));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setPreferredSize(new Dimension(200, 40));
        
        // Aqui simulo que guardo los datos
        btnGuardar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Preferencias guardadas en el perfil de " + usuario.getNombre());
        });
        
        panelBotones.add(btnGuardar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    // Metodo auxiliar para no repetir codigo con los checkbox
    private void estilizarCheckbox(JCheckBox chk) {
        chk.setBackground(new Color(18, 18, 18));
        chk.setForeground(Color.WHITE);
        chk.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chk.setFocusPainted(false);
    }
}