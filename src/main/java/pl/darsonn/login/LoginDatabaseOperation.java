package pl.darsonn.login;

import pl.darsonn.database.DatabaseOperation;
import pl.darsonn.utils.User;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginDatabaseOperation extends DatabaseOperation {
    private final Connection connection;
    private Statement statement;

    public LoginDatabaseOperation() {
        connection = super.getConnection();
    }

    public boolean checkIfUserExits(String username) {
        String request = "SELECT * FROM `users` HAVING `Username` = '" + username + "';";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(request);
            while (rs.next()) {
                if(!rs.getString("ID").isEmpty()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            logError(e);
        }
        return false;
    }

    public boolean checkIfCorrectLoginData(String username, String encryptedPassword) {
        String request = "SELECT * FROM `users` WHERE `Username` = '" + username + "' && `Password` = '" + encryptedPassword + "';";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(request);
            while (rs.next()) {
                if(!rs.getString("ID").isEmpty()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            logError(e);
        }
        return false;
    }

    public User getUserByUsername(String username) {
        String request = "SELECT * FROM `users` WHERE `Username` = '" + username + "';";

        try(PreparedStatement preparedStatement = connection.prepareStatement(request)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                int permissions = resultSet.getInt("Permissions");
                String lastLogin = resultSet.getString("LastLogin");

                return new User(id, username, permissions, lastLogin);
            }
        } catch (SQLException e) {
            logError(e);
        }
        return null;
    }

    public void changeUsersPassword(String username, String encryptedPassword) {
        String request = "UPDATE `users` SET `Password` = ? WHERE `Username` = ?;";
        try (final var statement = connection.prepareStatement(request)) {
            statement.setString(1, encryptedPassword);
            statement.setString(2, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            logError(e);
        }
    }

    public void updateLastLogin(String username) {
        String request = "UPDATE `users` SET `LastLogin` = ? WHERE `Username` = ?;";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try (final var statement = connection.prepareStatement(request)) {
            statement.setString(1, simpleDateFormat.format(date));
            statement.setString(2, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            logError(e);
        }
    }

    /*
    1 - normal read-only
    2 - super writable
    3 - admin
     */
}
