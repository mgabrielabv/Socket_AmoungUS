package com.MariaBermudez.Cliente.Vista;

import com.MariaBermudez.Cliente.Controlador.ControladorJuego;
import com.MariaBermudez.Cliente.Red.ClienteSocket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VentanaJuego extends JFrame implements ClienteSocket.ReceptorMensajes {
    private CanvasJuego canvas;
    private JTextArea areaLog;
    private JLabel lblEstado;
    private ControladorJuego controlador;
    private ClienteSocket socket;
    private String miId;
    private String miColor;
    private String miNombre;
    private String miPassword;

    private Map<String, JugadorRemoto> jugadores = new ConcurrentHashMap<>();

    public VentanaJuego(String nombre, String password, String color, String host, int puerto) {
        this.miNombre = nombre;
        this.miPassword = password;
        this.miColor = color;

        setTitle("Among Us - " + nombre + " (" + color + ")");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        canvas = new CanvasJuego();
        canvas.setBackground(new Color(20, 20, 30));
        add(canvas, BorderLayout.CENTER);

        JPanel panelLateral = new JPanel(new BorderLayout());
        panelLateral.setPreferredSize(new Dimension(250, 700));
        panelLateral.setBackground(new Color(40, 40, 50));

        JLabel lblTitulo = new JLabel("CONSOLA DE RED", SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        panelLateral.add(lblTitulo, BorderLayout.NORTH);

        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setBackground(new Color(20, 20, 30));
        areaLog.setForeground(new Color(0, 255, 0));
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelLateral.add(scrollLog, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(new Color(40, 40, 50));
        lblEstado = new JLabel("Conectando...");
        lblEstado.setForeground(Color.YELLOW);
        lblEstado.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelInferior.add(lblEstado, BorderLayout.CENTER);

        JButton btnDesconectar = new JButton("Desconectar");
        btnDesconectar.setBackground(new Color(200, 50, 50));
        btnDesconectar.setForeground(Color.WHITE);
        btnDesconectar.setFocusPainted(false);
        btnDesconectar.addActionListener(e -> desconectar());
        panelInferior.add(btnDesconectar, BorderLayout.SOUTH);

        panelLateral.add(panelInferior, BorderLayout.SOUTH);

        add(panelLateral, BorderLayout.EAST);

        conectarAlServidor(host, puerto);

        controlador = new ControladorJuego(this, canvas);

        configurarTeclas();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                desconectar();
            }
        });
    }

    private void conectarAlServidor(String host, int puerto) {
        socket = new ClienteSocket(host, puerto, this);

        if (socket.estaConectado()) {

            miId = "TEMP_" + System.currentTimeMillis();

            socket.enviar("AUTH," + miNombre + "," + miPassword + "," + miColor);

            lblEstado.setText("[OK] Conectado - " + host + ":" + puerto);
            lblEstado.setForeground(Color.GREEN);
            agregarLog("[OK] Conectado al servidor");
            agregarLog("[INFO] Autenticando usuario...");
        } else {
            lblEstado.setText("[ERROR] Error de conexion");
            lblEstado.setForeground(Color.RED);
            agregarLog("[ERROR] No se pudo conectar al servidor");
        }
    }

    private void configurarTeclas() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                controlador.teclaPresionada(e.getKeyCode());
            } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                controlador.teclaLiberada(e.getKeyCode());
            }
            return false;
        });
    }

    public void enviarMovimiento(int x, int y) {
        if (socket != null && socket.estaConectado()) {
            socket.enviar("MOVE," + x + "," + y);

            JugadorRemoto yo = jugadores.get(miId);
            if (yo != null) {
                yo.x = x;
                yo.y = y;
            }
            canvas.actualizarPosicion(miId, x, y);
        }
    }

    @Override
    public void procesarMensaje(String mensaje) {
        String[] partes = mensaje.split(",");
        String comando = partes[0];

        SwingUtilities.invokeLater(() -> {
            if (comando.equals("INIT")) {

                if (partes.length >= 5) {
                    String id = partes[1];
                    String color = partes[2];
                    int x = Integer.parseInt(partes[3]);
                    int y = Integer.parseInt(partes[4]);

                    if (color.equals(miColor) && miId.startsWith("TEMP_")) {

                        setMiId(id);
                        agregarLog("[OK] ID asignado: " + id);

                        jugadores.put(id, new JugadorRemoto(id, color, x, y));
                        canvas.agregarJugador(id, color, x, y);
                        agregarLog("[OK] Mi jugador registrado: " + color);
                    } else if (!id.equals(miId)) {

                        jugadores.put(id, new JugadorRemoto(id, color, x, y));
                        canvas.agregarJugador(id, color, x, y);
                        agregarLog("[INFO] Jugador conectado: " + color + " (ID: " + id + ")");
                    }
                }

            } else if (comando.equals("AUTH_OK")) {
                if (partes.length >= 3) {
                    miNombre = partes[1];
                    miColor = partes[2];
                }
                agregarLog("[OK] Autenticacion correcta para " + miNombre);
                socket.enviar("INIT," + miColor);

            } else if (comando.equals("AUTH_ERROR")) {
                String detalle = partes.length > 1 ? partes[1] : "DESCONOCIDO";
                lblEstado.setText("[ERROR] Auth fallida");
                lblEstado.setForeground(Color.RED);
                agregarLog("[ERROR] Autenticacion fallida: " + detalle);
                JOptionPane.showMessageDialog(this, "No se pudo autenticar: " + detalle, "Error", JOptionPane.ERROR_MESSAGE);
                desconectar();

            } else if (comando.equals("AUTH_REQUIRED")) {
                lblEstado.setText("[ERROR] Debes autenticarte");
                lblEstado.setForeground(Color.RED);
                agregarLog("[ERROR] El servidor requiere autenticacion");

            } else if (comando.equals("JUGADOR_EXISTENTE")) {

                if (partes.length >= 5) {
                    String id = partes[1];
                    String color = partes[2];
                    int x = Integer.parseInt(partes[3]);
                    int y = Integer.parseInt(partes[4]);

                    if (!id.equals(miId) && !jugadores.containsKey(id)) {
                        jugadores.put(id, new JugadorRemoto(id, color, x, y));
                        canvas.agregarJugador(id, color, x, y);
                        agregarLog("[INFO] Jugador existente: " + color + " (ID: " + id + ")");
                    }
                }

            } else if (comando.equals("MOVE")) {
                // Formato: MOVE,id,x,y
                if (partes.length >= 4) {
                    String id = partes[1];
                    int x = Integer.parseInt(partes[2]);
                    int y = Integer.parseInt(partes[3]);

                    if (!id.equals(miId)) {
                        JugadorRemoto j = jugadores.get(id);
                        if (j != null) {
                            j.x = x;
                            j.y = y;
                            canvas.actualizarPosicion(id, x, y);

                            if (Math.random() < 0.1) {
                                agregarLog("[MOVE] " + j.color + " se movio a (" + x + "," + y + ")");
                            }
                        }
                    }
                }

            } else if (comando.equals("DESCONECTAR")) {
                if (partes.length >= 2) {
                    String id = partes[1];
                    JugadorRemoto j = jugadores.remove(id);
                    if (j != null) {
                        canvas.removerJugador(id);
                        agregarLog("[INFO] Jugador desconectado: " + j.color);
                    }
                }
            }

            canvas.repaint();
        });
    }

    private void agregarLog(String mensaje) {
        areaLog.append(mensaje + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    private void desconectar() {
        if (socket != null) {
            socket.cerrar();
        }
        if (controlador != null) {
            controlador.detener();
        }
        dispose();
    }

    public void setMiId(String id) {
        this.miId = id;
    }


    public String getMiId() {
        return miId;
    }

    public static class JugadorRemoto {
        public String id;
        public String color;
        public int x, y;

        public JugadorRemoto(String id, String color, int x, int y) {
            this.id = id;
            this.color = color;
            this.x = x;
            this.y = y;
        }
    }
}