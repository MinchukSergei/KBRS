package client.gui;

import client.gui.action_frames.LoginFrame;
import client.gui.action_frames.RegisterFrame;
import client.impl.ClientAPIImpl;
import org.apache.commons.lang3.StringUtils;
import security.Base64;
import security.SHA;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

/**
 * Created by USER on 25.09.2016.
 */
public class ControlButtonsListeners {
    private ClientGui mainFrame;
    private ClientAPIImpl clientAPI;

    public ClientGui getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(ClientGui mainFrame) {
        this.mainFrame = mainFrame;
    }

    public ClientAPIImpl getClientAPI() {
        return clientAPI;
    }

    public void setClientAPI(ClientAPIImpl clientAPI) {
        this.clientAPI = clientAPI;
    }

    public ActionListener getRegisterButtonListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RegisterFrame registerFrame = new RegisterFrame("Register");
                String[] labels = {"Login", "Password"};
                JTextField[] textFields = {new JTextField(), new JPasswordField()};
                registerFrame.createAndShowGUI(labels, textFields);
                clientAPI.setAuthenticated(null);
            }
        };
    }

    public ActionListener getLoginButtonListener(final JButton button) {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (clientAPI.getAuthenticated() != null) {
                    clientAPI.setAuthenticated(null);
                    button.setText("Login");
                    JOptionPane.showMessageDialog(mainFrame, "You have successfully log out.");
                    return;
                }
                LoginFrame loginFrame = new LoginFrame("Login");
                loginFrame.setClientAPI(clientAPI);
                loginFrame.setParentButton(button);
                String[] labels = {"Login", "Password"};
                JTextField[] textFields = {new JTextField(), new JPasswordField()};
                loginFrame.createAndShowGUI(labels, textFields);
            }
        };
    }



    public ActionListener getGenerateRSAKeyButtonListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isAuthenticated()) {
                    return;
                }
                mainFrame.setEnabled(false);
                new Thread() {
                    @Override
                    public void run() {
                        clientAPI.sendKeyAndReceiveSessionKey();
                        mainFrame.setEnabled(true);
                    }
                }.start();

            }
        };
    }

    public ActionListener getSendFilenameButtonListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isAuthenticated()) {
                    return;
                }
                String filename = mainFrame.getFilenameTextField().getText();
                if (StringUtils.isBlank(filename)) {
                    JOptionPane.showMessageDialog(mainFrame, "Fill filename.");
                    return;
                }

                try {

                    if (clientAPI.sendFilename(filename)) {

                        String receivedFile = clientAPI.receiveFile(clientAPI.getKsPass());
                        if (receivedFile != null) {
                            FileFrame fileFrame = new FileFrame(filename);
                            fileFrame.appendText(receivedFile);
                            fileFrame.setDefaultSettings();
                        }
                    }
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
                } catch (NoSuchAlgorithmException e1) {
                    JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
                } catch (SQLException e1) {
                    JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
                } catch (InvalidKeySpecException e1) {
                    JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
                }
            }
        };
    }


    private boolean isAuthenticated() {
        if (clientAPI.getAuthenticated() == null) {
            JOptionPane.showMessageDialog(null, "You must logged.");
            return false;
        }
        return true;
    }
}
