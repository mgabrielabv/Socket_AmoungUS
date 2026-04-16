package com.MariaBermudez.Cliente.Vista;

import javax.swing.*;
import java.awt.*;

public class VentanaLogin extends JFrame {
    private JTextField txtNombre;
    private JPasswordField txtPassword;
    private JComboBox<String> comboColor;
    private JTextField txtHost;
    private JTextField txtPuerto;
    private JButton btnConectar;

    public VentanaLogin() {
        setTitle(" Among Us - Login");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(30, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);

        // Título
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titulo = new JLabel(" AMONG US CLIENTE");
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        panel.add(titulo, gbc);

        // Separador
        gbc.gridy = 1;
        panel.add(new JSeparator(), gbc);

        // Host
        gbc.gridwidth = 1; gbc.gridy = 2; gbc.gridx = 0;
        JLabel lblHost = new JLabel("Servidor:");
        lblHost.setForeground(Color.LIGHT_GRAY);
        panel.add(lblHost, gbc);
        gbc.gridx = 1;
        txtHost = new JTextField("localhost", 15);
        panel.add(txtHost, gbc);

        // Puerto
        gbc.gridy = 3; gbc.gridx = 0;
        JLabel lblPuerto = new JLabel("Puerto:");
        lblPuerto.setForeground(Color.LIGHT_GRAY);
        panel.add(lblPuerto, gbc);
        gbc.gridx = 1;
        txtPuerto = new JTextField("1234", 15);
        panel.add(txtPuerto, gbc);

        // Separador
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);

        // Nombre
        gbc.gridwidth = 1; gbc.gridy = 5; gbc.gridx = 0;
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setForeground(Color.LIGHT_GRAY);
        panel.add(lblNombre, gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(15);
        panel.add(txtNombre, gbc);

        // Contraseña
        gbc.gridy = 6; gbc.gridx = 0;
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setForeground(Color.LIGHT_GRAY);
        panel.add(lblPass, gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        panel.add(txtPassword, gbc);

        // Color
        gbc.gridy = 7; gbc.gridx = 0;
        JLabel lblColor = new JLabel("Color:");
        lblColor.setForeground(Color.LIGHT_GRAY);
        panel.add(lblColor, gbc);
        gbc.gridx = 1;
        String[] colores = {" Rojo", " Azul", " Verde", " Amarillo", " Morado", " Naranja", " Negro", " Blanco"};
        comboColor = new JComboBox<>(colores);
        comboColor.setBackground(Color.WHITE);
        panel.add(comboColor, gbc);

        // Botón conectar
        gbc.gridy = 8; gbc.gridx = 0; gbc.gridwidth = 2;
        btnConectar = new JButton(" CONECTAR AL SERVIDOR");
        btnConectar.setBackground(new Color(76, 175, 80));
        btnConectar.setForeground(Color.WHITE);
        btnConectar.setFont(new Font("Arial", Font.BOLD, 14));
        btnConectar.setFocusPainted(false);
        panel.add(btnConectar, gbc);

        add(panel);

        // Acción del botón
        btnConectar.addActionListener(e -> conectar());

        // Enter en campos
        txtNombre.addActionListener(e -> conectar());
        txtPassword.addActionListener(e -> conectar());
        txtPuerto.addActionListener(e -> conectar());
    }

    private void conectar() {
        String nombre = txtNombre.getText().trim();
        String password = new String(txtPassword.getPassword());
        String colorSeleccionado = (String) comboColor.getSelectedItem();
        String host = txtHost.getText().trim();
        String puertoStr = txtPuerto.getText().trim();

        if (nombre.isEmpty() || password.isEmpty() || host.isEmpty() || puertoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, " Completa todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int puerto;
        try {
            puerto = Integer.parseInt(puertoStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, " Puerto inválido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Extraer solo el nombre del color
        String color = colorSeleccionado.split(" ")[1];

        // Abrir ventana de juego
        VentanaJuego juego = new VentanaJuego(nombre, password, color, host, puerto);
        juego.setVisible(true);
        this.dispose();
    }
}