package pl.darsonn.forms;

import pl.darsonn.Main;
import pl.darsonn.login.LoginValidation;
import pl.darsonn.utils.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginForm {
    public JPanel panel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private void loginButtonActivated() {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        User user = LoginValidation.checkCredentials(username, password);
        if(user != null) {
            Main.successfullyLogin(user);
        } else {
            JOptionPane.showMessageDialog(panel, "Dane nie są poprawne!");
            usernameField.setText("");
            passwordField.setText("");
        }
    }

    public LoginForm() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginButtonActivated();
            }
        });
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButtonActivated();
                }
            }
        });
    }
}
