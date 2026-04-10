package com.MariaBermudez.Cliente.Red;

import java.io.*;
import java.net.Socket;

public class ClienteSocket {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread escuchador;
    private ReceptorMensajes receptor;
    private boolean conectado = false;

    public ClienteSocket(String host, int puerto, ReceptorMensajes receptor) {
        this.receptor = receptor;
        try {
            socket = new Socket(host, puerto);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            conectado = true;
            iniciarEscucha();
            System.out.println(" Conectado al servidor " + host + ":" + puerto);
        } catch (IOException e) {
            System.err.println(" Error conectando al servidor: " + e.getMessage());
            conectado = false;
        }
    }

    private void iniciarEscucha() {
        escuchador = new Thread(() -> {
            try {
                String mensaje;
                while ((mensaje = in.readLine()) != null) {
                    System.out.println(" Recibido: " + mensaje);
                    receptor.procesarMensaje(mensaje);
                }
            } catch (IOException e) {
                System.out.println(" Conexión cerrada");
            } finally {
                conectado = false;
            }
        });
        escuchador.setDaemon(true);
        escuchador.start();
    }

    public void enviar(String mensaje) {
        if (out != null && conectado) {
            out.println(mensaje);
            System.out.println(" Enviado: " + mensaje);
        }
    }

    public boolean estaConectado() {
        return conectado && socket != null && !socket.isClosed();
    }

    public void cerrar() {
        try {
            conectado = false;
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Interfaz para recibir mensajes
    public interface ReceptorMensajes {
        void procesarMensaje(String mensaje);
    }
}