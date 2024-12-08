package server_http;

import java.io.*;
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
    protected String resource, contentType, contentLength;
    protected byte[] body;

    public HTTPHandler(String request, String method, String resource, String contentType, String contentLength, byte[] body) {
        this.method = HTTPMethod.valueOf(method);
        this.resource = resource;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.body = body;
    }

    public static HTTPHandler readCompleteRequest(DataInputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[65536];
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
        String method = firstLine[0];
        String resource = firstLine.length > 1 ? firstLine[1] : "";
        String contentType = getContentType(request);
        String contentLengthStr = getContentLength(request) + "";
        // Obtener el cuerpo de la solicitud
        if(contentLength == -1) return new HTTPHandler(request, method, resource, contentType, contentLengthStr, null);
        
        byte[] full_request = buffer.toByteArray();
        byte[] body = new byte[contentLength];
        System.arraycopy(full_request, full_request.length - contentLength, body, 0, contentLength);

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

    public byte[] getResponse() {
        byte[] response;

        // Routes
        if(this.method == HTTPMethod.GET){
            if (resource.equals("/")) response = Responses.getFileResponse("./resources/index.html");
            else if(resource.equals("/main.js")) response = Responses.getFileResponse("./resources/main.js");
            else if(resource.equals("/favicon.ico")) response = Responses.getFileResponse("./resources/favicon.ico");
            else if(resource.equals("/list")) response = Responses.getFilesResponse("./resources/public");
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
            else if (resource.startsWith("/rename"))
            response = Responses.renameResponse("./resources/public/" + resource.split("/")[2],"./resources/public/" + resource.split("/")[3]);
            else response = Responses.getNotFound();
        } else if(this.method == HTTPMethod.DELETE){
            if (resource.equals("/")) response = Responses.getFileResponse("./resources/index.html");
            else if (resource.startsWith("/delete"))
            response = Responses.getDeleteFileResponse("./resources/public/" + resource.split("/")[2]);
            else response = Responses.getNotFound();
        }
        else response = Responses.getMethodNotAllowed();

        return response;
    }

    public void showFormatedRequest() {
        System.out.println("Method: " + method);
        System.out.println("resource: " + resource);
        System.out.println("Content-Type: " + contentType);
        System.out.println("Content-Length: " + contentLength);
        //System.out.println("Body: " + body);
    }

    public void showRequest() {
        System.out.println("Request: " + request);
    }   
}
