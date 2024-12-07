package server_http;

import java.io.*;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HTTPHandler {
    protected String request;
    protected enum HTTPMethod {
        GET,
        POST,
        PUT,
        DELETE,
        HEAD
    }
    protected HTTPMethod method;
    protected String resource, contentType, contentLength, body;

    public HTTPHandler(String request, String method, String resource, String contentType, String contentLength, String body) {
        this.method = HTTPMethod.valueOf(method);
        this.resource = resource;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.body = body;
    }

    public static HTTPHandler readCompleteRequest(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        boolean headersComplete = false;
        int contentLength = -1;

        while (true) {
            int bytesRead = input.read(data);
            if (bytesRead == -1) break; // Fin del flujo

            buffer.write(data, 0, bytesRead);
            String currentRequest = buffer.toString("UTF-8");

            // Verificar si los encabezados están completos
            if (!headersComplete && currentRequest.contains("\r\n\r\n")) {
                headersComplete = true;
                contentLength = getContentLength(currentRequest);

                // Si no hay cuerpo, terminamos aquí
                if (contentLength == -1) break;
            }

            // Si los encabezados están completos y se ha leído todo el cuerpo, salimos
            if (headersComplete && buffer.size() >= currentRequest.indexOf("\r\n\r\n") + 4 + contentLength) {
                break;
            }
        }

        String request = buffer.toString("UTF-8");
        String[] firstLine = request.split(" ");
        for (int i = 0; i < firstLine.length; i++) {
            System.out.println("firstLine[" + i + "]: " + firstLine[i]);
        }
        String method = firstLine[0];
        String resource = firstLine[1];
        String contentType = getContentType(request);
        String contentLengthStr = getContentLength(request) + "";
        String body = request.substring(request.indexOf("\r\n\r\n") + 4);
        return new HTTPHandler(request ,method, resource, contentType, contentLengthStr, body);
    }

    public static String getContentType(String request) {
        for (String line : request.split("\r\n")) {
            if (line.startsWith("Content-Type:")) {
                return line.split(":")[1].trim();
            }
        }
        return null; // No hay tipo de contenido
    }

    public static int getContentLength(String request) {
        for (String line : request.split("\r\n")) {
            if (line.startsWith("Content-Length:")) {
                return Integer.parseInt(line.split(":")[1].trim());
            }
        }
        return -1; // No hay cuerpo
    }

    public String getResponse() {
        String response = "";

        // Routes
        if(this.method == HTTPMethod.GET){
            if (resource.equals("/")) response = Responses.getFileResponse("./resources/index.html");
            else if(resource.equals("/main.js")) response = Responses.getFileResponse("./resources/main.js");
            else response = Responses.getFileResponse("./resources/public" + resource);
        } 
        else if(this.method == HTTPMethod.POST){
            if (resource.equals("/")) response = Responses.getFileResponse("./resources/index.html");
            else if (resource.startsWith("/upload"))
                response = Responses.getCreateFileResponse("./resources/public/" + resource.split("/")[2], body, false);
            else response = Responses.getNotFound();
        }
        else if(this.method == HTTPMethod.PUT){
            if (resource.equals("/")) response = Responses.getFileResponse("./resources/index.html");
            else if (resource.startsWith("/upload"))
                response = Responses.getCreateFileResponse("./resources/public/" + resource.split("/")[2], body, true);
            else response = Responses.getNotFound();
        }
        else response = Responses.getMethodNotFound();

        return response;
    }

    public void showFormatedRequest() {
        System.out.println("Method: " + method);
        System.out.println("resource: " + resource);
        System.out.println("Content-Type: " + contentType);
        System.out.println("Content-Length: " + contentLength);
        System.out.println("Body: " + body);
    }

    public void showRequest() {
        System.out.println("Request: " + request);
    }   
}
