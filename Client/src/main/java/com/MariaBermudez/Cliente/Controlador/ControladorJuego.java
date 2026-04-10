package com.MariaBermudez.Cliente.Controlador;

import com.MariaBermudez.Cliente.Vista.CanvasJuego;
import com.MariaBermudez.Cliente.Vista.VentanaJuego;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class ControladorJuego implements ActionListener {
    private VentanaJuego ventana;
    private CanvasJuego canvas;
    private Timer timer;

    private int posX = 400;
    private int posY = 300;
    private final int VELOCIDAD = 5;

    private Set<Integer> teclasPresionadas = new HashSet<>();

    public ControladorJuego(VentanaJuego ventana, CanvasJuego canvas) {
        this.ventana = ventana;
        this.canvas = canvas;

        // Iniciar timer para el game loop
        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }

    public void teclaPresionada(int keyCode) {
        teclasPresionadas.add(keyCode);
    }

    public void teclaLiberada(int keyCode) {
        teclasPresionadas.remove(keyCode);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        actualizarPosicion();
    }

    private void actualizarPosicion() {
        boolean movido = false;
        int nuevoX = posX;
        int nuevoY = posY;

        // Movimiento con WASD
        if (teclasPresionadas.contains(KeyEvent.VK_W) || teclasPresionadas.contains(KeyEvent.VK_UP)) {
            nuevoY -= VELOCIDAD;
            movido = true;
        }
        if (teclasPresionadas.contains(KeyEvent.VK_S) || teclasPresionadas.contains(KeyEvent.VK_DOWN)) {
            nuevoY += VELOCIDAD;
            movido = true;
        }
        if (teclasPresionadas.contains(KeyEvent.VK_A) || teclasPresionadas.contains(KeyEvent.VK_LEFT)) {
            nuevoX -= VELOCIDAD;
            movido = true;
        }
        if (teclasPresionadas.contains(KeyEvent.VK_D) || teclasPresionadas.contains(KeyEvent.VK_RIGHT)) {
            nuevoX += VELOCIDAD;
            movido = true;
        }

        // Limitar a bordes
        nuevoX = Math.max(20, Math.min(780, nuevoX));
        nuevoY = Math.max(20, Math.min(580, nuevoY));

        if (movido && (nuevoX != posX || nuevoY != posY)) {
            posX = nuevoX;
            posY = nuevoY;

            // Enviar al servidor
            ventana.enviarMovimiento(posX, posY);

            // Actualizar canvas local
            canvas.actualizarPosicion(ventana.getMiId(), posX, posY);
        }
    }

    public void detener() {
        timer.stop();
    }
}