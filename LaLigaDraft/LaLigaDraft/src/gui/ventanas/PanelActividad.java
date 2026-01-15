package gui.ventanas;

import javax.swing.*;
import java.awt.*;
import gui.clases.Usuario;

public class PanelActividad extends JPanel {

    public PanelActividad(Usuario usuario) {
        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 18)); // Fondo oscuro como el resto

        // Título del panel
        JLabel titulo = new JLabel("Última Actividad", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titulo, BorderLayout.NORTH);

        // Contenedor de las noticias
        JPanel listaActividad = new JPanel();
        listaActividad.setLayout(new BoxLayout(listaActividad, BoxLayout.Y_AXIS));
        listaActividad.setBackground(new Color(18, 18, 18));

        // Ejemplo de entradas de actividad (esto podrías cargarlo de la BD más adelante)
        agregarNoticia(listaActividad, "Mercado", "El mercado de fichajes se ha actualizado.");
        agregarNoticia(listaActividad, "Sistema", "Bienvenido a la liga, " + usuario.getNombre() + ".");
        agregarNoticia(listaActividad, "Competición", "La nueva jornada comenzará pronto.");

        JScrollPane scroll = new JScrollPane(listaActividad);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private void agregarNoticia(JPanel panel, String categoria, String mensaje) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(new Color(30, 30, 30));
        item.setMaximumSize(new Dimension(550, 80));
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 10, 5, 10),
            BorderFactory.createLineBorder(new Color(50, 50, 50), 1)
        ));

        JLabel lblCat = new JLabel(categoria.toUpperCase());
        lblCat.setFont(new Font("Arial", Font.BOLD, 12));
        lblCat.setForeground(new Color(52, 152, 219));
        
        JLabel lblMsg = new JLabel(mensaje);
        lblMsg.setFont(new Font("Arial", Font.PLAIN, 14));
        lblMsg.setForeground(Color.WHITE);

        item.add(lblCat, BorderLayout.NORTH);
        item.add(lblMsg, BorderLayout.CENTER);
        
        panel.add(item);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }
}