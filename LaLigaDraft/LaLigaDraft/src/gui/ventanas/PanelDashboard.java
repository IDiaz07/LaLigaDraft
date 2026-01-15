package gui.ventanas;

import javax.swing.*;
import java.awt.*;

import bd.GestorDatos;
import gui.clases.Liga;
import gui.clases.Usuario;
import gui.clases.Jugador;

public class PanelDashboard extends JPanel {

    private final Usuario usuario;
    private final Liga liga;

    public PanelDashboard(Usuario usuario) {
        this.usuario = usuario;
        this.liga = GestorDatos.ligas.get(usuario.getLigaActualId());

        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 18));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(crearTitulo(), BorderLayout.NORTH);
        add(crearResumen(), BorderLayout.CENTER);
    }

    // ---------------- TÍTULO ----------------

    private JPanel crearTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel titulo = new JLabel("Dashboard");
        titulo.setFont(new Font("Arial", Font.BOLD, 26));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel(
                liga != null ? "Liga: " + liga.getNombre() : "Sin liga activa"
        );
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitulo.setForeground(Color.LIGHT_GRAY);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(subtitulo, BorderLayout.SOUTH);

        return panel;
    }

    // ---------------- CONTENIDO ----------------

    private JPanel crearResumen() {
        JPanel grid = new JPanel(new GridLayout(3, 2, 15, 15));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        grid.add(crearCard("👤 Usuario", usuario.getNombre()));
        grid.add(crearCard("💰 Saldo",
                liga != null ? usuario.getSaldo(liga.getId()) + " M€" : "-"));

        grid.add(crearCard("⚽ Jugadores",
                usuario.getJugadoresLigaActual().size() + " / 15"));

        grid.add(crearCard("🏆 Puntos",
                calcularPuntosEquipo() + " pts"));

        grid.add(crearCard("👥 Participantes",
                liga != null ? liga.getUsuariosIds().size() : 0));

        grid.add(crearCard("🛒 Mercado",
                liga != null && liga.mercadoExpirado()
                        ? "Expirado"
                        : "Activo"));

        return grid;
    }

    // ---------------- CARD ----------------

    private JPanel crearCard(String titulo, Object valor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(28, 28, 28));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(Color.LIGHT_GRAY);
        lblTitulo.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lblValor = new JLabel(String.valueOf(valor));
        lblValor.setForeground(Color.WHITE);
        lblValor.setFont(new Font("Arial", Font.BOLD, 22));

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);

        return card;
    }

    // ---------------- LÓGICA ----------------

    private int calcularPuntosEquipo() {
        int total = 0;
        for (int idJ : usuario.getJugadoresLigaActual()) {
            Jugador j = GestorDatos.jugadores.get(idJ);
            if (j != null) {
                total += j.getTotalPuntos();
            }
        }
        return total;
    }
}
