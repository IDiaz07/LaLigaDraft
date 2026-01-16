package gui.clases;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MI MOTOR DE NOTICIAS Y GENERACIÓN DE EVENTOS (NPC ENGINE)
 * He desarrollado esta clase para simular que la liga está "viva" mediante
 * la generación automática de textos.
 * No toco la BD aquí, uso mis propios algoritmos para crear ambiente.
 */
public class MotorNoticias {

    // --- MIS DATOS HARDCODED PARA LA GENERACIÓN ---
    // He preparado estos arrays para combinar titulares aleatoriamente
    
    private static final String[] SUJETOS = {
        "El delantero estrella", "El portero suplente", "El entrenador", "La directiva", 
        "Un grupo de inversores", "El capitán del equipo", "Una joven promesa", 
        "El veterano mediocentro", "El árbitro del último encuentro", "La afición",
        "El director deportivo", "Un ojeador internacional", "La prensa local"
    };

    private static final String[] ACCIONES_MERCADO = {
        "está pensando aceptar una oferta de", "rechazó firmar ayer con", 
        "ha sido visto cenando con el presidente de", "ha roto las negociaciones con",
        "busca salir cedido hacia", "podría irse gratis a",
        "ha llegado a un acuerdo verbal con", "exige cobrar más para no irse a",
        "está siendo seguido por", "ha sido ofrecido por su agente a"
    };

    private static final String[] ACCIONES_DEPORTIVAS = {
        "se ha retirado lesionado de mi entrenamiento", "ha tenido una discusión en el vestuario",
        "ha prometido marcar 3 goles la próxima jornada", "se ha quejado del estado del césped",
        "será sancionado por la federación", "ha firmado con una nueva marca deportiva",
        "ha pedido cambiar su dorsal", "es duda seria para el siguiente partido",
        "ha sido convocado con la selección", "perderá la titularidad por bajo rendimiento"
    };

    private static final String[] EQUIPOS_RIVALES = {
        "el Manchester City", "el Real Madrid", "el FC Barcelona", "el Bayern Múnich", 
        "el PSG", "la Juventus", "un equipo árabe", 
        "el Inter de Miami", "el Chelsea", "el Liverpool", "el Atlético",
        "Boca Juniors", "River Plate", "Flamengo"
    };

    private static final String[] ADJETIVOS_IMPACTO = {
        "¡ESCÁNDALO!", "¡ÚLTIMA HORA!", "[CONFIDENCIAL]", "¡BOMBAZO!", 
        "¿TRAICIÓN?", "INCREÍBLE:", "SORPRESA:", "OFICIAL:", 
        "RUMOR:", "EXCLUSIVA:"
    };
    
    private static final String[] FUENTES = {
        "según Marca", "vía Fabrizio Romano", "según fuentes del club",
        "leído en L'Equipe", "según El Chiringuito", "confirmado por la BBC",
        "visto en Twitter", "según su representante"
    };

    // --- MI LÓGICA INTERNA ---
    
    private List<String> historialNoticias;
    private Random generador;

    /**
     * Constructor: Inicializo mis listas y el generador aleatorio.
     */
    public MotorNoticias() {
        this.historialNoticias = new ArrayList<>();
        this.generador = new Random();
        
        // Lleno el buffer inicial para que no esté vacío al empezar
        inicializarBufferNoticias();
    }

    private void inicializarBufferNoticias() {
        for (int i = 0; i < 10; i++) {
            generarNuevaNoticia();
        }
    }

    /**
     * Algoritmo principal: Construyo la frase uniendo trozos de mis arrays.
     */
    public String generarNuevaNoticia() {
        StringBuilder noticia = new StringBuilder();
        
        // 1. Decido qué tipo de noticia quiero generar (0, 1 o 2)
        int tipo = generador.nextInt(3);
        
        // 2. Añado el encabezado sensacionalista
        noticia.append(obtenerElementoAleatorio(ADJETIVOS_IMPACTO)).append(" ");
        
        if (tipo == 0) { // TEMA MERCADO
            noticia.append(obtenerElementoAleatorio(SUJETOS)).append(" ");
            noticia.append(obtenerElementoAleatorio(ACCIONES_MERCADO)).append(" ");
            noticia.append(obtenerElementoAleatorio(EQUIPOS_RIVALES)).append(".");
        } else if (tipo == 1) { // TEMA DEPORTIVO
            noticia.append(obtenerElementoAleatorio(SUJETOS)).append(" ");
            noticia.append(obtenerElementoAleatorio(ACCIONES_DEPORTIVAS)).append(".");
        } else { // RUMORES
            noticia.append("Se rumorea que ");
            noticia.append(obtenerElementoAleatorio(SUJETOS)).append(" ");
            noticia.append(obtenerElementoAleatorio(ACCIONES_MERCADO)).append(" ");
            noticia.append("un club desconocido.");
        }
        
        // 3. Añado una fuente aleatoria (50% de probabilidad)
        if (generador.nextBoolean()) {
            noticia.append(" (").append(obtenerElementoAleatorio(FUENTES)).append(")");
        }
        
        // 4. Genero una hora falsa reciente para darle realismo
        String fechaHora = LocalDateTime.now()
                .minusHours(generador.nextInt(48)) 
                .minusMinutes(generador.nextInt(60))
                .format(DateTimeFormatter.ofPattern("HH:mm"));
        
        String resultadoFinal = "[" + fechaHora + "] " + noticia.toString();
        
        // Guardo en mi historial
        historialNoticias.add(0, resultadoFinal);
        
        // Limpio memoria si me paso de 100 noticias
        if (historialNoticias.size() > 100) {
            historialNoticias.remove(historialNoticias.size() - 1);
        }
        
        return resultadoFinal;
    }

    // Método auxiliar para no repetir código al sacar del array
    private String obtenerElementoAleatorio(String[] array) {
        if (array == null || array.length == 0) return "Desconocido";
        return array[generador.nextInt(array.length)];
    }

    /**
     * Mi algoritmo de predicción simple para el dashboard.
     */
    public String predecirResultadoProximoPartido() {
        int probLocal = generador.nextInt(40) + 30; // Entre 30 y 70
        int probVisitante = 100 - probLocal - generador.nextInt(10); 
        int probEmpate = 100 - probLocal - probVisitante;
        
        return "MI PREDICCIÓN IA: Local " + probLocal + "% - Empate " + probEmpate + "% - Visitante " + probVisitante + "%";
    }
}