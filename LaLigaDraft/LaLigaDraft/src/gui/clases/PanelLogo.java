package gui.clases;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * COMPONENTE DE LOGOTIPO VECTORIAL
 * Esta clase dibuja programáticamente el escudo y título de la aplicación
 * utilizando la API Graphics2D de Java.
 * No requiere archivos de imagen externos, asegurando portabilidad y calidad.
 */
public class PanelLogo extends JPanel {

    public PanelLogo() {
        // Tamaño preferido para que se vea bien en las ventanas
        setPreferredSize(new Dimension(400, 160));
        setBackground(new Color(18, 18, 18)); // Mismo fondo que la app
        setOpaque(false); // Para que se integre con el fondo de la ventana padre
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Convertimos a Graphics2D para tener mejores herramientas de dibujo
        Graphics2D g2 = (Graphics2D) g;
        
        // Activamos el "Antialiasing" para que los bordes se vean suaves (HD)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int centerX = width / 2;
        int startY = 20;

        // --- 1. DIBUJAR EL ESCUDO ---
        int shieldW = 80;
        int shieldH = 100;
        int shieldX = centerX - shieldW / 2;

        // Creamos la forma del escudo con curvas
        Path2D shield = new Path2D.Double();
        shield.moveTo(shieldX, startY);
        shield.lineTo(shieldX + shieldW, startY); // Parte superior recta
        shield.lineTo(shieldX + shieldW, startY + shieldH * 0.6); // Lateral derecho
        // Curva inferior hasta la punta
        shield.quadTo(centerX, startY + shieldH * 1.3, shieldX, startY + shieldH * 0.6);
        shield.closePath();

        // Relleno con degradado azul
        GradientPaint gradiente = new GradientPaint(
                centerX, startY, new Color(52, 152, 219), // Azul claro arriba
                centerX, startY + shieldH, new Color(41, 128, 185)); // Azul oscuro abajo
        g2.setPaint(gradiente);
        g2.fill(shield);

        // Borde blanco del escudo
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3)); // Grosor del borde
        g2.draw(shield);

        // --- 2. DIBUJAR ICONO DE BALÓN DENTRO ---
        int ballSize = 40;
        int ballX = centerX - ballSize / 2;
        int ballY = startY + 25;
        
        g2.setColor(Color.WHITE);
        g2.fillOval(ballX, ballY, ballSize, ballSize); // Fondo blanco balón
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(ballX, ballY, ballSize, ballSize); // Borde negro balón
        
        // Dibujo simple de hexágonos internos
        g2.drawLine(centerX - 5, ballY + 10, centerX + 5, ballY + 10);
        g2.drawLine(centerX - 5, ballY + 10, centerX - 12, ballY + 22);
        g2.drawLine(centerX + 5, ballY + 10, centerX + 12, ballY + 22);
        g2.drawLine(centerX - 12, ballY + 22, centerX, ballY + 32);
        g2.drawLine(centerX + 12, ballY + 22, centerX, ballY + 32);

        // --- 3. DIBUJAR EL TEXTO (TÍTULO) ---
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
        String titulo = "LALIGA DRAFT";
        // Calculamos el ancho del texto para centrarlo perfectamente
        FontMetrics fm = g2.getFontMetrics();
        int textX = centerX - fm.stringWidth(titulo) / 2;
        g2.drawString(titulo, textX, startY + shieldH + 35);

        // --- 4. DIBUJAR EL SUBTÍTULO ---
        g2.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        g2.setColor(new Color(189, 195, 199)); // Gris claro
        String subtitulo = "Fantasy Manager Edition 2025";
        fm = g2.getFontMetrics();
        textX = centerX - fm.stringWidth(subtitulo) / 2;
        g2.drawString(subtitulo, textX, startY + shieldH + 60);
    }
}