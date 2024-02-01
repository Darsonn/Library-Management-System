package pl.darsonn.forms;

import pl.darsonn.login.LoginDatabaseOperation;
import pl.darsonn.login.LoginValidation;
import pl.darsonn.utils.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChangePasswordForm {
    public JPanel panel;
    private JLabel usernameLabel;
    private JButton changePasswordButton;
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmNewPasswordField;
    private final User user;
    private final LoginDatabaseOperation loginDatabaseOperation;

    public ChangePasswordForm(User user) {
        this.user = user;
        loginDatabaseOperation = new LoginDatabaseOperation();

        usernameLabel.setText(user.getUsername());
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(oldPasswordField.getPassword().length < 4 ||
                        newPasswordField.getPassword().length < 4 ||
                        confirmNewPasswordField.getPassword().length < 4) {
                    JOptionPane.showMessageDialog(panel, "Password is too short!");
                    return;
                }

                if(newPasswordField.getPassword() == confirmNewPasswordField.getPassword()) {
                    JOptionPane.showMessageDialog(panel, "The new password does not match the confirmation password");
                    return;
                }

                if(loginDatabaseOperation.checkIfCorrectLoginData(user.getUsername(), String.valueOf(oldPasswordField.getPassword()))) {
                    JOptionPane.showMessageDialog(panel, "The old password does not match!");
                    return;
                }

                LoginValidation.changePassword(user.getUsername(), String.valueOf(newPasswordField.getPassword()));

                JOptionPane.showMessageDialog(panel, "Password changed successfully");

                JFrame jFrame = (JFrame) SwingUtilities.getWindowAncestor(panel);
                jFrame.setVisible(false);
                jFrame.dispose();
            }
        });
    }
}
