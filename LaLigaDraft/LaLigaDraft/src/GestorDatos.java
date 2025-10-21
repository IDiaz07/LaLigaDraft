import java.io.*;
import java.util.*;

public class GestorDatos {
    public static Map<Integer, Usuario> usuarios = new HashMap<>();
    public static Map<Integer, Jugador> jugadores = new HashMap<>();
    public static Map<Integer, Liga> ligas = new HashMap<>();

    private static int contadorUsuarios = 0;
    private static int contadorJugadores = 0;
    private static int contadorLigas = 0;

    private static final String FILE_USUARIOS = "usuarios.txt";
    private static final String FILE_JUGADORES = "jugadores.txt";
    private static final String FILE_LIGAS = "ligas.txt";

    // ----------------- USUARIOS -----------------
    public static Usuario registrarUsuario(String nombre, String email, String telefono, String contrasena, int saldoInicial) {
        int id = ++contadorUsuarios;
        Usuario u = new Usuario(id, nombre, email, telefono, contrasena, saldoInicial);
        usuarios.put(id, u);
        guardarUsuarios();
        return u;
    }

    public static void guardarUsuarios() {
        File f = new File(FILE_USUARIOS);
        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            for (Usuario u : usuarios.values()) {
                pw.println(u.toFileString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void cargarUsuarios() {
        File f = new File(FILE_USUARIOS);
        //System.out.println("Ruta absoluta buscada: " + f.getAbsolutePath());
        if (!f.exists()) { /*System.out.println("⚠️ El archivo usuarios.txt no existe en esa ruta.");*/ return;}

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                Usuario u = Usuario.fromFileString(linea);
                if (u != null) {
                    usuarios.put(u.getId(), u);
                    contadorUsuarios = Math.max(contadorUsuarios, u.getId());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----------------- JUGADORES -----------------
    public static Jugador registrarJugador(
            String nombre,
            String equipo,
            int edad,
            String nacionalidad,
            int numeroCamiseta,
            int valorMercado,
            Estado estado,
            Posicion pos,
            int idPropietario) {

        int id = ++contadorJugadores;
        Jugador j = new Jugador(id, nombre, equipo, edad, nacionalidad, numeroCamiseta, valorMercado, estado, pos, idPropietario);
        jugadores.put(id, j);

        Usuario u = usuarios.get(idPropietario);
        if (u != null) {
            u.addJugador(id);
            guardarUsuarios(); // reflejar relación en usuarios.txt
        }
        guardarJugadores();
        return j;
    }

    public static void guardarJugadores() {
        File f = new File(FILE_JUGADORES);
        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            for (Jugador j : jugadores.values()) {
                pw.println(j.toFileString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void cargarJugadores() {
        File f = new File(FILE_JUGADORES);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                Jugador j = Jugador.fromFileString(linea);
                if (j != null) {
                    jugadores.put(j.getId(), j);
                    contadorJugadores = Math.max(contadorJugadores, j.getId());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----------------- LIGAS -----------------
    // Versión básica (para compatibilidad)
    public static Liga registrarLiga(String nombre) {
        return registrarLiga(nombre, true, null);
    }

    public static Liga registrarLiga(String nombre, boolean publica, String codigoInvitacion) {
        int id = ++contadorLigas;
        Liga l = new Liga(id, nombre, publica, codigoInvitacion);
        ligas.put(id, l);
        guardarLigas();
        return l;
    }

    public static void guardarLigas() {
        File f = new File(FILE_LIGAS);
        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            for (Liga l : ligas.values()) {
                pw.println(l.toFileString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void cargarLigas() {
        File f = new File(FILE_LIGAS);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                Liga l = Liga.fromFileString(linea);
                if (l != null) {
                    ligas.put(l.getId(), l);
                    contadorLigas = Math.max(contadorLigas, l.getId());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----------------- UTILIDADES DE LIGAS -----------------
    public static void agregarUsuarioALiga(int idUsuario, int idLiga) {
        Usuario u = usuarios.get(idUsuario);
        Liga l = ligas.get(idLiga);
        if (u == null || l == null) return;

        l.addUsuario(idUsuario);
        u.addLiga(idLiga);

        guardarUsuarios();
        guardarLigas();
    }

    public static Liga buscarLigaPublicaDisponible() {
        for (Liga l : ligas.values()) {
            if (l.isPublica()) {
                return l;
            }
        }
        return null;
    }

    // ----------------- INICIALIZACIÓN -----------------
    public static void cargarTodo() {
        cargarUsuarios();
        cargarJugadores();
        cargarLigas();
    }

    public static void guardarTodo() {
        guardarUsuarios();
        guardarJugadores();
        guardarLigas();
    }
    
 // ----------------- ASIGNAR EQUIPO INICIAL -----------------
    public static void asignarEquipoInicial(Usuario usuario) {
        // Solo si no tiene jugadores ya
        if (usuario.getJugadores() != null && !usuario.getJugadores().isEmpty()) {
            return;
        }

        // Filtrar jugadores disponibles (sin propietario)
        List<Jugador> libres = new ArrayList<>();
        for (Jugador j : jugadores.values()) {
            if (j.getPropietario() == 0) { // o usa null si no hay propietario
                libres.add(j);
            }
        }

        // Agrupar por posición
        List<Jugador> por = new ArrayList<>();
        List<Jugador> def = new ArrayList<>();
        List<Jugador> med = new ArrayList<>();
        List<Jugador> del = new ArrayList<>();

        for (Jugador j : libres) {
            switch (j.getPosicion()) {
                case POR: por.add(j); break;
                case DEF: def.add(j); break;
                case MED: med.add(j); break;
                case DEL: del.add(j); break;
                default: break;
            }
        }

        Random r = new Random();
        List<Jugador> asignados = new ArrayList<>();

        // Helper para coger n aleatorios de una lista
        java.util.function.BiConsumer<List<Jugador>, Integer> coger = (lista, n) -> {
            for (int i = 0; i < n && !lista.isEmpty(); i++) {
                Jugador j = lista.remove(r.nextInt(lista.size()));
                asignados.add(j);
            }
        };

        coger.accept(por, 2);
        coger.accept(def, 5);
        coger.accept(med, 5);
        coger.accept(del, 3);

        // Asignar propietario
        for (Jugador j : asignados) {
            j.setPropietario(usuario.getId());
            usuario.addJugador(j.getId());
        }

        guardarUsuarios();
        guardarJugadores();
        System.out.println("✅ Se asignaron " + asignados.size() + " jugadores a " + usuario.getNombre());
    }

}
