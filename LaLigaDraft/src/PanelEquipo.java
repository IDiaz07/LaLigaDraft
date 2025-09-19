import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelEquipo extends JPanel {

    private List<JLabel> jugadores = new ArrayList<>();
    private JPanel campo; // panel del campo de fútbol
    private JComboBox<String> comboFormacion; // combo para seleccionar formación

    public PanelEquipo() {
        setLayout(new BorderLayout());
        setBackground(Color.DARK_GRAY); // fondo gris alrededor

        // Panel de campo
        campo = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 120, 0)); // césped
                g.fillRect(0, 0, getWidth(), getHeight());

                g.setColor(Color.white);
                int w = getWidth();
                int h = getHeight();

                // Círculo central
                g.drawOval(w / 2 - 50, h / 2 - 50, 100, 100);
                // Línea central
                g.drawLine(0, h / 2, w, h / 2);
                // Arcos
                g.drawRect(w / 2 - 60, 0, 120, 60);
                g.drawRect(w / 2 - 60, h - 60, 120, 60);
            }
        };
        campo.setPreferredSize(new Dimension(400, 400));
        campo.setBorder(BorderFactory.createLineBorder(Color.white, 2));
        add(campo, BorderLayout.CENTER);

        // Crear jugadores
        for (int i = 0; i < 11; i++) {
            JLabel jugador = new JLabel("◻", SwingConstants.CENTER);
            jugador.setFont(new Font("Arial", Font.PLAIN, 24));
            jugador.setSize(40, 40);
            jugadores.add(jugador);
            campo.add(jugador);
        }

        // Panel inferior para ComboBox (donde antes estaban los botones)
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInferior.setBackground(new Color(18, 18, 18));
        panelInferior.setPreferredSize(new Dimension(400, 50));

        // ComboBox para seleccionar formación
        String[] formaciones = {"4-4-2", "4-3-3", "3-5-2"};
        comboFormacion = new JComboBox<>(formaciones);
        comboFormacion.setSelectedIndex(0); // formación inicial
        comboFormacion.addActionListener(e -> {
            String seleccion = (String) comboFormacion.getSelectedItem();
            setFormacion(seleccion);
        });

        panelInferior.add(comboFormacion);
        add(panelInferior, BorderLayout.SOUTH);

        // Formación inicial
        setFormacion("4-4-2");
    }

    /** Cambiar formación */
    public void setFormacion(String formacion) {
        int width = campo.getWidth() > 0 ? campo.getWidth() : 400;
        int height = campo.getHeight() > 0 ? campo.getHeight() : 400;

        // Portero abajo
        jugadores.get(0).setLocation(width / 2 - 20, height - 50);

        switch (formacion) {
            case "4-4-2":
                colocarLinea(1, 4, height * 3 / 4);
                colocarLinea(5, 4, height / 2);
                colocarLinea(9, 2, height / 5);
                break;
            case "4-3-3":
                colocarLinea(1, 4, height * 3 / 4);
                colocarLinea(5, 3, height / 2);
                colocarLinea(8, 3, height / 5);
                break;
            case "3-5-2":
                colocarLinea(1, 3, height * 3 / 4);
                colocarLinea(4, 5, height / 2);
                colocarLinea(9, 2, height / 5);
                break;
        }
    }

    /** Coloca jugadores en línea centrada horizontalmente */
    private void colocarLinea(int startIndex, int cantidad, int y) {
        int width = campo.getWidth() > 0 ? campo.getWidth() : 400;
        int espacio = width / (cantidad + 1);
        for (int i = 0; i < cantidad; i++) {
            JLabel jugador = jugadores.get(startIndex + i);
            int x = espacio * (i + 1) - 20;
            jugador.setLocation(x, y);
        }
    }
}
