import java.sql.*;
import java.util.*;

public class GestorDatos {

    private static final String DB_URL = "jdbc:sqlite:database.db";

    // -------------------- CAMPOS ESTÁTICOS --------------------
    public static Map<Integer, Usuario> usuarios = new HashMap<>();
    public static Map<Integer, Jugador> jugadores = new HashMap<>();
    public static Map<Integer, Liga> ligas = new HashMap<>();

    // -------------------- CONEXIÓN --------------------
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // -------------------- CREAR TABLAS --------------------
    public static void crearTablas() {
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {

            st.execute("""
                CREATE TABLE IF NOT EXISTS usuarios (
                    id INTEGER PRIMARY KEY,
                    nombre TEXT,
                    email TEXT,
                    telefono TEXT,
                    contrasena TEXT,
                    saldo INTEGER,
                    equipoMostrado BOOLEAN,
                    ligaActualId INTEGER
                );
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS jugadores (
                    id INTEGER PRIMARY KEY,
                    nombre TEXT,
                    equipo TEXT,
                    edad INTEGER,
                    nacionalidad TEXT,
                    numeroCamiseta INTEGER,
                    valorMercado INTEGER,
                    valorInicial INTEGER,
                    estado TEXT,
                    posicion TEXT,
                    goles INTEGER,
                    asistencias INTEGER,
                    tarjetasAmarillas INTEGER,
                    tarjetasRojas INTEGER,
                    puntosPorJornada TEXT
                );
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS ligas (
                    id INTEGER PRIMARY KEY,
                    nombre TEXT,
                    publica BOOLEAN,
                    codigoInvitacion TEXT,
                    ultimaActualizacionMercado INTEGER
                );
            """);

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

            System.out.println("[OK] Tablas creadas o existentes.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // -------------------- CARGAR DATOS DESDE DB --------------------
    public static Map<Integer, Usuario> cargarUsuarios() {
        usuarios.clear();
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM usuarios")) {

            while (rs.next()) {
            	Usuario u = new Usuario(
            	        rs.getInt("id"),
            	        rs.getString("nombre"),
            	        rs.getString("email"),
            	        rs.getString("telefono"),
            	        rs.getString("contrasena"),
            	        rs.getInt("saldo")
            	);
            	u.setEquipoMostrado(rs.getBoolean("equipoMostrado"));
            	u.setLigaActualId(rs.getInt("ligaActualId"));
                usuarios.put(u.getId(), u);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    public static Map<Integer, Jugador> cargarJugadores() {
        jugadores.clear();
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM jugadores")) {

            while (rs.next()) {
                Jugador j = new Jugador(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("equipo"),
                    rs.getInt("edad"),
                    rs.getString("nacionalidad"),
                    rs.getInt("numeroCamiseta"),
                    rs.getInt("valorMercado"),
                    Estado.valueOf(rs.getString("estado")),
                    Posicion.valueOf(rs.getString("posicion"))
                );

                // Cargar estadísticas
                j.setValorMercado(rs.getInt("valorInicial"));
                j.setGoles(rs.getInt("goles"));
                j.setAsistencias(rs.getInt("asistencias"));
                j.setTarjetasAmarillas(rs.getInt("tarjetasAmarillas"));
                j.setTarjetasRojas(rs.getInt("tarjetasRojas"));

                // Cargar puntos por jornada
                String puntosStr = rs.getString("puntosPorJornada");
                if (puntosStr != null && !puntosStr.isEmpty()) {
                    String[] partes = puntosStr.split(",");
                    for (int i = 0; i < partes.length && i < 32; i++) {
                        j.getPuntosPorJornada()[i] = partes[i].equals("null") ? null : Integer.parseInt(partes[i]);
                    }
                }

                jugadores.put(j.getId(), j);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jugadores;
    }

    public static Map<Integer, Liga> cargarLigas() {
        ligas.clear();
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM ligas")) {

            while (rs.next()) {
            	Liga l = new Liga(
            		    rs.getInt("id"),
            		    rs.getString("nombre"),
            		    rs.getBoolean("publica"),
            		    rs.getString("codigoInvitacion")
            		);
            		l.setUltimaActualizacionMercado(rs.getLong("ultimaActualizacionMercado"));
                ligas.put(l.getId(), l);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ligas;
    }

    // -------------------- GUARDAR DATOS --------------------
    public static void guardarUsuarios() { /* igual que tu versión */ }
    public static void guardarJugadores() { /* igual que tu versión */ }
    public static void guardarLigas() { /* igual que tu versión */ }

    // -------------------- ASIGNAR JUGADORES A USUARIOS --------------------
    public static void asignarUsuariosJugadores() { /* igual que tu versión */ }
    public static void asignarEquipoInicial(Usuario usuario) { /* igual que tu versión */ }

    // -------------------- CARGAR TODO --------------------
    public static void cargarTodo() {
        usuarios = cargarUsuarios();
        jugadores = cargarJugadores();
        ligas = cargarLigas();

        // Asignar jugadores a usuarios según tabla intermedia
        asignarUsuariosJugadores();
    }

    // -------------------- INICIALIZAR DB --------------------
    public static void inicializar() {
        crearTablas();
        cargarTodo();
    }

    // -------------------- MAIN --------------------
    public static void main(String[] args) {
        inicializar();
        System.out.println("✅ Base de datos cargada correctamente. Usuarios: " + usuarios.size());
    }
}
