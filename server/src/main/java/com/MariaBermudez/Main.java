package com.MariaBermudez;

import com.MariaBermudez.Network.ServidorSocket;

public class Main{
    public static void main(String[] args) {
        ServidorSocket servidor = new ServidorSocket(1234);
        servidor.iniciar();
    }
}
