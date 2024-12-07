package server_http;

import java.util.HashMap;

public class MIME {
    public static final HashMap<String, String> types = new HashMap<>();
    
    static {
        types.put("doc", "application/msword");
        types.put("pdf", "application/pdf");
        types.put("rar", "application/x-rar-compressed");
        types.put("mp3", "audio/mpeg");
        types.put("jpg", "image/jpeg");
        types.put("jpeg", "image/jpeg");
        types.put("png", "image/png");
        types.put("html", "text/html");
        types.put("htm", "text/html");
        types.put("c", "text/plain");
        types.put("txt", "text/plain");
        types.put("java", "text/plain");
        types.put("mp4", "video/mp4");
        types.put("mpeg", "video/mpeg");
        types.put("avi", "video/x-msvideo");
        types.put("json", "application/json");
        types.put("xml", "application/xml");
        types.put("zip", "application/zip");
        types.put("css", "text/css");
        types.put("js", "application/javascript");
        types.put("csv", "text/csv");
        types.put("svg", "image/svg+xml");
        types.put("gif", "image/gif");
        types.put("ico", "image/x-icon");
        types.put("webp", "image/webp");
    }

    public static String typeByExtension(String extension) {
        return types.get(extension);
    }

}
