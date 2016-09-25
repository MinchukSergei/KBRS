package client.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by USER on 25.09.2016.
 */
public class FileFrame extends JFrame {
    private JTextArea fileTextArea;

    public void setDefaultSettings() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(500, 600));
        setVisible(true);
        pack();
    }

    public FileFrame(String filename) {
        setTitle(filename);
        setContent();
    }

    private void setContent() {
        setMainPanel();
    }

    public void appendText(String line) {
        fileTextArea.append(line);
    }


    private void setMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        fileTextArea = new JTextArea();
        fileTextArea.setEditable(false);

        JScrollPane fileScrollPane = new JScrollPane(fileTextArea);

        mainPanel.add(fileScrollPane, BorderLayout.CENTER);
        add(mainPanel);
    }
}
