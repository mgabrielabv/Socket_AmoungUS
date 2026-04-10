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

    // Mapa de jugadores conectados
    private Map<String, JugadorRemoto> jugadores = new ConcurrentHashMap<>();

    public VentanaJuego(String nombre, String color, String host, int puerto) {
        this.miNombre = nombre;
        this.miColor = color;

        setTitle(" Among Us - " + nombre + " (" + color + ")");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        setLayout(new BorderLayout());

        // Canvas de juego
        canvas = new CanvasJuego();
        canvas.setBackground(new Color(20, 20, 30));
        add(canvas, BorderLayout.CENTER);

        // Panel lateral
        JPanel panelLateral = new JPanel(new BorderLayout());
        panelLateral.setPreferredSize(new Dimension(250, 700));
        panelLateral.setBackground(new Color(40, 40, 50));

        // Título lateral
        JLabel lblTitulo = new JLabel("📡 CONSOLA DE RED", SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        panelLateral.add(lblTitulo, BorderLayout.NORTH);

        // Área de log
        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setBackground(new Color(20, 20, 30));
        areaLog.setForeground(new Color(0, 255, 0));
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelLateral.add(scrollLog, BorderLayout.CENTER);

        // Panel inferior con estado
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(new Color(40, 40, 50));
        lblEstado = new JLabel(" Conectando...");
        lblEstado.setForeground(Color.YELLOW);
        lblEstado.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelInferior.add(lblEstado, BorderLayout.CENTER);

        // Botón desconectar
        JButton btnDesconectar = new JButton(" Desconectar");
        btnDesconectar.setBackground(new Color(200, 50, 50));
        btnDesconectar.setForeground(Color.WHITE);
        btnDesconectar.setFocusPainted(false);
        btnDesconectar.addActionListener(e -> desconectar());
        panelInferior.add(btnDesconectar, BorderLayout.SOUTH);

        panelLateral.add(panelInferior, BorderLayout.SOUTH);

        add(panelLateral, BorderLayout.EAST);

        // Conectar al servidor
        conectarAlServidor(host, puerto);

        // Iniciar controlador
        controlador = new ControladorJuego(this, canvas);

        // Configurar teclas
        configurarTeclas();

        // Al cerrar ventana
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
            // Enviar INIT
            socket.enviar("INIT," + miColor);
            miId = String.valueOf(System.currentTimeMillis()); // ID temporal
            jugadores.put(miId, new JugadorRemoto(miId, miColor, 400, 300));

            lblEstado.setText(" Conectado - " + host + ":" + puerto);
            lblEstado.setForeground(Color.GREEN);
            agregarLog(" Conectado al servidor");
            agregarLog(" Jugador: " + miNombre + " (" + miColor + ")");
        } else {
            lblEstado.setText(" Error de conexión");
            lblEstado.setForeground(Color.RED);
            agregarLog(" No se pudo conectar al servidor");
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

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // Opcional: apuntar con el mouse
            }
        });
    }

    public void enviarMovimiento(int x, int y) {
        if (socket != null && socket.estaConectado()) {
            socket.enviar("MOVE," + x + "," + y);

            // Actualizar posición local
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
                String color = partes[1];

                // Verificar si es nuestro color
                if (color.equals(miColor)) {
                    // Es nuestro jugador - usar miId
                    jugadores.put(miId, new JugadorRemoto(miId, color, 400, 300));
                    canvas.agregarJugador(miId, color, 400, 300);
                    agregarLog("Jugador creado: " + color);
                } else {
                    // Es otro jugador
                    String id = "J" + System.currentTimeMillis();
                    jugadores.put(id, new JugadorRemoto(id, color, 400, 300));
                    canvas.agregarJugador(id, color, 400, 300);
                    agregarLog(" Nuevo jugador: " + color);
                }

            } else if (comando.equals("MOVE")) {
                int x = Integer.parseInt(partes[1]);
                int y = Integer.parseInt(partes[2]);

                // Actualizar posición de otros jugadores
                for (Map.Entry<String, JugadorRemoto> entry : jugadores.entrySet()) {
                    if (!entry.getKey().equals(miId)) {
                        JugadorRemoto j = entry.getValue();
                        j.x = x;
                        j.y = y;
                        canvas.actualizarPosicion(entry.getKey(), x, y);
                        agregarLog(" " + j.color + " se movió a (" + x + "," + y + ")");
                        break;
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
        dispose();
    }

    public Map<String, JugadorRemoto> getJugadores() {
        return jugadores;
    }

    public String getMiId() {
        return miId;
    }

    // Clase interna para jugadores remotos
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