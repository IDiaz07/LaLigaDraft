package gui.clases;
import java.util.*;
import java.util.stream.Collectors;

public class Usuario {
    private int id;
    private String nombre;
    private String email;
    private String telefono;
    private String contrasena;

    // Mapa para guardar el saldo en cada liga
    private Map<Integer, Integer> saldoPorLiga = new HashMap<>();
    
    private List<Integer> jugadores; 
    private List<Integer> ligas;     
    private boolean equipoMostrado;
    private int ligaActualId = -1;   

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

    // =============================================================
    // AQUÍ ESTÁN LOS MÉTODOS QUE BUSCA GESTORDATOS (NO BORRAR)
    // =============================================================
    
    
    
    public void addJugador(int idJugador) {
        if (!jugadores.contains(idJugador)) {
            jugadores.add(idJugador);
        }
    }
    // =============================================================

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    
    public List<Integer> getJugadores() { return jugadores; }
    public void setJugadores(List<Integer> jugadores) { this.jugadores = jugadores; }
    
    public List<Integer> getLigas() { return ligas; }
    
    public boolean isEquipoMostrado() { return equipoMostrado; }
    public void setEquipoMostrado(boolean equipoMostrado) { this.equipoMostrado = equipoMostrado; }
    
    public int getLigaActualId() { return ligaActualId; }
    public void setLigaActualId(int ligaActualId) { this.ligaActualId = ligaActualId; }

    @Override
    public String toString() {
        String jugStr = jugadores.stream().map(String::valueOf).collect(Collectors.joining(","));
        String ligStr = ligas.stream().map(String::valueOf).collect(Collectors.joining(","));
        String saldosStr = saldoPorLiga.entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.joining(","));

        return id + ";" + nombre + ";" + email + ";" + telefono + ";" + contrasena + ";" +
               "0" + ";" + jugStr + ";" + ligStr + ";" + (equipoMostrado ? "1" : "0") + ";" +
               ligaActualId + ";" + saldosStr;
    }

    public static Usuario fromFileString(String linea) {
        String[] partes = linea.split(";", -1); 
        if (partes.length < 5) return null;

        Usuario u = new Usuario(Integer.parseInt(partes[0]), partes[1], partes[2], partes[3], partes[4]);

        if (partes.length >= 7 && !partes[6].isEmpty()) {
            for (String j : partes[6].split(",")) if (!j.trim().isEmpty()) u.addJugador(Integer.parseInt(j.trim()));
        }
        if (partes.length >= 8 && !partes[7].isEmpty()) {
            for (String l : partes[7].split(",")) if (!l.trim().isEmpty()) u.ligas.add(Integer.parseInt(l.trim()));
        }
        if (partes.length >= 9) u.equipoMostrado = "1".equals(partes[8]);
        if (partes.length >= 10) {
            try { u.ligaActualId = Integer.parseInt(partes[9]); } catch (Exception e) { u.ligaActualId = -1; }
        }
        if (partes.length >= 11 && !partes[10].isEmpty()) {
            for (String s : partes[10].split(",")) {
                String[] kv = s.split(":");
                if (kv.length == 2) {
                    try { u.saldoPorLiga.put(Integer.parseInt(kv[0]), Integer.parseInt(kv[1])); } catch (Exception e){}
                }
            }
        }
        return u;
    }
        public void addLiga(int idLiga) {
        if (!ligas.contains(idLiga)) {
            ligas.add(idLiga);
        }
    }
    
     

        public int getSaldo(int idLiga) {
            if (saldoPorLiga == null) return 0;
            return saldoPorLiga.getOrDefault(idLiga, 0);
        }

        public void actualizarSaldo(int idLiga, int nuevoSaldo) {
            // --- PROTECCIÓN CONTRA EL ERROR NULLPOINTER ---
            if (saldoPorLiga == null) {
                saldoPorLiga = new HashMap<>();
            }
            // ----------------------------------------------
            
            saldoPorLiga.put(idLiga, nuevoSaldo);
        }
}