package client.gui.action_frames;

import dao.DAOuser;
import dao.impl.DAOuserImpl;
import entities.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Created by USER on 25.09.2016.
 */
public class LoginFrame extends ActionFrame {
    public LoginFrame(String title) {
        super(title);
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

                DAOuser daOuser = new DAOuserImpl();
                User user = new User();
                user.setUserLogin(loginField.getText());
                user.setUserPassword(passwordField.getText());
                try {
                    user = daOuser.isConfirmed(user);

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(LoginFrame.this, ex.getMessage());
                }
                if (user == null) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Incorrect credentials.");
                    return;
                } else {
                    clientAPI.setAuthenticated(user);
                    try {
                        daOuser.getKeys(user);
                    } catch (SQLException e1) {
                        JOptionPane.showMessageDialog(LoginFrame.this, e1.getMessage());
                    }
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
