package client.gui.action_frames;

import security.Base64;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by USER on 25.09.2016.
 */
public class ChangeEmailKeyFrame extends ActionFrame {
    public ChangeEmailKeyFrame(String title) {
        super(title);
    }

    @Override
    protected void setOkButtonActionListener() {
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int emailKeyNumber = 0; //Email key number textField

                JTextField emailField = fields[emailKeyNumber];

                if (!isFieldsCompleted()) {
                    JOptionPane.showMessageDialog(ChangeEmailKeyFrame.this, "Fill all fields.");
                    return;
                }

                String newEmailKey = emailField.getText();
                if (Base64.isBase64(newEmailKey)) {
                    clientAPI.setEmailKeyB64(newEmailKey);
                } else {
                    JOptionPane.showMessageDialog(ChangeEmailKeyFrame.this, "This key should have base64 string type");
                    return;
                }
                int n = JOptionPane.showConfirmDialog(
                        ChangeEmailKeyFrame.this,
                        "Your email key has successfully changed.",
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
