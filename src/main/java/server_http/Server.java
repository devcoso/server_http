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
            try (
                InputStream input = client.getInputStream();
                DataOutputStream output = new DataOutputStream(client.getOutputStream());
            ) {
                while (true) {
                    HTTPHandler handler = HTTPHandler.readCompleteRequest(input);
                    handler.showFormatedRequest();
                    byte[] response = handler.getResponse();
                    output.write(response);
                }
            } catch (SocketTimeoutException e) {
                System.out.println("Client disconnected: " + client.getInetAddress().getHostAddress());
            } catch (Exception e) {
                e.printStackTrace();
            } 
            finally {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                client.getKeepAlive();
                client.setSoTimeout(10000);
                pool.execute(new ServerThread(client));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main( String[] args ) throws Exception
    {
        new Server();
    }
}
