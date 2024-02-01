package pl.darsonn.forms;

import pl.darsonn.Main;

import javax.swing.*;

public class WelcomeForm {
    public JPanel panel;
    private JLabel welcomeMessage;
    private JButton button1;

    public WelcomeForm(String username) {
        welcomeMessage.setText("Welcome " + username + "!");
        button1.addActionListener(e -> Main.intoMainForm());
    }
}
