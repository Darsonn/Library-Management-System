package pl.darsonn;

import pl.darsonn.forms.LoginForm;
import pl.darsonn.forms.MainForm;
import pl.darsonn.forms.WelcomeForm;
import pl.darsonn.utils.User;

import javax.swing.*;

public class Main {
    private static JFrame jFrame;
    private static User user;
    public static void main(String[] args) {
        jFrame = new JFrame();

        openLoginForm();
    }

    public static void openLoginForm() {
        jFrame.setTitle("Login");
        jFrame.setContentPane(new LoginForm().panel);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
    }

    public static void successfullyLogin(User user) {
        Main.user = user;
        jFrame.setContentPane(new WelcomeForm(user.getUsername()).panel);
        jFrame.setTitle("Welcome");
        jFrame.pack();
    }

    public static void intoMainForm() {
        jFrame.setContentPane(new MainForm(user).panel);
        jFrame.setTitle("Library Management System");
        jFrame.pack();
    }
}