package client.gui.action_frames;

import client.gui.FileFrame;
import security.FileEncrypter;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.FileNotFoundException;
import java.sql.SQLException;

/**
 * Created by USER on 19.11.2016.
 */
public class StorageFrame extends JFrame {
    private String[] filenames;

    public StorageFrame(String title) {
        super(title);
        setTitle(title);
        setPreferredSize(new Dimension(350, 200));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

    }

    public void setFilenames(String[] filenames) {
        this.filenames = filenames;
    }

    public void setGui() {
        final JList<String> list = new JList<>(filenames);
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);

        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    FileEncrypter fileEncrypter = new FileEncrypter();
                    try {
                        if (((JList) e.getSource()).getSelectedValue() != null) {
                            String file = fileEncrypter.getClientFile((String) ((JList) e.getSource()).getSelectedValue());
                            FileFrame fileFrame = new FileFrame((String) ((JList) e.getSource()).getSelectedValue());
                            fileFrame.appendText(file);
                            fileFrame.setDefaultSettings();
                            list.clearSelection();
                        }
                    } catch (FileNotFoundException | SQLException e1) {
                        JOptionPane.showMessageDialog(null, e1.getMessage());
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(list);
        this.add(scrollPane);
        pack();
        setVisible(true);
    }
}
