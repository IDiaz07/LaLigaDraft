package gui.ventanas;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import bd.GestorDatos;
import gui.clases.Jugador;
import gui.clases.Usuario;

import gui.enums.Estado;
import gui.enums.Posicion;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel que gestiona la plantilla del usuario.
 * Permite ver la alineación en un campo gráfico y la lista completa de
 * jugadores.
 */
public class PanelEquipo extends JPanel {

    private final Usuario usuario;
    private final JPanel cards;
    private final CardLayout cardLayout;

    private JPanel campo;
    private List<JLabel> labelsTitulares = new ArrayList<>();
    private JComboBox<String> comboFormacion;

    private List<Jugador> titularesActuales = new ArrayList<>();
    private List<Jugador> suplentesActuales = new ArrayList<>();

    
    public PanelEquipo(Usuario usuario) {
        this.usuario = usuario;

        setLayout(new BorderLayout());
        setBackground(Color.DARK_GRAY);

        // 1. Configurar CardLayout
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        add(cards, BorderLayout.CENTER);

        // 2. Crear las dos secciones (Paneles)
        JPanel panelAlineacion = crearPanelAlineacion();
        JPanel panelPlantilla = crearPanelPlantilla();

        cards.add(panelAlineacion, "Alineacion");
        cards.add(panelPlantilla, "Plantilla");

        // 3. Panel de Navegación (botones "Alineación" y "Plantilla")
        JPanel panelNavegacion = crearPanelNavegacion();
        add(panelNavegacion, BorderLayout.NORTH);

        // ======================= INICIALIZACIÓN =======================
        seleccionarJugadoresIniciales();
        actualizarFormacion();
        cardLayout.show(cards, "Alineacion"); // Mostrar la alineación por defecto
    }

