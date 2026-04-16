package com.MariaBermudez.Network;
import com.MariaBermudez.DataBase.JugadorDAO;
import com.MariaBermudez.DataBase.PartidaDAO;
import com.MariaBermudez.Models.Jugador;
import java.io.*;
import java.net.Socket;

public class ManejadorCliente extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Jugador jugador;
    private final JugadorDAO jugadorDAO = new JugadorDAO();
    private final PartidaDAO partidaDAO = new PartidaDAO();
    private boolean autenticado = false;
    private String colorAutenticado;
    private String nombreAutenticado;

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

                if (comando.equals("AUTH")) {
                    if (partes.length < 4) {
                        enviar("AUTH_ERROR,FORMATO_INVALIDO");
                        continue;
                    }

                    String nombre = partes[1].trim();
                    String contrasena = partes[2].trim();
                    String color = partes[3].trim();

                    if (nombre.isEmpty() || contrasena.isEmpty() || color.isEmpty()) {
                        enviar("AUTH_ERROR,DATOS_INVALIDOS");
                        continue;
                    }

                    com.MariaBermudez.DataBase.Jugador jugadorBD = jugadorDAO.iniciarSesion(nombre, contrasena);
                    if (jugadorBD != null) {
                        autenticado = true;
                        colorAutenticado = jugadorBD.getColor();
                        nombreAutenticado = nombre;
                        enviar("AUTH_OK," + nombre + "," + colorAutenticado);
                    } else {
                        boolean registrado = jugadorDAO.registrarJugador(nombre, color, contrasena);
                        if (registrado) {
                            autenticado = true;
                            colorAutenticado = color;
                            nombreAutenticado = nombre;
                            enviar("AUTH_OK," + nombre + "," + colorAutenticado);
                        } else {
                            enviar("AUTH_ERROR,CREDENCIALES_O_NOMBRE_EN_USO");
                        }
                    }

                } else if (comando.equals("INIT")) {
                    if (!autenticado) {
                        enviar("AUTH_REQUIRED");
                        continue;
                    }

                    if (partes.length < 2 && colorAutenticado == null) {
                        enviar("INIT_ERROR,FORMATO_INVALIDO");
                        continue;
                    }

                    String color = colorAutenticado != null ? colorAutenticado : partes[1];
                    String id = "J" + this.getId();
                    this.jugador = new Jugador(id, color);

                    enviar("INIT," + id + "," + color + ",400,300");

                    ServidorSocket.broadcast("INIT," + id + "," + color + ",400,300", this);

                    for (String jugadorExistente : ServidorSocket.getJugadoresExistentes(this)) {
                        enviar("JUGADOR_EXISTENTE," + jugadorExistente);
                    }

                    if (ServidorSocket.partidaActualId != -1 && nombreAutenticado != null) {
                        partidaDAO.agregarJugador(ServidorSocket.partidaActualId, nombreAutenticado);
                    }

                } else if (comando.equals("MOVE") && jugador != null) {
                    if (!autenticado) {
                        enviar("AUTH_REQUIRED");
                        continue;
                    }

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