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
                    String color = partes[1];
                    String id = "J" + this.getId();
                    this.jugador = new Jugador(id, color);

                    enviar("INIT," + id + "," + color + ",400,300");

                    ServidorSocket.broadcast("INIT," + id + "," + color + ",400,300", this);

                    for (String jugadorExistente : ServidorSocket.getJugadoresExistentes(this)) {
                        enviar("JUGADOR_EXISTENTE," + jugadorExistente);
                    }

                } else if (comando.equals("MOVE") && jugador != null) {
                    int x = Integer.parseInt(partes[1]);
                    int y = Integer.parseInt(partes[2]);
                    jugador.x = x;
                    jugador.y = y;

                    ServidorSocket.broadcast("MOVE," + jugador.id + "," + x + "," + y, this);
                }
            }
        } catch (IOException e) {
            System.out.println("Desconexion detectada: " + (jugador != null ? jugador.id : "desconocido"));
        } finally {
            cerrarTodo();
        }
    }

    public void enviar(String mensaje) {
        out.println(mensaje);
    }

    public Jugador getJugador() {
        return jugador;
    }

    private void cerrarTodo() {
        try {
            ServidorSocket.clientes.remove(this);
            if (jugador != null) {
                ServidorSocket.broadcast("DESCONECTAR," + jugador.id, this);
            }
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}