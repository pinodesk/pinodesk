package com.gitlab.muhammadkholidb.bianglala;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.util.SystemInfo;

import net.miginfocom.swing.MigLayout;

public class Bianglala {

    private static JPanel createPanel() {
        JPanel panel = new JPanel(new MigLayout("fill"));

        panel.add(new JLabel("First Name"));
        panel.add(new JTextField(10));
        panel.add(new JLabel("Surname"), "gap unrelated"); // Unrelated size is resolved per platform
        panel.add(new JTextField(10), "wrap"); // Wraps to the next row
        panel.add(new JLabel("Address"));
        panel.add(new JTextField(), "span, grow"); // Spans cells in row and grows to fit that

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (SystemInfo.isMacOS && System.getProperty("apple.laf.useScreenMenuBar") == null) {
                    System.setProperty("apple.laf.useScreenMenuBar", "true");
                }
                try {
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                JFrame frame = new JFrame("Example 01");
                frame.getContentPane().add(createPanel());
                frame.pack();
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}