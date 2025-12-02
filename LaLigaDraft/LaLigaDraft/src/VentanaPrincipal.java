import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    private final Usuario usuario;
    private CardLayout cardLayout;
    private JPanel panelContenido;

    public VentanaPrincipal(Usuario usuario) {
        this.usuario = usuario;

        // Obtener la liga actual
        Liga ligaActual = GestorDatos.ligas.get(usuario.getLigaActualId());

        setTitle("Liga Fantasy");
        setSize(400, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // ====== Panel central con CardLayout ======
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(new Color(18, 18, 18)); // fondo oscuro

        String[] secciones = {"Mis Ligas", "Clasificacion", "Equipo", "Mercado", "Actividad"};

        // Crear secciones normales (excepto Equipo, Clasificacion y Mercado)
        for (String s : secciones) {
            if (!s.equals("Equipo") && !s.equals("Clasificacion") && !s.equals("Mercado")) {
                panelContenido.add(crearPanel(s), s);
            }
        }

        // Panel "Equipo"
        try {
            PanelEquipo panelEquipo = new PanelEquipo(usuario);
            JPanel panelContenedorEquipo = new JPanel(new BorderLayout());
            panelContenedorEquipo.add(panelEquipo, BorderLayout.CENTER);
            panelContenido.add(panelContenedorEquipo, "Equipo");
        } catch (Throwable t) {
            panelContenido.add(crearPanel("Equipo"), "Equipo");
        }

        // Panel "Clasificación"
        try {
            if (ligaActual != null) {
                GestorDatos.cargarUsuariosLiga(ligaActual);
            }
            PanelClasificacion panelClasificacion = new PanelClasificacion(ligaActual);
            JPanel panelContenedorClasificacion = new JPanel(new BorderLayout());
            panelContenedorClasificacion.setBackground(new Color(18, 18, 18));
            panelClasificacion.setBackground(new Color(18, 18, 18));
            panelContenedorClasificacion.add(panelClasificacion, BorderLayout.CENTER);
            panelContenido.add(panelContenedorClasificacion, "Clasificacion");
        } catch (Throwable t) {
            JPanel panelClasificacion = crearPanel("Clasificacion - Error: " + t.getMessage());
            panelClasificacion.setBackground(new Color(18, 18, 18));
            panelContenido.add(panelClasificacion, "Clasificacion");
        }

        // Panel "Mercado"
        PanelMercado panelMercado = new PanelMercado(usuario);
        // ====== Barra de navegación inferior ======
        JPanel panelMenu = new JPanel(new GridLayout(1, secciones.length));
        panelMenu.setBackground(new Color(28, 28, 28));

        for (String s : secciones) {
            JButton boton = new JButton(s);
            boton.setPreferredSize(new Dimension(0, 60));
            boton.setFocusPainted(false);
            boton.setBackground(new Color(28, 28, 28));
            boton.setForeground(Color.WHITE);
            boton.setBorderPainted(false);
            boton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            boton.addActionListener((e) -> {
                cardLayout.show(panelContenido, s);

                // Mostrar saldo solo en "Mercado" y actualizar con el saldo de la liga actual
                if ("Mercado".equals(s)) {
                    panelMercado.setSaldo(usuario.getSaldo(usuario.getLigaActualId()));
                    panelMercado.mostrarSaldo(true);
                } else {
                    panelMercado.mostrarSaldo(false);
                }
            });

            panelMenu.add(boton);
        }
        panelContenido.add(panelMercado, "Mercado");


        // ====== Layout general ======
        setLayout(new BorderLayout());
        add(panelContenido, BorderLayout.CENTER);
        add(panelMenu, BorderLayout.SOUTH);

        cardLayout.show(panelContenido, "Mis Ligas");

        // Abrir ventana de equipo inicial si el usuario no tiene jugadores
        if (usuario.getJugadores() != null && usuario.getJugadores().size() == 15) {
            SwingUtilities.invokeLater(() -> {
                VentanaEquipoInicial ve = new VentanaEquipoInicial(usuario);
                ve.setVisible(true);
            });
        }
    }

    private JPanel crearPanel(String texto) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(18, 18, 18));
        JLabel label = new JLabel("Sección: " + texto, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
}
