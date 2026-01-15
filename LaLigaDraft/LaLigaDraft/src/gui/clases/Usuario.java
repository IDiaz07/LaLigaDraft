package gui.clases;

import java.util.*;
import java.util.stream.Collectors;

public class Usuario {

    private int id;
    private String nombre;
    private String email;
    private String telefono;
    private String contrasena;

    private Map<Integer, Integer> saldoPorLiga = new HashMap<>();
    private Map<Integer, List<Integer>> jugadoresPorLiga = new HashMap<>();
    private List<Integer> ligas = new ArrayList<>();

    private boolean equipoMostrado;
    private int ligaActualId = -1;

    // -------------------- CONSTRUCTOR --------------------

    public Usuario(int id, String nombre, String email, String telefono, String contrasena) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.contrasena = contrasena;
    }

    // -------------------- JUGADORES POR LIGA --------------------

    public List<Integer> getJugadoresLigaActual() {
        return jugadoresPorLiga.getOrDefault(ligaActualId, new ArrayList<>());
    }

    public List<Integer> getJugadoresLiga(int ligaId) {
        return jugadoresPorLiga.getOrDefault(ligaId, new ArrayList<>());
    }

    public void setJugadoresParaLiga(int ligaId, List<Integer> jugadores) {
        jugadoresPorLiga.put(ligaId, jugadores);
    }

    public void addJugadorALiga(int ligaId, int jugadorId) {
        jugadoresPorLiga
                .computeIfAbsent(ligaId, k -> new ArrayList<>())
                .add(jugadorId);
    }

    // -------------------- LIGAS --------------------

    public void addLiga(int idLiga) {
        if (!ligas.contains(idLiga)) {
            ligas.add(idLiga);
        }
    }

    public List<Integer> getLigas() {
        return ligas;
    }

    public int getLigaActualId() {
        return ligaActualId;
    }

    public void setLigaActualId(int ligaActualId) {
        this.ligaActualId = ligaActualId;
    }

    // -------------------- SALDO --------------------

    public int getSaldo(int idLiga) {
        return saldoPorLiga.getOrDefault(idLiga, 0);
    }

    public void actualizarSaldo(int idLiga, int nuevoSaldo) {
        saldoPorLiga.put(idLiga, nuevoSaldo);
    }

    // -------------------- DATOS BÁSICOS --------------------

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    // -------------------- ESTADO UI --------------------

    public boolean isEquipoMostrado() {
        return equipoMostrado;
    }

    public void setEquipoMostrado(boolean equipoMostrado) {
        this.equipoMostrado = equipoMostrado;
    }

    // -------------------- SERIALIZACIÓN --------------------

    @Override
    public String toString() {

        String ligasStr = ligas.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String saldosStr = saldoPorLiga.entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.joining(","));

        String jugadoresStr = jugadoresPorLiga.entrySet().stream()
                .map(e -> e.getKey() + "=" +
                        e.getValue().stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(",")))
                .collect(Collectors.joining("|"));

        return id + ";" + nombre + ";" + email + ";" + telefono + ";" + contrasena + ";" +
                ligasStr + ";" + jugadoresStr + ";" +
                (equipoMostrado ? "1" : "0") + ";" +
                ligaActualId + ";" + saldosStr;
    }

    // -------------------- DESERIALIZACIÓN --------------------

    public static Usuario fromFileString(String linea) {

        String[] partes = linea.split(";", -1);
        if (partes.length < 5) return null;

        Usuario u = new Usuario(
                Integer.parseInt(partes[0]),
                partes[1],
                partes[2],
                partes[3],
                partes[4]
        );

        if (partes.length > 5 && !partes[5].isEmpty()) {
            for (String l : partes[5].split(",")) {
                u.addLiga(Integer.parseInt(l));
            }
        }

        if (partes.length > 6 && !partes[6].isEmpty()) {
            for (String bloque : partes[6].split("\\|")) {
                String[] kv = bloque.split("=");
                if (kv.length != 2) continue;

                int ligaId = Integer.parseInt(kv[0]);
                List<Integer> jugadores = new ArrayList<>();

                for (String j : kv[1].split(",")) {
                    jugadores.add(Integer.parseInt(j));
                }

                u.setJugadoresParaLiga(ligaId, jugadores);
            }
        }

        if (partes.length > 7) {
            u.equipoMostrado = "1".equals(partes[7]);
        }

        if (partes.length > 8) {
            try {
                u.ligaActualId = Integer.parseInt(partes[8]);
            } catch (Exception e) {
                u.ligaActualId = -1;
            }
        }

        if (partes.length > 9 && !partes[9].isEmpty()) {
            for (String s : partes[9].split(",")) {
                String[] kv = s.split(":");
                if (kv.length == 2) {
                    u.saldoPorLiga.put(
                            Integer.parseInt(kv[0]),
                            Integer.parseInt(kv[1])
                    );
                }
            }
        }

        return u;
    }
}
