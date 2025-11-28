import java.util.*;
import java.util.stream.Collectors;

public class Usuario {
    private int id;
    private String nombre;
    private String email;
    private String telefono;
    private String contrasena;

    // NUEVO: saldo independiente por liga
    private Map<Integer, Integer> saldoPorLiga = new HashMap<>();
    
    private List<Integer> jugadores; // IDs
    private List<Integer> ligas;     // IDs
    private boolean equipoMostrado;
    private int ligaActualId = -1;   // ID de la liga que está viendo actualmente

    public Usuario(int id, String nombre, String email, String telefono, String contrasena) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.contrasena = contrasena;
        this.jugadores = new ArrayList<>();
        this.ligas = new ArrayList<>();
        this.equipoMostrado = false;
    }

    // ====================== GETTERS ======================
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getContrasena() { return contrasena; }
    public List<Integer> getJugadores() { return jugadores; }
    public List<Integer> getLigas() { return ligas; }
    public boolean isEquipoMostrado() { return equipoMostrado; }
    public int getLigaActualId() { return ligaActualId; }

    // ====================== SALDO POR LIGA ======================
    // Obtener saldo de la liga actual
    public int getSaldo() {
        return getSaldo(ligaActualId);
    }

    // Obtener saldo de una liga específica
    public int getSaldo(int ligaId) {
        return saldoPorLiga.getOrDefault(ligaId, 100_000_000);
    }

    // Establecer saldo de la liga actual
    public void setSaldo(int saldo) {
        setSaldo(ligaActualId, saldo);
    }

    // Establecer saldo de una liga específica
    public void setSaldo(int ligaId, int saldo) {
        saldoPorLiga.put(ligaId, saldo);
    }

    // ====================== LIGA ACTUAL ======================
    public void setLigaActualId(int ligaActualId) {
        this.ligaActualId = ligaActualId;
    }

    // ====================== OTROS MÉTODOS ======================
    public void addJugador(int idJugador) { jugadores.add(idJugador); }
    public void addLiga(int idLiga) {
        ligas.add(idLiga);
        // Inicializar saldo en la liga al unirse
        saldoPorLiga.put(idLiga, 100_000_000);
    }
    public void setEquipoMostrado(boolean equipoMostrado) { this.equipoMostrado = equipoMostrado; }
    public void setJugadores(List<Integer> jugadores) { this.jugadores = jugadores; }

    // ====================== EXPORTAR / IMPORTAR ======================
    public String toFileString() {
        String jugStr = String.join(",", jugadores.stream().map(String::valueOf).collect(Collectors.toList()));
        String ligStr = String.join(",", ligas.stream().map(String::valueOf).collect(Collectors.toList()));
        String saldoStr = saldoPorLiga.entrySet()
                              .stream()
                              .map(e -> e.getKey() + ":" + e.getValue())
                              .collect(Collectors.joining(","));
        return id + ";" + nombre + ";" + email + ";" + telefono + ";" + contrasena + ";" +
               jugStr + ";" + ligStr + ";" + (equipoMostrado ? "1" : "0") + ";" + ligaActualId + ";" + saldoStr;
    }

    public static Usuario fromFileString(String linea) {
        String[] partes = linea.split(";");
        if (partes.length < 6) return null;

        int id = Integer.parseInt(partes[0]);
        String nombre = partes[1];
        String email = partes[2];
        String telefono = partes[3];
        String contrasena = partes[4];

        Usuario u = new Usuario(id, nombre, email, telefono, contrasena);

        // Jugadores
        if (partes.length >= 7 && !partes[6].isEmpty()) {
            for (String j : partes[6].split(",")) {
                if (!j.trim().isEmpty()) u.addJugador(Integer.parseInt(j.trim()));
            }
        }

        // Ligas
        if (partes.length >= 8 && !partes[7].isEmpty()) {
            for (String l : partes[7].split(",")) {
                if (!l.trim().isEmpty()) u.ligas.add(Integer.parseInt(l.trim()));
            }
        }

        // Equipo mostrado
        if (partes.length >= 9) u.equipoMostrado = partes[8].equals("1");

        // Liga actual
        if (partes.length >= 10) {
            try { u.ligaActualId = Integer.parseInt(partes[9]); } 
            catch (NumberFormatException e) { u.ligaActualId = -1; }
        }

        // Saldo por liga
        if (partes.length >= 11 && !partes[10].isEmpty()) {
            for (String s : partes[10].split(",")) {
                String[] kv = s.split(":");
                if (kv.length == 2) {
                    u.saldoPorLiga.put(Integer.parseInt(kv[0]), Integer.parseInt(kv[1]));
                }
            }
        }

        return u;
    }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nombre='" + nombre + "', ligas=" + ligas +
               ", equipoMostrado=" + equipoMostrado + ", ligaActualId=" + ligaActualId + ", saldoPorLiga=" + saldoPorLiga + "}";
    }
}
