package gui.ventanas;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * AGENDA INSTITUCIONAL DEL CLUB
 * Módulo de gestión de calendario y planificación deportiva.
 * Utiliza algoritmos de fechas (GregorianCalendar) para generar la vista mensual.
 * NO CONECTA CON BASE DE DATOS (Sandbox seguro).
 */
public class VentanaCalendario extends JFrame {

    private JLabel lblMesAno;
    private JTable tablaCalendario;
    private DefaultTableModel modeloCalendario;
    private JTextArea areaDetalles;
    
    // Variables de lógica de fechas
    private GregorianCalendar cal;
    private int diaActual, mesActual, anioActual;
    private int mesNavegacion, anioNavegacion;

    // EVENTOS PREDEFINIDOS (Texto plano, sin emojis)
    private final String[] EVENTOS = {
        "Entrenamiento fisico (10:00h)",
        "Rueda de prensa oficial",
        "Sesion de video analisis",
        "Viaje convocado",
        "Revision medica",
        "Reunion Directiva",
        "PARTIDO DE LIGA",
        "DESCANSO"
    };

    public VentanaCalendario() {
        setTitle("Agenda del Manager 2025");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        // Inicializar fecha del sistema
        cal = new GregorianCalendar();
        diaActual = cal.get(Calendar.DAY_OF_MONTH);
        mesActual = cal.get(Calendar.MONTH);
        anioActual = cal.get(Calendar.YEAR);
        
        mesNavegacion = mesActual;
        anioNavegacion = anioActual;

        // --- LAYOUT PRINCIPAL ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(20, 20, 20));

        // 1. CABECERA (Navegación de Meses)
        JPanel pNorte = new JPanel(new BorderLayout());
        pNorte.setBackground(new Color(30, 30, 30));
        pNorte.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JButton btnPrev = crearBotonNav("<");
        JButton btnNext = crearBotonNav(">");
        
        lblMesAno = new JLabel("", SwingConstants.CENTER);
        lblMesAno.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblMesAno.setForeground(Color.WHITE);

        // Lógica de navegación
        btnPrev.addActionListener(e -> {
            if (mesNavegacion == 0) { mesNavegacion = 11; anioNavegacion--; } 
            else { mesNavegacion--; }
            actualizarCalendario();
        });

        btnNext.addActionListener(e -> {
            if (mesNavegacion == 11) { mesNavegacion = 0; anioNavegacion++; } 
            else { mesNavegacion++; }
            actualizarCalendario();
        });

        pNorte.add(btnPrev, BorderLayout.WEST);
        pNorte.add(lblMesAno, BorderLayout.CENTER);
        pNorte.add(btnNext, BorderLayout.EAST);

        // 2. TABLA CALENDARIO (CENTRO)
        modeloCalendario = new DefaultTableModel() {
            public boolean isCellEditable(int rowIndex, int mColIndex) { return false; }
        };
        
        tablaCalendario = new JTable(modeloCalendario);
        estilizarTabla();
        
        // Evento al clicar un día
        tablaCalendario.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                int row = tablaCalendario.getSelectedRow();
                int col = tablaCalendario.getSelectedColumn();
                Object valor = modeloCalendario.getValueAt(row, col);
                if (valor != null) {
                    mostrarDetallesDia(Integer.parseInt(valor.toString()));
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaCalendario);
        scroll.getViewport().setBackground(new Color(20, 20, 20));
        scroll.setBorder(null);

        // 3. PANEL LATERAL (DETALLES)
        JPanel pDetalles = new JPanel(new BorderLayout());
        pDetalles.setPreferredSize(new Dimension(280, 0));
        pDetalles.setBackground(new Color(25, 25, 25));
        pDetalles.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(60,60,60)));

        JLabel lblInfo = new JLabel(" AGENDA DEL DIA ", SwingConstants.CENTER);
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblInfo.setForeground(new Color(46, 204, 113)); // Verde
        lblInfo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        areaDetalles = new JTextArea();
        areaDetalles.setEditable(false);
        areaDetalles.setBackground(new Color(25, 25, 25));
        areaDetalles.setForeground(Color.LIGHT_GRAY);
        areaDetalles.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        areaDetalles.setLineWrap(true);
        areaDetalles.setWrapStyleWord(true);
        areaDetalles.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        areaDetalles.setText("\nSelecciona un dia para ver los eventos programados.");

        pDetalles.add(lblInfo, BorderLayout.NORTH);
        pDetalles.add(areaDetalles, BorderLayout.CENTER);

        // Añadir todo
        mainPanel.add(pNorte, BorderLayout.NORTH);
        mainPanel.add(scroll, BorderLayout.CENTER);
        mainPanel.add(pDetalles, BorderLayout.EAST);

        add(mainPanel);

        // Inicializar estructura de columnas
        String[] diasSemana = {"Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom"};
        for (String d : diasSemana) modeloCalendario.addColumn(d);
        modeloCalendario.setRowCount(6); // 6 semanas máximo
        
        actualizarCalendario();
    }

    /**
     * ALGORITMO DE CÁLCULO DE FECHAS
     * Determina en qué casilla empieza el mes y cuántos días tiene.
     */
    private void actualizarCalendario() {
        // Limpiar
        for (int i=0; i<6; i++) 
            for (int j=0; j<7; j++) 
                modeloCalendario.setValueAt(null, i, j);

        // Cálculos matemáticos de fecha
        GregorianCalendar calTemp = new GregorianCalendar(anioNavegacion, mesNavegacion, 1);
        int diasEnMes = calTemp.getActualMaximum(Calendar.DAY_OF_MONTH);
        int diaInicio = calTemp.get(Calendar.DAY_OF_WEEK);
        
        // Ajuste Lunes=0 ... Domingo=6
        int columnaInicio = diaInicio - 2;
        if (columnaInicio < 0) columnaInicio += 7;

        // Rellenar matriz
        int fila = 0;
        int col = columnaInicio;
        
        for (int dia = 1; dia <= diasEnMes; dia++) {
            modeloCalendario.setValueAt(dia, fila, col);
            col++;
            if (col > 6) { col = 0; fila++; }
        }

        // Actualizar título
        String[] meses = {"ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", 
                          "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};
        lblMesAno.setText(meses[mesNavegacion] + " " + anioNavegacion);
    }

    private void mostrarDetallesDia(int dia) {
        Random r = new Random(dia * 100 + mesNavegacion); // Semilla fija para consistencia
        StringBuilder sb = new StringBuilder();
        
        sb.append("FECHA: ").append(dia).append("/").append(mesNavegacion+1).append("\n\n");
        sb.append("EVENTOS PROGRAMADOS:\n");
        sb.append("--------------------\n\n");
        
        int numEventos = r.nextInt(3) + 1; // 1 a 3 eventos
        for(int i=0; i<numEventos; i++) {
            String ev = EVENTOS[r.nextInt(EVENTOS.length)];
            sb.append("- ").append(ev).append("\n\n");
        }
        
        areaDetalles.setText(sb.toString());
    }

    // --- ESTILOS VISUALES ---
    
    private void estilizarTabla() {
        tablaCalendario.setRowHeight(80);
        tablaCalendario.setBackground(new Color(20, 20, 20));
        tablaCalendario.setForeground(Color.WHITE);
        tablaCalendario.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tablaCalendario.setGridColor(new Color(60, 60, 60));
        tablaCalendario.setSelectionBackground(new Color(52, 152, 219));
        tablaCalendario.getTableHeader().setBackground(new Color(40, 40, 40));
        tablaCalendario.getTableHeader().setForeground(new Color(200, 200, 200));
        tablaCalendario.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Renderizador personalizado para pintar días especiales
        tablaCalendario.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSel, boolean hasFocus, int row, int col) {
                
                super.getTableCellRendererComponent(table, value, isSel, hasFocus, row, col);
                setHorizontalAlignment(JLabel.CENTER);
                setBorder(null);

                if (value != null) {
                    int d = Integer.parseInt(value.toString());
                    // Si es HOY
                    if (d == diaActual && mesNavegacion == mesActual && anioNavegacion == anioActual) {
                        setBackground(new Color(46, 204, 113)); // Verde
                        setForeground(Color.BLACK);
                        setBorder(new LineBorder(Color.WHITE, 2));
                    } else if (col >= 5) { // Finde
                        setBackground(new Color(30, 30, 35));
                        setForeground(new Color(231, 76, 60)); // Rojo
                    } else {
                        setBackground(new Color(20, 20, 20));
                        setForeground(Color.WHITE);
                    }
                } else {
                    setBackground(new Color(15, 15, 15));
                    setText("");
                }
                
                if (isSel && value != null) {
                    setBackground(new Color(52, 152, 219));
                    setForeground(Color.WHITE);
                }
                return this;
            }
        });
    }

    private JButton crearBotonNav(String txt) {
        JButton b = new JButton(txt);
        b.setBackground(new Color(50, 50, 50));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Consolas", Font.BOLD, 20));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(60, 40));
        return b;
    }
}