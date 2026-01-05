package gui.clases;

import gui.enums.Estado;
import gui.enums.Posicion;

/**
 * Modelo que representa a un jugador de fútbol.
 * Contiene información personal, estadísticas y puntuaciones por jornada.
 */
public class Jugador {
    private int id;
    private String nombre;
    private String equipo;
    private int edad;
    private String nacionalidad;
    private int numeroCamiseta;

    private int valorMercado;
    private int valorInicial;
    private Estado estado;
    private Posicion posicion;
    private Integer[] puntosPorJornada; // Puntos obtenidos en cada jornada (32 jornadas)

    // Estadísticas 
    private int goles;
    private int asistencias;
    private int tarjetasAmarillas;
    private int tarjetasRojas;

    public Jugador(int id, String nombre, String equipo, int edad, String nacionalidad, int numeroCamiseta,
            int valorMercado, Estado estado, Posicion posicion) {
        this.id = id;
        this.nombre = nombre;
        this.equipo = equipo;
        this.edad = edad;
        this.nacionalidad = nacionalidad;
        this.numeroCamiseta = numeroCamiseta;

        this.valorMercado = valorMercado;
        this.valorInicial = valorMercado; 
        this.estado = estado;
        this.posicion = posicion;

        this.puntosPorJornada = new Integer[32];
        this.goles = 0;
        this.asistencias = 0;
        this.tarjetasAmarillas = 0;
        this.tarjetasRojas = 0;
    }

    // Metodos getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEquipo() {
        return equipo;
    }

    public int getEdad() {
        return edad;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public int getNumeroCamiseta() {
        return numeroCamiseta;
    }

    public int getValorMercado() {
        return valorMercado;
    }

    public int getValorInicial() {
        return valorInicial;
    }

    public Estado getEstado() {
        return estado;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public Integer[] getPuntosPorJornada() {
        return puntosPorJornada;
    }

    public int getGoles() {
        return goles;
    }

    public int getAsistencias() {
        return asistencias;
    }

    public int getTarjetasAmarillas() {
        return tarjetasAmarillas;
    }

    public int getTarjetasRojas() {
        return tarjetasRojas;
    }

    // Metodos setters
    public void setValorMercado(int valor) {
        this.valorMercado = valor;
    }

    public void setValorInicial(int valor) {
        this.valorInicial = valor;
    } 

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public void setGoles(int goles) {
        this.goles = goles;
    }

    public void setAsistencias(int asistencias) {
        this.asistencias = asistencias;
    }

    public void setTarjetasAmarillas(int tA) {
        this.tarjetasAmarillas = tA;
    }

    public void setTarjetasRojas(int tR) {
        this.tarjetasRojas = tR;
    }

    // Métodos adicionales
    public void addGol() {
        this.goles++;
    }

    public void addAsistencia() {
        this.asistencias++;
    }

    public void addTarjetaAmarilla() {
        this.tarjetasAmarillas++;
    }

    public void addTarjetaRoja() {
        this.tarjetasRojas++;
    }

    // ----------------- LÓGICA -----------------
    public void setPuntosEnJornada(int jornada, Integer puntos) {
        if (jornada < 1 || jornada > 32)
            throw new IllegalArgumentException("Jornada inválida");
        puntosPorJornada[jornada - 1] = puntos;
    }

    public int getTotalPuntos() {
        int total = 0;
        for (Integer p : puntosPorJornada) {
            if (p != null)
                total += p;
        }
        return total;
    }

    public String toFileString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(";")
                .append(nombre).append(";")
                .append(equipo).append(";")
                .append(edad).append(";")
                .append(nacionalidad).append(";")
                .append(numeroCamiseta).append(";")
                .append(valorMercado).append(";")
                .append(valorInicial).append(";")
                .append(estado).append(";")
                .append(posicion).append(";")
                .append(goles).append(";")
                .append(asistencias).append(";")
                .append(tarjetasAmarillas).append(";")
                .append(tarjetasRojas).append(";");

        for (int i = 0; i < 32; i++) {
            if (i > 0)
                sb.append(",");
            sb.append(puntosPorJornada[i] == null ? "null" : puntosPorJornada[i]);
        }
        return sb.toString();
    }

    public static Jugador fromFileString(String linea) {
        String[] partes = linea.split(";");
        if (partes.length < 15)
            return null;

        int id = Integer.parseInt(partes[0]);
        String nombre = partes[1];
        String equipo = partes[2];
        int edad = Integer.parseInt(partes[3]);
        String nacionalidad = partes[4];
        int numeroCamiseta = Integer.parseInt(partes[5]);
        int valorMercado = Integer.parseInt(partes[6]);
        int valorInicial = Integer.parseInt(partes[7]);
        Estado estado = Estado.valueOf(partes[8]);
        Posicion pos = Posicion.valueOf(partes[9]);
        int goles = Integer.parseInt(partes[10]);
        int asistencias = Integer.parseInt(partes[11]);
        int amarillas = Integer.parseInt(partes[12]);
        int rojas = Integer.parseInt(partes[13]);

        Jugador j = new Jugador(id, nombre, equipo, edad, nacionalidad, numeroCamiseta, valorMercado, estado, pos);
        j.setValorInicial(valorInicial); // se modifica
        j.setGoles(goles);
        j.setAsistencias(asistencias);
        j.setTarjetasAmarillas(amarillas);
        j.setTarjetasRojas(rojas);

        if (partes.length >= 15) {
            String[] puntos = partes[14].split(",");
            for (int i = 0; i < puntos.length && i < 32; i++) {
                if (!puntos[i].equals("null")) {
                    j.puntosPorJornada[i] = Integer.parseInt(puntos[i]);
                }
            }
        }

        return j;
    }

    @Override
    public String toString() {
        return nombre + " (" + equipo + ", " + posicion + ", " + estado + ")";
    }
}
