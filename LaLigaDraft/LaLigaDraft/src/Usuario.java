import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Usuario {
    private int id;
    private String nombre;
    private String email;
    private String telefono;
    private String contrasena;
    private int saldo;
    private List<Integer> jugadores; // IDs
    private List<Integer> ligas;     // IDs
    private boolean equipoMostrado;
    private int ligaActualId = -1; // NUEVO: ID de la liga que está viendo actualmente

    public Usuario(int id, String nombre, String email, String telefono, String contrasena, int saldoInicial) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.contrasena = contrasena;
        this.saldo = saldoInicial;
        this.jugadores = new ArrayList<>();
        this.ligas = new ArrayList<>();
        this.equipoMostrado = false;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getContrasena() { return contrasena; }
    public int getSaldo() { return saldo; }
    public List<Integer> getJugadores() { return jugadores; }
    public List<Integer> getLigas() { return ligas; }
    public boolean isEquipoMostrado() { return equipoMostrado; }
    
    // NUEVO: Getter y Setter para la liga actual
    public int getLigaActualId() { return ligaActualId; }
    public void setLigaActualId(int ligaActualId) { this.ligaActualId = ligaActualId; }

    // Métodos auxiliares
    public void addJugador(int idJugador) { jugadores.add(idJugador); }
    public void addLiga(int idLiga) { ligas.add(idLiga); }
    public void setEquipoMostrado(boolean equipoMostrado) { this.equipoMostrado = equipoMostrado; }
    public void setJugadores(List<Integer> jugadores) { this.jugadores = jugadores; }


    // Exportar a línea (compatible Java 8/11) - ¡MODIFICADO!
    public String toFileString() {
        String jugStr = String.join(",", jugadores.stream().map(String::valueOf).collect(Collectors.toList()));
        String ligStr = String.join(",", ligas.stream().map(String::valueOf).collect(Collectors.toList()));
        
        // Se añade ligaActualId al final
        return id + ";" + nombre + ";" + email + ";" + telefono + ";" + contrasena + ";" + saldo + ";" +
               jugStr + ";" + ligStr + ";" + (equipoMostrado ? "1" : "0") + ";" + ligaActualId;
    }

    // Crear desde línea - ¡MODIFICADO!
    public static Usuario fromFileString(String linea) {
        String[] partes = linea.split(";");
        if (partes.length < 6) return null;

        int id = Integer.parseInt(partes[0]);
        String nombre = partes[1];
        String email = partes[2];
        String telefono = partes[3];
        String contrasena = partes[4];
        int saldo = Integer.parseInt(partes[5]);

        Usuario u = new Usuario(id, nombre, email, telefono, contrasena, saldo);

        if (partes.length >= 7 && !partes[6].isEmpty()) {
            for (String j : partes[6].split(",")) {
                if (!j.trim().isEmpty()) u.addJugador(Integer.parseInt(j.trim()));
            }
        }
        if (partes.length >= 8 && !partes[7].isEmpty()) {
            for (String l : partes[7].split(",")) {
                if (!l.trim().isEmpty()) u.addLiga(Integer.parseInt(l.trim()));
            }
        }
        if (partes.length >= 9) {
            u.equipoMostrado = partes[8].equals("1");
        }
        
        // Se lee ligaActualId
        if (partes.length >= 10) { 
            try {
                u.ligaActualId = Integer.parseInt(partes[9]);
            } catch (NumberFormatException e) {
                u.ligaActualId = -1;
            }
        }

        return u;
    }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nombre='" + nombre + "', saldo=" + saldo +
               ", equipoMostrado=" + equipoMostrado + ", ligaActualId=" + ligaActualId + "}";
    }
}
