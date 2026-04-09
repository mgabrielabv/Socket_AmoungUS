package com.MariaBermudez.Network;
import com.MariaBermudez.Models.Jugador;
import java.io.*;
import java.net.Socket;

public class ManejadorCliente extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Jugador jugador;

    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String entrada;
            while ((entrada = in.readLine()) != null) {
                String[] partes = entrada.split(",");
                String comando = partes[0];

                if (comando.equals("INIT")) {
                    this.jugador = new Jugador(String.valueOf(this.getId()), partes[1]);
                } else if (comando.equals("MOVE") && jugador != null) {
                    jugador.x = Integer.parseInt(partes[1]);
                    jugador.y = Integer.parseInt(partes[2]);
                }

                ServidorSocket.broadcast(entrada, this);
            }
        } catch (IOException e) {
            System.out.println("Desconexión detectada");
        } finally {
            cerrarTodo();
        }
    }

    public void enviar(String mensaje) {
        out.println(mensaje);
    }

    private void cerrarTodo() {
        try {
            ServidorSocket.clientes.remove(this);
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}