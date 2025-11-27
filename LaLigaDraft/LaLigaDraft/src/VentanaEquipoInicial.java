import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;


public class VentanaEquipoInicial extends JFrame {

    private final Usuario usuario;

    public VentanaEquipoInicial(Usuario usuario) {
        this.usuario = usuario;
        setTitle("Tu equipo inicial - " + usuario.getNombre());
        setSize(400, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Panel principal con 4 filas (una por posición)
        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(crearTablaPorPosicion("Porteros", Posicion.POR));
        mainPanel.add(crearTablaPorPosicion("Defensas", Posicion.DEF));
        mainPanel.add(crearTablaPorPosicion("Mediocentros", Posicion.MED));
        mainPanel.add(crearTablaPorPosicion("Delanteros", Posicion.DEL));

        JButton btnContinuar = new JButton("Continuar");
        btnContinuar.addActionListener(e -> dispose());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(btnContinuar, BorderLayout.SOUTH);
    }

    private JPanel crearTablaPorPosicion(String titulo, Posicion pos) {
        List<Jugador> lista = new ArrayList<>();
        for (int idJ : usuario.getJugadores()) {
            Jugador j = GestorDatos.jugadores.get(idJ);
            if (j != null && j.getPosicion() == pos) {
                lista.add(j);
            }
        }

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
        tabla.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(tabla);

        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(titulo, SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(label, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }
}
