package bd;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import gui.clases.Jugador;
import gui.clases.Liga;
import gui.clases.Usuario;
import gui.enums.Estado;
import gui.enums.Posicion;

/**
 * Clase principal para la gestión de la base de datos y la persistencia de
 * datos.
 * Maneja la conexión con SQLite, y la carga/guardado de Usuarios, Jugadores y
 * Ligas.
 */
public class GestorDatos {

    private static final String DB_URL = "jdbc:sqlite:database.db";

    // -------------------- CAMPOS ESTÁTICOS --------------------
    // Mapas en memoria que almacenan toda la información de la aplicación
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

            //nombres de columnas usuarioId / ligaId (camelCase)
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

            st.execute("""
                        CREATE TABLE IF NOT EXISTS usuarios_ligas (
                            usuarioId INTEGER,
                            ligaId INTEGER,
                            PRIMARY KEY(usuarioId, ligaId),
                            FOREIGN KEY(usuarioId) REFERENCES usuarios(id),
                            FOREIGN KEY(ligaId) REFERENCES ligas(id)
                        );
                    """);

            // no creamos ligas_mercado aquí porque podrías haberla creado manualmente,
            // pero si quieres que se cree automáticamente, descomenta lo siguiente:
            st.execute("""
                        CREATE TABLE IF NOT EXISTS ligas_mercado (
                            liga_id INTEGER,
                            jugador_id INTEGER,
                            PRIMARY KEY(liga_id, jugador_id),
                            FOREIGN KEY(liga_id) REFERENCES ligas(id),
                            FOREIGN KEY(jugador_id) REFERENCES jugadores(id)
                        );
                    """);

