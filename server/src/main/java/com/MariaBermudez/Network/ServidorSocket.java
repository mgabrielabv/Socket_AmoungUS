package com.MariaBermudez.Network;

import com.MariaBermudez.DataBase.PartidaDAO;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServidorSocket {
    private int puerto;
    public static ArrayList<ManejadorCliente> clientes = new ArrayList<>();
    public static int partidaActualId = -1;

    public ServidorSocket(int puerto) {
        this.puerto = puerto;
    }

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor escuchando en puerto: " + puerto);

            PartidaDAO partidaDAO = new PartidaDAO();
            partidaActualId = partidaDAO.crearPartida();
            System.out.println("Partida creada con ID: " + partidaActualId);

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


    public static List<String> getJugadoresExistentes(ManejadorCliente excluir) {
        List<String> jugadores = new ArrayList<>();
        for (ManejadorCliente c : clientes) {
            if (c != excluir && c.getJugador() != null) {
                jugadores.add(c.getJugador().id + "," + c.getJugador().color + "," + c.getJugador().x + "," + c.getJugador().y);
            }
        }
        return jugadores;
    }
}