package server_http;

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
    protected String path;

    public HTTPHandler(String string) {
        this.request = string;
        if(string.contains("GET")) {
            this.method = HTTPMethod.GET;
        } else if(string.contains("POST")) {
            this.method = HTTPMethod.POST;
        } else if(string.contains("PUT")) {
            this.method = HTTPMethod.PUT;
        } else if(string.contains("DELETE")) {
            this.method = HTTPMethod.DELETE;
        } else if(string.contains("HEAD")) {
            this.method = HTTPMethod.HEAD;
        }
        this.path = request.split(" ")[1];
    }

    public String getResponse() {
        String response = "";

        if(this.method == HTTPMethod.GET){
            if (path.equals("/")) response = Responses.getFileResponse("./resources/index.html");
            else if(path.equals("/main.js")) response = Responses.getFileResponse("./resources/main.js");
            else response = Responses.getFileResponse("./resources/public" + path);
        } else response = Responses.getMethodNotFound();

        return response;
    }

    public void showFormatedRequest() {
        System.out.println("Method: " + method);
        System.out.println("Path: " + path);
    }

    public void showRequest() {
        System.out.println("Request: " + request);
    }   
}
