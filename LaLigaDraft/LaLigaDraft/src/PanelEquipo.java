import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class PanelEquipo extends JPanel {

    private final Usuario usuario;
    // Contenedor principal con CardLayout
    private final JPanel cards; 
    private final CardLayout cardLayout;
    
    // Antiguo campo (ahora será la vista "Alineación")
    private JPanel campo;
    private List<JLabel> labelsTitulares = new ArrayList<>();
    private JComboBox<String> comboFormacion;

    private List<Jugador> titularesActuales = new ArrayList<>();
    private List<Jugador> suplentesActuales = new ArrayList<>();

    // -------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------
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

    // -------------------------------------------------------------
    // Panel de Navegación con botones
    // -------------------------------------------------------------
    private JPanel crearPanelNavegacion() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(18, 18, 18));
        
        JButton btnAlineacion = new JButton("Alineación");
        btnAlineacion.addActionListener(e -> cardLayout.show(cards, "Alineacion"));
        panel.add(btnAlineacion);

        JButton btnPlantilla = new JButton("Plantilla");
        btnPlantilla.addActionListener(e -> cardLayout.show(cards, "Plantilla"));
        panel.add(btnPlantilla);
        
        return panel;
    }

    // -------------------------------------------------------------
    // Método que contiene la vista de la Alineación (el campo)
    // -------------------------------------------------------------
    private JPanel crearPanelAlineacion() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.DARK_GRAY);
        
        // ======================= CAMPO (Contenido existente) =======================
        campo = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 120, 0));
                g.fillRect(0, 0, getWidth(), getHeight());

                g.setColor(Color.white);
                int w = getWidth(), h = getHeight();
                g.drawOval(w / 2 - 50, h / 2 - 50, 100, 100);
                g.drawLine(0, h / 2, w, h / 2);
                g.drawRect(w / 2 - 60, 0, 120, 60);
                g.drawRect(w / 2 - 60, h - 60, 120, 60);
            }
        };
        campo.setPreferredSize(new Dimension(400, 400));
        campo.setBorder(BorderFactory.createLineBorder(Color.white, 2));
        panel.add(campo, BorderLayout.CENTER);

        // ======================= TITULARES (Contenido existente) =======================
        for (int i = 0; i < 11; i++) {
            JLabel lbl = crearLabelJugador(i);
            labelsTitulares.add(lbl);
            campo.add(lbl);
        }

        // ======================= PANEL INFERIOR (Contenido existente) =======================
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInferior.setBackground(new Color(18, 18, 18));
        panelInferior.setPreferredSize(new Dimension(400, 50));

        comboFormacion = new JComboBox<>(new String[]{"4-4-2", "4-3-3", "3-5-2"});
        comboFormacion.addActionListener(e -> actualizarFormacion());
        panelInferior.add(comboFormacion);
        panel.add(panelInferior, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // -------------------------------------------------------------
    // Panel que muestra la Plantilla en formato visual de tabla/lista
    // -------------------------------------------------------------
    private JPanel crearPanelPlantilla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.DARK_GRAY);

        // Obtener la plantilla completa
        List<Jugador> todaLaPlantilla = usuario.getJugadores().stream()
                .map(id -> GestorDatos.jugadores.get(id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Ordenar la plantilla por posición (POR, DEF, MED, DEL)
        todaLaPlantilla.sort(Comparator.comparing(Jugador::getPosicion)
                                        .thenComparing(Jugador::getValorMercado, Comparator.reverseOrder()));

        // Nombres de las columnas
        String[] nombresColumnas = {"Posición", "Nombre", "Valor (€)", "Puntos Totales"};
        Object[][] datos = todaLaPlantilla.stream()
            .map(j -> new Object[]{
                j.getPosicion().toString(),
                j.getNombre() + " (" + j.getEquipo() + ")",
                String.format("%,d", j.getValorMercado()), // Formato de moneda para visualización
                j.getTotalPuntos()
            })
            .toArray(Object[][]::new);

        JTable tablaPlantilla = new JTable(datos, nombresColumnas);
        
        // Estilización de la tabla (opcional)
        tablaPlantilla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaPlantilla.setRowHeight(25);
        tablaPlantilla.setBackground(new Color(40, 40, 40));
        tablaPlantilla.setForeground(Color.WHITE);
        tablaPlantilla.getTableHeader().setBackground(new Color(60, 60, 60));
        tablaPlantilla.getTableHeader().setForeground(Color.WHITE);
        tablaPlantilla.setEnabled(false); // La tabla es solo para visualización

        JScrollPane scrollPane = new JScrollPane(tablaPlantilla);
        scrollPane.getViewport().setBackground(new Color(40, 40, 40));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Etiqueta de título
        JLabel titulo = new JLabel("Plantilla Completa", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(titulo, BorderLayout.NORTH);

        return panel;
    }

    // -------------------------------------------------------------
    // MÉTODOS EXISTENTES (Pequeños ajustes para coherencia y Tooltip)
    // -------------------------------------------------------------

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
        if (!porteros.isEmpty()) titularesActuales.add(porteros.get(0));
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

    private List<Jugador> filtrarPorPosicion(List<Jugador> lista, Posicion pos) {
        return lista.stream()
                .filter(j -> j.getPosicion() == pos)
                .collect(Collectors.toList());
    }

    /** Cambia la alineación según formación */
    private void actualizarFormacion() {
        String formacion = (String) comboFormacion.getSelectedItem();
        if (formacion == null) return;

        int def, med, del;
        switch (formacion) {
            case "4-3-3": def = 4; med = 3; del = 3; break;
            case "3-5-2": def = 3; med = 5; del = 2; break;
            default: def = 4; med = 4; del = 2; break;
        }

        List<Jugador> todos = usuario.getJugadores().stream()
                .map(id -> GestorDatos.jugadores.get(id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<Jugador> porteros = filtrarPorPosicion(todos, Posicion.POR);
        List<Jugador> defensas = filtrarPorPosicion(todos, Posicion.DEF);
        List<Jugador> medios = filtrarPorPosicion(todos, Posicion.MED);
        List<Jugador> delanteros = filtrarPorPosicion(todos, Posicion.DEL);

        titularesActuales.clear();
        titularesActuales.add(porteros.isEmpty() ? jugadorPlaceholder(Posicion.POR) : porteros.get(0));
        titularesActuales.addAll(defensas.stream().limit(def).collect(Collectors.toList()));
        titularesActuales.addAll(medios.stream().limit(med).collect(Collectors.toList()));
        titularesActuales.addAll(delanteros.stream().limit(del).collect(Collectors.toList()));

        suplentesActuales = new ArrayList<>(todos);
        suplentesActuales.removeAll(titularesActuales);

        while (titularesActuales.size() < 11)
            titularesActuales.add(jugadorPlaceholder(Posicion.MED));

        mostrarJugadoresEnCampo(formacion);
    }

    private Jugador jugadorPlaceholder(Posicion pos) {
        return new Jugador(-1, "Vacante " + pos, "", 0, "", 0, 0, Estado.SANO, pos, usuario.getId());
    }

    private void mostrarJugadoresEnCampo(String formacion) {
        int w = campo.getWidth() > 0 ? campo.getWidth() : 400;
        int h = campo.getHeight() > 0 ? campo.getHeight() : 400;

        // Portero
        colocarJugador(titularesActuales.get(0), labelsTitulares.get(0), w / 2 - 40, h - 50);

        int def, med, del;
        switch (formacion) {
            case "4-3-3": def = 4; med = 3; del = 3; break;
            case "3-5-2": def = 3; med = 5; del = 2; break;
            default: def = 4; med = 4; del = 2; break;
        }

        colocarLinea(1, def, h * 3 / 4);
        colocarLinea(1 + def, med, h / 2);
        colocarLinea(1 + def + med, del, h / 5);
    }

    private void colocarLinea(int start, int cantidad, int y) {
        int w = campo.getWidth() > 0 ? campo.getWidth() : 400;
        int espacio = w / (cantidad + 1);
        for (int i = 0; i < cantidad; i++) {
            int idx = start + i;
            if (idx >= titularesActuales.size()) return;
            Jugador j = titularesActuales.get(idx);
            JLabel lbl = labelsTitulares.get(idx);
            int x = espacio * (i + 1) - 40;
            colocarJugador(j, lbl, x, y);
        }
    }

    private void colocarJugador(Jugador j, JLabel lbl, int x, int y) {
        lbl.setLocation(x, y);
        lbl.setText(j.getNombre());
        // Ajuste en el formato del valor para el Tooltip
        lbl.setToolTipText(j.getPosicion() + " | " + j.getEquipo() +
                " | Valor: " + String.format("%,d", j.getValorMercado()) + "€ | Pts: " + j.getTotalPuntos());
    }

    /** Refresca el campo con los jugadores actuales (sin recalcular) */
    private void refrescarCampo() {
        String formacion = (String) comboFormacion.getSelectedItem();
        if (formacion == null) formacion = "4-4-2";
        mostrarJugadoresEnCampo(formacion);
        campo.repaint();
    }

    /** Ventana para intercambiar con suplentes */
    private void mostrarVentanaCambio(Jugador titular, int indexTitular) {
        List<Jugador> suplentesMismaPos = suplentesActuales.stream()
                .filter(j -> j.getPosicion() == titular.getPosicion())
                .collect(Collectors.toList());

        if (suplentesMismaPos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay suplentes disponibles para " + titular.getPosicion());
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