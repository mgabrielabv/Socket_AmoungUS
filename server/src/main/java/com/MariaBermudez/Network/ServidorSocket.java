package com.MariaBermudez.Network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServidorSocket {
    private int puerto;
    public static ArrayList<ManejadorCliente> clientes = new ArrayList<>();

    public ServidorSocket(int puerto) {
        this.puerto = puerto;
    }

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor escuchando en puerto: " + puerto);

            while (true) {
                Socket socket = serverSocket.accept();
                ManejadorCliente manejador = new ManejadorCliente(socket);
                clientes.add(manejador);
                manejador.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String mensaje, ManejadorCliente remitente) {
        for (ManejadorCliente c : clientes) {
            if (c != remitente) {
                c.enviar(mensaje);
            }
        }
    }
}