import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class PanelMercado extends JPanel {

    private Usuario usuario;
    private JTable tablaMercado;
    private DefaultTableModel modeloTabla;
    private JLabel labelSaldo;
    private Liga ligaActual;

    public PanelMercado(Usuario usuario) {
        this.usuario = usuario;
        
        // Cargar liga actual
        if (GestorDatos.ligas.containsKey(usuario.getLigaActualId())) {
            this.ligaActual = GestorDatos.ligas.get(usuario.getLigaActualId());
        }

        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240)); 

        // Panel superior
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBackground(Color.WHITE);
        panelInfo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titulo = new JLabel("MERCADO DE FICHAJES");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        
        int saldoVal = (ligaActual != null) ? usuario.getSaldo(ligaActual.getId()) : 0;
        labelSaldo = new JLabel("Saldo: " + formatearDinero(saldoVal));
        labelSaldo.setFont(new Font("Arial", Font.BOLD, 16));
        labelSaldo.setForeground(new Color(0, 100, 0)); 

        panelInfo.add(titulo, BorderLayout.WEST);
        panelInfo.add(labelSaldo, BorderLayout.EAST);
        add(panelInfo, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"Pos", "Nombre", "Equipo", "Puntos", "Valor Mercado (€)"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaMercado = new JTable(modeloTabla);
        tablaMercado.setRowHeight(30);
        tablaMercado.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tablaMercado);
        add(scroll, BorderLayout.CENTER);

        // Botón
        JPanel panelBoton = new JPanel();
        panelBoton.setBackground(Color.WHITE);
        JButton btnFichar = new JButton("FICHAR JUGADOR SELECCIONADO");
        btnFichar.setBackground(new Color(50, 205, 50));
        btnFichar.setForeground(Color.WHITE);
        btnFichar.addActionListener(e -> accionFichar());
        panelBoton.add(btnFichar);
        add(panelBoton, BorderLayout.SOUTH);

        cargarJugadoresEnVenta();
    }

    private void cargarJugadoresEnVenta() {
        modeloTabla.setRowCount(0); 
        if (ligaActual == null) return;
        for (int idJugador : ligaActual.getMercadoIds()) {
            Jugador j = GestorDatos.jugadores.get(idJugador);
            if (j != null) {
                modeloTabla.addRow(new Object[]{
                    j.getPosicion(), j.getNombre(), j.getEquipo(),
                    j.getTotalPuntos(), formatearDinero(j.getValorMercado())
                });
            }
        }
    }

    private void accionFichar() {
        int fila = tablaMercado.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un jugador.");
            return;
        }
        int idJugador = ligaActual.getMercadoIds().get(fila);
        Jugador j = GestorDatos.jugadores.get(idJugador);

        int confirm = JOptionPane.showConfirmDialog(this, "¿Fichar a " + j.getNombre() + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (GestorDatos.tramitarCompra(usuario, j, ligaActual)) {
                JOptionPane.showMessageDialog(this, "¡Fichado!");
                cargarJugadoresEnVenta();
                actualizarSaldoVisual();
            } else {
                JOptionPane.showMessageDialog(this, "No tienes suficiente dinero.");
            }
        }
    }
    
    private void actualizarSaldoVisual() {
        if (ligaActual != null) labelSaldo.setText("Saldo: " + formatearDinero(usuario.getSaldo(ligaActual.getId())));
    }
    private String formatearDinero(int c) { return NumberFormat.getNumberInstance(Locale.GERMANY).format(c) + " €"; }
    
    // Métodos para evitar errores en VentanaPrincipal
    public void setSaldo(int saldo) { actualizarSaldoVisual(); }
    public void mostrarSaldo(boolean b) { labelSaldo.setVisible(b); }
}