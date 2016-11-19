package client.gui;

import client.impl.ClientAPIImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 25.09.2016.
 */
public class ClientGui extends JFrame {
    private JPanel controlPanel;
    private JPanel logPanel;
    private JTextArea logTextArea;
    private ClientAPIImpl clientAPI;

    private JTextField filenameTextField;

    public JTextArea getLogTextArea() {
        return logTextArea;
    }

    public void setLogTextArea(JTextArea logTextArea) {
        this.logTextArea = logTextArea;
    }

    public JTextField getFilenameTextField() {
        return filenameTextField;
    }

    public void setFilenameTextField(JTextField filenameTextField) {
        this.filenameTextField = filenameTextField;
    }

    public void setDefaultSettings() {
        setTitle("Lab1 KBRS");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(700, 400));
        setVisible(true);
        pack();
    }

    public ClientGui(ClientAPIImpl clientAPI) {
        this.clientAPI = clientAPI;
        setMainPanel();
    }

    public ClientAPIImpl getClientAPI() {
        return clientAPI;
    }

    public void setClientAPI(ClientAPIImpl clientAPI) {
        this.clientAPI = clientAPI;
    }

    private void setMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        setLogPanel();
        mainPanel.add(logPanel, BorderLayout.CENTER);

        setControlPanel();
        mainPanel.add(controlPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    private void setLogPanel() {
        logPanel = new JPanel();
        logPanel.setLayout(new BorderLayout());

        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logTextArea);

        logPanel.add(logScrollPane, BorderLayout.CENTER);
    }

    private void setControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new SpringLayout());

        String[] buttonLabels = {"Login", "Generate new RSA key", "Local Storage", "Get file"};
        List<JButton> controlButtons = new ArrayList<JButton>();

        ControlButtonsListeners c = new ControlButtonsListeners();
        c.setClientAPI(clientAPI);
        c.setMainFrame(this);

        for (int i = 0; i < buttonLabels.length; i++) {
            controlButtons.add(new JButton(buttonLabels[i]));
            controlPanel.add(controlButtons.get(i));
            if (i > 0) {
                controlButtons.get(i).setVisible(false);
            }
        }

        c.setControlButtons(controlButtons);
        ActionListener[] buttonListeners = {
                c.getLoginButtonListener(),
                c.getGenerateRSAKeyButtonListener(),
                c.getLocalStorage(),
                c.getSendFilenameButtonListener()
        };

        for (int i = 0; i < buttonLabels.length; i++) {
            controlButtons.get(i).addActionListener(buttonListeners[i]);
        }

        filenameTextField = new JTextField("Filename");
        controlPanel.add(filenameTextField);

        int startX = 10;
        int startY = 10;
        int marginX = 10;
        int marginY = 10;
        int colsNumber = 1;
        SpringUtilities.makeGrid(controlPanel,
                buttonLabels.length + 1, colsNumber, //rows, cols; 1 means adding a textField (filename)
                startX, startY,        //initX, initY
                marginX, marginY);
    }
}
