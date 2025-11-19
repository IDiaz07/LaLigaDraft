import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class VentanaSeleccionLiga extends JFrame {
    private final Usuario usuario;

    public VentanaSeleccionLiga(Usuario usuario) {
        this.usuario = usuario;
        setTitle("Elegir liga");
        setSize(420, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        JLabel titulo = new JLabel("¡Bienvenido, " + usuario.getNombre() + "!");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        root.add(titulo, BorderLayout.NORTH);

        // --- Opciones ---
        JPanel opciones = new JPanel(new GridLayout(3, 1, 12, 12));
        JButton btnPublica = new JButton("Unirme a una liga pública");
        JButton btnPrivada = new JButton("Crear liga privada para mis amigos");
        JButton btnUnirseCodigo = new JButton("Unirse a una liga privada de amigos");

        opciones.add(btnPublica);
        opciones.add(btnPrivada);
        opciones.add(btnUnirseCodigo);
        root.add(opciones, BorderLayout.CENTER);

        JButton btnSalir = new JButton("Salir");
        root.add(btnSalir, BorderLayout.SOUTH);

        setContentPane(root);

        // --- Listeners ---
        btnPublica.addActionListener(e -> unirseALigaPublica());
        btnPrivada.addActionListener(e -> crearLigaPrivada());
        btnUnirseCodigo.addActionListener(e -> unirseConCodigo());
        btnSalir.addActionListener(e -> {
            dispose();
            // Asumiendo que VentanaInicio existe
            // SwingUtilities.invokeLater(() -> new VentanaInicio().setVisible(true)); 
        });
    }

    // ------------------- LIGA PÚBLICA -------------------
    private void unirseALigaPublica() {
        Liga liga = GestorDatos.buscarLigaPublicaDisponible();
        if (liga == null) {
            liga = GestorDatos.registrarLiga("Liga Pública", true, null);
        }
        GestorDatos.agregarUsuarioALiga(usuario.getId(), liga.getId());
        JOptionPane.showMessageDialog(this, "Te uniste a: " + liga.getNombre());
        
        // CAMBIO: Establecer liga actual y guardar
        usuario.setLigaActualId(liga.getId()); 
        GestorDatos.guardarUsuarios(); 

        abrirPrincipal();
    }

    // ------------------- CREAR LIGA PRIVADA -------------------
    private void crearLigaPrivada() {
        JTextField nombre = new JTextField();
        JTextField codigo = new JTextField();
        JPanel form = new JPanel(new GridLayout(0,1,8,8));
        form.add(new JLabel("Nombre de la liga:"));
        form.add(nombre);
        form.add(new JLabel("Código de invitación (opcional):"));
        form.add(codigo);

        int ok = JOptionPane.showConfirmDialog(this, form, "Crear liga privada",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok == JOptionPane.OK_OPTION) {
            String nom = nombre.getText().trim();
            String cod = codigo.getText().trim();
            if (nom.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.");
                return;
            }
            Liga liga = GestorDatos.registrarLiga(nom, false, cod.isEmpty() ? null : cod);
            GestorDatos.agregarUsuarioALiga(usuario.getId(), liga.getId());
            JOptionPane.showMessageDialog(this, "Liga creada: " + liga.getNombre());
            
            // CAMBIO: Establecer liga actual y guardar
            usuario.setLigaActualId(liga.getId());
            GestorDatos.guardarUsuarios();

            abrirPrincipal();
        }
    }

    // ------------------- UNIRSE POR CÓDIGO -------------------
    private void unirseConCodigo() {
        String codigoInput = JOptionPane.showInputDialog(this,
                "Introduce el código de invitación de la liga:",
                "Unirse a liga privada", JOptionPane.PLAIN_MESSAGE);

        if (codigoInput == null || codigoInput.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes introducir un código válido.");
            return;
        }

        final String codigo = codigoInput.trim();

        // Buscar liga privada con ese código
        Liga liga = GestorDatos.ligas.values().stream()
                .filter(l -> !l.isPublica() && codigo.equals(l.getCodigoInvitacion()))
                .findFirst()
                .orElse(null);

        if (liga == null) {
            int crear = JOptionPane.showConfirmDialog(this,
                    "No se encontró ninguna liga con ese código.\n¿Deseas crear una nueva con ese código?",
                    "Liga no encontrada", JOptionPane.YES_NO_OPTION);

            if (crear == JOptionPane.YES_OPTION) {
                String nombre = JOptionPane.showInputDialog(this, "Nombre de la nueva liga:");
                if (nombre != null && !nombre.trim().isEmpty()) {
                    Liga nueva = GestorDatos.registrarLiga(nombre.trim(), false, codigo);
                    GestorDatos.agregarUsuarioALiga(usuario.getId(), nueva.getId());
                    
                    // CORRECCIÓN A: Si se crea la liga, usar el nombre de la nueva liga.
                    JOptionPane.showMessageDialog(this, 
                                                  "Se creó y te uniste a: " + nueva.getNombre(),
                                                  "¡Liga privada creada!",
                                                  JOptionPane.INFORMATION_MESSAGE);
                    
                    usuario.setLigaActualId(nueva.getId());
                    GestorDatos.guardarUsuarios();
                    
                    abrirPrincipal();
                }
            }
            return;
        }

        // Ya existe -> unir al usuario
        GestorDatos.agregarUsuarioALiga(usuario.getId(), liga.getId());
        
        // CORRECCIÓN B: Si se une a una liga existente, usar el nombre de la liga encontrada.
        JOptionPane.showMessageDialog(this, 
                                      "Te uniste correctamente a: " + liga.getNombre(),
                                      "¡Unido a liga privada!", 
                                      JOptionPane.INFORMATION_MESSAGE);
        
        usuario.setLigaActualId(liga.getId());
        GestorDatos.guardarUsuarios();

        abrirPrincipal();
    }

    // ------------------- ABRIR PRINCIPAL -------------------
    private void abrirPrincipal() {
        dispose();
        SwingUtilities.invokeLater(() -> new VentanaPrincipal(usuario).setVisible(true));
    }
}