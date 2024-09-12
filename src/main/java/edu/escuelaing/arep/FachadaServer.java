package edu.escuelaing.arep;
import java.io.*;
import java.net.*;

public class FachadaServer {
    public static void main(String[] args) {
        int port = 8080;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Fachada de servicios escuchando en el puerto " + port);
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    String inputLine, outputLine;
                    String comando = null;
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println("Recibí: " + inputLine);
                        if (inputLine.startsWith("GET")) {
                            String path =inputLine.split(" ")[1];
                            System.out.println("path: "+path);
                            if (path.startsWith("/computar")){
                                comando = inputLine.split(" ")[1].substring(18);
                                System.out.println("Comando: " + comando);
                                String resultado = enviarAReflexCalculator(comando);
                                out.println("HTTP/1.1 200 OK");
                                out.println("Content-Type: application/json");
                                out.println("Content-Length: " + resultado.length());
                                out.println();
                                out.println(resultado);
                            }else if (path.startsWith("/calculadora")){
                                System.out.println("Entro donde deberia");
                                outputLine = "<!DOCTYPE html>\n" +
                                "<html>\n" +
                                "<head>\n" +
                                "<title>Calculadora Distribuida</title>\n" +
                                "<meta charset=\"UTF-8\">\n" +
                                "</head>\n" +
                                "<body>\n" +
                                "<h1>Calculadora Distribuida</h1>\n" +
                                "<form>\n" +
                                "<label for=\"command\">Comando (e.g., sin(30), bbl(5,3,8)): </label>\n" +
                                "<input type=\"text\" id=\"command\" name=\"command\">\n" +
                                "<input type=\"button\" value=\"Computar\" onclick=\"computeCommand()\">\n" +
                                "</form>\n" +
                                "<div id=\"result\"></div>\n" +
                                "<script>\n" +
                                "\n" +
                                "        function computeCommand() {\n" +
                                "            let command = document.getElementById(\"command\").value;\n" +
                                "            fetch(`/computar?comando=${encodeURIComponent(command)}`)\n" +
                                "                .then(response => response.json())\n" +
                                "                .then(data => document.getElementById(\"result\").innerHTML = `Resultado: ${data.resultado}`)\n" +
                                "                .catch(error => document.getElementById(\"result\").innerHTML = `Error: ${error}`);\n" +
                                "\n" +
                                "        }\n" +
                                "</script>\n" +
                                "</body>\n" +
                                "</html>";
                                out.println(outputLine);
                            }
                            
                        }
                        if (inputLine.isEmpty()) {
                            break;
                        }
                    }

                   
                }
            }

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    private static String enviarAReflexCalculator(String comando) throws IOException {
        try (Socket calculatorSocket = new Socket("localhost", 5000)) {
            PrintWriter out = new PrintWriter(calculatorSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(calculatorSocket.getInputStream()));
            out.println(comando);
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
}