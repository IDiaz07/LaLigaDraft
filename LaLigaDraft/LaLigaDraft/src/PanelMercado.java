import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class PanelMercado extends JPanel {

    private JLabel labelSaldo;
    private JPanel panelSaldo; // ahora es atributo de la clase
    private int saldo; // saldo inicial

    public PanelMercado(int saldoInicial) {
        this.saldo = saldoInicial;
        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 18));

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

        add(barraBusqueda, BorderLayout.NORTH);

        // Panel de saldo
        panelSaldo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSaldo.setBackground(new Color(18, 18, 18));
        labelSaldo = new JLabel();
        labelSaldo.setForeground(Color.WHITE);
        labelSaldo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelSaldo.add(labelSaldo);
        panelSaldo.setVisible(false); // oculto por defecto

        add(panelSaldo, BorderLayout.SOUTH);

        // Mostrar saldo inicial
        actualizarLabelSaldo();
    }

    // Mostrar u ocultar saldo
    public void mostrarSaldo(boolean mostrar) {
        panelSaldo.setVisible(mostrar); // ahora se muestra/oculta el panel completo
        revalidate();
        repaint();
    }

    // Actualizar el saldo mostrado
    public void setSaldo(int nuevoSaldo) {
        this.saldo = nuevoSaldo;
        actualizarLabelSaldo();
    }

    // Formatear el saldo con puntos
    private void actualizarLabelSaldo() {
        DecimalFormat df = new DecimalFormat("#,###");
        labelSaldo.setText("Saldo: " + df.format(saldo) + " €");
    }
}
