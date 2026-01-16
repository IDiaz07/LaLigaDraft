package gui.clases;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 * COMPONENTE DE IDENTIDAD CORPORATIVA
 * Renderiza el logotipo oficial de la aplicación utilizando gráficos vectoriales 2D.
 * Incluye escudo, tipografía personalizada y efectos de iluminación (gradientes).
 */
public class PanelLogo extends JPanel {

    public PanelLogo() {
        setPreferredSize(new Dimension(400, 150)); // Tamaño ideal para cabeceras
        setBackground(new Color(18, 18, 18)); // Fondo transparente/oscuro
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;
        // ACTIVAR ANTIALIASING 
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();
        int centroX = ancho / 2;

        // --- 1. EL ESCUDO (SHIELD) ---
        int tamEscudo = 60;
        int yEscudo = 10;
        
        Path2D escudo = new Path2D.Double();
        escudo.moveTo(centroX - tamEscudo, yEscudo); // Arriba Izq
        escudo.lineTo(centroX + tamEscudo, yEscudo); // Arriba Der
        escudo.lineTo(centroX + tamEscudo, yEscudo + 40); // Bajada recta
        escudo.curveTo(centroX + tamEscudo, yEscudo + 90, 
                       centroX - tamEscudo, yEscudo + 90, 
                       centroX, yEscudo + 110); // Punta de abajo
        escudo.lineTo(centroX - tamEscudo, yEscudo + 40); // Vuelta arriba
        escudo.closePath();

        // Gradiente Premium (Azul Oscuro a Azul Claro)
        GradientPaint gradiente = new GradientPaint(
                centroX, yEscudo, new Color(41, 128, 185), 
                centroX, yEscudo + 100, new Color(22, 160, 133));
        
        g2.setPaint(gradiente);
        g2.fill(escudo);

        // Borde Dorado
        g2.setColor(new Color(241, 196, 15));
        g2.setStroke(new BasicStroke(3));
        g2.draw(escudo);

        // --- 2. LA ESTRELLA (DENTRO DEL ESCUDO) ---
        drawStar(g2, centroX, yEscudo + 45, 20, 10);

        // --- 3. EL TEXTO (TIPOGRAFÍA) ---
        Font fuenteTitulo = new Font("Segoe UI", Font.BOLD, 32);
        Font fuenteSub = new Font("Segoe UI", Font.PLAIN, 12);
        
        String textoPrincipal = "LALIGA DRAFT";
        String textoSecundario = "MANAGER 2025";

        FontMetrics fm = g.getFontMetrics(fuenteTitulo);
        int xTexto = centroX - (fm.stringWidth(textoPrincipal) / 2);
        int yTexto = yEscudo + 150; // Debajo del escudo

        // Sombra del texto (Efecto 3D simple)
        g2.setColor(new Color(0, 0, 0, 150));
        g2.setFont(fuenteTitulo);
        g2.drawString(textoPrincipal, xTexto + 2, yTexto + 2);

        // Texto Principal Blanco
        g2.setColor(Color.WHITE);
        g2.drawString(textoPrincipal, xTexto, yTexto);

        // Texto Secundario Dorado
        g2.setFont(fuenteSub);
        fm = g.getFontMetrics(fuenteSub);
        int xSub = centroX - (fm.stringWidth(textoSecundario) / 2);
        g2.setColor(new Color(241, 196, 15));
        g2.drawString(textoSecundario, xSub, yTexto + 20);
    }
    

    // Método matemático para dibujar una estrella perfecta
    private void drawStar(Graphics2D g, int x, int y, int outerRadius, int innerRadius) {
        Path2D star = new Path2D.Double();
        double angle = Math.PI / 2; // Empezar arriba
        double angleStep = Math.PI / 5; // 5 puntas

        star.moveTo(x + Math.cos(angle) * outerRadius, y - Math.sin(angle) * outerRadius); // Arriba es negativo en Y

        for (int i = 0; i < 5; i++) {
            angle += angleStep;
            star.lineTo(x + Math.cos(angle) * innerRadius, y - Math.sin(angle) * innerRadius);
            angle += angleStep;
            star.lineTo(x + Math.cos(angle) * outerRadius, y - Math.sin(angle) * outerRadius);
        }
        star.closePath();
        
        g.setColor(Color.WHITE);
        g.fill(star);
    }
}