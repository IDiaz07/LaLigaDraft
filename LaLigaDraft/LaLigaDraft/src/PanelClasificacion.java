import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PanelClasificacion extends JPanel {
	
	private JTable clasificacion;
	private DefaultTableModel tableModel;
    private final Liga ligaActual; // Objeto Liga que vamos a mostrar

    // CONSTRUCTOR MODIFICADO: Ahora necesita la Liga
	public PanelClasificacion(Liga liga) {
        this.ligaActual = liga;
		setLayout(new BorderLayout());
		setBackground(new Color(18, 18, 18));
        
        if (ligaActual == null) {
            JLabel error = new JLabel("<html><center>⚠️ No estás unido a ninguna liga. <br>Por favor, únete a una liga desde la pantalla anterior para ver la clasificación.</center></html>", SwingConstants.CENTER);
            error.setForeground(new Color(231, 76, 60)); // Rojo oscuro
            error.setFont(new Font("Segoe UI", Font.BOLD, 14));
            add(error, BorderLayout.CENTER);
            return;
        }
        
        // Título de la Liga
        JLabel titulo = new JLabel("Clasificación - " + ligaActual.getNombre(), SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);
        
        // Inicializar tabla de clasificación
        inicializarTabla();
        cargarDatosClasificacion();

        JScrollPane scrollPane = new JScrollPane(clasificacion);
        scrollPane.setBackground(new Color(18, 18, 18));
        scrollPane.getViewport().setBackground(new Color(18, 18, 18));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.CENTER);
	}
    
    private void inicializarTabla() {
        String[] columnas = {"Pos", "Usuario", "Puntos"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        clasificacion = new JTable(tableModel);
        clasificacion.setBackground(new Color(28, 28, 28));
        clasificacion.setForeground(Color.WHITE);
        clasificacion.setSelectionBackground(new Color(40, 40, 40));
        clasificacion.setGridColor(new Color(40, 40, 40));
        clasificacion.setRowHeight(30);
        
        // Estilo del encabezado
        clasificacion.getTableHeader().setBackground(new Color(40, 40, 40));
        clasificacion.getTableHeader().setForeground(Color.WHITE);
        clasificacion.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        clasificacion.getTableHeader().setReorderingAllowed(false);

        // Renderer para centrar contenido
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Aplicar renderer y ancho
        clasificacion.getColumnModel().getColumn(0).setPreferredWidth(50);
        clasificacion.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        clasificacion.getColumnModel().getColumn(1).setPreferredWidth(200);
        
        clasificacion.getColumnModel().getColumn(2).setPreferredWidth(100);
        clasificacion.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
    }
    
    // Método que carga los datos de la liga actual
    private void cargarDatosClasificacion() {
        if (ligaActual == null) return;
        
        tableModel.setRowCount(0);
        
        // Obtenemos la lista de usuarios ya ordenada por puntos (gracias al método en Liga)
        List<Usuario> listaClasificacion = ligaActual.getClasificacion(); 
        
        int posicion = 1;
        for (Usuario u : listaClasificacion) {
            // Si no puedes acceder a puntosUsuario directamente (es privado en Liga),
            // quizás debas moverlo a GestorDatos o hacerlo público.
            // Asumo que tienes acceso o que tienes una versión pública/estática
            // Para este ejemplo, asumimos que Liga.puntosUsuario(Usuario) es accesible o existe un equivalente.
            
            // Si da error, puedes crear una clase auxiliar para calcular los puntos
            int puntos = 0; // **Reemplazar con la llamada correcta a la lógica de puntos**
            try {
                // Hacemos una llamada simple, pero esto depende de tu estructura exacta
                // Por simplicidad, asumimos que tienes un método accesible.
                puntos = calcularPuntos(u); 
            } catch (Exception e) {
                puntos = 0; // En caso de que falle la lógica
            }

            tableModel.addRow(new Object[]{
                posicion++, 
                u.getNombre(), 
                puntos
            });
        }
    }
    
    private int calcularPuntos(Usuario usuario) {
        int total = 0;
        for (int idJ : usuario.getJugadores()) {
            Jugador j = GestorDatos.jugadores.get(idJ);
            if (j != null) total += j.getTotalPuntos(); // Asumiendo que Jugador tiene getTotalPuntos()
        }
        return total;
    }
}
