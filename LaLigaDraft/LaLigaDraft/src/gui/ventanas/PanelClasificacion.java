package gui.ventanas;

import java.awt.*; 
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import bd.GestorDatos;
import gui.clases.Jugador;
import gui.clases.Liga;
import gui.clases.Usuario;

public class PanelClasificacion extends JPanel {

    private JTable clasificacion;
    private DefaultTableModel tableModel;
    private final Liga ligaActual;
    private JLabel lblJornada; 

    public PanelClasificacion(Liga liga) {
        this.ligaActual = liga;
        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 18));
        
        // --- ENCABEZADO ---
        JPanel panelNorte = new JPanel(new GridLayout(2, 1));
        panelNorte.setBackground(new Color(18, 18, 18));
        panelNorte.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel titulo = new JLabel("Clasificación - " + ligaActual.getNombre(), SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);

        lblJornada = new JLabel("Jornada " + obtenerJornadaActual() + " / 32", SwingConstants.CENTER);
        lblJornada.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblJornada.setForeground(new Color(46, 204, 113));

        panelNorte.add(titulo);
        panelNorte.add(lblJornada);
        add(panelNorte, BorderLayout.NORTH);

        // --- TABLA ---
        String[] columnas = {"Pos", "Usuario", "Puntos Total", "Equipo"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        clasificacion = new JTable(tableModel);
        estilizarTabla();
        JScrollPane scroll = new JScrollPane(clasificacion);
        scroll.getViewport().setBackground(new Color(18, 18, 18));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        // --- BOTÓN SIMULAR ---
        JButton btnSimular = new JButton("SIMULAR JORNADA " + (obtenerJornadaActual() + 1));
        btnSimular.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnSimular.setBackground(new Color(46, 204, 113));
        btnSimular.setForeground(Color.WHITE);
        btnSimular.setFocusPainted(false);
        btnSimular.addActionListener(e -> simularJornada());
        add(btnSimular, BorderLayout.SOUTH);

        cargarDatosClasificacion();
    }

    private void estilizarTabla() {
        clasificacion.setBackground(new Color(30, 30, 30));
        clasificacion.setForeground(Color.WHITE);
        clasificacion.setRowHeight(30);
        clasificacion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        clasificacion.getTableHeader().setBackground(new Color(40, 40, 40));
        clasificacion.getTableHeader().setForeground(new Color(231, 76, 60));
        clasificacion.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<4; i++) clasificacion.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }

    private void cargarDatosClasificacion() {
        tableModel.setRowCount(0);
        java.util.List<Usuario> usuariosLiga = new ArrayList<>();
        
        // --- CORRECCIÓN: Buscamos usuarios mirando su ID de liga, 
        // así no necesitamos tocar la clase Liga.java ---
        for (Usuario u : GestorDatos.usuarios.values()) {
            if (u.getLigaActualId() == ligaActual.getId()) {
                usuariosLiga.add(u);
            }
        }
        // ------------------------------------------------

        usuariosLiga.sort((u1, u2) -> Integer.compare(calcularPuntos(u2), calcularPuntos(u1)));

        int pos = 1;
        for (Usuario u : usuariosLiga) {
            int puntos = calcularPuntos(u);
            int numJugadores = (u.getJugadores() != null) ? u.getJugadores().size() : 0;
            tableModel.addRow(new Object[]{pos++, u.getNombre(), puntos, numJugadores + " Jugadores"});
        }
    }

    private int calcularPuntos(Usuario u) {
        int total = 0;
        if (u.getJugadores() == null) return 0;
        for (Integer idJugador : u.getJugadores()) {
            Jugador j = GestorDatos.jugadores.get(idJugador);
            // Comprobamos que j exista y tenga lista de puntos
            if (j != null && j.getPuntosPorJornada() != null) {
                for(Integer p : j.getPuntosPorJornada()) {
                    if(p != null) total += p;
                }
            }
        }
        return total;
    }

    private int obtenerJornadaActual() {
        if (GestorDatos.jugadores.isEmpty()) return 0;
        // Buscamos un jugador cualquiera que no sea nulo
        for (Jugador j : GestorDatos.jugadores.values()) {
            if (j != null && j.getPuntosPorJornada() != null) {
                int cont = 0;
                for (Integer puntos : j.getPuntosPorJornada()) {
                    if (puntos != null) cont++;
                }
                return cont;
            }
        }
        return 0;
    }

    private void simularJornada() {
        // Ventana de carga simple
        JDialog carga = new JDialog();
        carga.setUndecorated(true);
        carga.setSize(300, 50);
        carga.setLocationRelativeTo(this);
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        carga.add(new JLabel(" Jugando partidos...", SwingConstants.CENTER), BorderLayout.NORTH);
        carga.add(bar, BorderLayout.CENTER);
        carga.setModal(false); 
        carga.setVisible(true);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(1500); 
                Random r = new Random();
                int jornadaActual = obtenerJornadaActual();
                
                for (Jugador j : GestorDatos.jugadores.values()) {
                    if (j != null) {
                        int puntos = r.nextInt(16);
                        // Asegúrate de que este método existe en Jugador.java
                        j.setPuntosEnJornada(jornadaActual + 1, puntos);
                    }
                }
                return null;
            }
            @Override
            protected void done() {
                carga.dispose();
                cargarDatosClasificacion();
                lblJornada.setText("Jornada " + obtenerJornadaActual() + " / 32");
                JOptionPane.showMessageDialog(PanelClasificacion.this, "Jornada finalizada!");
            }
        };
        worker.execute();
    }
}