            System.out.println("[OK] Tablas creadas o existentes.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // -------------------- CARGAR DATOS --------------------
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
                        rs.getString("contrasena"));
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
                        Posicion.valueOf(rs.getString("posicion")));

                j.setValorInicial(rs.getInt("valorInicial"));
                j.setGoles(rs.getInt("goles"));
                j.setAsistencias(rs.getInt("asistencias"));
                j.setTarjetasAmarillas(rs.getInt("tarjetasAmarillas"));
                j.setTarjetasRojas(rs.getInt("tarjetasRojas"));

                String puntosStr = rs.getString("puntosPorJornada");
                if (puntosStr != null && !puntosStr.isEmpty()) {
                    String[] partes = puntosStr.split(",");
                    for (int i = 0; i < partes.length && i < 32; i++) {
                        j.getPuntosPorJornada()[i] = "null".equals(partes[i]) ? null : Integer.parseInt(partes[i]);
                    }
                }

                jugadores.put(j.getId(), j);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jugadores;
    }

    /**
     * Carga todas las ligas y devuelve el mapa de ligas.
     * Se encarga también de recuperar el mercado desde la tabla ligas_mercado.
     */
    public static Map<Integer, Liga> cargarLigas() {
        ligas.clear();

        String sql = "SELECT * FROM ligas";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Liga liga = new Liga(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getBoolean("publica"),
                        rs.getString("codigoInvitacion") // column name as created
                );

                liga.setUltimaActualizacionMercado(rs.getLong("ultimaActualizacionMercado"));
                ligas.put(liga.getId(), liga);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Cargar mercado guardado (si existe la tabla ligas_mercado)
        String sqlMercado = "SELECT jugador_id FROM ligas_mercado WHERE liga_id = ?";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlMercado)) {

            for (Liga l : ligas.values()) {

                ps.setInt(1, l.getId());
                try (ResultSet rm = ps.executeQuery()) {

                    l.getMercadoIds().clear();

                    while (rm.next()) {
                        l.getMercadoIds().add(rm.getInt("jugador_id"));
                    }
                }
            }
            
        //excepcion
        } catch (SQLException e) {
            // Si la tabla no existe o hay problemas, lo mostramos pero no rompemos la
            // carga.
            System.err.println("[WARN] no se pudo cargar ligas_mercado: " + e.getMessage());
        }

        return ligas;
    }

    public static void cargarUsuariosLiga(Liga liga) {
        String sql = """
                    SELECT u.id, u.nombre, u.email, u.telefono, u.contrasena,
                           u.saldo, u.equipoMostrado, u.ligaActualId
                    FROM usuarios u
                    JOIN usuarios_ligas ul ON u.id = ul.usuarioId
                    WHERE ul.ligaId = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, liga.getId());
            ResultSet rs = ps.executeQuery();

            liga.getUsuariosIds().clear();

            while (rs.next()) {
                int id = rs.getInt("id");

                if (!usuarios.containsKey(id)) {
                    Usuario u = new Usuario(
                            id,
                            rs.getString("nombre"),
                            rs.getString("email"),
                            rs.getString("telefono"),
                            rs.getString("contrasena"));
                    u.setEquipoMostrado(rs.getBoolean("equipoMostrado"));
                    u.setLigaActualId(rs.getInt("ligaActualId"));
                    usuarios.put(id, u);
                }

                liga.addUsuario(id);
                usuarios.get(id).setJugadores(cargarJugadoresUsuario(id));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------- GUARDAR PLANTILLAS --------------------
    public static void guardarPlantillas() {
        String sqlInsert = """
                    INSERT OR IGNORE INTO usuarios_jugadores (usuario_id, jugador_id, liga_id)
                    VALUES (?, ?, ?)
                """;

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert)) {

            int total = 0;
            for (Usuario u : usuarios.values()) {
                List<Integer> jug = u.getJugadores();
                if (jug == null || jug.isEmpty())
                    continue;

                int ligaId = u.getLigaActualId();
                if (ligaId == 0 && !ligas.isEmpty()) {
                    ligaId = ligas.keySet().iterator().next();
                    u.setLigaActualId(ligaId);
                }

                Set<Integer> unicos = new HashSet<>(jug);
                for (int idJ : unicos) {
                    ps.setInt(1, u.getId());
                    ps.setInt(2, idJ);
                    ps.setInt(3, ligaId);
                    total += ps.executeUpdate();
                }
            }
            System.out.println("✅ Plantillas guardadas: " + total + " registros únicos");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------- GUARDAR ENTIDADES --------------------
    public static Usuario registrarUsuario(String nombre, String email, String telefono,
            String contrasena, int saldoInicial) {
        try (Connection conn = getConnection()) {

            int nuevoId = 1;
            String sqlMaxId = "SELECT COALESCE(MAX(id),0)+1 AS nuevoId FROM usuarios";
            try (PreparedStatement ps = conn.prepareStatement(sqlMaxId);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    nuevoId = rs.getInt("nuevoId");
            }

            Usuario nuevo = new Usuario(nuevoId, nombre, email, telefono, contrasena);
            nuevo.actualizarSaldo(-1, saldoInicial);

            String sqlInsert = """
                        INSERT INTO usuarios (id, nombre, email, telefono,
                                              contrasena, saldo, equipoMostrado, ligaActualId)
                        VALUES (?, ?, ?, ?, ?, ?, ?, NULL)
                    """;

            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setInt(1, nuevo.getId());
                ps.setString(2, nuevo.getNombre());
                ps.setString(3, nuevo.getEmail());
                ps.setString(4, nuevo.getTelefono());
                ps.setString(5, nuevo.getContrasena());
                ps.setInt(6, saldoInicial);
                ps.setBoolean(7, nuevo.isEquipoMostrado());
                ps.executeUpdate();
            }

            usuarios.put(nuevo.getId(), nuevo);
            System.out.println("✅ Usuario registrado correctamente: " + nombre);
            return nuevo;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void guardarUsuarios() {
        String sql = """
                    UPDATE usuarios
                    SET nombre = ?, email = ?, telefono = ?, contrasena = ?,
                        saldo = ?, equipoMostrado = ?, ligaActualId = ?
                    WHERE id = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Usuario u : usuarios.values()) {
                ps.setString(1, u.getNombre());
                ps.setString(2, u.getEmail());
                ps.setString(3, u.getTelefono());
                ps.setString(4, u.getContrasena());
                ps.setInt(5, u.getSaldo(u.getLigaActualId()));
                ps.setBoolean(6, u.isEquipoMostrado());
                ps.setInt(7, u.getLigaActualId());
                ps.setInt(8, u.getId());
                ps.executeUpdate();
            }
            System.out.println("Usuarios guardados correctamente en BD.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void guardarJugadores() {
        String sql = """
                    INSERT OR REPLACE INTO jugadores (
                        id, nombre, equipo, edad, nacionalidad, numeroCamiseta,
                        valorMercado, valorInicial, estado, posicion,
                        goles, asistencias, tarjetasAmarillas, tarjetasRojas,
                        puntosPorJornada
                    )
                    VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                """;

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Jugador j : jugadores.values()) {
                ps.setInt(1, j.getId());
                ps.setString(2, j.getNombre());
                ps.setString(3, j.getEquipo());
                ps.setInt(4, j.getEdad());
                ps.setString(5, j.getNacionalidad());
                ps.setInt(6, j.getNumeroCamiseta());

                ps.setInt(7, j.getValorMercado());
                ps.setInt(8, j.getValorInicial());
                ps.setString(9, j.getEstado().name());
                ps.setString(10, j.getPosicion().name());

                ps.setInt(11, j.getGoles());
                ps.setInt(12, j.getAsistencias());
                ps.setInt(13, j.getTarjetasAmarillas());
                ps.setInt(14, j.getTarjetasRojas());

                StringBuilder sb = new StringBuilder();
                Integer[] puntos = j.getPuntosPorJornada();
                for (int i = 0; i < puntos.length; i++) {
                    if (i > 0)
                        sb.append(",");
                    sb.append(puntos[i] == null ? "null" : puntos[i]);
                }
                ps.setString(15, sb.toString());

                ps.executeUpdate();
            }

            System.out.println("✅ Jugadores guardados correctamente en la base de datos.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void guardarLigas() {
        String sql = """
                    INSERT OR REPLACE INTO ligas (
                        id, nombre, publica, codigoInvitacion, ultimaActualizacionMercado
                    )
                    VALUES (?,?,?,?,?)
                """;

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            // ==================== GUARDAR DATOS DE LIGAS ====================
            for (Liga l : ligas.values()) {
                ps.setInt(1, l.getId());
                ps.setString(2, l.getNombre());
                ps.setBoolean(3, l.isPublica());
                ps.setString(4, l.getCodigoInvitacion());
                ps.setLong(5, l.getUltimaActualizacionMercado());
                ps.executeUpdate();
            }

            // ==================== GUARDAR MERCADO DE CADA LIGA ====================
            String sqlMercadoDelete = "DELETE FROM ligas_mercado WHERE liga_id = ?";
            String sqlMercadoInsert = "INSERT INTO ligas_mercado (liga_id, jugador_id) VALUES (?, ?)";

            try (PreparedStatement del = conn.prepareStatement(sqlMercadoDelete);
                    PreparedStatement ins = conn.prepareStatement(sqlMercadoInsert)) {

                for (Liga l : ligas.values()) {

                    // Borrar mercado antiguo
                    del.setInt(1, l.getId());
                    del.executeUpdate();

                    // Insertar mercado actual
                    for (Integer idJ : l.getMercadoIds()) {
                        ins.setInt(1, l.getId());
                        ins.setInt(2, idJ);
                        ins.executeUpdate();
                    }
                }
            }

            System.out.println("✓ Mercado de ligas guardado correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------- LIGAS --------------------
    public static Liga buscarLigaPublicaDisponible() {
        for (Liga l : ligas.values()) {
            if (l.isPublica())
                return l;
        }
        return null;
    }

    public static Liga registrarLiga(String nombre, boolean publica, String codigo) {
        for (Liga l : ligas.values()) {
            if (l.getNombre().equalsIgnoreCase(nombre)) {
                if (!publica && Objects.equals(l.getCodigoInvitacion(), codigo))
                    return null;
                if (publica)
                    return null;
            }
        }

        String sql = """
                    INSERT INTO ligas (nombre, publica, codigoInvitacion, ultimaActualizacionMercado)
                    VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            long ahora = System.currentTimeMillis();
            ps.setString(1, nombre);
            ps.setBoolean(2, publica);
            ps.setString(3, codigo);
            ps.setLong(4, ahora);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                Liga liga = new Liga(id, nombre, publica, codigo);
                liga.setUltimaActualizacionMercado(ahora);
                ligas.put(id, liga);
                return liga;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void agregarUsuarioALiga(int userId, int ligaId) {
        String sql = "INSERT OR IGNORE INTO usuarios_ligas (usuarioId, ligaId) VALUES (?, ?)";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, ligaId);
            ps.executeUpdate();

            Usuario u = usuarios.get(userId);
            Liga l = ligas.get(ligaId);

            if (u != null && l != null) {
                if (!u.getLigas().contains(ligaId))
                    u.getLigas().add(ligaId);
                if (!l.getUsuariosIds().contains(userId))
                    l.getUsuariosIds().add(userId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------- PLANTILLAS USUARIO --------------------
    public static List<Integer> cargarJugadoresUsuario(int idUsuario) {
        List<Integer> lista = new ArrayList<>();
        String sql = "SELECT jugador_id FROM usuarios_jugadores WHERE usuario_id = ?";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lista.add(rs.getInt("jugador_id"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static void asignarUsuariosJugadores() {
        for (Usuario usuario : usuarios.values()) {
            if (usuario.getJugadores() == null || usuario.getJugadores().isEmpty()) {
                List<Integer> ids = cargarJugadoresUsuario(usuario.getId());
                if (ids == null || ids.isEmpty()) {
                    asignarEquipoInicial(usuario);
                } else {
                    usuario.setJugadores(ids);
                }
            }
        }
    }

    public static void asignarEquipoInicial(Usuario usuario) {
        int intentos = 0;
        final int MAX_INTENTOS = 1000;
        List<Integer> listaIds = new ArrayList<>();
        Random rand = new Random();

        while (intentos < MAX_INTENTOS) {
            listaIds.clear();

            List<Jugador> porteros = new ArrayList<>();
            List<Jugador> defensas = new ArrayList<>();
            List<Jugador> mediocentros = new ArrayList<>();
            List<Jugador> delanteros = new ArrayList<>();

            Set<Integer> idsOcupados = new HashSet<>();
            for (Usuario u : usuarios.values()) {
                if (u.getId() != usuario.getId() && u.getJugadores() != null) {
                    idsOcupados.addAll(u.getJugadores());
                }
            }

            for (Jugador j : jugadores.values()) {
                if (idsOcupados.contains(j.getId()))
                    continue;
                switch (j.getPosicion()) {
                    case POR -> porteros.add(j);
                    case DEF -> defensas.add(j);
                    case MED -> mediocentros.add(j);
                    case DEL -> delanteros.add(j);
                }
            }

            Collections.shuffle(porteros, rand);
            Collections.shuffle(defensas, rand);
            Collections.shuffle(mediocentros, rand);
            Collections.shuffle(delanteros, rand);

            int count;

            count = Math.min(2, porteros.size());
            for (int i = 0; i < count; i++)
                listaIds.add(porteros.get(i).getId());

            count = Math.min(5, defensas.size());
            for (int i = 0; i < count; i++)
                listaIds.add(defensas.get(i).getId());

            count = Math.min(5, mediocentros.size());
            for (int i = 0; i < count; i++)
                listaIds.add(mediocentros.get(i).getId());

            count = Math.min(3, delanteros.size());
            for (int i = 0; i < count; i++)
                listaIds.add(delanteros.get(i).getId());

            int valorTotal = 0;
            for (int idJ : listaIds) {
                Jugador j = jugadores.get(idJ);
                if (j != null)
                    valorTotal += j.getValorMercado();
            }

            if (valorTotal >= 110_000_000 && valorTotal <= 140_000_000) {
                usuario.setJugadores(listaIds);
                System.out.println("✅ Equipo asignado a " + usuario.getNombre() +
                        ": " + (valorTotal / 1_000_000) + "M€");
                return;
            }

            intentos++;
        }

        usuario.setJugadores(listaIds);
        int valorTotal = listaIds.stream()
                .mapToInt(id -> jugadores.get(id).getValorMercado())
                .sum();
        System.out.println("[WARN] No se encontró combinación exacta para usuario " +
                usuario.getNombre() + ", valor total: " + valorTotal);
    }

    // -------------------- CARGAR / INICIALIZAR --------------------
    public static void cargarTodo() {
        usuarios = cargarUsuarios();
        jugadores = cargarJugadores();
        ligas = cargarLigas(); // ahora devuelve Map<Integer,Liga>
        asignarUsuariosJugadores();
        guardarPlantillas();
    }

    public static void inicializar() {
        crearTablas();
        cargarTodo();
    }

    // -------------------- MERCADO --------------------
    public static boolean tramitarCompra(Usuario comprador, Jugador jugador, Liga liga) {
        int saldoActual = comprador.getSaldo(liga.getId());
        if (saldoActual < jugador.getValorMercado()) {
            System.out.println("❌ No tienes suficiente dinero.");
            return false;
        }

        int coste = jugador.getValorMercado();
        comprador.actualizarSaldo(liga.getId(), saldoActual - coste);

        liga.getMercadoIds().remove(Integer.valueOf(jugador.getId()));
        comprador.addJugador(jugador.getId());
        guardarPlantillas();
        System.out.println("✅ Compra realizada: " + jugador.getNombre());
        return true;
    }

    public static void rellenarMercadoSiVacio() {
        for (Liga l : ligas.values()) {
            if (l.getMercadoIds().isEmpty()) {
                System.out.println("Rellenando mercado de liga " + l.getNombre() + "...");
                int contador = 0;
                for (Integer id : jugadores.keySet()) {
                    l.getMercadoIds().add(id);
                    contador++;
                    if (contador >= 10)
                        break;
                }
            }
        }
    }

}
