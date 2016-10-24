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
public class RegisterFrame extends ActionFrame {
    public RegisterFrame(String title) {
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
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Fill all fields.");
                    return;
                }


                if (!isPasswordLengthCorrect(passwordField.getText())) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Password should contains min 5 symbols.");
                    return;
                }

                DAOuser daOuser = new DAOuserImpl();
                User newUser = new User();
                newUser.setUserLogin(loginField.getText());
                //newUser.setUserPassword(passwordField.getText());
                try {
                    daOuser.registerUser(newUser);
                } catch (SQLException ex) {
                    if (ex.getErrorCode() == 1062) {
                        JOptionPane.showMessageDialog(RegisterFrame.this, "This login is not available.");
                    } else {
                        JOptionPane.showMessageDialog(RegisterFrame.this, ex.getMessage());
                    }
                }
                int n = JOptionPane.showConfirmDialog(
                        RegisterFrame.this,
                        "You have successfully registered.",
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
