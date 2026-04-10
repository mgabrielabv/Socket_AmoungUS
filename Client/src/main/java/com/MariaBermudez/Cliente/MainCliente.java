package com.MariaBermudez.Cliente;

import com.MariaBermudez.Cliente.Vista.VentanaLogin;

import javax.swing.*;

public class MainCliente {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaLogin login = new VentanaLogin();
            login.setVisible(true);
        });
    }
}