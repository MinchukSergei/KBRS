package server;

import server.impl.ServerAPIImpl;
import util.ClientCommands;
import util.ServerCommands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by USER on 25.09.2016.
 */
public class ClientThread implements Runnable{
    private ServerAPIImpl serverAPI;

    public ClientThread(ServerAPIImpl serverAPI) {
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
                if (fromClient.available() == 0) {
                    return;
                }
                int command = fromClient.read();

                ClientCommands target = null;
                for (ClientCommands c: ClientCommands.values()) {
                    if (c.getValue() == command) {
                        target = c;
                        break;
                    }
                }

                switch (target) {
                    case SEND_PUBLIC_RSA_KEY:
                        serverAPI.receivePublicRSAKey();
                        serverAPI.sendEncodedSessionKey();
                        break;
                    case SEND_FILENAME:
                        String filename = serverAPI.receiveFilename();
                        if (filename == null) {
                            System.out.println("Can't receive filename from client.");
                            break;
                        }
                        serverAPI.sendEncodedFile(filename);
                        break;
                }

            } catch (IOException e) {
                System.out.println(e.getMessage());
            } catch (InvalidKeyException e) {
                System.out.println(e.getMessage());
            } catch (InvalidKeySpecException e) {
                System.out.println(e.getMessage());
            } catch (NoSuchAlgorithmException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
