package server_http;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Responses {

    public static String getFileResponse(String filepath) {
        //Obtener archivo index
        File index = new File(filepath);
        if (!index.exists()) {
            return getNotFound();
        }

        try {
            // Leer todo el contenido del archivo como un String
            String content = new String(Files.readAllBytes(Paths.get(index.getPath())));
            return "HTTP/1.1 200 OK\n" +
                "Content-Type: text/html\n" +
                "Content-Length: " + index.length() + "\n" +
                "\n" +
                content;
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return getNotFound();
        }
    }

    //Errors

    public static String getNotFound(){
        return "HTTP/1.1 404 Not Found\n" +
                "Content-Type: text/html\n" +
                "Content-Length: 9\n" +
                "\n" +
                "Not Found";
    }

    public static String getMethodNotFound(){
        return "HTTP/1.1 405 Method Not Allowed\n" +
                "Content-Type: text/html\n" +
                "Content-Length: 15\n" +
                "\n" +
                "Method Not Allowed";
    }

}
