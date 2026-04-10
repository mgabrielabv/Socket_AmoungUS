package com.MariaBermudez.Cliente.Vista;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CanvasJuego extends JPanel {
    private Map<String, JugadorVisual> jugadores = new ConcurrentHashMap<>();
    private Image fondo;

    public CanvasJuego() {
        setPreferredSize(new Dimension(800, 600));
        fondo = crearFondoNave();
    }

    private Image crearFondoNave() {
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();

        g2d.setColor(new Color(30, 30, 40));
        g2d.fillRect(0, 0, 800, 600);

        g2d.setColor(new Color(50, 50, 70));
        for (int i = 0; i < 800; i += 50) {
            g2d.drawLine(i, 0, i, 600);
        }
        for (int i = 0; i < 600; i += 50) {
            g2d.drawLine(0, i, 800, i);
        }

        g2d.setColor(new Color(80, 80, 100));
        g2d.fillRect(0, 0, 800, 10);
        g2d.fillRect(0, 590, 800, 10);
        g2d.fillRect(0, 0, 10, 600);
        g2d.fillRect(790, 0, 10, 600);

        g2d.setColor(new Color(100, 100, 120));
        g2d.fillRect(200, 150, 80, 80);
        g2d.fillRect(500, 350, 80, 80);
        g2d.fillRect(300, 400, 60, 60);

        g2d.dispose();
        return img;
    }

    public void agregarJugador(String id, String color, int x, int y) {
        jugadores.put(id, new JugadorVisual(id, color, x, y));
        repaint();
    }

    public void actualizarPosicion(String id, int x, int y) {
        JugadorVisual j = jugadores.get(id);
        if (j != null) {
            j.x = x;
            j.y = y;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);

        for (JugadorVisual j : jugadores.values()) {
            dibujarJugador(g2d, j);
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Jugadores conectados: " + jugadores.size(), 10, 20);
        g2d.drawString("Usa WASD para moverte", 10, 35);
    }

    private void dibujarJugador(Graphics2D g2d, JugadorVisual jugador) {
        Color colorJugador = obtenerColor(jugador.color);

        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillOval(jugador.x - 13, jugador.y - 13, 36, 36);

        g2d.setColor(colorJugador);
        g2d.fillOval(jugador.x - 15, jugador.y - 15, 30, 30);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(jugador.x - 15, jugador.y - 15, 30, 30);

        g2d.setColor(colorJugador.darker());
        g2d.fillRoundRect(jugador.x - 25, jugador.y - 10, 12, 20, 5, 5);

        g2d.setColor(new Color(150, 200, 255, 180));
        g2d.fillOval(jugador.x - 5, jugador.y - 10, 15, 12);

        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillOval(jugador.x - 2, jugador.y - 8, 5, 5);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics fm = g2d.getFontMetrics();
        int anchoTexto = fm.stringWidth(jugador.id);
        g2d.drawString(jugador.id, jugador.x - anchoTexto/2, jugador.y - 20);
    }

    private Color obtenerColor(String colorStr) {
        switch (colorStr.toLowerCase()) {
            case "rojo": return new Color(200, 50, 50);
            case "azul": return new Color(50, 50, 200);
            case "verde": return new Color(50, 200, 50);
            case "amarillo": return new Color(200, 200, 50);
            case "morado": return new Color(150, 50, 200);
            case "naranja": return new Color(255, 150, 50);
            case "negro": return new Color(50, 50, 50);
            case "blanco": return new Color(220, 220, 220);
            default: return Color.GRAY;
        }
    }

    private static class JugadorVisual {
        String id;
        String color;
        int x, y;

        JugadorVisual(String id, String color, int x, int y) {
            this.id = id;
            this.color = color;
            this.x = x;
            this.y = y;
        }
    }
}