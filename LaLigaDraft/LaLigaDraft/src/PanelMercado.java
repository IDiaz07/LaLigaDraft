import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PanelMercado extends JPanel {

    private final Usuario usuario;
    private Liga ligaActual;

    private JLabel lblSaldoInferior;
    private JPanel contenedorLista;
    private JScrollPane scroll;

    private JLabel lblTemporizador;
    private Timer timerRenovacion;

    private JTextField buscador;

    public PanelMercado(Usuario usuario) {
        this.usuario = usuario;

        if (GestorDatos.ligas.containsKey(usuario.getLigaActualId())) {
            this.ligaActual = GestorDatos.ligas.get(usuario.getLigaActualId());
        }

        if (ligaActual != null) {

            boolean expirado = ligaActual.mercadoExpirado();
            boolean corrupto = ligaActual.getMercadoIds() == null || ligaActual.getMercadoIds().size() == 0;

            if (expirado || corrupto) {
                ligaActual.generarMercadoRandom();
                GestorDatos.guardarLigas();
            }
        }


        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 18));

        add(construirHeader(), BorderLayout.NORTH);
        add(construirMarketplace(), BorderLayout.CENTER);
        add(construirBarraSaldo(), BorderLayout.SOUTH);
    }


    /* --------------------------- HEADER: SOLO BUSCADOR + TEMPORIZADOR --------------------------- */
    private JPanel construirHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(18, 18, 18));
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        // Buscador
        buscador = new JTextField();
        buscador.setPreferredSize(new Dimension(200, 35));
        buscador.setBackground(new Color(35, 35, 35));
        buscador.setForeground(Color.WHITE);
        buscador.setCaretColor(Color.WHITE);
        buscador.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        buscador.setToolTipText("Buscar jugador…");
        buscador.addCaretListener(e -> refrescarLista());

        lblTemporizador = new JLabel();
        lblTemporizador.setForeground(new Color(160, 160, 160));
        lblTemporizador.setFont(new Font("Arial", Font.PLAIN, 12));

        JPanel panelDerecha = new JPanel(new GridLayout(2, 1));
        panelDerecha.setBackground(new Color(18, 18, 18));
        panelDerecha.add(new JLabel("")); // espacio
        panelDerecha.add(lblTemporizador);

        header.add(buscador, BorderLayout.CENTER);
        header.add(panelDerecha, BorderLayout.SOUTH);

        iniciarTemporizador();

        return header;
    }


    /* --------------------------- LISTA DE JUGADORES --------------------------- */
    private JScrollPane construirMarketplace() {

        contenedorLista = new JPanel();
        contenedorLista.setLayout(new BoxLayout(contenedorLista, BoxLayout.Y_AXIS));
        contenedorLista.setBackground(new Color(18, 18, 18));

        scroll = new JScrollPane(contenedorLista);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(18, 18, 18));
        scroll.getVerticalScrollBar().setUnitIncrement(20);

        refrescarLista();
        return scroll;
    }


    /* --------------------------- RECONSTRUIR LISTA --------------------------- */
    private void refrescarLista() {

        contenedorLista.removeAll();

        if (ligaActual == null) return;

        List<Jugador> lista = new ArrayList<>();

        for (Integer id : ligaActual.getMercadoIds()) {
            Jugador j = GestorDatos.jugadores.get(id);
            if (j != null) lista.add(j);
        }

        // Filtro por buscador
        if (!buscador.getText().trim().isEmpty()) {
            String text = buscador.getText().trim().toLowerCase();
            lista.removeIf(j -> !j.getNombre().toLowerCase().contains(text));
        }

        // Ordenar por valor de mayor a menor (como en la imagen)
        lista.sort(Comparator.comparing(Jugador::getValorMercado).reversed());

        // Construir tarjetas
        for (Jugador j : lista) {
            ItemMercado item = new ItemMercado(j, usuario, ligaActual);
            contenedorLista.add(item);
            contenedorLista.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        contenedorLista.revalidate();
        contenedorLista.repaint();
    }


    /* --------------------------- BARRA INFERIOR DE SALDO (ESTILO DAZN) --------------------------- */
    private JPanel construirBarraSaldo() {

        JPanel barra = new JPanel(new BorderLayout());
        barra.setPreferredSize(new Dimension(400, 60));
        barra.setBackground(new Color(231, 76, 60)); // 🔥 rojo como en imagen

        lblSaldoInferior = new JLabel(formatearSaldo(), SwingConstants.RIGHT);
        lblSaldoInferior.setForeground(Color.WHITE);
        lblSaldoInferior.setFont(new Font("Arial", Font.BOLD, 22));
        lblSaldoInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

        barra.add(lblSaldoInferior, BorderLayout.CENTER);

        return barra;
    }

    /* --------------------------- TEMPORIZADOR --------------------------- */
    private void iniciarTemporizador() {
        if (ligaActual == null) return;

        timerRenovacion = new Timer(1000, e -> {

            long ahora = System.currentTimeMillis();
            long resta = ligaActual.getUltimaActualizacionMercado() + 24L * 60 * 60 * 1000 - ahora;

            if (resta <= 0) {
                lblTemporizador.setText("Renovando…");
                ligaActual.generarMercadoRandom();
                GestorDatos.guardarLigas();
                refrescarLista();
                return;
            }

            long segundos = (resta / 1000) % 60;
            long minutos = (resta / (1000 * 60)) % 60;
            long horas = (resta / (1000 * 60 * 60));

            lblTemporizador.setText(
                    String.format("%02dh %02dm %02ds", horas, minutos, segundos)
            );
        });

        timerRenovacion.start();
    }

    /* --------------------------- SALDO --------------------------- */
    private String formatearSaldo() {
        if (ligaActual == null) return "0 €";
        return String.format("%,d €", usuario.getSaldo(ligaActual.getId()));
    }


 // ===== MÉTODOS COMPATIBLES CON VentanaPrincipal =====

    public void setSaldo(int saldo) {
        if (lblSaldoInferior != null) {
            lblSaldoInferior.setText(String.format("%,d €", saldo));
        }
    }

    public void mostrarSaldo(boolean mostrar) {
        if (lblSaldoInferior != null) {
            lblSaldoInferior.setVisible(mostrar);
        }
    }

    // Mantener compatibilidad con versiones anteriores
    public void actualizarSaldoVisual() {
        setSaldo(usuario.getSaldo(ligaActual.getId()));
    }


}
