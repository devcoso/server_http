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
            // Leer el contenido del archivo
            byte[] content = Files.readAllBytes(Paths.get(index.getPath()));
            //Convertir el contenido a String

            // Retornar el contenido del archivo con tablas MIME
            String MIMEType = MIME.typeByExtension(index.getName().substring(index.getName().lastIndexOf(".") + 1));
            return "HTTP/1.1 200 OK\n" +
                "Content-Type:"+ MIMEType + "\n" +
                "Content-Length: " + index.length() + "\n" +
                "\n" +
                new String(content);
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return getNotFound();
        }
    }

    public static String getCreateFileResponse(String filepath, String body, boolean isPUT) {
        //Crear archivo
        File file = new File(filepath);
        file.getParentFile().mkdirs();
        
        try {
            if (file.createNewFile() || (file.exists() && isPUT)) {
                // Decodificar el body de base64 a String
                byte[] decodedBytes = java.util.Base64.getDecoder().decode(body);
                // Escribir el contenido en el archivo
                Files.write(Paths.get(file.getPath()), decodedBytes);
                return "HTTP/1.1 201 Created\n" +
                    "Content-Type: text/html\n" +
                    "Content-Length: 7\n" +
                    "\n" +
                    "Created";
            } else {
                return "HTTP/1.1 409 Conflict\n" +
                    "Content-Type: text/html\n" +
                    "Content-Length: 8\n" +
                    "\n" +
                    "Conflict";
            }
        } catch (IOException e) {
            System.err.println("Error al crear el archivo: " + e.getMessage());
            return getInternalServerError();
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

    public static String getBadRequest(){
        return "HTTP/1.1 400 Bad Request\n" +
                "Content-Type: text/html\n" +
                "Content-Length: 11\n" +
                "\n" +
                "Bad Request";
    }

    public static String getInternalServerError(){
        return "HTTP/1.1 500 Internal Server Error\n" +
                "Content-Type: text/html\n" +
                "Content-Length: 21\n" +
                "\n" +
                "Internal Server Error";
    }

}
