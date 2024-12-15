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
        int headerLength = 0;

        while (true) {
            int bytesRead = input.read(data);
            if (bytesRead == -1) break; // Fin del flujo

            buffer.write(data, 0, bytesRead);
            
            // Verificar si los encabezados están completos
            if (!headersComplete) {
                String currentRequest = buffer.toString("UTF-8");
                if(currentRequest.indexOf("\r\n\r\n") == -1) continue;
                headerLength = currentRequest.indexOf("\r\n\r\n") + 4;
                headersComplete = true;
                contentLength = getContentLength(currentRequest);

                // Si no hay cuerpo, terminamos aquí
                if (contentLength == -1) break;
            }

            if (headersComplete && buffer.size() >= contentLength + headerLength) {
                break;
            }
        }

        String request = buffer.toString("UTF-8");
        if (request.isEmpty()) return null;
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
            else if(resource.equals("/parameters")) response = Responses.getFileResponse("./resources/parameters.html");
            else if(resource.startsWith("/parameters?")) response = Responses.getParametersResponse(resource.split("\\?")[1]);
            else if(resource.equals("/main.js")) response = Responses.getFileResponse("./resources/main.js");
            else if(resource.equals("/favicon.ico")) response = Responses.getFileResponse("./resources/favicon.ico");
            else if(resource.equals("/armando.jpeg")) response = Responses.getFileResponse("./resources/armando.jpeg");
            else if(resource.equals("/list")) response = Responses.getFilesResponse("./resources/public");
            else response = Responses.getFileResponse("./resources/public" + resource);
        } 
        else if(this.method == HTTPMethod.POST){
            if (resource.startsWith("/upload"))
                response = Responses.getCreateFileResponse("./resources/public/" + resource.split("/")[2], body, false);
            else response = Responses.getNotFound();
        }
        else if(this.method == HTTPMethod.PUT){
            if (resource.startsWith("/upload")) 
            response = Responses.getCreateFileResponse("./resources/public/" + resource.split("/")[2], body, true);
            else if (resource.startsWith("/rename"))
            response = Responses.renameResponse("./resources/public/" + resource.split("/")[2],"./resources/public/" + resource.split("/")[3]);
            else response = Responses.getNotFound();
        } else if(this.method == HTTPMethod.DELETE){
            if (resource.startsWith("/delete"))
            response = Responses.getDeleteFileResponse("./resources/public/" + resource.split("/")[2]);
            else response = Responses.getNotFound();
        } else if(this.method == HTTPMethod.HEAD){
            if (resource.equals("/")) response = Responses.getFileResponse("./resources/index.html");
            else if(resource.equals("/parameters")) response = Responses.getFileResponse("./resources/parameters.html");
            else if(resource.startsWith("/parameters?")) response = Responses.getParametersResponse(resource.split("\\?")[1]);
            else if(resource.equals("/main.js")) response = Responses.getFileResponse("./resources/main.js");
            else if(resource.equals("/favicon.ico")) response = Responses.getFileResponse("./resources/favicon.ico");
            else if(resource.equals("/armando.jpeg")) response = Responses.getFileResponse("./resources/armando.jpeg");
            else if(resource.equals("/list")) response = Responses.getFilesResponse("./resources/public");
            else response = Responses.getFileResponse("./resources/public" + resource);

            // Eliminar el cuerpo de la respuesta
            String responseStr = new String(response);
            responseStr = responseStr.substring(0, responseStr.indexOf("\n\n") + 2);
            System.out.println(responseStr);
            response = responseStr.getBytes();
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
