import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

	private final Usuario usuario;
    private CardLayout cardLayout;
    private JPanel panelContenido;
    private JLabel labelSaldo;
    private JPanel panelSaldo;
    private int saldo = 500000; // saldo inicial ficticio

    public VentanaPrincipal(Usuario usuario) {
    	this.usuario = usuario;
    	
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

        // Crear secciones normales
        for (String s : secciones) {
            if (!s.equals("Equipo") && !s.equals("Clasificacion") && !s.equals("Mercado")) {
                panelContenido.add(crearPanel(s), s);
            }
        }

        // Panel "Equipo" (sin cambios)
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
            PanelClasificacion panelClasificacion = new PanelClasificacion();
            JPanel panelContenedorClasificacion = new JPanel(new BorderLayout());
            panelContenedorClasificacion.setBackground(new Color(18, 18, 18));
            panelClasificacion.setBackground(new Color(18, 18, 18));
            panelContenedorClasificacion.add(panelClasificacion, BorderLayout.CENTER);
            panelContenido.add(panelContenedorClasificacion, "Clasificacion");
        } catch (Throwable t) {
        	JPanel panelClasificacion = crearPanel("Clasificacion");
            panelClasificacion.setBackground(new Color(18, 18, 18)); // también aquí
            panelContenido.add(panelClasificacion, "Clasificacion");
        }

        // Panel "Mercado" con barra de búsqueda + saldo
        JPanel panelMercado = new JPanel(new BorderLayout());
        panelMercado.setBackground(new Color(18, 18, 18));

        // Barra de búsqueda
        JPanel barraBusqueda = new JPanel(new BorderLayout());
        barraBusqueda.setBackground(new Color(28, 28, 28));
        barraBusqueda.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JTextField campoBusqueda = new JTextField();
        campoBusqueda.setPreferredSize(new Dimension(200, 34));
        campoBusqueda.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        campoBusqueda.setBackground(new Color(40, 40, 40));
        campoBusqueda.setForeground(Color.WHITE);

        JButton botonBuscar = new JButton("🔍");
        botonBuscar.setBackground(new Color(231, 76, 60));
        botonBuscar.setForeground(Color.WHITE);
        botonBuscar.setFocusPainted(false);
        botonBuscar.setBorderPainted(false);

        barraBusqueda.add(campoBusqueda, BorderLayout.CENTER);
        barraBusqueda.add(botonBuscar, BorderLayout.EAST);

        panelMercado.add(barraBusqueda, BorderLayout.NORTH);

        // Panel de saldo en esquina inferior derecha (oculto por defecto)
        panelSaldo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSaldo.setBackground(new Color(18, 18, 18));
        labelSaldo = new JLabel("Saldo: " + saldo + " €");
        labelSaldo.setForeground(Color.WHITE);
        labelSaldo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelSaldo.add(labelSaldo);
        panelSaldo.setVisible(false);

        // Añadimos el panel de saldo al sur del Mercado
        panelMercado.add(panelSaldo, BorderLayout.SOUTH);

        panelContenido.add(panelMercado, "Mercado");

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

                // Mostrar saldo solo en "Mercado"
                if ("Mercado".equals(s)) {
                    panelSaldo.setVisible(true);
                } else {
                    panelSaldo.setVisible(false);
                }
            });

            panelMenu.add(boton);
        }

        // ====== Layout general ======
        setLayout(new BorderLayout());
        add(panelContenido, BorderLayout.CENTER);
        add(panelMenu, BorderLayout.SOUTH);

        cardLayout.show(panelContenido, "Mis Ligas");
    
        if (usuario.getJugadores() != null && usuario.getJugadores().size() == 15) {
            // Se asume que 15 jugadores es el equipo inicial asignado
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