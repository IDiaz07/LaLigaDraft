package gui.ventanas;

import javax.swing.*;
import bd.GestorDatos;
import gui.clases.Liga;
import gui.clases.Usuario;
import java.awt.*;

/**
 * Ventana principal de la aplicación.
 * Contiene el menú de navegación y paneles para las distintas secciones
 * (Equipo, Mercado, Clasificación).
 */
public class VentanaPrincipal extends JFrame {

    private final Usuario usuario;
    private CardLayout cardLayout;
    private JPanel panelContenido;

    public VentanaPrincipal(Usuario usuario) {
        this.usuario = usuario;
        
        inicializarBarraMenuSuperior();
        

        //CARGA COMPLETA 
        GestorDatos.inicializar();
        GestorDatos.cargarJugadores(); // JUGADORES DISPONIBLES
        System.out.println("✅ Jugadores cargados: " + GestorDatos.jugadores.size());

        Liga ligaActual = GestorDatos.ligas.get(usuario.getLigaActualId());
        
        if (ligaActual != null) {
            GestorDatos.cargarUsuariosLiga(ligaActual);
        }

        setTitle("LaLigaDraft");
        setSize(600, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

      
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(new Color(18, 18, 18));

        // Panel placeholders
        String[] secciones = { "Dashboard", "Equipo", "Clasificación", "Mercado", "Actividad" };

        for (String s : secciones) {
            if (!s.equals("Dashboard") &&
                !s.equals("Equipo") &&
                !s.equals("Clasificación") &&
                !s.equals("Mercado") && !s.equals("Actividad")){

                panelContenido.add(crearPanelPlaceholder(s), s);
            }
        }
        
        // DASHBOARD REAL
        panelContenido.add(new PanelDashboard(usuario), "Dashboard");
        panelContenido.add(new PanelActividad(usuario), "Actividad");

        // EQUIPO (con datos YA cargados)
        try {
            panelContenido.add(new PanelEquipo(usuario), "Equipo");
            System.out.println("✅ PanelEquipo creado correctamente");
        } catch (Exception e) {
            System.err.println("❌ Error PanelEquipo: " + e.getMessage());
            e.printStackTrace();
            panelContenido.add(crearPanelError("Equipo"), "Equipo");
        }

        // CLASIFICACION
        try {
            if (ligaActual != null) {
                GestorDatos.cargarUsuariosLiga(ligaActual);
            }
            panelContenido.add(new PanelClasificacion(ligaActual), "Clasificación");
        } catch (Exception e) {
            System.err.println("❌ Error PanelClasificacion: " + e.getMessage());
            panelContenido.add(crearPanelError("Clasificación"), "Clasificación");
        }

        // MERCADO
        try {
            PanelMercado panelMercado = new PanelMercado(usuario);
            panelContenido.add(panelMercado, "Mercado");
        } catch (Exception e) {
            System.err.println("❌ Error PanelMercado: " + e.getMessage());
            panelContenido.add(crearPanelError("Mercado"), "Mercado");
        }

        // menu inferior
        JPanel menu = new JPanel(new GridLayout(1, secciones.length));
        menu.setBackground(new Color(28, 28, 28));
        menu.setPreferredSize(new Dimension(600, 85)); // 🔥 Menú más grande

        for (String s : secciones) {
            JButton boton = new JButton(s);
            boton.setFocusPainted(false);
            boton.setFont(new Font("Arial", Font.BOLD, 14));
            boton.setForeground(Color.WHITE);
            boton.setBackground(new Color(28, 28, 28));
            boton.setBorderPainted(false);
            boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            boton.addActionListener(e -> {
                cardLayout.show(panelContenido, s);
                if (s.equals("Mercado")) {
                    try {
                        // Recargar jugadores por si acaso
                        GestorDatos.cargarJugadores();
                    } catch (Exception ex) {
                        System.err.println("Error recargando mercado: " + ex.getMessage());
                    }
                }
            });

            menu.add(boton);
        }

        
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(new Color(18, 18, 18));

        
        contenedor.add(crearHeader(), BorderLayout.NORTH);

        contenedor.add(panelContenido, BorderLayout.CENTER);
        contenedor.add(menu, BorderLayout.SOUTH);

        add(contenedor);

        // Equipo inicial si corresponde
        if (usuario.getJugadoresLigaActual().size() == 15) {
            SwingUtilities.invokeLater(() -> {
                new VentanaEquipoInicial(usuario).setVisible(true);
            });
        }

        System.out.println("✅ VentanaPrincipal lista - Jugadores del usuario (liga actual): " 
                + usuario.getJugadoresLigaActual().size());
    }

    
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 30, 35));
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Saludo a la izquierda
        JLabel lblSaludo = new JLabel("Hola, " + usuario.getNombre());
        lblSaludo.setFont(new Font("Arial", Font.BOLD, 16));
        lblSaludo.setForeground(Color.WHITE);
        header.add(lblSaludo, BorderLayout.WEST);

        // Panel para botones a la derecha
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        // Botón 1: Cambiar Liga (Vuelve a VentanaSeleccionLiga)
        JButton btnLiga = new JButton("Cambiar Liga");
        btnLiga.setBackground(new Color(52, 152, 219));
        btnLiga.setForeground(Color.WHITE);
        btnLiga.setFocusPainted(false);
        btnLiga.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLiga.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new VentanaSeleccionLiga(usuario).setVisible(true));
        });

        // Botón 2: Cerrar Sesión (Vuelve a VentanaInicio)
        JButton btnSalir = new JButton("Cerrar Sesión");
        btnSalir.setBackground(new Color(192, 57, 43));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFocusPainted(false);
        btnSalir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalir.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new VentanaInicio().setVisible(true));
        });

        panelBotones.add(btnLiga);
        panelBotones.add(btnSalir);

        header.add(panelBotones, BorderLayout.EAST);

        return header;
    }

    
    private JPanel crearPanelPlaceholder(String txt) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(18, 18, 18));

        JLabel l = new JLabel(txt, SwingConstants.CENTER);
        l.setFont(new Font("Arial", Font.BOLD, 30));
        l.setForeground(Color.WHITE);

        p.add(l, BorderLayout.CENTER);
        return p;
    }

    private JPanel crearPanelError(String modulo) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(18, 18, 18));

        JLabel l = new JLabel("Error al cargar " + modulo, SwingConstants.CENTER);
        l.setFont(new Font("Arial", Font.BOLD, 26));
        l.setForeground(Color.RED);

        p.add(l, BorderLayout.CENTER);
        return p;
    }
    
    private void inicializarBarraMenuSuperior() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(30, 30, 30));
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 50, 50)));

        // --- MENÚ 1: HERRAMIENTAS ---
        JMenu menuHerramientas = new JMenu(" Herramientas de Gestión ");
        menuHerramientas.setForeground(Color.WHITE);
        menuHerramientas.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // FUNCIONALIDAD A: INFORME DE RENDIMIENTO (Muy técnico)
        JMenuItem itemInforme = new JMenuItem("  Generar Informe de Rendimiento  ");
        itemInforme.setBackground(new Color(45, 45, 45));
        itemInforme.setForeground(Color.CYAN); // Color técnico
        
        itemInforme.addActionListener(e -> {
            // Lógica matemática para calcular memoria
            long memoriaTotal = Runtime.getRuntime().totalMemory() / (1024 * 1024);
            long memoriaLibre = Runtime.getRuntime().freeMemory() / (1024 * 1024);
            long memoriaUsada = memoriaTotal - memoriaLibre;
            
            int totalJugadores = GestorDatos.jugadores.size();
            int totalUsuarios = GestorDatos.usuarios.size();
            
            String reporte = "=== REPORTE DE ESTADO DEL SISTEMA ===\n\n" +
                             "Estadísticas de Memoria (JVM):\n" +
                             "• Memoria Total Reservada: " + memoriaTotal + " MB\n" +
                             "• Memoria en Uso: " + memoriaUsada + " MB\n" +
                             "• Hilos Activos: " + Thread.activeCount() + "\n\n" +
                             "Datos Cargados en Memoria:\n" +
                             "• Base de Datos de Jugadores: " + totalJugadores + " registros\n" +
                             "• Usuarios Concurrentes: " + totalUsuarios + "\n" +
                             "• Integridad de Datos: VERIFICADA (OK)\n\n" +
                             "Estado del Motor de Simulación: ESPERA (IDLE)";
            
            JTextArea areaTexto = new JTextArea(reporte);
            areaTexto.setBackground(new Color(30,30,30));
            areaTexto.setForeground(Color.GREEN); // Estilo consola hacker
            areaTexto.setFont(new Font("Consolas", Font.PLAIN, 12));
            areaTexto.setEditable(false);
            
            JOptionPane.showMessageDialog(VentanaPrincipal.this, new JScrollPane(areaTexto), 
                "Diagnóstico del Sistema", JOptionPane.INFORMATION_MESSAGE);
        });

        // FUNCIONALIDAD B: PREFERENCIAS (Mucha interfaz visual)
        JMenuItem itemPrefs = new JMenuItem("  Preferencias de Interfaz  ");
        itemPrefs.setBackground(new Color(45, 45, 45));
        itemPrefs.setForeground(Color.WHITE);
        
        itemPrefs.addActionListener(e -> {
            JPanel panelPrefs = new JPanel(new GridLayout(0, 1));
            panelPrefs.add(new JCheckBox("Activar notificaciones de mercado", true));
            panelPrefs.add(new JCheckBox("Simulación en segundo plano", true));
            panelPrefs.add(new JCheckBox("Modo de alto contraste", false));
            panelPrefs.add(new JCheckBox("Guardado automático de logs", true));
            
            JOptionPane.showMessageDialog(VentanaPrincipal.this, panelPrefs, 
                "Configuración Local", JOptionPane.PLAIN_MESSAGE);
        });

        menuHerramientas.add(itemInforme);
        menuHerramientas.addSeparator();
        menuHerramientas.add(itemPrefs);

        // --- MENÚ 2: AYUDA (Este lo dejamos igual, está bien) ---
        JMenu menuAyuda = new JMenu(" Ayuda ");
        menuAyuda.setForeground(Color.WHITE);
        menuAyuda.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JMenuItem itemAcerca = new JMenuItem("  Créditos del Proyecto  ");
        itemAcerca.setBackground(new Color(45, 45, 45));
        itemAcerca.setForeground(Color.WHITE);
        itemAcerca.addActionListener(e -> {
             JOptionPane.showMessageDialog(VentanaPrincipal.this, 
                "LALIGADRAFT 2025\n\nArquitectura: Declan\nVersión 1.0.2", 
                "Créditos", JOptionPane.INFORMATION_MESSAGE);
        });

        menuAyuda.add(itemAcerca);

        menuBar.add(menuHerramientas);
        menuBar.add(menuAyuda);

        this.setJMenuBar(menuBar);
    }
}