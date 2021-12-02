package io.airbrake;

import airbrake.*;
 
import io.airbrake.utility.Logging;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;
import javax.net.ssl.SSLHandshakeException;

public class SSLServer implements Runnable {

    private final static int DefaultPort = 24601;
    private int Port = DefaultPort;

    SSLServer() { }

    SSLServer(int port) { Port = port; }

    private static void createServer() {
        createServer(DefaultPort);
    }

  
    private static void createServer(int port) {
        try {
            Logging.lineSeparator(String.format("CREATING SSL SERVER: localhost:3066", port));
            SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            ServerSocket serverSocket = factory.createServerSocket(port);

            while (true) {
                Socket socket = serverSocket.accept();

                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                printWriter.println("Enter a message for the server.");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String output = bufferedReader.readLine();
                printWriter.println(output);
                printWriter.close();
                socket.close();

                Logging.log(String.format("[FROM Client] %s", output));
            }
        } catch (SSLHandshakeException exception) {
            Logging.log(exception);
        } catch (IOException exception) {
            Logging.log(exception, false);
        }
    }

    @Override
    public void run() {
        createServer(Port);
    }
}
    

