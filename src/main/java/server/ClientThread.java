package server;

import server.impl.ServerAPIImpl;
import sun.awt.windows.ThemeReader;
import util.ClientCommands;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by USER on 25.09.2016.
 */
public class ClientThread implements Runnable{
    private ServerAPIImpl serverAPI;

    public ClientThread(ServerAPIImpl serverAPI) {
        System.out.println("Client " + serverAPI.getSocket().getPort() + " connected.");
        this.serverAPI = serverAPI;
    }

    public void run() {
        InputStream fromClient = null;
        try {
            fromClient = serverAPI.getSocket().getInputStream();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        while (true) {
            try {
                Thread.sleep(500);
                if (fromClient.available() != 0) {
                    System.out.println("Client " + serverAPI.getSocket().getPort() + " disconnected.");
                    serverAPI.getSocket().close();
                    return;
                }
                int command = fromClient.read();

                ClientCommands target = ClientCommands.getCommandByValue(command);

                if (target == null) {
                    System.out.println("Unknown command from client.");
                    System.exit(0);
                }
                switch (target) {
                    case SEND_PUBLIC_RSA_KEY:
                        System.out.println("Client " + serverAPI.getSocket().getPort() +
                                " sends public RSA key.");
                        serverAPI.receivePublicRSAKey();
                        System.out.println("Sending  to Client " + serverAPI.getSocket().getPort() +
                                " encoded session key.");
                        serverAPI.sendEncodedSessionKey();
                        break;
                    case SEND_FILENAME:
                        System.out.println("Client " + serverAPI.getSocket().getPort() +
                                " sends filename.");
                        String filename = serverAPI.receiveFilename();
                        if (filename == null) {
                            System.out.println("Can't receive filename from client.");
                            System.exit(0);
                        }
                        serverAPI.sendEncodedFile(filename);
                        System.out.println("Server sending to Client " + serverAPI.getSocket().getPort() +
                                " encoded file.");
                        break;
                }

            } catch (IOException e) {
                System.out.println("Client " + serverAPI.getSocket().getPort() + " disconnected.");
                return;
            } catch (InvalidKeyException e) {
                System.out.println(e.getMessage());
            } catch (InvalidKeySpecException e) {
                System.out.println(e.getMessage());
            } catch (NoSuchAlgorithmException e) {
                System.out.println(e.getMessage());
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
//            } finally {
//                try {
//                    serverAPI.getSocket().close();
//                } catch (IOException e) {
//                    System.out.println(e.getMessage());
//                }
//            }
        }
    }
}
