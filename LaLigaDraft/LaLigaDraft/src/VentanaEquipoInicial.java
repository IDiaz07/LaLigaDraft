import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class VentanaEquipoInicial extends JFrame {

    private final Usuario usuario;

    public VentanaEquipoInicial(Usuario usuario) {
        this.usuario = usuario;
        setTitle("Tu equipo inicial - " + usuario.getNombre());
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        
        // Panel principal con 4 tablas
        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Crear cada tabla
        mainPanel.add(crearTablaPorPosicion("Porteros", Posicion.POR));
        mainPanel.add(crearTablaPorPosicion("Defensas", Posicion.DEF));
        mainPanel.add(crearTablaPorPosicion("Mediocentros", Posicion.MED));
        mainPanel.add(crearTablaPorPosicion("Delanteros", Posicion.DEL));

        JButton btnContinuar = new JButton("Continuar");
        btnContinuar.addActionListener(e -> dispose());

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        add(btnContinuar, BorderLayout.SOUTH);
    }

    private JPanel crearTablaPorPosicion(String titulo, Posicion pos) {
        // Filtramos jugadores del usuario por esa posición
        List<Jugador> lista = new ArrayList<>();
        for (int idJ : usuario.getJugadores()) {
            Jugador j = GestorDatos.jugadores.get(idJ);
            if (j != null && j.getPosicion() == pos) {
                lista.add(j);
            }
        }

        // Modelo de tabla
        String[] columnas = {"Posición", "Nombre", "Equipo", "Valor Mercado", "Puntos Totales"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        for (Jugador j : lista) {
            modelo.addRow(new Object[]{
                    j.getPosicion(),
                    j.getNombre(),
                    j.getEquipo(),
                    j.getValorMercado(),
                    j.getTotalPuntos()
            });
        }

        JTable tabla = new JTable(modelo);
        tabla.setEnabled(false);
        JScrollPane scroll = new JScrollPane(tabla);
        tabla.getTableHeader().setReorderingAllowed(false);

        // Panel con título
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(titulo, SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(label, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }
}
