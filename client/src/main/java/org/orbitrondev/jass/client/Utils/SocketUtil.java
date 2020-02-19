/*
 * fhnw-jass is jass game programmed in java for a school project.
 * Copyright (C) 2020 Manuele Vaccari
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.orbitrondev.jass.client.Utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.orbitrondev.jass.client.Entity.LoginEntity;
import org.orbitrondev.jass.client.Message.Message;
import org.orbitrondev.jass.lib.Message.MessageData;
import org.orbitrondev.jass.lib.ServiceLocator.Service;
import org.orbitrondev.jass.lib.ServiceLocator.ServiceLocator;

import javax.net.SocketFactory;
import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;

/**
 * Backend utility class. Acts as an interface between the program and the server.
 *
 * @author Manuele Vaccari
 * @version %I%, %G%
 * @since 0.0.1
 */
public class SocketUtil extends Thread implements Service, Closeable {
    private static final Logger logger = LogManager.getLogger(SocketUtil.class);

    private Socket socket;
    private volatile boolean serverReachable = true;

    private ArrayList<Message> lastMessages = new ArrayList<>();

    /**
     * Creates a Socket (insecure or secure) to the backend.
     *
     * @param ipAddress A String containing the ip address to reach the server.
     * @param port      An integer containing the port which the server uses.
     * @param secure    A boolean defining whether to use SSL or not.
     *
     * @since 0.0.1
     */
    public SocketUtil(String ipAddress, int port, boolean secure) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        super();
        this.setName("BackendThread");
        this.setDaemon(true);

        if (secure) {
            logger.info("Connecting to server at: " + ipAddress + ":" + port + " (with SSL)");

            /*
             * @author https://stackoverflow.com/questions/53323855/sslserversocket-and-certificate-setup
             */
            // Create and initialize the SSLContext with key material
            char[] trustStorePassword = "JassGame".toCharArray();
            char[] keyStorePassword = "JassGame".toCharArray();

            // First initialize the key and trust material
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(getClass().getResourceAsStream("/ssl/client.keystore"), trustStorePassword);

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(getClass().getResourceAsStream("/ssl/client.keystore"), keyStorePassword);

            // KeyManagers decide which key material to use
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, keyStorePassword);

            // TrustManagers decide whether to allow connections
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), SecureRandom.getInstanceStrong());

            SocketFactory factory = sslContext.getSocketFactory();
            socket = factory.createSocket(ipAddress, port);

            // The next line is entirely optional!
            // The SSL handshake would happen automatically, the first time we send data.
            // Or we can immediately force the handshaking with this method:
            ((SSLSocket) socket).startHandshake();
        } else {
            logger.info("Connecting to server at: " + ipAddress + ":" + port);
            socket = new Socket(ipAddress, port);
        }

        // Create thread to read incoming messages
        this.start();
    }

    @Override
    public void run() {
        while (serverReachable) {
            Message msg = null;
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String msgText = in.readLine(); // Will wait here for complete line
                if (msgText == null) break; // In case the server closes the socket

                logger.info("Receiving message: " + msgText);

                // Break message into individual parts, and remove extra spaces
                MessageData msgData = MessageData.unserialize(msgText);

                // Create a message object of the correct class, using reflection
                if (msgData == null) {
                    logger.error("Received invalid message");
                } else {
                    msg = Message.fromDataObject(msgData);
                    if (msg == null) {
                        logger.error("Received invalid message of type " + msgData.getMessageType());
                    } else {
                        logger.info("Received message of type " + msgData.getMessageType());
                    }
                }
            } catch (SocketException e) {
                logger.info("Server disconnected");
                close();
                continue;
            } catch (IOException e) {
                logger.error(e.toString());
            }

            lastMessages.add(msg);
        }
    }

    /**
     * Wait until the corresponding "Result" response arrives from the server.
     *
     * @since 0.0.1
     */
    public Message waitForResultResponse(int id) {
        while (true) {
            if (lastMessages.size() != 0) {
                for (Message m : lastMessages) {
                    if (m.getId() == id) {
                        lastMessages.remove(m);
                        return m;
                    }
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) { /* Ignore */ }
        }
    }


    /**
     * Send a message to this server. In case of an exception, log the client out.
     */
    public void send(Message msg) {
        try {
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
            logger.info("Sending message: " + this.toString());
            out.write(msg.toString() + "\n"); // This will send the serialized MessageData object
            out.flush();
        } catch (IOException e) {
            logger.info("Server unreachable; logged out");
            close();
        }
    }

    /**
     * @return "true" if logged in, otherwise "false"
     *
     * @since 0.0.2
     */
    public boolean isLoggedIn() {
        return ServiceLocator.get("login") != null;
    }

    /**
     * @return A string containing the token if logged in, otherwise "null"
     *
     * @since 0.0.2
     */
    public String getToken() {
        LoginEntity login = (LoginEntity) ServiceLocator.get("login");
        if (login != null) {
            return login.getToken();
        }
        return null;
    }

    /**
     * Verifies that the string is a valid ip address.
     *
     * @param ipAddress A String containing the ip address.
     *
     * @return "true" if valid, "false" if not.
     *
     * @author https://stackoverflow.com/questions/5667371/validate-ipv4-address-in-java
     * @since 0.0.1
     */
    public static boolean isValidIpAddress(String ipAddress) {
        return ipAddress.matches("^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$");
    }

    /**
     * Verifies that the string is in the valid range of open ports.
     *
     * @param port An integer containing the port.
     *
     * @return "true" if valid, "false" if not.
     *
     * @since 0.0.1
     */
    public static boolean isValidPortNumber(int port) {
        return port >= 1024 && port <= 65535;
    }

    /**
     * Closes the socket.
     *
     * @since 0.0.1
     */
    @Override
    public void close() {
        serverReachable = false;
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) { /* Ignore */ }
        }
    }

    @Override
    public String getServiceName() {
        return "backend";
    }
}
