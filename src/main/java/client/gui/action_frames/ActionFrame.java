package client.gui.action_frames;

import client.ClientAPI;
import client.gui.SpringUtilities;
import client.impl.ClientAPIImpl;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by USER on 25.09.2016.
 */
public abstract class ActionFrame extends JFrame {
    private String title;
    protected JButton okButton;
    protected JTextField[] fields;
    protected ClientAPIImpl clientAPI;

    public ActionFrame(String title) {
        this.title = title;
    }

    public ClientAPIImpl getClientAPI() {
        return clientAPI;
    }

    public void setClientAPI(ClientAPIImpl clientAPI) {
        this.clientAPI = clientAPI;
    }

    public void createAndShowGUI(String[] labels, JTextField[] fields) {
        this.fields = fields;
        int numPairs = labels.length;

        //Create and populate the panel.
        JPanel p = new JPanel(new SpringLayout());
        for (int i = 0; i < numPairs; i++) {
            JLabel l = new JLabel(labels[i], JLabel.TRAILING);
            p.add(l);
            l.setLabelFor(fields[i]);
            p.add(fields[i]);
        }

        okButton = new JButton("OK");
        setOkButtonActionListener();
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(getCancelButtonActionListener());

        p.add(okButton);
        p.add(cancelButton);

        //Lay out the panel.
        SpringUtilities.makeGrid(p,
                numPairs + 1, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

        //Create and set up the window.
        setTitle(title);
        setPreferredSize(new Dimension(350, 200));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        //Set up the content pane.
        p.setOpaque(true);  //content panes must be opaque
        setContentPane(p);

        //Display the window.
        pack();
        setVisible(true);
    }

    private ActionListener getCancelButtonActionListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ActionFrame.this.dispose();
            }
        };
    }

    protected abstract void setOkButtonActionListener();

    protected boolean isPasswordLengthCorrect(String password) {
        return password.length() >= 5;
    }

    protected boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    //Check at least 1 field is empty
    protected boolean isFieldsCompleted() {
        for (JTextField f : fields) {
            if (f.getText().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
