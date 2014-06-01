/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package serverapplication;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Victor
 */
public class Protocolo {
    private String nick;
    private ClientConn conn;
    private Socket client;
 
    /* a hash table from user nicks to the corresponding connections */
    private static Hashtable<String, ClientConn> nicks = 
        new Hashtable<String, ClientConn>();
 
    private static final String msg_OK = "MENSAJE ENVIADO";
    private static final String msg_FILE_OK = "ARCHIVO ENVIADO";
    private static final String msg_NICK_IN_USE = "NICK IN USE";
    private static final String msg_SPECIFY_NICK = "SPECIFY NICK";
    private static final String msg_INVALID = "COMANDO INVALIDO";
    private static final String msg_SEND_FAILED = "NO SE ENCONTRÓ AL USUARIO CON EL NICK INGRESADO.";
 
    /**
     * Adds a nick to the hash table 
     * returns false if the nick is already in the table, true otherwise
     */
    private static boolean add_nick(String nick, ClientConn c) {
        if (nicks.containsKey(nick)) {
            return false;
        } else {
            nicks.put(nick, c);
            return true;
        }
    }
 
    public Protocolo(ClientConn c, Socket client) {
        this.client = client;
        nick = null;
        conn = c;
    }
 
    private void log(String msg) {
        System.err.println(msg);
    }
 
    public boolean isAuthenticated() {
        return ! (nick == null);
    }
 
    /**
     * Implements the authentication protocol.
     * This consists of checking that the message starts with the NICK command
     * and that the nick following it is not already in use.
     * returns: 
     *  msg_OK if authenticated
     *  msg_NICK_IN_USE if the specified nick is already in use
     *  msg_SPECIFY_NICK if the message does not start with the NICK command 
     */
    private String authenticate(String msg) {
        if(msg.startsWith("NICK")) {
            String tryNick = msg.substring(5);
            if(add_nick(tryNick, this.conn)) {
                log("Nick " + tryNick + " ha entrado a la sala de chat.");
                this.nick = tryNick;
                return msg_OK;
            } else {
                return msg_NICK_IN_USE;
            }
        } else {
            return msg_SPECIFY_NICK;
        }
    }
 
    //Envia el mensaje al cliente destinatario.
    private boolean sendMsg(String recipient, String msg) {
        if (nicks.containsKey(recipient)) {
            ClientConn c = nicks.get(recipient);
            c.sendMsg(nick + " escribió: " + msg);
            return true;
        } else {
            return false;
        }
    }
    
    //Envía el archivo al cliente destinatario.
    private boolean sendFile(String receptor, String nombreArchivo){
        if(nicks.containsKey(receptor)){
            ClientConn c = nicks.get(receptor);
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            OutputStream os = null;
            File archivo = new File(nombreArchivo);
            byte [] mybytearray  = new byte [(int)archivo.length()];
            try{                
                fis = new FileInputStream(archivo);
                bis = new BufferedInputStream(fis);
                bis.read(mybytearray,0,mybytearray.length);
                os = client.getOutputStream();
                os.write(mybytearray,0,mybytearray.length);
                os.flush();
                c.sendMsg(nick + " te ha enviado un archivo: " + nombreArchivo);
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Protocolo.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } catch (IOException ex) {
                Logger.getLogger(Protocolo.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;
        }
        else return false;
    }
 
    //Procesa el mensaje escrito por el cliente usando este protocolo.
    public String process(String msg) {
        if (!isAuthenticated()) 
            return authenticate(msg);
 
        String[] msg_parts = msg.split(" ", 3);
        String comando = msg_parts[0];
        //Si el comando es un mensaje, procesarla y enviar dicho mensaje al cliente señalado por el cliente que envió el mensaje.
        if(comando.equals("MENSAJE")) {
            if(msg_parts.length < 3)
                return msg_INVALID;
            String contacto_a_enviar = msg_parts[1];
            String mensaje = msg_parts[2];
            if(sendMsg(contacto_a_enviar, mensaje))
                return msg_OK;
            else return msg_SEND_FAILED;
        }
        //Si el comando trata de procesar un archivo, envía éste al destinatario
        else if(comando.equals("ARCHIVO")){
            if(msg_parts.length < 3)
                return msg_INVALID;
            String contacto_a_enviar = msg_parts[1];
            String nombre_archivo = msg_parts[2];
            if(sendFile(contacto_a_enviar, nombre_archivo))
                return msg_FILE_OK;
            else return msg_SEND_FAILED;
        }
        else {
            return msg_INVALID;
        }
    }
}
