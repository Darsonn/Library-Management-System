package pl.darsonn.forms;

import pl.darsonn.Main;
import pl.darsonn.database.DatabaseOperation;
import pl.darsonn.login.LoginValidation;
import pl.darsonn.utils.Book;
import pl.darsonn.utils.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainForm {
    public JPanel panel;
    private JTabbedPane tabbedPane1;
    private JButton createNewUserButton;
    private JButton changeUsersUsernameButton;
    private JButton changeUsersPermissionsButton;
    private JButton deleteUserButton;
    private JTable table1;
    private JButton reloadButton;
    private JButton searchByISBNButton;
    private JButton searchByTitleButton;
    private JButton searchByAuthorButton;
    private JTable table2;
    private JButton resetPasswordButton;
    private JLabel usernameLabel;
    private JLabel permissionsLabel;
    private JLabel lastLoginLabel;
    private JButton logoutButton;
    private JButton createNewBookButton;
    private JButton deleteBookButton;
    private JButton showAllButton;
    private JButton searchByGenreButton;
    private final User user;
    private static final DatabaseOperation databaseOperation = new DatabaseOperation();

    private String[][] getUsersElementInArray() {
        List<User> users = databaseOperation.getUsers();
        String[][] listContent = new String[users.size()-1][3];

        int i = 0;
        for(User user1 : users) {
            if(user1.getID() != user.getID()) {
                listContent[i][0] = String.valueOf(user1.getID());
                listContent[i][1] = user1.getUsername();
                listContent[i][2] = String.valueOf(user1.getPermissions());
                i++;
            }
        }

        return listContent;
    }

    private String[][] getBooksElementsInArray() {
        List<Book> books = databaseOperation.getBooks();
        String[][] listContent = new String[books.size()][4];

        int i = 0;
        for(Book book : books) {
            listContent[i][0] = String.valueOf(book.getISBN());
            listContent[i][1] = book.getTitle();
            listContent[i][2] = book.getAuthor();
            listContent[i][3] = book.getGenre();
            i++;
        }

        return listContent;
    }

    public MainForm(User user) {
        this.user = user;

        usernameLabel.setText(user.getUsername());
        permissionsLabel.setText(String.valueOf(user.getPermissions()));
        lastLoginLabel.setText(user.getLastLogin());

        if(user.getPermissions() != 3) {
            tabbedPane1.setEnabledAt(1, false);
        }
        if(user.getPermissions() < 2) {
            createNewBookButton.setEnabled(false);
            deleteBookButton.setEnabled(false);
        }
        reloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadButton.setEnabled(false);

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        reloadButton.setEnabled(true);
                    }
                }, 1500);

                String[] columnsName = {"ID", "Username", "Permissions"};
                String[][] newData = getUsersElementInArray();

                DefaultTableModel model = new DefaultTableModel(newData, columnsName);
                table1.setModel(model);
            }
        });
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton clickedButton = (JButton) e.getSource();

                Book book;
                String answer;
                if(clickedButton == searchByAuthorButton) {
                    answer = JOptionPane.showInputDialog(panel, "Insert author");
                    book = databaseOperation.getBookByAuthor(answer);

                    if(book != null) {
                        reloadBookTable(book);
                    } else {
                        JOptionPane.showMessageDialog(panel, "Failed to find book.\n" +
                                        "Check provided information!", "Searching book",
                                JOptionPane.WARNING_MESSAGE, null);
                    }
                } else if (clickedButton == searchByISBNButton) {
                    answer = JOptionPane.showInputDialog(panel, "Insert ISBN");

                    book = databaseOperation.getBookByISBNNumber(answer);

                    if(book != null) {
                        reloadBookTable(book);
                    } else {
                        JOptionPane.showMessageDialog(panel, "Failed to find book.\n" +
                                        "Check provided information!", "Searching book",
                                JOptionPane.WARNING_MESSAGE, null);
                    }
                } else {
                    answer = JOptionPane.showInputDialog(panel, "Insert title");

                    book = databaseOperation.getBookByTitle(answer);

                    if(book != null) {
                        reloadBookTable(book);
                    } else {
                        JOptionPane.showMessageDialog(panel, "Failed to find book.\n" +
                                        "Check provided information!", "Searching book",
                                JOptionPane.WARNING_MESSAGE, null);
                    }
                }
            }
        };
        searchByISBNButton.addActionListener(listener);
        searchByTitleButton.addActionListener(listener);
        searchByAuthorButton.addActionListener(listener);
        resetPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Change password");
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                frame.setContentPane(new ChangePasswordForm(user).panel);
                frame.pack();
                frame.setVisible(true);
            }
        });
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(panel, "Successfully logged out.");

                JFrame jFrame = (JFrame) SwingUtilities.getWindowAncestor(panel);
                jFrame.setVisible(false);
                jFrame.dispose();

                Main.openLoginForm();
            }
        });
        createNewUserButton.addActionListener(e -> createNewUser());

        ActionListener listener1 = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton clickedButton = (JButton) e.getSource();

                int id = Integer.parseInt((String) table1.getValueAt(table1.getSelectedRow(), 0));
                String username = String.valueOf(table1.getValueAt(table1.getSelectedRow(), 1));
                int permissions = Integer.parseInt((String) table1.getValueAt(table1.getSelectedRow(), 2));

                if(clickedButton == deleteUserButton) {
                    if(JOptionPane.showConfirmDialog(panel,
                            "ID: " + id + "\n" +
                                    "Username: " + username + "\n" +
                                    "Permissions level: " + permissions + "\n\n" +
                                    "Are you sure you want to delete this user?\n" +
                                    "This action cannot be reversed!", "Deleting a user",
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null) == 0) {
                        JOptionPane.showMessageDialog(panel, "User successfully deleted", "Deleting a user",
                                JOptionPane.INFORMATION_MESSAGE, null);

                        databaseOperation.deleteUserByUsername(username);
                    }
                } else if(clickedButton == changeUsersUsernameButton) {
                    String newUsername = JOptionPane.showInputDialog(panel, "Insert new username",
                            "Changing user's name", JOptionPane.WARNING_MESSAGE);

                    if(newUsername.length() < 5) {
                        JOptionPane.showMessageDialog(panel, "Failed to change username.\n" +
                                "The username you entered is too short!", "Changing user's name",
                                JOptionPane.WARNING_MESSAGE, null);
                    } else {
                        if(databaseOperation.checkIfUsernameIsTaken(newUsername)) {
                            JOptionPane.showMessageDialog(panel, "Failed to change username.\n" +
                                            "Provided username is already taken!", "Changing user's name",
                                    JOptionPane.WARNING_MESSAGE, null);
                        } else {
                            JOptionPane.showMessageDialog(panel, "The username has been changed.", "Changing user's name",
                                    JOptionPane.INFORMATION_MESSAGE, null);

                            databaseOperation.updateUsername(username, newUsername);
                        }
                    }
                } else {
                    String[] options = {"1", "2", "3"};
                    int newPermissionsLevel = Integer.parseInt((String) JOptionPane.showInputDialog(panel,
                            "Insert new permissions level", "Changing user's permissions",
                            JOptionPane.QUESTION_MESSAGE, null, options, String.valueOf(permissions)));

                    if(newPermissionsLevel == permissions)
                        JOptionPane.showMessageDialog(panel, "Failed to change user's permissions level.\n" +
                                        "The introduced permission level is the same as before", "Changing user's permissions",
                                JOptionPane.WARNING_MESSAGE, null);
                    else {
                        JOptionPane.showMessageDialog(panel, "User's permission level has been changed.",
                                "Changing user's permissions", JOptionPane.INFORMATION_MESSAGE, null);

                        databaseOperation.updateUsersPermissionsLevel(username, newPermissionsLevel);
                    }
                }
            }
        };
        changeUsersUsernameButton.addActionListener(listener1);
        changeUsersPermissionsButton.addActionListener(listener1);
        deleteUserButton.addActionListener(listener1);
        ActionListener listener2 = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton clickedButton = (JButton) e.getSource();

                if(clickedButton == createNewBookButton) {
                    String ISBN = JOptionPane.showInputDialog(panel, "Insert ISBN number",
                            "Creating new book", JOptionPane.QUESTION_MESSAGE);

                    if(databaseOperation.getBookByISBNNumber(ISBN) != null) {
                        JOptionPane.showMessageDialog(panel, "Failed to create new book.\n" +
                                        "Provided ISBN number is already in database!", "Creating new book",
                                JOptionPane.WARNING_MESSAGE, null);
                        return;
                    }

                    String title = JOptionPane.showInputDialog(panel, "Insert book's title",
                            "Creating new book", JOptionPane.QUESTION_MESSAGE);
                    String author = JOptionPane.showInputDialog(panel, "Insert book's author",
                            "Creating new book", JOptionPane.QUESTION_MESSAGE);
                    String genre = JOptionPane.showInputDialog(panel, "Insert book's genre",
                            "Creating new book", JOptionPane.QUESTION_MESSAGE);

                    databaseOperation.createNewBook(ISBN, title, author, genre);

                    JOptionPane.showMessageDialog(panel, "Book has been created.\n",
                            "Creating new book", JOptionPane.INFORMATION_MESSAGE, null);
                } else {
                    String ISBN = (String) table2.getValueAt(table2.getSelectedRow(), 0);
                    String title = (String) table2.getValueAt(table2.getSelectedRow(), 1);
                    String author = (String) table2.getValueAt(table2.getSelectedRow(), 2);
                    String genre = (String) table2.getValueAt(table2.getSelectedRow(), 3);

                    if(JOptionPane.showConfirmDialog(panel,
                            "ISBN: " + ISBN + "\n" +
                                    "Title: " + title + "\n" +
                                    "Author: " + author + "\n" +
                                    "Genre: " + genre + "\n\n" +
                                    "Are you sure you want to delete this book?\n" +
                                    "This action cannot be reversed!", "Deleting a book",
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null) == 0) {
                        JOptionPane.showMessageDialog(panel, "Book successfully deleted", "Deleting a book",
                                JOptionPane.INFORMATION_MESSAGE, null);

                        databaseOperation.deleteBookByISBN(ISBN);
                    }
                }
            }
        };
        createNewBookButton.addActionListener(listener2);
        deleteBookButton.addActionListener(listener2);
        showAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] columnsName = {"ISBN", "Title", "Author", "Genre"};
                String[][] newData = getBooksElementsInArray();

                DefaultTableModel model = new DefaultTableModel(newData, columnsName);
                table2.setModel(model);
            }
        });
    }

    private void createUIComponents() {
        String[] columnNames = {"ISBN", "Title", "Author", "Genre"};
        table2 = new JTable(getBooksElementsInArray(), columnNames);

        columnNames = new String[]{"ID", "Username", "Permissions"};
        table1 = new JTable(getUsersElementInArray(), columnNames);
    }

    private void createNewUser() {
        String username = JOptionPane.showInputDialog(panel, "Insert username",
                "Creating new user", JOptionPane.QUESTION_MESSAGE);

        if(username == null) return;
        if(username.length() < 5) {
            JOptionPane.showMessageDialog(panel, "The username you entered is too short!", "Creating new user",
                    JOptionPane.WARNING_MESSAGE, null);
            return;
        } else {
            if(databaseOperation.checkIfUsernameIsTaken(username)) {
                JOptionPane.showMessageDialog(panel, "Provided username is already taken!", "Creating new user",
                        JOptionPane.WARNING_MESSAGE, null);
                return;
            }
        }

        String password = JOptionPane.showInputDialog(panel, "Insert temp password",
                "Creating new user", JOptionPane.QUESTION_MESSAGE);

        if(password == null) return;
        if(password.length() < 8) {
            JOptionPane.showMessageDialog(panel, "The password you entered is too short!", "Creating new user",
                    JOptionPane.WARNING_MESSAGE, null);
            return;
        }

        LoginValidation.encryptPassword(password.getBytes());

        String encryptedPassword = LoginValidation.getEncryptedPassword();

        int newPermissionsLevel = Integer.parseInt((String) JOptionPane.showInputDialog(panel,
                "Insert permissions level", "Creating new user",
                JOptionPane.QUESTION_MESSAGE, null, new Object[]{"1", "2", "3"}, "1"));

        databaseOperation.createNewUser(username, encryptedPassword, newPermissionsLevel);

        JOptionPane.showMessageDialog(panel, "User has been created.\n",
                "Creating new user", JOptionPane.INFORMATION_MESSAGE, null);
    }

    private void reloadBookTable(Book book) {
        String[] columnsName = {"ISBN", "Title", "Author", "Genre"};
        String[][] newData = new String[1][4];

        newData[0][0] = book.getISBN();
        newData[0][1] = book.getTitle();
        newData[0][2] = book.getAuthor();
        newData[0][3] = book.getGenre();

        DefaultTableModel model = new DefaultTableModel(newData, columnsName);
        table2.setModel(model);
    }
}
