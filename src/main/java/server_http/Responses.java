package server_http;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Responses {

    public static byte[] getFileResponse(String filepath) {
        File file = new File(filepath);
        if (!file.exists()) {
            return getNotFound();
        }

        try {
            // Leer el contenido del archivo
            byte[] content = Files.readAllBytes(Paths.get(file.getPath()));
            // Retornar el contenido del archivo con tablas MIME
            String MIMEType = MIME.typeByExtension(file.getName().substring(file.getName().lastIndexOf(".") + 1));
            
            
            String header = "HTTP/1.1 200 OK\n" +
            "Content-Type: " + MIMEType + "\n" +
            "Content-Length: " + content.length + "\n" +
            "\n";
            
            byte[] response = new byte[content.length + header.length()];

            byte[] headerBytes = header.getBytes();
            System.arraycopy(headerBytes, 0, response, 0, headerBytes.length);
            System.arraycopy(content, 0, response, headerBytes.length, content.length);

            return response;
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return getNotFound();
        }
    }

    public static byte[] getCreateFileResponse(String filepath, byte[] bytes, boolean isPUT) {
        //Crear archivo
        File file = new File(filepath);
        file.getParentFile().mkdirs();
        
        try {
            if (file.createNewFile() || (file.exists() && isPUT)) {
                // Escribir el contenido en el archivo
                Files.write(Paths.get(file.getPath()), bytes);
                String jsonresponse = "{\"message\": \"El archivo fue creado\"}";
                return ("HTTP/1.1 201 Created\n" +
                    "Content-Type: application/json\n" +
                    "Content-Length:" + jsonresponse.length() + "\n" +
                    "\r\n" +
                    jsonresponse).getBytes();
            } else {
                String jsonresponse = "{\"message\": \"El archivo ya existe\"}";
                return ("HTTP/1.1 409 Conflict\n" +
                    "Content-Type: application/json\n" +
                    "Content-Length:" + jsonresponse.length() + "\n" +
                    "\r\n" +
                    jsonresponse).getBytes();
            }
        } catch (IOException e) {
            System.err.println("Error al crear el archivo: " + e.getMessage());
            return getInternalServerError();
        }
    }


    public static byte[] getFilesResponse(String filepath) {
        //Obtener archivos
        File folder = new File(filepath);
        File[] files = folder.listFiles();
        // Hacer json de forma 
        // const files = [
        //     {
        //         name : 'archivo1.txt',
        //         size : '1.2MB',
        //     },
        //     {
        //         name : 'archivo2.pdf',
        //         size : '0.2MB',
        //     },
        // ];
        String jsonresponse = "[";
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            jsonresponse += "{";
            jsonresponse += "\"name\": \"" + file.getName() + "\",";
            jsonresponse += "\"size\": \"" + file.length() + " bytes\"";
            jsonresponse += "}";
            if (i < files.length - 1) {
                jsonresponse += ",";
            }
        }
        jsonresponse += "]";

        return ("HTTP/1.1 200 OK\n" +
            "Content-Type: application/json\n" +
            "Content-Length:" + jsonresponse.length() + "\n" +
            "\r\n" +
            jsonresponse).getBytes();
    }

    public static byte[] getDeleteFileResponse(String filepath) {
        //Eliminar archivo
        File file = new File(filepath);
        if (file.delete()) {
            String jsonresponse = "{\"message\": \"El archivo fue eliminado\"}";
            return ("HTTP/1.1 200 OK\n" +
                "Content-Type: application/json\n" +
                "Content-Length:" + jsonresponse.length() + "\n" +
                "\r\n" +
                jsonresponse).getBytes();
        } else {
            String jsonresponse = "{\"message\": \"El archivo no existe\"}";
            return ("HTTP/1.1 404 Not Found\n" +
                "Content-Type: application/json\n" +
                "Content-Length:" + jsonresponse.length() + "\n" +
                "\r\n" +
                jsonresponse).getBytes();
        }
    }

    public static byte[] renameResponse(String filepath, String $newPath) {
        //Renombrar archivo
        File file = new File(filepath);
        file.getParentFile().mkdirs();
        try {
            if (file.exists()) {
                // Cambiar el nombre del archivo
                File newFile = new File($newPath);
                file.renameTo(newFile);
                String jsonresponse = "{\"message\": \"El archivo fue renombrado\"}";
                return ("HTTP/1.1 200 OK\n" +
                    "Content-Type: application/json\n" +
                    "Content-Length:" + jsonresponse.length() + "\n" +
                    "\r\n" +
                    jsonresponse).getBytes();
            } else {
                String jsonresponse = "{\"message\": \"El archivo ya existe\"}";
                return ("HTTP/1.1 409 Conflict\n" +
                    "Content-Type: application/json\n" +
                    "Content-Length:" + jsonresponse.length() + "\n" +
                    "\r\n" +
                    jsonresponse).getBytes();
            }
        } catch (Exception e) {
            System.err.println("Error al renombrar el archivo: " + e.getMessage());
            return getInternalServerError();
        }
    }

    public static byte[] getParametersResponse(String resource) {
        //Obtener parametros
        String[] parameters = resource.split("&");


        String htmlParameters = "";
        for (int i = 0; i < parameters.length; i++) {
            String[] parameter = parameters[i].split("=");

            // si no hay valor
            if (parameter.length == 1) {
                parameter = new String[]{parameter[0], ""};
            }

            htmlParameters += "<tr class=\"bg-white dark:bg-gray-800 border-b text-gray-900 dark:text-white dark:border-gray-700\">" + "\r\n" +
                    "<th scope=\"row\" class=\"px-6 py-4 font-medium whitespace-nowrap\">" + "\r\n" +
                        parameter[0] + "\r\n" +
                    "</th>" + "\r\n" +
                    "<td class=\"px-6 py-4 text-gray-800 dark:text-gray-100 \">" + "\r\n" +
                        parameter[1] + "\r\n" +
                    "</td>" + "\r\n" +
                "</tr>" + "\r\n";
        }
        htmlParameters += "</tbody>\r\n" + 
                        "        </table>\r\n" + 
                        "    </div>\r\n" + 
                        "</body>\r\n" + 
                        "</html>";

        File file = new File("./resources/show_parameters.html");
        if (!file.exists()) {
            return getNotFound();
        }

        try {
            // Leer el contenido del archivo
            byte[] template = Files.readAllBytes(Paths.get(file.getPath()));
            // Retornar el contenido del archivo con tablas MIME
            String MIMEType = MIME.typeByExtension(file.getName().substring(file.getName().lastIndexOf(".") + 1));
            
            
            String header = "HTTP/1.1 200 OK\n" +
            "Content-Type: " + MIMEType + "\n" +
            "Content-Length: " + (template.length + htmlParameters.length())   + "\n" +
            "\n";
            
            byte[] response = new byte[template.length + header.length() + htmlParameters.length()];

            byte[] headerBytes = header.getBytes();
            byte[] htmlParametersBytes = htmlParameters.getBytes();
            System.arraycopy(headerBytes, 0, response, 0, headerBytes.length);
            System.arraycopy(template, 0, response, headerBytes.length, template.length);
            System.arraycopy(htmlParametersBytes, 0, response, headerBytes.length + template.length, htmlParametersBytes.length);

            return response;
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return getNotFound();
        }
    }

    //Errors
    public static byte[]  getNotFound(){

        File index = new File("./resources/404.html");
        if (!index.exists()) {
            return ("HTTP/1.1 404 Not Found\n" +
            "Content-Type: text/html\n" +
            "Content-Length: 9\n" +
            "\n" +
            "Not Found").getBytes();
        }

        try {
            // Leer el contenido del archivo
            byte[] content = Files.readAllBytes(Paths.get(index.getPath()));
            // Retornar el contenido del archivo con tablas MIME
            String MIMEType = MIME.typeByExtension(index.getName().substring(index.getName().lastIndexOf(".") + 1));
            
            
            String header = "HTTP/1.1 200 OK\n" +
            "Content-Type: " + MIMEType + "\n" +
            "Content-Length: " + content.length + "\n" +
            "\n";
            
            byte[] response = new byte[content.length + header.length()];

            byte[] headerBytes = header.getBytes();
            System.arraycopy(headerBytes, 0, response, 0, headerBytes.length);
            System.arraycopy(content, 0, response, headerBytes.length, content.length);

            return response;
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return ("HTTP/1.1 404 Not Found\n" +
                "Content-Type: text/html\n" +
                "Content-Length: 9\n" +
                "\n" +
                "Not Found").getBytes();
        }
    }

    public static byte[]  getMethodNotAllowed(){
        return ("HTTP/1.1 405 Method Not Allowed\n" +
                "Content-Type: text/html\n" +
                "Content-Length: 17\n" +
                "\n" +
                "Method Not Allowed").getBytes();
    }

    public static byte[] getBadRequest(){
        return ("HTTP/1.1 400 Bad Request\n" +
                "Content-Type: text/html\n" +
                "Content-Length: 11\n" +
                "\n" +
                "Bad Request").getBytes();
    }

    public static byte[] getInternalServerError(){
        return ("HTTP/1.1 500 Internal Server Error\n" +
                "Content-Type: text/html\n" +
                "Content-Length: 21\n" +
                "\n" +
                "Internal Server Error").getBytes();
    }

}
