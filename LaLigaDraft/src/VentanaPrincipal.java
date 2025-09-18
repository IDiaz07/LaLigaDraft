import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class VentanaPrincipal extends JFrame {

    private CardLayout cardLayout;
    private JPanel panelContenido;

    public VentanaPrincipal() {
        setTitle("Liga Fantasy");
        setSize(400, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Panel inferior (barra de navegación)
        JPanel panelMenu = new JPanel(new GridLayout(1, 5));
        String[] secciones = {"Mis Ligas", "Clasificacion", "Equipo", "Mercado", "Actividad"};

        // Panel central con CardLayout
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);

        // Crear secciones
        for (String s : secciones) {
            if (!s.equals("Equipo") && !s.equals("Clasificacion")) {
                panelContenido.add(crearPanel(s), s);
            }
            
        }

        // Panel "Equipo" con PanelEquipo y botones de formaciones
        PanelEquipo panelEquipo = new PanelEquipo();
        JPanel panelContenedorEquipo = new JPanel(new BorderLayout());
        panelContenedorEquipo.add(panelEquipo, BorderLayout.CENTER);

        panelContenido.add(panelContenedorEquipo, "Equipo");
        
        PanelClasificacion panelClasificacion = new PanelClasificacion();
        JPanel panelContenedorClasificacion = new JPanel(new BorderLayout());
        panelContenedorClasificacion.add(panelClasificacion, BorderLayout.CENTER);
        
        panelContenido.add(panelContenedorClasificacion, "Clasificacion");

        // Crear botones de navegación
        for (String s : secciones) {
            JButton boton = new JButton(s);
            boton.setPreferredSize(new Dimension(0, 60)); // más altos
            boton.addActionListener((ActionEvent e) -> cardLayout.show(panelContenido, s));
            panelMenu.add(boton);
        }

        JPanel contenedorMenu = new JPanel(new BorderLayout());
        contenedorMenu.add(panelMenu, BorderLayout.CENTER);
        contenedorMenu.setPreferredSize(new Dimension(400, 70));

        // Layout general
        setLayout(new BorderLayout());
        add(panelContenido, BorderLayout.CENTER);
        add(contenedorMenu, BorderLayout.SOUTH);

        cardLayout.show(panelContenido, "Mis Ligas");
    }

    // Método auxiliar para crear paneles genéricos
    private JPanel crearPanel(String texto) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Sección: " + texto, SwingConstants.CENTER), BorderLayout.CENTER);
        return panel;
    }
}