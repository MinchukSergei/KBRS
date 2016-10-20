package client.gui;

import client.gui.action_frames.ChangeEmailFrame;
import client.gui.action_frames.ChangeEmailKeyFrame;
import client.gui.action_frames.LoginFrame;
import client.gui.action_frames.RegisterFrame;
import client.impl.ClientAPIImpl;

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
                String[] labels = {"Login", "Password", "Email"};
                JTextField[] textFields = {new JTextField(), new JPasswordField(), new JTextField()};
                registerFrame.createAndShowGUI(labels, textFields);
                clientAPI.setAuthenticated(null);
                clientAPI.setEmailKeyB64(null);
            }
        };
    }

    public ActionListener getLoginButtonListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LoginFrame loginFrame = new LoginFrame("Login");
                loginFrame.setClientAPI(clientAPI);
                String[] labels = {"Login", "Password"};
                JTextField[] textFields = {new JTextField(), new JPasswordField()};
                loginFrame.createAndShowGUI(labels, textFields);
                clientAPI.setEmailKeyB64(null);
            }
        };
    }

    public ActionListener getSendCurrentPublicRSAKey() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isAuthenticated()) {
                    return;
                }
                mainFrame.setEnabled(false);
                new Thread() {
                    @Override
                    public void run() {
                        clientAPI.sendCurrentRSAKeyAndReceiveSessionKey();
                    }
                }.start();
                mainFrame.setEnabled(true);
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
                        clientAPI.setEmailKeyB64(null);
                    }
                }.start();
                mainFrame.setEnabled(true);
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
                if (filename.isEmpty()) {
                    JOptionPane.showMessageDialog(mainFrame, "Fill filename.");
                    return;
                }
                try {
                    if (clientAPI.sendFilename(filename)) {
                        String receivedFile = clientAPI.receiveFile();
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
