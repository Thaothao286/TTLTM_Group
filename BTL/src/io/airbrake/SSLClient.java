package io.airbrake;
import io.airbrake.utility.Logging;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.net.ssl.SSLHandshakeException;

public class SSLClient implements Runnable {
    private final static String DefaultHost = "localhost:3066";
    private final static int DefaultPort = 24601;
    private final static int DefaultTimeout = 2000;
    private final static boolean DefaultShouldSleep = false;

    private String Host = DefaultHost;
    private int Port = DefaultPort;
    private int Timeout = DefaultTimeout;
    private boolean ShouldSleep = DefaultShouldSleep;
    private String Message;

    SSLClient() {
    }

    public SSLClient(String host) {
        Host = host;
    }

    public SSLClient(int port) {
        Port = port;
    }

    public SSLClient(boolean shouldSleep) {
        ShouldSleep = shouldSleep;
    }

    private void connect() {
        connect(DefaultHost, DefaultPort, DefaultTimeout, DefaultShouldSleep);
    }

    private void connect(String host) {
        connect(host, DefaultPort, DefaultTimeout, DefaultShouldSleep);
    }

    private void connect(int port) {
        connect(DefaultHost, port, DefaultTimeout, DefaultShouldSleep);
    }

    private void connect(boolean shouldSleep) {
        connect(DefaultHost, DefaultPort, DefaultTimeout, shouldSleep);
    }

    /**
     * Attempt SSL connection to passed host and port as client.
     *
     * @param host        Host to connect to.
     * @param port        Port to connect to.
     * @param timeout     Timeout (in milliseconds) to allow for socket connection.
     * @param shouldSleep Indicates if thread should be artificially slept.
     */
    private void connect(String host, int port, int timeout, boolean shouldSleep) {
        try {
            Logging.lineSeparator(
                    String.format("CONNECTING TO %s:%d WITH %d MS TIMEOUT%s",host,port,timeout,shouldSleep ? " AND 500 MS SLEEP" : ""),80);

            while (true) {
                SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

                SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
       
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
                if (shouldSleep) {
        
                    Thread.sleep(500);
                }
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                Logging.log("[FROM Server] " + reader.readLine());
     BufferedReader userInputBR = new BufferedReader(new InputStreamReader(System.in));

                Message = userInputBR.readLine();

                writer.println(Message);

                if ("quit".equalsIgnoreCase(Message) || "exit".equalsIgnoreCase(Message)) {
                    Logging.lineSeparator("DISCONNECTING");
                    socket.close();
                    break;
                }
                Logging.log("[TO Server] " + reader.readLine());
            }
        } catch (SSLHandshakeException exception) {
            Logging.log(exception);
        } catch (InterruptedException | IOException exception) {
            Logging.log(exception, false);
        }
    }

    @Override
    public void run() {
        connect(Host, Port, Timeout, ShouldSleep);
    }
}    

