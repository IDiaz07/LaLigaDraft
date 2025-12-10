package bd;
import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AsignadorUsuariosJugadores {

    private static final String DB_URL = "jdbc:sqlite:database.db";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // -------------------- CREAR TABLA USUARIOS_JUGADORES --------------------
    public static void crearTablaUsuariosJugadores() {
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {

            st.execute("""
                CREATE TABLE IF NOT EXISTS usuarios_jugadores (
                    usuario_id INTEGER,
                    jugador_id INTEGER,
                    liga_id INTEGER,
                    PRIMARY KEY(usuario_id, jugador_id, liga_id),
                    FOREIGN KEY(usuario_id) REFERENCES usuarios(id),
                    FOREIGN KEY(jugador_id) REFERENCES jugadores(id),
                    FOREIGN KEY(liga_id) REFERENCES ligas(id)
                );
            """);

            System.out.println("[OK] Tabla usuarios_jugadores creada o existente.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // -------------------- LEER LIGA ACTUAL DE USUARIOS --------------------
    public static Map<Integer, Integer> leerLigaActualUsuarios(String rutaUsuarios) {
        Map<Integer, Integer> usuarioALiga = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(rutaUsuarios))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] partes = linea.split(";", -1);
                if (partes.length < 10) continue; // asegurarse que tenga ligaActualId
                try {
                    int usuarioId = Integer.parseInt(partes[0].trim());
                    int ligaActualId = Integer.parseInt(partes[9].trim());
                    usuarioALiga.put(usuarioId, ligaActualId);
                } catch (NumberFormatException e) {
                    System.out.println("[WARN] Usuario ignorado por formato inválido: " + linea);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usuarioALiga;
    }

    // -------------------- ASIGNAR JUGADORES --------------------
    public static void asignarJugadores(String rutaJugadores, String rutaUsuarios) {
        Map<Integer, Integer> usuarioALiga = leerLigaActualUsuarios(rutaUsuarios);

        String sql = "INSERT OR IGNORE INTO usuarios_jugadores (usuario_id, jugador_id, liga_id) VALUES (?, ?, ?);";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             BufferedReader br = new BufferedReader(new FileReader(rutaJugadores))) {

            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] campos = linea.split(";", -1);
                if (campos.length < 11) {
                    System.out.println("[WARN] Línea ignorada (menos de 11 campos): " + linea);
                    continue;
                }

                try {
                    int jugadorId = Integer.parseInt(campos[0].trim());
                    int propietarioId = Integer.parseInt(campos[10].trim());

                    Integer ligaId = usuarioALiga.get(propietarioId);

                    ps.setInt(1, propietarioId);
                    ps.setInt(2, jugadorId);

                    if (ligaId != null && ligaId != -1) {
                        ps.setInt(3, ligaId);
                    } else {
                        ps.setNull(3, Types.INTEGER);
                    }

                    ps.executeUpdate();

                } catch (NumberFormatException e) {
                    System.out.println("[WARN] Línea ignorada por formato inválido: " + linea);
                }
            }

            System.out.println("[OK] Jugadores asignados a usuarios con liga actual.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------- MAIN --------------------
    public static void main(String[] args) {
        crearTablaUsuariosJugadores();
        asignarJugadores("jugadores.txt", "usuarios.txt");
    }
}
