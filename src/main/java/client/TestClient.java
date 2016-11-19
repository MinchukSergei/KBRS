package client;

import client.gui.ClientGui;
import client.impl.ClientAPIImpl;
import entities.User;
import security.AES;
import security.CryptoSystem;
import security.FileEncrypter;
import security.RSA;
import util.ClientCommands;
import util.ResourceBundleManager;

import javax.crypto.SecretKey;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;

public  class TestClient {
    private static Socket socket;

    public static void main(String[] args) {
        FileEncrypter fe = new FileEncrypter();
//        try {
//            fe.saveFileToClient("Prev", "HELLO BRAT");
//        } catch (SQLException | FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        try {
//            String file = fe.getClientFile("Prev");
//            System.out.println(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        ClientAPIImpl clientAPI = null;
        try {
            socket = new Socket(
                    ResourceBundleManager.getByName("server.host.address"),
                    Integer.parseInt(ResourceBundleManager.getByName("server.port.number"))
                    );
            clientAPI = new ClientAPIImpl(socket);
            clientAPI.setFromServer(socket.getInputStream());
            clientAPI.setToServer(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        ClientGui clientGui = new ClientGui(clientAPI);
        clientAPI.setMainFrame(clientGui);
        clientGui.setDefaultSettings();
    }
}
