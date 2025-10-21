import java.util.*;

public class Liga {
    private int id;
    private String nombre;
    private boolean publica;           // NUEVO: indica si es pública o privada
    private String codigoInvitacion;   // NUEVO: solo para privadas
    private List<Integer> usuarios;    // guardamos IDs de usuarios
    private List<Integer> mercado;     // guardamos IDs de jugadores
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

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public boolean isPublica() { return publica; }
    public String getCodigoInvitacion() { return codigoInvitacion; }
    public List<Integer> getUsuariosIds() { return usuarios; }
    public List<Integer> getMercadoIds() { return mercado; }

    // Añadir usuario a la liga
    public void addUsuario(int idUsuario) {
        if (!usuarios.contains(idUsuario)) {
            usuarios.add(idUsuario);
            // también reflejamos en el usuario
            if (GestorDatos.usuarios.containsKey(idUsuario)) {
                GestorDatos.usuarios.get(idUsuario).addLiga(id);
            }
        }
    }

    // Clasificación (usando GestorDatos para obtener usuarios reales)
    public List<Usuario> getClasificacion() {
        List<Usuario> lista = new ArrayList<>();
        for (int idU : usuarios) {
            Usuario u = GestorDatos.usuarios.get(idU);
            if (u != null) lista.add(u);
        }

        lista.sort((u1, u2) -> Integer.compare(
            puntosUsuario(u2),
            puntosUsuario(u1)
        ));
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

    // Renovar mercado con 8 jugadores
    public void renovarMercado(List<Integer> nuevosJugadores) {
        if (nuevosJugadores.size() != 8) {
            throw new IllegalArgumentException("El mercado debe tener exactamente 8 jugadores");
        }
        this.mercado.clear();
        this.mercado.addAll(nuevosJugadores);
        this.ultimaActualizacionMercado = System.currentTimeMillis();
    }

    public boolean mercadoExpirado() {
        long ahora = System.currentTimeMillis();
        return (ahora - ultimaActualizacionMercado) >= 24 * 60 * 60 * 1000;
    }

    // Exportar liga a archivo
    // Formato: id;nombre;publica;codigo;usuarios;mercado;timestamp
    public String toFileString() {
        String usuStr = String.join(",", usuarios.stream().map(String::valueOf).toList());
        String merStr = String.join(",", mercado.stream().map(String::valueOf).toList());
        return id + ";" + nombre + ";" + (publica ? "1" : "0") + ";" +
               (codigoInvitacion == null ? "" : codigoInvitacion) + ";" +
               usuStr + ";" + merStr + ";" + ultimaActualizacionMercado;
    }

    // Crear liga desde archivo
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
