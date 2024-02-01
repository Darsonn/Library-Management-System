package pl.darsonn.database;

import pl.darsonn.login.LoginDatabaseOperation;
import pl.darsonn.utils.Book;
import pl.darsonn.utils.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseOperation {
    private Connection connection;
    private Statement statement;
    private static final Logger logger = Logger.getLogger(LoginDatabaseOperation.class.getName());

    public DatabaseOperation() {
        String request = "jdbc:mysql://localhost:3306/librarymanagementsystem?useUnicode=true&characterEncoding=utf8";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(request, "root", "");
            logger.log(Level.FINE, "Połączenie z bazą danych jest poprawne");
        } catch (ClassNotFoundException | SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
            System.exit(101);
        }
    }

    public Connection getConnection() {
        String request = "jdbc:mysql://localhost:3306/librarymanagementsystem?useUnicode=true&characterEncoding=utf8";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(request, "root", "");
            logger.log(Level.FINE, "Połączenie z bazą danych jest poprawne");
            return connection;

        } catch (ClassNotFoundException | SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
            System.exit(101);
            return null;
        }
    }

    public void logError(SQLException e) {
        logger.log(Level.WARNING, "Błąd podczas manipulacji danymi w bazie danych", e);
    }

    public List<User> getUsers() {
        List<User> userList = new ArrayList<>();

        String request = "SELECT * FROM `users`;";
        try(PreparedStatement preparedStatement = connection.prepareStatement(request)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String username = resultSet.getString("Username");
                int permissions = resultSet.getInt("Permissions");
                String lastLogin = resultSet.getString("LastLogin");

                userList.add(new User(id, username, permissions, lastLogin));
            }
        } catch (SQLException e) {
            logError(e);
        }

        return userList;
    }

    public void createNewUser(String username, String encryptedPassword, int permissionsLevel) {
        String request = "INSERT INTO `users`(`ID`, `Username`, `Password`, `Permissions`, `LastLogin`) VALUES (?,?,?,?,?)";
        try (final var statement = connection.prepareStatement(request)) {
            statement.setString(1, null);
            statement.setString(2, username);
            statement.setString(3, encryptedPassword);
            statement.setInt(4, permissionsLevel);
            statement.setTimestamp(5, new Timestamp(new Date().getTime()));
            statement.execute();
        } catch (SQLException e) {
            logError(e);
        }
    }

    public void deleteUserByUsername(String username) {
        String request = "DELETE FROM `users` WHERE `Username` = ?;";
        try (final var statement = connection.prepareStatement(request)) {
            statement.setString(1, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            logError(e);
        }
    }

    public void updateUsername(String oldUsername, String newUsername) {
        String request = "UPDATE `users` SET `Username` = ? WHERE `Username` = '" + oldUsername + "';";
        try (final var statement = connection.prepareStatement(request)) {
            statement.setString(1, newUsername);
            statement.executeUpdate();
        } catch (SQLException e) {
            logError(e);
        }
    }

    public void updateUsersPermissionsLevel(String username, int permissionsLevel) {
        String request = "UPDATE `users` SET `Permissions` = ? WHERE `Username` = '" + username + "';";
        try (final var statement = connection.prepareStatement(request)) {
            statement.setInt(1, permissionsLevel);
            statement.executeUpdate();
        } catch (SQLException e) {
            logError(e);
        }
    }

    public boolean checkIfUsernameIsTaken(String username) {
        String request = "SELECT `Username` FROM `users` WHERE `Username` = '" + username + "';";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(request);
            while (rs.next()) {
                if(!rs.getString("Username").isEmpty()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            logError(e);
        }
        return false;
    }

    public Book getBookByISBNNumber(String ISBN) {
        String request = "SELECT * FROM `books` WHERE `ISBN` = '" + ISBN + "';";

        try(PreparedStatement preparedStatement = connection.prepareStatement(request)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String title = resultSet.getString("Title");
                String author = resultSet.getString("Author");
                String genre = resultSet.getString("Genre");

                return new Book(ISBN, title, author, genre);
            }
        } catch (SQLException e) {
            logError(e);
        }
        return null;
    }

    public Book getBookByTitle(String bookTitle) {
        String request = "SELECT * FROM `books` WHERE `Title` = '" + bookTitle + "' LIMIT 1;";

        try(PreparedStatement preparedStatement = connection.prepareStatement(request)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String ISBN = resultSet.getString("ISBN");
                String author = resultSet.getString("Author");
                String genre = resultSet.getString("Genre");

                return new Book(ISBN, bookTitle, author, genre);
            }
        } catch (SQLException e) {
            logError(e);
        }
        return null;
    }

    public Book getBookByAuthor(String author) {
        String request = "SELECT * FROM `books` WHERE `Author` = '" + author + "' LIMIT 1;";

        try(PreparedStatement preparedStatement = connection.prepareStatement(request)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String ISBN = resultSet.getString("ISBN");
                String title = resultSet.getString("Title");
                String genre = resultSet.getString("Genre");

                return new Book(ISBN, title, author, genre);
            }
        } catch (SQLException e) {
            logError(e);
        }
        return null;
    }

    public void createNewBook(String ISBN, String title, String author, String genre) {
        String request = "INSERT INTO `books`(`ISBN`, `Title`, `Author`, `Genre`) VALUES (?,?,?,?)";
        try (final var statement = connection.prepareStatement(request)) {
            statement.setString(1, ISBN);
            statement.setString(2, title);
            statement.setString(3, author);
            statement.setString(4, genre);
            statement.execute();
        } catch (SQLException e) {
            logError(e);
        }
    }

    public void deleteBookByISBN(String ISBN) {
        String request = "DELETE FROM `books` WHERE `ISBN` = ?;";
        try (final var statement = connection.prepareStatement(request)) {
            statement.setString(1, ISBN);
            statement.executeUpdate();
        } catch (SQLException e) {
            logError(e);
        }
    }

    public List<Book> getBooks() {
        List<Book> bookList = new ArrayList<>();

        String request = "SELECT * FROM `books` LIMIT 25;";
        try(PreparedStatement preparedStatement = connection.prepareStatement(request)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String ISBN = resultSet.getString("ISBN");
                String title = resultSet.getString("Title");
                String author = resultSet.getString("Author");
                String genre = resultSet.getString("Genre");

                bookList.add(new Book(ISBN, title, author, genre));
            }
        } catch (SQLException e) {
            logError(e);
        }

        return bookList;
    }
}
