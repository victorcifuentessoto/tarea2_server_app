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

import java.net.*; 
import java.io.*;
 
public class ServerApplication {
    private static int port = 8091; /* Puerto a ser escuchado */
 
    public static void main (String[] args) throws IOException {
 
        ServerSocket server = null;
        try {
            server = new ServerSocket(port); /* Crea el servidor con el puerto 8091 */
        } catch (IOException e) {
            System.err.println("Puerto no encontrado: " + port);
            System.err.println(e);
            System.exit(1);
        }
 
        Socket client = null;
        while(true) {
            try {
                client = server.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.err.println(e);
                System.exit(1);
            }
            /* Thread para escuchar múltiples clientes */
            Thread t = new Thread(new ClientConn(client));
            t.start();
        }
    }
}
 
class ClientConn implements Runnable {
    private Socket client = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
 
    ClientConn(Socket client) {
        this.client = client;
        try {
            /* obtain an input stream to this client ... */
            in = new BufferedReader(new InputStreamReader(
                        client.getInputStream()));
            /* ... and an output stream to the same client */
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println(e);
        }
    }
 
    public void run() {
        String msg, response;
        Protocolo protocol = new Protocolo(this, client);
        try {
            //Espera respuesta del cliente
            while ((msg = in.readLine()) != null) {
                response = protocol.process(msg);
                if(msg.contains("UPDATE")){
                    out.println(response);
                }
                else out.println("SERVIDOR: " + response);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
 
    public void sendMsg(String msg, String contacto) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        File file = new File("Cliente_" + contacto + ".txt");
        if(file.exists() && !file.isDirectory()){
            PrintWriter writer = new PrintWriter("temp.txt", "UTF-8");
            BufferedReader reader = new BufferedReader(new FileReader("Cliente_" + contacto + ".txt"));
            String line;
            while((line = reader.readLine()) != null){
                writer.println(line);
            }
            writer.println(msg);
            reader.close();
            writer.close();
            File tempFile = new File("temp.txt");
            PrintWriter newWriterFile = new PrintWriter("Cliente_" + contacto + ".txt", "UTF-8");
            BufferedReader readerTmp = new BufferedReader(new FileReader("temp.txt"));
            while((line = readerTmp.readLine()) != null){
                newWriterFile.println(line);
            }
            newWriterFile.close();
            readerTmp.close();
            tempFile.delete();
        }
        else{
            PrintWriter writer = new PrintWriter("Cliente_" + contacto + ".txt", "UTF-8");
            writer.println(msg);
            writer.close();
        }
    }
    
    public String receiveMsg(String contacto) throws FileNotFoundException, IOException{
        File file = new File("Cliente_" + contacto + ".txt");
        if(file.exists() && !file.isDirectory()){
            BufferedReader reader = new BufferedReader(new FileReader("Cliente_" + contacto + ".txt"));
            String line;
            String mensajes_nuevos = "";
            while((line = reader.readLine()) != null){
                mensajes_nuevos = mensajes_nuevos + line + "\n";
            }
            reader.close();
            file.delete();
            return mensajes_nuevos;
        }
        else return "";
    }
}