    // panel de navegacion con botones
    private JPanel crearPanelNavegacion() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(18, 18, 18));

        JButton btnAlineacion = new JButton("Alineación");
        btnAlineacion.addActionListener(e -> {
            cardLayout.show(cards, "Alineacion");
            refrescarCampo();
        });
        panel.add(btnAlineacion);

        JButton btnPlantilla = new JButton("Plantilla");
        btnPlantilla.addActionListener(e -> cardLayout.show(cards, "Plantilla"));
        panel.add(btnPlantilla);

        return panel;
    }

    // el campo
    private JPanel crearPanelAlineacion() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.DARK_GRAY);

        campo = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 120, 0));
                g.fillRect(0, 0, getWidth(), getHeight());

                g.setColor(Color.white);
                int w = getWidth(), h = getHeight();
                if (w > 0 && h > 0) {
                    g.drawOval(w / 2 - 50, h / 2 - 50, 100, 100);
                    g.drawLine(0, h / 2, w, h / 2);
                    g.drawRect(w / 2 - 60, 0, 120, 60);
                    g.drawRect(w / 2 - 60, h - 60, 120, 60);
                }
            }
        };
        campo.setPreferredSize(new Dimension(600, 700));
        campo.setBorder(BorderFactory.createLineBorder(Color.white, 2));
        panel.add(campo, BorderLayout.CENTER);

        // jugadores titulares
        for (int i = 0; i < 11; i++) {
            JLabel lbl = crearLabelJugador(i);
            labelsTitulares.add(lbl);
            campo.add(lbl);
        }

        // panel inferior
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInferior.setBackground(new Color(18, 18, 18));
        panelInferior.setPreferredSize(new Dimension(600, 50));

        comboFormacion = new JComboBox<>(new String[] { "4-4-2", "4-3-3", "3-5-2" });
        comboFormacion.addActionListener(e -> actualizarFormacion());
        panelInferior.add(comboFormacion);
        panel.add(panelInferior, BorderLayout.SOUTH);

        return panel;
    }

    // muestra lo mismo pero de forma mas visual, en modo tabla/plantilla
    private JPanel crearPanelPlantilla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(18, 18, 18));

        // 1. Obtener la plantilla completa y calcular el valor total
        List<Jugador> todaLaPlantilla = usuario.getJugadores().stream()
                .map(id -> GestorDatos.jugadores.get(id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        long valorTotal = todaLaPlantilla.stream()
                .mapToLong(Jugador::getValorMercado)
                .sum();

        todaLaPlantilla.sort(Comparator.comparing(Jugador::getPosicion)
                .thenComparing(Jugador::getValorMercado, Comparator.reverseOrder()));

        // 2. Preparar la tabla de jugadores
        String[] nombresColumnas = { "Posición", "Nombre", "Valor (€)", "Puntos Totales" };
        Object[][] datos = todaLaPlantilla.stream()
                .map(j -> new Object[] {
                        j.getPosicion().toString(),
                        j.getNombre() + " (" + j.getEquipo() + ")",
                        String.format("%,d", j.getValorMercado()),
                        j.getTotalPuntos()
                })
                .toArray(Object[][]::new);
        DefaultTableModel modelo = new DefaultTableModel(datos, nombresColumnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tablaPlantilla = new JTable(modelo);

        // Estilos
        tablaPlantilla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaPlantilla.setRowHeight(30);
        tablaPlantilla.setBackground(new Color(40, 40, 40));
        tablaPlantilla.setForeground(Color.WHITE);
        tablaPlantilla.setGridColor(new Color(60, 60, 60));
        tablaPlantilla.getTableHeader().setBackground(new Color(60, 60, 60));
        tablaPlantilla.getTableHeader().setForeground(Color.WHITE);
        tablaPlantilla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaPlantilla.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tablaPlantilla.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaPlantilla.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tablaPlantilla.getColumnModel().getColumn(2).setPreferredWidth(120);
        tablaPlantilla.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tablaPlantilla.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaPlantilla.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(tablaPlantilla);
        scrollPane.getViewport().setBackground(new Color(40, 40, 40));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scrollPane, BorderLayout.CENTER);

        // 3. Panel para mostrar el Valor Total
        JPanel panelValorTotal = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelValorTotal.setBackground(new Color(18, 18, 18));
        panelValorTotal.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel lblValorTotal = new JLabel(
                "Valor del Equipo: " + String.format("%,d", valorTotal) + " €");
        lblValorTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblValorTotal.setForeground(Color.WHITE);
        panelValorTotal.add(lblValorTotal);

        panel.add(panelValorTotal, BorderLayout.SOUTH);

        return panel;
    }

    /** Crear label de jugador con listener */
    private JLabel crearLabelJugador(int index) {
        JLabel lbl = new JLabel("◻", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setSize(80, 25);
        lbl.setForeground(Color.WHITE);
        lbl.setOpaque(true);
        lbl.setBackground(new Color(0, 0, 0, 90));
        lbl.setBorder(BorderFactory.createLineBorder(Color.white, 1));

        lbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (index < titularesActuales.size()) {
                    Jugador seleccionado = titularesActuales.get(index);
                    mostrarVentanaCambio(seleccionado, index);
                }
            }
        });

        return lbl;
    }

    /** Selecciona los 15 jugadores del usuario (11 titulares + 4 suplentes) */
    private void seleccionarJugadoresIniciales() {
        List<Jugador> todos = usuario.getJugadores().stream()
                .map(id -> GestorDatos.jugadores.get(id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        titularesActuales.clear();
        suplentesActuales.clear();

        // Clasificar por posición
        List<Jugador> porteros = filtrarPorPosicion(todos, Posicion.POR);
        List<Jugador> defensas = filtrarPorPosicion(todos, Posicion.DEF);
        List<Jugador> medios = filtrarPorPosicion(todos, Posicion.MED);
        List<Jugador> delanteros = filtrarPorPosicion(todos, Posicion.DEL);

        // Titulares (4-4-2 por defecto)
        if (!porteros.isEmpty())
            titularesActuales.add(porteros.get(0));
        titularesActuales.addAll(defensas.stream().limit(4).collect(Collectors.toList()));
        titularesActuales.addAll(medios.stream().limit(4).collect(Collectors.toList()));
        titularesActuales.addAll(delanteros.stream().limit(2).collect(Collectors.toList()));

        // Suplentes (resto hasta 15)
        List<Jugador> resto = new ArrayList<>(todos);
        resto.removeAll(titularesActuales);
        suplentesActuales.addAll(resto.stream().limit(4).collect(Collectors.toList()));

        while (titularesActuales.size() < 11)
            titularesActuales.add(jugadorPlaceholder(Posicion.MED));
        while (suplentesActuales.size() < 4)
            suplentesActuales.add(jugadorPlaceholder(Posicion.MED));
    }

    private void actualizarFormacion() {
        System.out.println("🔄 Actualizando formación...");

        String formacion = (String) comboFormacion.getSelectedItem();
        if (formacion == null)
            formacion = "4-4-2";

        
        List<Jugador> todos = usuario.getJugadores().stream()
                .map(id -> GestorDatos.jugadores.get(id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        System.out.println("Jugadores usuario: " + todos.size());

        String[] partes = formacion.split("-");
        int def = Integer.parseInt(partes[0]);
        int med = Integer.parseInt(partes[1]);
        int del = Integer.parseInt(partes[2]);

        System.out.println("Formación: " + def + "-" + med + "-" + del);

        // CLASIFICAR JUGADORES
        List<Jugador> por = filtrarPorPosicion(todos, Posicion.POR);
        List<Jugador> defen = filtrarPorPosicion(todos, Posicion.DEF);
        List<Jugador> medi = filtrarPorPosicion(todos, Posicion.MED);
        List<Jugador> delan = filtrarPorPosicion(todos, Posicion.DEL);

        System.out
                .println("POR:" + por.size() + " DEF:" + defen.size() + " MED:" + medi.size() + " DEL:" + delan.size());

        // LIMPIAR
        titularesActuales.clear();

        // RELLENAR EXACTAMENTE 11 POSICIONES
        // Posición 0: PORTERO
        titularesActuales.add(por.isEmpty() ? jugadorPlaceholder(Posicion.POR) : por.get(0));

        // Posiciones 1-def: DEFENSAS
        for (int i = 0; i < def; i++) {
            titularesActuales.add(i < defen.size() ? defen.get(i) : jugadorPlaceholder(Posicion.DEF));
        }

        // Posiciones def+1 a def+med: MEDIOS
        for (int i = 0; i < med; i++) {
            titularesActuales.add(i < medi.size() ? medi.get(i) : jugadorPlaceholder(Posicion.MED));
        }

        // Resto hasta 11: DELANTEROS
        for (int i = titularesActuales.size(); i < 11; i++) {
            titularesActuales.add(i - titularesActuales.size() < delan.size() ? delan.get(i - titularesActuales.size())
                    : jugadorPlaceholder(Posicion.DEL));
        }

        System.out.println("Titulares creados: " + titularesActuales.size());
        for (int i = 0; i < titularesActuales.size(); i++) {
            System.out.println("Pos " + i + ": " + titularesActuales.get(i).getNombre());
        }

        suplentesActuales.clear();
        suplentesActuales.addAll(todos);
        suplentesActuales.removeAll(titularesActuales);

        mostrarJugadoresEnCampo(formacion);
    }

    private void mostrarJugadoresEnCampo(String formacion) {
        System.out.println("📍 Mostrando en campo...");

        // OCULTAR TODOS LOS LABELS
        for (JLabel lbl : labelsTitulares) {
            lbl.setVisible(false);
            lbl.setText(" ");
        }

        int w = Math.max(campo.getWidth(), 600);
        int h = Math.max(campo.getHeight(), 700);

        String[] partes = formacion.split("-");
        int def = Integer.parseInt(partes[0]);
        int med = Integer.parseInt(partes[1]);
        int del = Integer.parseInt(partes[2]);

        int index = 0;

        // PORTERO (index 0)
        if (index < titularesActuales.size() && index < labelsTitulares.size()) {
            colocarJugador(titularesActuales.get(index), labelsTitulares.get(index), w / 2 - 40, h - 50);
            index++;
        }

        // DEFENSAS (usar titularesActuales)
        colocarLinea(def, h * 3 / 4, index);
        index += def;

        // MEDIOS
        colocarLinea(med, h / 2, index);
        index += med;

        // DELANTEROS
        colocarLinea(del, h / 5, index);

        campo.repaint();
        System.out.println("✅ Campo repintado");
    }

    private void colocarLinea(int cantidad, int y, int startIndex) {
        int w = Math.max(campo.getWidth(), 600);
        int espacio = w / (cantidad + 1);

        for (int i = 0; i < cantidad; i++) {
            int idx = startIndex + i;
            if (idx >= labelsTitulares.size() || idx >= titularesActuales.size()) {
                break;
            }

            Jugador j = titularesActuales.get(idx);
            JLabel lbl = labelsTitulares.get(idx);
            int x = espacio * (i + 1) - 40;

            lbl.setBounds(x, y, 80, 25);
            lbl.setText(j.getNombre());
            lbl.setToolTipText(j.getPosicion() + " | " + j.getEquipo());
            lbl.setVisible(true);
        }
    }

    private List<Jugador> filtrarPorPosicion(List<Jugador> lista, Posicion pos) {
        return lista.stream().filter(j -> j.getPosicion() == pos).collect(Collectors.toList());
    }

    private Jugador jugadorPlaceholder(Posicion pos) {
        return new Jugador(-1, "Vacante " + pos.name(), "SIN", 25, "España", 0, 0, Estado.SANO, pos);
    }

    private void colocarJugador(Jugador j, JLabel lbl, int x, int y) {
        lbl.setLocation(x, y);
        lbl.setText(j.getNombre());
        lbl.setToolTipText(j.getPosicion() + " | " + j.getEquipo() +
                " | Valor: " + String.format("%,d", j.getValorMercado()) + "€ | Pts: " + j.getTotalPuntos());
        lbl.setVisible(true);
    }

    // Refresca el campo con los jugadores actuales (sin recalcular) 
    private void refrescarCampo() {
        String formacion = (String) comboFormacion.getSelectedItem();
        if (formacion == null)
            formacion = "4-4-2";
        mostrarJugadoresEnCampo(formacion);
        campo.repaint();
    }

    // Ventana para intercambiar con suplentes 
    private void mostrarVentanaCambio(Jugador titular, int indexTitular) {
        List<Jugador> suplentesMismaPos = suplentesActuales.stream()
                .filter(j -> j.getPosicion() == titular.getPosicion())
                .collect(Collectors.toList());

        if (titular.getId() == -1 || suplentesMismaPos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay suplentes disponibles para esa posición o la casilla está vacía.");
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Cambiar Jugador", true);
        dialog.setSize(350, 300);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JLabel lblTitular = new JLabel("Titular actual: " + titular.getNombre(), SwingConstants.CENTER);
        lblTitular.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitular.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        dialog.add(lblTitular, BorderLayout.NORTH);

        JPanel panelSuplentes = new JPanel(new GridLayout(0, 1, 8, 8));
        panelSuplentes.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        for (Jugador s : suplentesMismaPos) {
            JButton btn = new JButton(s.getNombre() + " | " + s.getEquipo() + " | Pts: " + s.getTotalPuntos());
            btn.addActionListener(e -> {
                // Intercambiar titular ↔ suplente
                Jugador titularAntiguo = titularesActuales.get(indexTitular);
                titularesActuales.set(indexTitular, s);
                suplentesActuales.remove(s);
                suplentesActuales.add(titularAntiguo);

                // Refrescar visualmente
                refrescarCampo();
                dialog.dispose();
            });
            panelSuplentes.add(btn);
        }

        dialog.add(panelSuplentes, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
}
