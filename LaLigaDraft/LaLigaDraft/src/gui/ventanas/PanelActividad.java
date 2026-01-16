package gui.ventanas;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Random;

import gui.clases.Usuario;
import gui.clases.MotorNoticias;

/**
 * PANEL DE ACTIVIDAD PROFESIONAL (SYSTEM LOG & FEED)
 * Implementación de alto nivel para la visualización de eventos asíncronos.
 * Incluye renderizado vectorial personalizado (Java 2D) para evitar problemas de codificación con emojis.
 * Arquitectura Maestro-Detalle con filtrado en tiempo real.
 */
public class PanelActividad extends JPanel {

    // --- CONSTANTES DE ESTILO ---
    private static final Color COL_FONDO = new Color(18, 18, 18);
    private static final Color COL_PANEL = new Color(30, 30, 30);
    private static final Color COL_BORDE = new Color(60, 60, 60);
    private static final Color COL_TEXTO_PRI = Color.WHITE;
    private static final Color COL_TEXTO_SEC = Color.GRAY;
    
    // Colores Semánticos
    private static final Color COL_MERCADO = new Color(46, 204, 113); // Verde (Fichajes)
    private static final Color COL_LESION = new Color(231, 76, 60);   // Rojo (Enfermería)
    private static final Color COL_PARTIDO = new Color(52, 152, 219); // Azul (Competición)
    private static final Color COL_ECONOMIA = new Color(155, 89, 182); // Morado (Finanzas/Gestión)

    // --- MODELO DE DATOS ---
    private Usuario usuario;
    private MotorNoticias motorIA;
    private DefaultListModel<EventoSistema> modeloLista;
    private List<EventoSistema> cacheEventos; // Copia de respaldo para filtros
    
    // --- COMPONENTES VISUALES ---
    private JList<EventoSistema> listaVisual;
    private JLabel lblStatsTotal;
    private JLabel lblStatsCritico;
    
    // Componentes del Panel de Detalles (Lado Derecho)
    private JPanel panelDetalles;
    private JLabel detTitulo;
    private JTextArea detContenido;
    private JLabel detID;
    private JLabel detTimestamp;

    public PanelActividad(Usuario usuario) {
        this.usuario = usuario;
        this.motorIA = new MotorNoticias();
        this.modeloLista = new DefaultListModel<>();
        this.cacheEventos = new ArrayList<>();
        
        setLayout(new BorderLayout());
        setBackground(COL_FONDO);
        
        // 1. INICIALIZACIÓN DE LA INTERFAZ
        inicializarBarraHerramientas();
        inicializarAreaCentral();
        inicializarBarraEstado();
        
        // 2. CARGA DE DATOS SIMULADA
        simularCargaInicial();
    }

