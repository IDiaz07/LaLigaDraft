import java.util.*;
import java.util.stream.Collectors;

public class Liga {
    private int id;
    private String nombre;
    private boolean publica;           // indica si es pública o privada
    private String codigoInvitacion;  // solo para privadas
    private List<Integer> usuarios;    // IDs de usuarios
    private List<Integer> mercado;     // IDs de jugadores
    private long ultimaActualizacionMercado;

    public Liga(int id, String nombre) {
        this(id, nombre, true, null);
    }

    public Liga(int id, String nombre, boolean publica, String codigoInvitacion) {
        this.id = id;
        this.nombre = nombre;
        this.publica = publica;
        this.codigoInvitacion = codigoInvitacion;
        this.usuarios = new ArrayList<>();
        this.mercado = new ArrayList<>();
        this.ultimaActualizacionMercado = System.currentTimeMillis();
    }

    // ============================== GETTERS ==============================
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public boolean isPublica() { return publica; }
    public String getCodigoInvitacion() { return codigoInvitacion; }
    public List<Integer> getUsuariosIds() { return usuarios; }
    public List<Integer> getMercadoIds() { return mercado; }
    public long getUltimaActualizacionMercado() { return ultimaActualizacionMercado; }

    public void setUltimaActualizacionMercado(long timestamp) {
        this.ultimaActualizacionMercado = timestamp;
    }

    // ============================== MÉTODOS ==============================
    public void addUsuario(int idUsuario) {
        if (!usuarios.contains(idUsuario)) {
            usuarios.add(idUsuario);
            Usuario u = GestorDatos.usuarios.get(idUsuario);
            if (u != null) {
                u.addLiga(id);
            }
        }
    }

    public void addJugadorAlMercado(int idJugador) {
        if (!mercado.contains(idJugador)) {
            mercado.add(idJugador);
        }
    }

    public boolean mercadoExpirado() {
        long ahora = System.currentTimeMillis();
        return (ahora - ultimaActualizacionMercado) >= 24L * 60 * 60 * 1000; // 24h
    }

    public void renovarMercado(List<Integer> nuevosJugadores) {
        if (nuevosJugadores.size() != 8) {
            throw new IllegalArgumentException("El mercado debe tener exactamente 8 jugadores");
        }
        mercado.clear();
        mercado.addAll(nuevosJugadores);
        ultimaActualizacionMercado = System.currentTimeMillis();
    }

    public List<Usuario> getClasificacion() {
        List<Usuario> lista = new ArrayList<>();
        for (int idU : usuarios) {
            Usuario u = GestorDatos.usuarios.get(idU);
            if (u != null) lista.add(u);
        }
        lista.sort((u1, u2) -> Integer.compare(puntosUsuario(u2), puntosUsuario(u1)));
        return lista;
    }

    private int puntosUsuario(Usuario usuario) {
        int total = 0;
        for (int idJ : usuario.getJugadores()) {
            Jugador j = GestorDatos.jugadores.get(idJ);
            if (j != null) total += j.getTotalPuntos();
        }
        return total;
    }

    // ============================== EXPORT / IMPORT ==============================
    public String toFileString() {
        String usuStr = usuarios.stream().map(String::valueOf).collect(Collectors.joining(","));
        String merStr = mercado.stream().map(String::valueOf).collect(Collectors.joining(","));
        return id + ";" + nombre + ";" + (publica ? "1" : "0") + ";" +
               (codigoInvitacion == null ? "" : codigoInvitacion) + ";" +
               usuStr + ";" + merStr + ";" + ultimaActualizacionMercado;
    }

    public static Liga fromFileString(String linea) {
        String[] partes = linea.split(";");
        if (partes.length < 7) return null;

        int id = Integer.parseInt(partes[0]);
        String nombre = partes[1];
        boolean publica = "1".equals(partes[2]);
        String codigo = partes[3].isEmpty() ? null : partes[3];

        Liga l = new Liga(id, nombre, publica, codigo);

        if (!partes[4].isEmpty()) {
            for (String u : partes[4].split(",")) {
                l.usuarios.add(Integer.parseInt(u));
            }
        }

        if (!partes[5].isEmpty()) {
            for (String j : partes[5].split(",")) {
                l.mercado.add(Integer.parseInt(j));
            }
        }

        l.ultimaActualizacionMercado = Long.parseLong(partes[6]);
        return l;
    }

    @Override
    public String toString() {
        return "Liga{id=" + id +
               ", nombre='" + nombre + '\'' +
               ", publica=" + publica +
               ", usuarios=" + usuarios.size() +
               ", mercado=" + mercado.size() + " jugadores}";
    }
}
