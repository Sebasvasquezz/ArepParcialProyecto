package edu.escuelaing.arep;
import java.lang.Math;
import java.util.ArrayList;
import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import java.util.Arrays;


public class ReflexCalculator {
    public static void main(String[] args) {
        int port = 5000; 
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servicio de calculadora escuchando en el puerto " + port);
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    String comando = in.readLine();
                    String resultado = procesarComando(comando);
                    out.println(resultado);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String  procesarComando(String comando) {
        Class <?> clase = Math.class;
        Method method = null;

        try {
            method=clase.getMethod(comando);
        } catch (Exception e) {
            throw new RuntimeException(e);

        }

        Object res= null;
        try {
            res=method.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return res.toString();
    }
}


