package server;

import dao.DAOuser;
import dao.impl.DAOuserImpl;
import entities.User;
import security.CryptoSystem;
import security.SHA;
import server.impl.ServerAPIImpl;
import sun.awt.windows.ThemeReader;
import util.ClientCommands;
import util.CredentialMessage;
import util.ServerCommands;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by USER on 25.09.2016.
 */
public class ClientThread implements Runnable{
    private ServerAPIImpl serverAPI;
    private CredentialMessage credentialMessage;
    private byte[] sessionToken;

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
                int command = fromClient.read();
                if (command == -1) {
                    System.out.println("Client " + serverAPI.getSocket().getPort() + " disconnected.");
                    serverAPI.getSocket().close();
                    return;
                }
                ClientCommands target = ClientCommands.getCommandByValue(command);

                if (target == null) {
                    System.out.println("Unknown command from client.");
                    System.exit(0);
                }
                switch (target) {
                    case SEND_PUBLIC_RSA_KEY:
                        byte[] sessTok = serverAPI.receiveSessionToken();
                        if (!Arrays.equals(sessTok, sessionToken)) {
                            serverAPI.sendResultCheckSessionToken(ServerCommands.INCORRECT_TOKEN);
                            return;
                        } else {
                            serverAPI.sendResultCheckSessionToken(ServerCommands.CORRECT_TOKEN);
                        }
                        System.out.println("Client " + serverAPI.getSocket().getPort() +
                                " sends public RSA key.");
                        serverAPI.receivePublicRSAKey();
                        System.out.println("Sending  to Client " + serverAPI.getSocket().getPort() +
                                " encoded session key.");
                        serverAPI.sendEncodedSessionKey();
                        byte[] rndNewKSData = new byte[32];
                        new Random().nextBytes(rndNewKSData);
                        DAOuser daouser = new DAOuserImpl();
                        User u = new User();
                        u.setUserLogin(credentialMessage.getLogin());
                        u.setUserKSData(rndNewKSData);
                        try {
                            daouser.setKSData(u);
                        } catch (SQLException ignored) {}
                        serverAPI.sendKSData(rndNewKSData);
                        break;
                    case SEND_FILENAME:
                        byte[] sessTok2 = serverAPI.receiveSessionToken();
                        if (!Arrays.equals(sessTok2, sessionToken)) {
                            serverAPI.sendResultCheckSessionToken(ServerCommands.INCORRECT_TOKEN);
                            return;
                        } else {
                            serverAPI.sendResultCheckSessionToken(ServerCommands.CORRECT_TOKEN);
                        }
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
//                    case SEND_DS_PUBLIC_KEY:
//                        serverAPI.receiveDSPublicKey();
//                        break;
                    case SEND_CREDENTIALS:
                        credentialMessage = serverAPI.receiveCredentials();
                        boolean result = serverAPI.sendCredentialsCheckResult(credentialMessage);
                        DAOuser daOuser;
                        User user = null;
                        if (result) {
                            daOuser = new DAOuserImpl();
                            user = new User();
                            user.setUserLogin(credentialMessage.getLogin());
                            try {
                                daOuser.getKeys(user);
                                if (user.getUserPubKey() != null) {
                                    serverAPI.setPublicKey(KeyFactory.getInstance(CryptoSystem.RSA).generatePublic(new X509EncodedKeySpec(user.getUserPubKey())));
                                }
                                serverAPI.sendEncodedSessionKey();
                            } catch (SQLException ignored) {}
                            sessionToken = SHA.generateSessionToken(credentialMessage);
                            serverAPI.sendSessionToken(sessionToken);
                            byte[] rndKSData = user.getUserKSData();
                            serverAPI.sendKSData(rndKSData);
                        }
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
