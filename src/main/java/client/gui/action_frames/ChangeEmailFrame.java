package client.gui.action_frames;

import dao.DAOuser;
import dao.impl.DAOuserImpl;
import entities.User;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Created by USER on 25.09.2016.
 */
public class ChangeEmailFrame extends ActionFrame {
    public ChangeEmailFrame(String title) {
        super(title);
    }

    @Override
    protected void setOkButtonActionListener() {
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int emailNumber = 0; //Email number textField

                JTextField emailField = fields[emailNumber];

                String lastEmail = clientAPI.getAuthenticated().getUserEmail();

                if (!isFieldsCompleted()) {
                    JOptionPane.showMessageDialog(ChangeEmailFrame.this, "Fill all fields.");
                    return;
                }

                DAOuser daOuser = new DAOuserImpl();
                User user = clientAPI.getAuthenticated();
                user.setUserEmail(emailField.getText());
                try {
                    daOuser.updateEmail(user);
                } catch (SQLException ex) {
                    user.setUserEmail(lastEmail);
                    JOptionPane.showMessageDialog(ChangeEmailFrame.this, ex.getMessage());
                    return;
                }

                int n = JOptionPane.showConfirmDialog(
                        ChangeEmailFrame.this,
                        "Your email has successfully changed.",
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
