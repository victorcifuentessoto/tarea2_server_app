/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package serverapplication;

/**
 *
 * @author Victor
 */

import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;

public class ServerApplication {

    private ServerSocket server1;
    private ServerSocket server2;
    private int port1 = 8090;
    private int port2 = 8091;
 
    public ServerApplication() {
        try {
            server1 = new ServerSocket(port1);
            server2 = new ServerSocket(port2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    public static void main(String[] args) {
        ServerApplication example = new ServerApplication();
        example.handleConnection();
    }
 
    public void handleConnection() {
        System.out.println("Esperando respuesta del cliente...");
        
        //Loop donde se espera respuesta del cliente.

        while (true) {
            try {
                Socket socket1 = server1.accept();
                Socket socket2 = server2.accept();
                new ConnectionHandler(socket1);
                new ConnectionHandler(socket2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
 
class ConnectionHandler implements Runnable {
    private Socket socket;
 
    public ConnectionHandler(Socket socket) {
        this.socket = socket;
 
        Thread t = new Thread(this);
        t.start();
    }
 
    public void run() {
        try
        {
            //Lee el mensaje enviado por el cliente.
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            String message = (String) ois.readObject();
            System.out.println("Mensaje recibido: " + message);
            
            //Variable para escribir el mensaje de respuesta al cliente.
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            
            //Reconocimiento de comandos especiales (protocolo)
            
            //Separando los parametros del mensaje.
            StringTokenizer token = new StringTokenizer(message);
            String comando = token.nextToken();
            String mensaje = token.nextToken();
            String IP = token.nextToken();
            String contacto_a_enviar = token.nextToken();
            
            switch (comando) {
                case "MENSAJE":
                    break;
                case "ARCHIVO":
                    break;
            }
 
            //Envía el mensaje de respuesta al cliente.
            oos.writeObject("Hi...");
 
            ois.close();
            oos.close();
            socket.close();
 
            System.out.println("Esperando respuesta del cliente...");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
}
