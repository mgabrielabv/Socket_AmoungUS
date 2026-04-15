package com.MariaBermudez.Cliente.Vista;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CanvasJuego extends JPanel {
    private Map<String, JugadorVisual> jugadores = new ConcurrentHashMap<>();
    private Image fondo;
    private boolean fondoCargado = false;

    private static final String URL_FONDO =
            "https://i.pinimg.com/originals/ec/01/62/ec01621b4abc17edfba4689f691be582.jpg";

    public CanvasJuego() {
        setPreferredSize(new Dimension(800, 600));
        cargarFondo();
    }

    private void cargarFondo() {
        try {
            URL url = new URL(URL_FONDO);
            ImageIcon icono = new ImageIcon(url);
            fondo = icono.getImage();
            fondoCargado = true;
            System.out.println("[OK] Fondo cargado correctamente");
        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo cargar el fondo: " + e.getMessage());
            fondo = crearFondoRespaldo();
            fondoCargado = true;
        }
    }

    private Image crearFondoRespaldo() {

        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();


        g2d.setColor(new Color(10, 10, 20));
        g2d.fillRect(0, 0, 800, 600);


        g2d.setColor(Color.WHITE);
        for (int i = 0; i < 100; i++) {
            int x = (int)(Math.random() * 800);
            int y = (int)(Math.random() * 600);
            g2d.fillOval(x, y, 2, 2);
        }


        g2d.setColor(new Color(60, 60, 80));
        g2d.fillRect(0, 0, 800, 10);
        g2d.fillRect(0, 590, 800, 10);
        g2d.fillRect(0, 0, 10, 600);
        g2d.fillRect(790, 0, 10, 600);


        g2d.setColor(new Color(80, 80, 100));
        g2d.fillRect(200, 150, 80, 80);
        g2d.fillRect(500, 350, 80, 80);
        g2d.fillRect(300, 400, 60, 60);
        g2d.fillRect(600, 100, 50, 100);
        g2d.fillRect(100, 450, 70, 70);

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

    public void removerJugador(String id) {
        jugadores.remove(id);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        if (fondo != null) {
            g2d.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        }


        for (JugadorVisual j : jugadores.values()) {
            dibujarJugador(g2d, j);
        }


        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(5, 5, 200, 50);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Jugadores: " + jugadores.size(), 15, 25);
        g2d.drawString("WASD para moverte", 15, 45);
    }

    private void dibujarJugador(Graphics2D g2d, JugadorVisual jugador) {
        Color colorJugador = obtenerColor(jugador.color);


        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillOval(jugador.x - 13, jugador.y - 11, 36, 36);


        g2d.setColor(colorJugador);
        g2d.fillOval(jugador.x - 15, jugador.y - 15, 30, 30);


        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(jugador.x - 15, jugador.y - 15, 30, 30);


        g2d.setColor(colorJugador.darker());
        g2d.fillRoundRect(jugador.x - 25, jugador.y - 10, 12, 20, 5, 5);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(jugador.x - 25, jugador.y - 10, 12, 20, 5, 5);


        g2d.setColor(new Color(150, 200, 255, 200));
        g2d.fillOval(jugador.x - 5, jugador.y - 10, 15, 12);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(jugador.x - 5, jugador.y - 10, 15, 12);


        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.fillOval(jugador.x - 2, jugador.y - 8, 5, 5);


        g2d.setColor(colorJugador.darker());
        g2d.fillOval(jugador.x - 10, jugador.y + 12, 8, 10);
        g2d.fillOval(jugador.x + 2, jugador.y + 12, 8, 10);


        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        FontMetrics fm = g2d.getFontMetrics();
        int anchoTexto = fm.stringWidth(jugador.id);


        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(jugador.x - anchoTexto/2 - 3, jugador.y - 32, anchoTexto + 6, 14);


        g2d.setColor(Color.WHITE);
        g2d.drawString(jugador.id, jugador.x - anchoTexto/2, jugador.y - 22);
    }

    private Color obtenerColor(String colorStr) {
        if (colorStr == null) return Color.GRAY;

        switch (colorStr.toLowerCase()) {
            case "rojo": return new Color(215, 30, 30);
            case "azul": return new Color(30, 80, 215);
            case "verde": return new Color(30, 150, 50);
            case "amarillo": return new Color(240, 240, 30);
            case "morado": return new Color(120, 30, 180);
            case "naranja": return new Color(240, 120, 30);
            case "negro": return new Color(40, 40, 50);
            case "blanco": return new Color(230, 230, 240);
            case "cyan": return new Color(30, 200, 200);
            case "rosa": return new Color(230, 100, 180);
            case "marron": return new Color(120, 70, 30);
            case "gris": return new Color(130, 130, 140);
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