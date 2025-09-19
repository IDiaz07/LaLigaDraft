import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

public class PanelMercado extends JPanel {

    public PanelMercado() {
        // Usar BorderLayout en este panel
        setLayout(new BorderLayout());

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
            revalidate();
            repaint();
        });

        // Añadir los tres botones principales
        panelBotones.add(btnMercado);
        panelBotones.add(btnOperaciones);
        panelBotones.add(btnHistorico);

        // Añadir paneles al JPanel principal
        add(panelBotones, BorderLayout.NORTH);
        add(panelSubOperaciones, BorderLayout.CENTER);
    }
}