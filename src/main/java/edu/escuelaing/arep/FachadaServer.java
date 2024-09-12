package edu.escuelaing.arep;

import java.net.*;
import java.io.*;

public class FachadaServer {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor de fachada escuchando en el puerto " + port);
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    String command = sacarComando(in);

                    if (command != null) {
                        String response = enviarSolicitudCalculo(command);
                        enviarRespuesta(out, "application/json", response);
                    } else {
                        String clientHtml = obtenerCliente();
                        enviarRespuesta(out, "text/html", clientHtml);
                    }
                }
            }
        }
    }

    private static String sacarComando(BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println("Recibí: " + line);
            if (line.startsWith("GET /computar?comando=")) {
                return line.split("=")[1].split(" ")[0];
            }
            if (!in.ready()) {
                break;
            }
        }
        return null;
    }

    private static String enviarSolicitudCalculo(String command) throws IOException {
        URL calcServiceUrl = new URL("http://localhost:5000/compreflex?comando=" + command);
        HttpURLConnection connection = (HttpURLConnection) calcServiceUrl.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader calcIn = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return calcIn.readLine();
        }
    }

    private static void enviarRespuesta(PrintWriter out, String contentType, String body) {
        out.println("HTTP/1.1 200 OK\r\n" + "Content-Type: " + contentType + "\r\n\r\n" + body);
    }

    public static String obtenerCliente() {
        return "<!DOCTYPE html>\n"
        + "<html>\n"
        + "<head>\n"
        + "<meta charset=\"UTF-8\">\n"
        + "<title>Calculadora Reflexiva</title>\n"
        + "</head>\n"
        + "<body>\n"
        + "<h1>Calculadora Reflexiva</h1>\n"
        + "<form>\n"
        + "<label for=\"comando\">Comando con parametros:</label><br>\n"
        + "<input type=\"text\" id=\"comando\" name=\"comando\" placeholder=\"Comando (e.g., sin(30), bbl(5,3,8)):\"><br><br>\n"
        + "<input type=\"button\" value=\"Calcular\" onclick=\"loadGetMsg()\">\n"
        + "</form>\n"
        + "<div id=\"getrespmsg\"></div>\n"
        + "<script>\n"
        + "function loadGetMsg() {\n"
        + "let comando = document.getElementById(\"comando\").value;\n"
        + "const xhttp = new XMLHttpRequest();\n"
        + "xhttp.onload = function() {\n"
        + "document.getElementById(\"getrespmsg\").innerHTML = this.responseText;\n"
        + "}\n"
        + "xhttp.open(\"GET\", \"/computar?comando=\" + comando);\n"
        + "xhttp.send();\n"
        + "}\n"
        + "</script>\n"
        + "</body>\n"
        + "</html>";
    }
}
