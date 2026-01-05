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

        //CARGA COMPLETA 
        GestorDatos.inicializar();
        GestorDatos.cargarJugadores(); // JUGADORES DISPONIBLES
        System.out.println("✅ Jugadores cargados: " + GestorDatos.jugadores.size());

        Liga ligaActual = GestorDatos.ligas.get(usuario.getLigaActualId());

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
            if (!s.equals("Equipo") && !s.equals("Clasificación") && !s.equals("Mercado")) {
                panelContenido.add(crearPanelPlaceholder(s), s);
            }
        }

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

        // Mostrar Dashboard por defecto
        cardLayout.show(panelContenido, "Dashboard");

        // Equipo inicial si corresponde
        if (usuario.getJugadores() != null && usuario.getJugadores().size() == 15) {
            SwingUtilities.invokeLater(() -> {
                new VentanaEquipoInicial(usuario).setVisible(true);
            });
        }

        System.out.println("✅ VentanaPrincipal lista - Jugadores del usuario: " + usuario.getJugadores().size());
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
}