import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class ItemMercado extends JPanel {

    private final Jugador jugador;
    private final Usuario usuario;
    private final Liga liga;

    private static final DecimalFormat df = new DecimalFormat("#,###");

    public ItemMercado(Jugador jugador, Usuario usuario, Liga liga) {
        this.jugador = jugador;
        this.usuario = usuario;
        this.liga = liga;

        setLayout(new BorderLayout());
        setBackground(new Color(35, 35, 48));

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createLineBorder(new Color(70, 70, 85), 1)
        ));

        setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        add(construirContenido(), BorderLayout.CENTER);
    }

    /** PANEL COMPLETO */
    private JPanel construirContenido() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(45, 45, 60));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        panel.add(construirInfoJugador(), BorderLayout.CENTER);
        panel.add(construirBoton(), BorderLayout.EAST);

        return panel;
    }

    /** INFORMACIÓN IZQUIERDA */
    private JPanel construirInfoJugador() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(45, 45, 60));

        JLabel lblNombre = new JLabel(jugador.getNombre());
        lblNombre.setFont(new Font("Arial", Font.BOLD, 17));
        lblNombre.setForeground(Color.WHITE);

        JLabel lblPosicion = new JLabel(formatoPosicion(jugador.getPosicion()));
        lblPosicion.setOpaque(true);
        lblPosicion.setForeground(Color.WHITE);
        lblPosicion.setFont(new Font("Arial", Font.BOLD, 13));
        lblPosicion.setBackground(colorPosicion(jugador.getPosicion()));
        lblPosicion.setBorder(BorderFactory.createEmptyBorder(3, 7, 3, 7));

        JLabel lblEquipo = new JLabel("Equipo: " + jugador.getEquipo());
        lblEquipo.setForeground(Color.LIGHT_GRAY);

        JLabel lblEstado = new JLabel(formatoEstado(jugador.getEstado()));
        lblEstado.setForeground(colorEstado(jugador.getEstado()));

        JLabel lblValor = new JLabel("Valor: " + df.format(jugador.getValorMercado()) + " €");
        lblValor.setForeground(Color.WHITE);

        panel.add(lblNombre);
        panel.add(Box.createVerticalStrut(3));
        panel.add(lblPosicion);
        panel.add(lblEquipo);
        panel.add(lblEstado);
        panel.add(lblValor);

        return panel;
    }

    /** BOTÓN DE COMPRA */
    private JPanel construirBoton() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(45, 45, 60));

        JButton btn = new JButton("Fichar");
        btn.setBackground(new Color(200, 60, 60));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 40));

        btn.addActionListener(e -> intentarFichaje());

        panel.add(btn);
        return panel;
    }

    /** LÓGICA DE COMPRA */
    private void intentarFichaje() {
        int saldo = usuario.getSaldo(liga.getId());
        int precio = jugador.getValorMercado();

        if (saldo < precio) {
            JOptionPane.showMessageDialog(this, "Dinero insuficiente", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (GestorDatos.tramitarCompra(usuario, jugador, liga)) {
            JOptionPane.showMessageDialog(this,
                    "Has fichado a " + jugador.getNombre(),
                    "Compra exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /** UTILIDADES */
    private String formatoPosicion(Posicion p) {
        return switch (p) {
            case POR -> "POR";
            case DEF -> "DEF";
            case MED -> "MED";
            case DEL -> "DEL";
        };
    }

    private Color colorPosicion(Posicion p) {
        return switch (p) {
            case POR -> new Color(52, 152, 219);   // azul
            case DEF -> new Color(155, 89, 182);   // morado
            case MED -> new Color(46, 204, 113);   // verde
            case DEL -> new Color(231, 76, 60);    // rojo
        };
    }

    private String formatoEstado(Estado e) {
        return switch (e) {
            case SANO -> "✔ Sano";
            case LESIONADO -> "✘ Lesionado";
            case SANCIONADO -> "⚠ Sancionado";
        };
    }

    private Color colorEstado(Estado e) {
        return switch (e) {
            case SANO -> Color.GREEN;
            case LESIONADO -> Color.RED;
            case SANCIONADO -> Color.ORANGE;
        };
    }
}
