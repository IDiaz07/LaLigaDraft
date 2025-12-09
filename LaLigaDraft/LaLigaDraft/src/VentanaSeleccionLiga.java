import java.awt.*;
import javax.swing.*;
import java.util.*;

public class VentanaSeleccionLiga extends JFrame {

    private final Usuario usuario;

    public VentanaSeleccionLiga(Usuario usuario) {
        this.usuario = usuario;

        // -----------------------------------
        // CONFIGURACIÓN DE LA VENTANA
        // -----------------------------------
        setTitle("Seleccionar Liga");
        setSize(600, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // -----------------------------------
        // PANEL PRINCIPAL
        // -----------------------------------
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(18, 18, 18));
        main.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // -----------------------------------
        // TÍTULO
        // -----------------------------------
        JLabel titulo = new JLabel("¡Bienvenido, " + usuario.getNombre() + "!");
        titulo.setFont(new Font("Arial", Font.BOLD, 34));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(30, 10, 50, 10));
        main.add(titulo, BorderLayout.NORTH);

        // -----------------------------------
        // BOTONES DE OPCIONES
        // -----------------------------------
        JPanel opciones = new JPanel(new GridLayout(3, 1, 30, 30));
        opciones.setBackground(new Color(18, 18, 18));

        JButton btnPublica = crearBotonGrande("Unirme a una Liga Pública");
        JButton btnPrivada = crearBotonGrande("Crear Liga Privada");
        JButton btnUnirseCodigo = crearBotonGrande("Unirme con Código");

        opciones.add(btnPublica);
        opciones.add(btnPrivada);
        opciones.add(btnUnirseCodigo);

        main.add(opciones, BorderLayout.CENTER);

        // -----------------------------------
        // BOTÓN SALIR
        // -----------------------------------
        JButton btnSalir = new JButton("Salir");
        btnSalir.setFont(new Font("Arial", Font.BOLD, 22));
        btnSalir.setBackground(new Color(40, 40, 40));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFocusPainted(false);
        btnSalir.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JPanel abajo = new JPanel(new FlowLayout());
        abajo.setBackground(new Color(18, 18, 18));
        abajo.add(btnSalir);

        main.add(abajo, BorderLayout.SOUTH);

        setContentPane(main);

        // -----------------------------------
        // LISTENERS (FUNCIONAMIENTO ORIGINAL)
        // -----------------------------------
        btnPublica.addActionListener(e -> unirseALigaPublica());
        btnPrivada.addActionListener(e -> crearLigaPrivada());
        btnUnirseCodigo.addActionListener(e -> unirseConCodigo());
        btnSalir.addActionListener(e -> dispose());
    }


    // -------------------------------------------------------------
    // BOTÓN GRANDE (ESTILO UNIFICADO LALIGADRAFT)
    // -------------------------------------------------------------
    private JButton crearBotonGrande(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Arial", Font.BOLD, 24));
        b.setBackground(new Color(231, 76, 60));
        b.setForeground(Color.WHITE);
        b.setFocusable(false);
        b.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }


    // -------------------------------------------------------------
    // LÓGICA ORIGINAL (NO MODIFICADA)
    // -------------------------------------------------------------
    private void unirseALigaPublica() {
        Liga liga = GestorDatos.buscarLigaPublicaDisponible();
        if (liga == null) {
            liga = GestorDatos.registrarLiga("Liga Pública", true, null);
        }

        GestorDatos.agregarUsuarioALiga(usuario.getId(), liga.getId());
        usuario.setLigaActualId(liga.getId());

        if (usuario.getJugadores() == null || usuario.getJugadores().isEmpty()) {
            GestorDatos.asignarEquipoInicial(usuario);
            new VentanaEquipoInicial(usuario).setVisible(true);
        }

        GestorDatos.guardarUsuarios();
        abrirPrincipal();
    }

    private void crearLigaPrivada() {

        JTextField nombre = new JTextField();
        JTextField codigo = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.add(new JLabel("Nombre de la liga:"));
        form.add(nombre);
        form.add(new JLabel("Código de invitación (opcional):"));
        form.add(codigo);

        int ok = JOptionPane.showConfirmDialog(this, form, "Crear liga privada",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (ok != JOptionPane.OK_OPTION) return;

        String nom = nombre.getText().trim();
        String cod = codigo.getText().trim();

        if (nom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.");
            return;
        }

        boolean existe = GestorDatos.ligas.values().stream()
                .anyMatch(l -> !l.isPublica() &&
                        l.getNombre().equalsIgnoreCase(nom) &&
                        Objects.equals(l.getCodigoInvitacion(), cod.isEmpty() ? null : cod));

        if (existe) {
            JOptionPane.showMessageDialog(this,
                    "Ya existe una liga privada con ese nombre y código.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Liga liga = GestorDatos.registrarLiga(nom, false, cod.isEmpty() ? null : cod);

        GestorDatos.agregarUsuarioALiga(usuario.getId(), liga.getId());
        usuario.setLigaActualId(liga.getId());

        if (usuario.getJugadores() == null || usuario.getJugadores().isEmpty()) {
            GestorDatos.asignarEquipoInicial(usuario);
            new VentanaEquipoInicial(usuario).setVisible(true);
        }

        GestorDatos.guardarUsuarios();
        abrirPrincipal();
    }

    private void unirseConCodigo() {
        String codigoInput = JOptionPane.showInputDialog(this,
                "Introduce el código de invitación:");

        if (codigoInput == null || codigoInput.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Código inválido.");
            return;
        }

        final String codigo = codigoInput.trim();

        Liga liga = GestorDatos.ligas.values().stream()
                .filter(l -> !l.isPublica() && codigo.equals(l.getCodigoInvitacion()))
                .findFirst().orElse(null);

        if (liga == null) {
            int crear = JOptionPane.showConfirmDialog(this,
                    "No existe ninguna liga con ese código.\n¿Crear una nueva?",
                    "Liga no encontrada",
                    JOptionPane.YES_NO_OPTION);

            if (crear != JOptionPane.YES_OPTION) return;

            String nombre = JOptionPane.showInputDialog(this, "Nombre de la nueva liga:");
            if (nombre == null || nombre.trim().isEmpty()) return;

            Liga nueva = GestorDatos.registrarLiga(nombre.trim(), false, codigo);
            GestorDatos.agregarUsuarioALiga(usuario.getId(), nueva.getId());
            usuario.setLigaActualId(nueva.getId());

            if (usuario.getJugadores() == null || usuario.getJugadores().isEmpty()) {
                GestorDatos.asignarEquipoInicial(usuario);
                new VentanaEquipoInicial(usuario).setVisible(true);
            }

            GestorDatos.guardarUsuarios();
            abrirPrincipal();
            return;
        }

        GestorDatos.agregarUsuarioALiga(usuario.getId(), liga.getId());
        usuario.setLigaActualId(liga.getId());

        if (usuario.getJugadores() == null || usuario.getJugadores().isEmpty()) {
            GestorDatos.asignarEquipoInicial(usuario);
            new VentanaEquipoInicial(usuario).setVisible(true);
        }

        GestorDatos.guardarUsuarios();
        abrirPrincipal();
    }

    private void abrirPrincipal() {
        dispose();
        SwingUtilities.invokeLater(() -> new VentanaPrincipal(usuario).setVisible(true));
    }
}
