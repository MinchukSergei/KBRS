package client.gui.action_frames;

import dao.DAOuser;
import dao.impl.DAOuserImpl;
import entities.User;
import security.CryptoSystem;
import security.DS;
import security.PrKeyReader;
import security.RSA;
import util.CredentialMessage;
import util.ServerCommands;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Created by USER on 25.09.2016.
 */
public class LoginFrame extends ActionFrame {
    public LoginFrame(String title) {
        super(title);
    }
    private JButton parentButton;

    public JButton getParentButton() {
        return parentButton;
    }

    public void setParentButton(JButton parentButton) {
        this.parentButton = parentButton;
    }

    @Override
    protected void setOkButtonActionListener() {
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int loginNumber = 0; //Login number textField
                int passwordNumber = 1; //Password number textField

                JTextField loginField = fields[loginNumber];
                JTextField passwordField = fields[passwordNumber];

                if (!isFieldsCompleted()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Fill all fields.");
                    return;
                }

//                DAOuser daOuser = new DAOuserImpl();
//                User user = new User();
//                user.setUserLogin("pes");
//                user.setUserDSPubKey(clientAPI.getDSKeyPair().getPublic().getEncoded());
//                try {
//                    daOuser.setDSPubKey(user);
//                } catch (SQLException ignored) {
//
//                }
//                byte[] prBytes = clientAPI.getDSKeyPair().getPrivate().getEncoded();
//                ClassLoader loader = ClassLoader.getSystemClassLoader();
//                URL url1 = loader.getResource("pr_key/pes_KBRS");
//                URL url2 = loader.getResource("pr_key/pes_KBRS2");
//                File p1 = new File(url1.getFile());
//                File p2 = new File(url2.getFile());
//                try {
//                    FileOutputStream fs = new FileOutputStream(p1);
//                    fs.write(Arrays.copyOfRange(prBytes, 0, 600));
//                    fs.close();
//                    fs = new FileOutputStream(p2);
//                    fs.write(Arrays.copyOfRange(prBytes, 600, prBytes.length));
//                    fs.close();
//                } catch (FileNotFoundException e1) {
//                    e1.printStackTrace();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }


                //daOuser.setPubKey(clientAPI.getDSKeyPair().getPublic());



                CredentialMessage credentialMessage = null;
                try {
                    //clientAPI.sendDSPublicKey(clientAPI.getDSKeyPair().getPublic());
                    String login = loginField.getText();
                    String password = passwordField.getText();
                    PrivateKey pk = null;
                    try {
                        byte[] pkBytes = PrKeyReader.getPrKey(login);
                        pk = KeyFactory.getInstance(CryptoSystem.RSA).generatePrivate(new PKCS8EncodedKeySpec(pkBytes));
                    } catch (NoSuchAlgorithmException ignored) {
                    } catch (InvalidKeySpecException e1) {
                        JOptionPane.showMessageDialog(LoginFrame.this, "Incorrect private key. Your local file or smart card are invalid.");
                        return;
                    } catch (FileNotFoundException e2) {
                        JOptionPane.showMessageDialog(LoginFrame.this, e2.getMessage());
                        return;
                    }

                    credentialMessage = DS.signCredentials(login, password, pk);
                    clientAPI.sendCredentials(credentialMessage);
                    ServerCommands result = clientAPI.receiveCredentialResult();
                    if (result.equals(ServerCommands.INCORRECT_CREDENTIALS)) {
                        JOptionPane.showMessageDialog(LoginFrame.this, "Incorrect credentials.");
                        return;
                    }
                    if (result.equals(ServerCommands.INCORRECT_SIGN)) {
                        JOptionPane.showMessageDialog(LoginFrame.this, "Incorrect sign.");
                        return;
                    }

                    byte [] sessionKey = clientAPI.receiveSessionEncodedKey();
                    if (sessionKey == null) {
                        JOptionPane.showMessageDialog(clientAPI.getMainFrame(), "Generate new RSA key.");
                        return;
                    } else {
                        clientAPI.setSessionKey(sessionKey);
                    }
                    User auth = new User();
                    auth.setUserLogin(credentialMessage.getLogin());
                    auth.setUserPassword(credentialMessage.getPassword());
                    DAOuser userDao = new DAOuserImpl();
                    try {
                        auth = userDao.isConfirmed(auth);
                    } catch (SQLException ignored) {}
                    clientAPI.setAuthenticated(auth);

                    byte[] token = clientAPI.receiveSessionToken();
                    clientAPI.setSessionToken(token);
                    byte[] ksData = clientAPI.receiveKSData();
                    clientAPI.setKsData(ksData);
                    clientAPI.setPsw(password);
                    clientAPI.setKsPass(clientAPI.getKSData(ksData, password));
                    parentButton.setText("Logout");
                } catch (IOException ignored) {
                }

                int n = JOptionPane.showConfirmDialog(
                        LoginFrame.this,
                        "You have successfully logged.",
                        "Success",
                        JOptionPane.OK_CANCEL_OPTION
                );
                if (n == JOptionPane.OK_OPTION || n == JOptionPane.CANCEL_OPTION) {
                    dispose();
                }
            }
        });
    }
}