    /**
     * Crea la barra superior con botones de filtrado estilizados.
     */
    private void inicializarBarraHerramientas() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolbar.setBackground(new Color(25, 25, 25));
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COL_BORDE));
        
        JLabel lblLabel = new JLabel("CANALES:");
        lblLabel.setForeground(COL_TEXTO_SEC);
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        toolbar.add(lblLabel);
        
        toolbar.add(crearBotonFiltro("GLOBAL", Color.WHITE));
        toolbar.add(crearBotonFiltro("MERCADO", COL_MERCADO));
        toolbar.add(crearBotonFiltro("ENFERMERIA", COL_LESION));
        toolbar.add(crearBotonFiltro("COMPETICION", COL_PARTIDO));
        // CAMBIO: Ahora es Economía en vez de Prensa
        toolbar.add(crearBotonFiltro("ECONOMIA", COL_ECONOMIA));
        
        JButton btnRefresh = new JButton("SIMULAR TIEMPO (+)");
        btnRefresh.setBackground(new Color(50, 50, 50));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> generarEventoProcedimental());
        toolbar.add(Box.createHorizontalStrut(20));
        toolbar.add(btnRefresh);

        add(toolbar, BorderLayout.NORTH);
    }

    /**
     * Configura el área principal dividida en Lista (Izquierda) y Detalles (Derecha).
     */
    private void inicializarAreaCentral() {
        // A. LISTA DE EVENTOS
        listaVisual = new JList<>(modeloLista);
        listaVisual.setBackground(COL_FONDO);
        listaVisual.setCellRenderer(new RenderizadorEventosComplejo());
        listaVisual.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Listener para actualizar detalles al clickar
        listaVisual.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarPanelDetalles(listaVisual.getSelectedValue());
            }
        });

        JScrollPane scrollLista = new JScrollPane(listaVisual);
        scrollLista.setBorder(BorderFactory.createEmptyBorder());
        scrollLista.getVerticalScrollBar().setUnitIncrement(16);

        // B. PANEL DE DETALLES (INSPECTOR)
        panelDetalles = new JPanel();
        panelDetalles.setLayout(new BoxLayout(panelDetalles, BoxLayout.Y_AXIS));
        panelDetalles.setBackground(new Color(22, 22, 22));
        panelDetalles.setPreferredSize(new Dimension(300, 0));
        panelDetalles.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, COL_BORDE),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Construcción de elementos del inspector
        detTitulo = new JLabel("Seleccione un evento");
        detTitulo.setForeground(COL_TEXTO_PRI);
        detTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        detTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        detContenido = new JTextArea(10, 20);
        detContenido.setLineWrap(true);
        detContenido.setWrapStyleWord(true);
        detContenido.setEditable(false);
        detContenido.setBackground(new Color(22, 22, 22));
        detContenido.setForeground(COL_TEXTO_SEC);
        detContenido.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        detContenido.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        detID = new JLabel("ID: -");
        detID.setForeground(Color.DARK_GRAY);
        detID.setFont(new Font("Consolas", Font.PLAIN, 11));
        detID.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        detTimestamp = new JLabel("--:--");
        detTimestamp.setForeground(COL_MERCADO);
        detTimestamp.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Añadir al panel derecho
        panelDetalles.add(detTitulo);
        panelDetalles.add(Box.createRigidArea(new Dimension(0, 10)));
        panelDetalles.add(detTimestamp);
        panelDetalles.add(Box.createRigidArea(new Dimension(0, 20)));
        panelDetalles.add(detContenido);
        panelDetalles.add(Box.createVerticalGlue());
        panelDetalles.add(detID);

        // Split Pane para dividir pantalla
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollLista, panelDetalles);
        split.setDividerLocation(500); // Ancho inicial lista
        split.setResizeWeight(0.7);
        split.setBorder(null);
        split.setDividerSize(2);
        
        add(split, BorderLayout.CENTER);
    }
    
    private void inicializarBarraEstado() {
        JPanel status = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        status.setBackground(new Color(20, 20, 20));
        status.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COL_BORDE));
        
        lblStatsTotal = new JLabel("Total: 0");
        lblStatsTotal.setForeground(Color.GRAY);
        
        lblStatsCritico = new JLabel("Prioridad Alta: 0");
        lblStatsCritico.setForeground(COL_LESION);
        
        status.add(lblStatsTotal);
        status.add(lblStatsCritico);
        
        add(status, BorderLayout.SOUTH);
    }

    // --- LÓGICA DE NEGOCIO Y DATOS ---

    private void simularCargaInicial() {
        // Generamos eventos semilla variados
        registrarEvento("MERCADO", "Se abre el mercado de invierno.", 1);
        registrarEvento("ECONOMIA", "Ingreso extraordinario por derechos de TV (2.5M€).", 2);
        registrarEvento("COMPETICION", "Próximo partido: Domingo 21:00h vs Real Madrid.", 1);
        registrarEvento("ENFERMERIA", "Parte médico: El capitán tiene sobrecarga muscular.", 0);
        registrarEvento("ECONOMIA", "Venta récord de camisetas esta semana.", 0);
    }
    
    private void generarEventoProcedimental() {
        // Generamos texto aleatorio con mi lógica interna
        // Si usamos el motorIA, a veces da noticias de prensa.
        // Aquí vamos a forzar lógica de gestión para que no se repita con la pestaña Prensa.
        
        Random r = new Random();
        int tipoAleatorio = r.nextInt(4);
        String tipo = "";
        String texto = "";
        int severidad = 1;

        switch (tipoAleatorio) {
            case 0: // MERCADO
                tipo = "MERCADO";
                texto = "Rumores de oferta por tu delantero estrella (" + (r.nextInt(50)+10) + "M€).";
                severidad = 2;
                break;
            case 1: // ENFERMERIA
                tipo = "ENFERMERIA";
                texto = "Lesión en el entrenamiento: Baja estimada de " + (r.nextInt(3)+1) + " semanas.";
                severidad = 3; // Crítico
                break;
            case 2: // COMPETICION
                tipo = "COMPETICION";
                texto = "La Federación confirma el horario de la Jornada " + (r.nextInt(38)+1) + ".";
                break;
            case 3: // ECONOMIA (NUEVO)
                tipo = "ECONOMIA";
                String[] economicos = {
                    "Pago de nóminas a la plantilla realizado correctamente.",
                    "Nuevo patrocinador local ofrece 500k€ por temporada.",
                    "Gastos de mantenimiento del estadio: -20.000€.",
                    "Inspección de Hacienda superada sin incidencias.",
                    "Aumento en el precio de los abonos de temporada."
                };
                texto = economicos[r.nextInt(economicos.length)];
                break;
        }
        
        registrarEvento(tipo, texto, severidad);
        
        // Scroll automático al nuevo elemento
        SwingUtilities.invokeLater(() -> {
            if (modeloLista.getSize() > 0) {
                listaVisual.ensureIndexIsVisible(0);
            }
        });
    }

    private void registrarEvento(String tipo, String mensaje, int nivel) {
        EventoSistema ev = new EventoSistema(tipo, mensaje, nivel);
        cacheEventos.add(0, ev); // Añadir al principio (LIFO)
        actualizarVistaFiltrada("GLOBAL"); // Refrescar
        actualizarEstadisticas();
    }
    
    private void actualizarVistaFiltrada(String filtro) {
        modeloLista.clear();
        for (EventoSistema ev : cacheEventos) {
            if (filtro.equals("GLOBAL") || ev.getCategoria().equals(filtro)) {
                modeloLista.addElement(ev);
            }
        }
    }
    
    private void actualizarEstadisticas() {
        lblStatsTotal.setText("Eventos Registrados: " + cacheEventos.size());
        long criticos = cacheEventos.stream().filter(e -> e.getNivelImportancia() >= 3).count();
        lblStatsCritico.setText("Alertas Críticas: " + criticos);
    }

    private void actualizarPanelDetalles(EventoSistema ev) {
        if (ev == null) return;
        detTitulo.setText(ev.getCategoria());
        detTitulo.setForeground(getColorPorCategoria(ev.getCategoria()));
        detContenido.setText(ev.getMensajeCompleto());
        detTimestamp.setText("REGISTRADO: " + ev.getFechaFormateada());
        detID.setText("UUID: " + ev.getIdUnico());
    }

    private JButton crearBotonFiltro(String etiqueta, Color color) {
        JButton btn = new JButton(etiqueta);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setForeground(color);
        btn.setBackground(new Color(40, 40, 40));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COL_BORDE),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> actualizarVistaFiltrada(etiqueta));
        return btn;
    }
    
    private Color getColorPorCategoria(String cat) {
        switch (cat) {
            case "MERCADO": return COL_MERCADO;
            case "ENFERMERIA": return COL_LESION;
            case "COMPETICION": return COL_PARTIDO;
            case "ECONOMIA": return COL_ECONOMIA; // Nuevo color
            default: return Color.WHITE;
        }
    }

    // ========================================================================
    // CLASES INTERNAS (OBJETO DE DATOS)
    // ========================================================================
    
    private class EventoSistema {
        private String idUnico;
        private String categoria;
        private String mensajeCompleto;
        private long timestamp;
        private int nivelImportancia; // 0=Info, 1=Normal, 2=Aviso, 3=Crítico

        public EventoSistema(String categoria, String mensaje, int nivel) {
            this.idUnico = UUID.randomUUID().toString();
            this.categoria = categoria;
            this.mensajeCompleto = mensaje;
            this.nivelImportancia = nivel;
            this.timestamp = System.currentTimeMillis();
        }

        public String getCategoria() { return categoria; }
        public String getMensajeCompleto() { return mensajeCompleto; }
        public int getNivelImportancia() { return nivelImportancia; }
        public String getIdUnico() { return idUnico; }
        
        public String getFechaFormateada() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            return sdf.format(new Date(timestamp));
        }
        
        public String getHoraCorta() {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(new Date(timestamp));
        }
    }

    // ========================================================================
    // CLASES INTERNAS (RENDERIZADO GRÁFICO PERSONALIZADO - CANVAS 2D)
    // ========================================================================
    
    /**
     * Renderizador avanzado que dibuja componentes gráficos en lugar de usar imágenes.
     */
    private class RenderizadorEventosComplejo extends JPanel implements ListCellRenderer<EventoSistema> {
        
        private IconoCanvas iconoCanvas;
        private JLabel lblCategoria;
        private JLabel lblHora;
        private JLabel lblResumen;
        private JPanel panelTexto;

        public RenderizadorEventosComplejo() {
            setLayout(new BorderLayout(15, 0));
            setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            setBackground(COL_FONDO);
            
            // 1. ICONO DIBUJADO (IZQUIERDA)
            iconoCanvas = new IconoCanvas(40); // 40x40 px
            add(iconoCanvas, BorderLayout.WEST);
            
            // 2. CONTENIDO TEXTUAL (CENTRO)
            panelTexto = new JPanel(new GridLayout(2, 1));
            panelTexto.setOpaque(false);
            
            JPanel pCabecera = new JPanel(new BorderLayout());
            pCabecera.setOpaque(false);
            
            lblCategoria = new JLabel();
            lblCategoria.setFont(new Font("Segoe UI", Font.BOLD, 11));
            
            lblHora = new JLabel();
            lblHora.setFont(new Font("Consolas", Font.PLAIN, 10));
            lblHora.setForeground(Color.GRAY);
            
            pCabecera.add(lblCategoria, BorderLayout.WEST);
            pCabecera.add(lblHora, BorderLayout.EAST);
            
            lblResumen = new JLabel();
            lblResumen.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblResumen.setForeground(COL_TEXTO_PRI);
            
            panelTexto.add(pCabecera);
            panelTexto.add(lblResumen);
            
            add(panelTexto, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends EventoSistema> list,
                                                      EventoSistema value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            
            // Configurar datos
            lblCategoria.setText(value.getCategoria());
            lblHora.setText(value.getHoraCorta());
            lblResumen.setText(value.getMensajeCompleto());
            
            // Colores según categoría
            Color colorTema = getColorPorCategoria(value.getCategoria());
            lblCategoria.setForeground(colorTema);
            
            // Pasar color al pintor de iconos
            iconoCanvas.setColorTema(colorTema);
            iconoCanvas.setTipoIcono(value.getCategoria());
            
            // Estado de selección
            if (isSelected) {
                setBackground(new Color(45, 45, 50));
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 4, 0, 0, colorTema),
                    BorderFactory.createEmptyBorder(8, 6, 8, 10)
                ));
            } else {
                setBackground(COL_FONDO);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(35,35,35)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));
            }
            
            return this;
        }
    }
    
    /**
     * LIENZO PERSONALIZADO (JAVA 2D API)
     * Dibuja formas geométricas para representar iconos sin depender de archivos de imagen ni emojis.
     */
    private class IconoCanvas extends JPanel {
        private int size;
        private Color colorTema;
        private String tipo;

        public IconoCanvas(int size) {
            this.size = size;
            this.colorTema = Color.WHITE;
            setPreferredSize(new Dimension(size, size));
            setOpaque(false); // Transparente
        }

        public void setColorTema(Color c) { this.colorTema = c; }
        public void setTipoIcono(String t) { this.tipo = t; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            
            // Antialiasing para bordes suaves (HD)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Fondo Circular suave
            g2.setColor(new Color(30, 30, 30));
            g2.fillOval(0, 0, size, size);
            
            // Borde del círculo con el color del tema
            g2.setColor(colorTema);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(2, 2, size-4, size-4);
            
            // DIBUJAR SÍMBOLO INTERNO SEGÚN TIPO
            int center = size / 2;
            int radius = size / 4;
            
            if ("MERCADO".equals(tipo)) {
                // Símbolo de Dólar/Euro (S con barra)
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("$", center - (fm.stringWidth("$")/2), center + (fm.getAscent()/3));
                
            } else if ("ENFERMERIA".equals(tipo)) {
                // Cruz médica (+)
                g2.setStroke(new BasicStroke(4));
                g2.drawLine(center, center - radius, center, center + radius);
                g2.drawLine(center - radius, center, center + radius, center);
                
            } else if ("COMPETICION".equals(tipo)) {
                // Pelota (Círculo relleno)
                g2.fillOval(center - radius + 2, center - radius + 2, radius*2 - 4, radius*2 - 4);
                
            } else if ("ECONOMIA".equals(tipo)) {
                // NUEVO: Gráfico de Barras (Estadísticas)
                g2.setStroke(new BasicStroke(3));
                // Dibuja 3 barras de diferente altura
                g2.drawLine(center - 6, center + 6, center - 6, center + 2); // Barra izq
                g2.drawLine(center, center + 6, center, center - 2);         // Barra centro
                g2.drawLine(center + 6, center + 6, center + 6, center - 6); // Barra der (más alta)
                
            } else {
                // Por defecto (Interrogación)
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.drawString("?", center-3, center+5);
            }
        }
    }
}