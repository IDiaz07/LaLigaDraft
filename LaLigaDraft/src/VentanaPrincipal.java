import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class VentanaPrincipal extends JFrame {

    private CardLayout cardLayout;
    private JPanel panelContenido;
    private JLabel labelSaldo;
    private int saldo = 500000; // saldo inicial ficticio

    public VentanaPrincipal() {
        setTitle("Liga Fantasy");
        setSize(400, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Panel superior con saldo
        JPanel panelSuperior = new JPanel(new BorderLayout());
        labelSaldo = new JLabel("Tu saldo: " + saldo + " €");
        labelSaldo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelSaldo.setHorizontalAlignment(SwingConstants.RIGHT);
        panelSuperior.add(labelSaldo, BorderLayout.EAST);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel inferior (barra de navegación)
        JPanel panelMenu = new JPanel(new GridLayout(1, 5));
        String[] secciones = {"Mis Ligas", "Clasificación", "Equipo", "Mercado", "Actividad"};

        // Panel central con CardLayout
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);

        // Crear secciones normales
        for (String s : secciones) {
            if (!s.equals("Equipo") && !s.equals("Mercado")) {
                panelContenido.add(crearPanel(s), s);
            }
        }

        // Panel "Equipo"
        PanelEquipo panelEquipo = new PanelEquipo();
        JPanel panelContenedorEquipo = new JPanel(new BorderLayout());
        panelContenedorEquipo.add(panelEquipo, BorderLayout.CENTER);
        panelContenido.add(panelContenedorEquipo, "Equipo");

        // Panel "Mercado"
        JPanel panelMercado = crearPanelMercado();
        panelContenido.add(panelMercado, "Mercado");

        // Crear botones de navegación con estilo simple
        for (String s : secciones) {
            JButton boton = new JButton(s);
            boton.setPreferredSize(new Dimension(0, 60));
            boton.setFocusPainted(false);
            boton.setBackground(new Color(52, 152, 219));
            boton.setForeground(Color.WHITE);
            boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            boton.addActionListener((e) -> cardLayout.show(panelContenido, s));
            panelMenu.add(boton);
        }

        JPanel contenedorMenu = new JPanel(new BorderLayout());
        contenedorMenu.add(panelMenu, BorderLayout.CENTER);
        contenedorMenu.setPreferredSize(new Dimension(400, 70));

        // Layout general
        setLayout(new BorderLayout());
        add(panelSuperior, BorderLayout.NORTH);   // saldo arriba
        add(panelContenido, BorderLayout.CENTER);
        add(contenedorMenu, BorderLayout.SOUTH);

        cardLayout.show(panelContenido, "Mis Ligas");
    }

    // Panel de Mercado con botones superiores
    private JPanel crearPanelMercado() {
        JPanel panel = new JPanel(new BorderLayout());

        // Panel superior con botones principales
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnMercado = new JButton("Mercado");
        JButton btnOperaciones = new JButton("Mis operaciones");
        JButton btnHistorico = new JButton("Histórico");

        // Panel para mostrar sub-botones de "Mis operaciones"
        JPanel panelSubOperaciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelSubOperaciones.setVisible(false); // inicialmente oculto

        JButton btnCompras = new JButton("Compras");
        JButton btnVentas = new JButton("Ventas");
        panelSubOperaciones.add(btnCompras);
        panelSubOperaciones.add(btnVentas);

        // Acción: mostrar/ocultar sub-botones al clickear "Mis operaciones"
        btnOperaciones.addActionListener((ActionEvent e) -> {
            panelSubOperaciones.setVisible(!panelSubOperaciones.isVisible());
            panel.revalidate();
            panel.repaint();
        });

        // Añadir los tres botones principales
        panelBotones.add(btnMercado);
        panelBotones.add(btnOperaciones);
        panelBotones.add(btnHistorico);

        panel.add(panelBotones, BorderLayout.NORTH);
        panel.add(panelSubOperaciones, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanel(String texto) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Sección: " + texto, SwingConstants.CENTER), BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
