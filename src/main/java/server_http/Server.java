package server_http;

import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    ServerSocket server;
    protected ExecutorService pool = Executors.newFixedThreadPool(1000);
    public static final int PORT = 8080;
    public static final String HOST = "localhost";

    class ServerThread implements Runnable {

        protected Socket client;

        public ServerThread(Socket client) {
            this.client = client;
        }

        public void run() {
            DataInputStream dis = null;
            DataOutputStream dos = null;
            try {
                dis = new DataInputStream(client.getInputStream());
                dos = new DataOutputStream(client.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                byte [] buffer = new byte[65536];
                int size = dis.read(buffer);
                String request = new String(buffer, 0, size);
                HTTPHandler handler = new HTTPHandler(request);
                handler.showFormatedRequest();
                String response = handler.getResponse();
                dos.write(response.getBytes());
                dos.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public Server() throws Exception {
        try {
            server = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket client = server.accept();
                System.out.println("Client connected: " + client.getInetAddress().getHostAddress());
                pool.execute(new ServerThread(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main( String[] args ) throws Exception
    {
        new Server();
    }
}
