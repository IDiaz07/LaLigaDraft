package gui.ventanas;

import javax.swing.*;

import bd.GestorDatos;
import gui.clases.Jugador;
import gui.clases.Usuario;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Ventana informativa que muestra el equipo inicial asignado aleatoriamente al
 * usuario.
 */
public class VentanaEquipoInicial extends JFrame {

    public VentanaEquipoInicial(Usuario usuario) {

        setTitle("Equipo Inicial");
        setSize(600, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(18, 18, 18));

        JLabel titulo = new JLabel("TU EQUIPO INICIAL", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 30));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(40, 10, 40, 10));
        main.add(titulo, BorderLayout.NORTH);

        DefaultListModel<String> modelo = new DefaultListModel<>();
        for (int id : usuario.getJugadores()) {
            Jugador j = GestorDatos.jugadores.get(id);
            modelo.addElement(j.getNombre() + " - " + j.getPosicion() + " - " + j.getEquipo());
        }

        JList<String> lista = new JList<>(modelo);
        lista.setFont(new Font("Arial", Font.PLAIN, 18));
        lista.setBackground(new Color(28, 28, 28));
        lista.setForeground(Color.WHITE);
        lista.setSelectionBackground(new Color(231, 76, 60));

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(null);
        main.add(scroll, BorderLayout.CENTER);

        JButton continuar = new JButton("Continuar");
        continuar.setFont(new Font("Arial", Font.BOLD, 24));
        continuar.setBackground(new Color(231, 76, 60));
        continuar.setForeground(Color.WHITE);
        continuar.setFocusPainted(false);
        continuar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        continuar.addActionListener((ActionEvent e) -> dispose());

        main.add(continuar, BorderLayout.SOUTH);

        add(main);
    }
}
