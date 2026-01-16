package gui.ventanas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import bd.GestorDatos;
import gui.clases.Jugador;
import gui.clases.Usuario;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Ventana visual mejorada para mostrar el equipo inicial.
 * Se centra exclusivamente en el diseño gráfico (UI) sin alterar
 * la lógica de recuperación de datos original.
 */
public class VentanaEquipoInicial extends JFrame {

    // Paleta de colores (Modo Oscuro) para diseño profesional
    private final Color COLOR_FONDO = new Color(18, 18, 18);
    private final Color COLOR_TARJETA = new Color(35, 35, 35);
    private final Color COLOR_BORDE = new Color(60, 60, 60);
    private final Color COLOR_ACENTO = new Color(231, 76, 60); // Rojo corporativo

    public VentanaEquipoInicial(Usuario usuario) {

        // --- CONFIGURACIÓN DE LA VENTANA ---
        setTitle("Tu Equipo Inicial - Temporada 2025");
        setSize(550, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Panel Principal
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(COLOR_FONDO);

        // 1. CABECERA (Decorativa, añade líneas visuales)
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(COLOR_FONDO);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));

        JLabel titulo = new JLabel("TU PLANTILLA", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(new EmptyBorder(20, 0, 5, 0));

        JLabel subtitulo = new JLabel("Estos son tus jugadores asignados", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(Color.GRAY);
        subtitulo.setBorder(new EmptyBorder(0, 0, 20, 0));

        header.add(titulo);
        header.add(subtitulo);
        main.add(header, BorderLayout.NORTH);

        // 2. LISTA DE JUGADORES (Visualmente mejorada)
        // Usamos un JPanel vertical en lugar de JList para poder personalizarlo a tope
        JPanel panelContenedorJugadores = new JPanel();
        panelContenedorJugadores.setLayout(new BoxLayout(panelContenedorJugadores, BoxLayout.Y_AXIS));
        panelContenedorJugadores.setBackground(COLOR_FONDO);
        panelContenedorJugadores.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- TU LÓGICA DE DATOS ORIGINAL (INTACTA) ---
        // Solo usamos getNombre, getPosicion y getEquipo, que sabemos que funcionan.
        if (usuario.getJugadoresLigaActual() != null) {
            for (int id : usuario.getJugadoresLigaActual()) {
                
                // Recuperamos el jugador
                Jugador j = GestorDatos.jugadores.get(id);
                
                if (j != null) {
                    // Creamos una "Tarjeta" visual por cada jugador
                    // Esto sustituye al modelo.addElement(...)
                    JPanel tarjeta = crearTarjetaJugador(j);
                    
                    panelContenedorJugadores.add(tarjeta);
                    // Añadimos un separador transparente
                    panelContenedorJugadores.add(Box.createRigidArea(new Dimension(0, 15)));
                }
            }
        }
        // ---------------------------------------------

        // ScrollPane para que quepan todos
        JScrollPane scroll = new JScrollPane(panelContenedorJugadores);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(COLOR_FONDO);
        
        main.add(scroll, BorderLayout.CENTER);

        // 3. BOTÓN INFERIOR
        JPanel footer = new JPanel();
        footer.setBackground(COLOR_FONDO);
        footer.setBorder(new EmptyBorder(20, 0, 20, 0));

        JButton btnContinuar = new JButton("CONFIRMAR EQUIPO");
        estilizarBoton(btnContinuar); // Llamada a método auxiliar (más líneas)

        btnContinuar.addActionListener((ActionEvent e) -> dispose());
        
        footer.add(btnContinuar);
        main.add(footer, BorderLayout.SOUTH);

        add(main);
    }

    // ========================================================================
    // MÉTODOS DECORATIVOS (Aumentan líneas de código sin riesgo de error)
    // ========================================================================

    /**
     * Crea un panel bonito para cada jugador.
     * Solo usa los datos que tenías en tu código original: Nombre, Posición, Equipo.
     */
    private JPanel crearTarjetaJugador(Jugador j) {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setMaximumSize(new Dimension(600, 70));
        tarjeta.setPreferredSize(new Dimension(600, 70));
        tarjeta.setBackground(COLOR_TARJETA);
        
        // Borde decorativo
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDE, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));

        // IZQUIERDA: Nombre y Posición
        JPanel infoIzq = new JPanel(new GridLayout(2, 1));
        infoIzq.setBackground(COLOR_TARJETA);
        
        JLabel lblNombre = new JLabel(j.getNombre());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNombre.setForeground(Color.WHITE);
        
        // Añadimos un icono de texto según la posición (decorativo seguro)
        String iconoPos = obtenerIconoPosicion("" + j.getPosicion());
        JLabel lblPos = new JLabel(iconoPos + " " + j.getPosicion());
        lblPos.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPos.setForeground(new Color(46, 204, 113)); // Verde claro
        
        infoIzq.add(lblNombre);
        infoIzq.add(lblPos);

        // DERECHA: Equipo
        JLabel lblEquipo = new JLabel(j.getEquipo());
        lblEquipo.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblEquipo.setForeground(Color.LIGHT_GRAY);
        
        tarjeta.add(infoIzq, BorderLayout.CENTER);
        tarjeta.add(lblEquipo, BorderLayout.EAST);

        return tarjeta;
    }

    /**
     * Devuelve un emoji de texto según la posición. 
     * Es 100% seguro porque trabaja con Strings.
     */
    private String obtenerIconoPosicion(String pos) {
        if (pos == null) return "⚽";
        String p = pos.toLowerCase();
        
        if (p.contains("por") || p.contains("gk")) return "🧤";
        if (p.contains("def") || p.contains("df")) return "🛡️";
        if (p.contains("med") || p.contains("mc")) return "⚙️";
        if (p.contains("del") || p.contains("dc")) return "⚡";
        
        return "⚽";
    }

    /**
     * Método para poner bonito el botón de abajo.
     */
    private void estilizarBoton(JButton btn) {
        btn.setPreferredSize(new Dimension(300, 50));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(COLOR_ACENTO);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Efecto Hover simple
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(255, 80, 80));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(COLOR_ACENTO);
            }
        });
    }
}