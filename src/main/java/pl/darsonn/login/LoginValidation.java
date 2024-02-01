package pl.darsonn.login;

import pl.darsonn.utils.User;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginValidation {
    private static String encryptedPassword;
    private static final LoginDatabaseOperation loginDatabaseOperation = new LoginDatabaseOperation();
    public static User checkCredentials(String username, String password) {
        if(username.length() < 5) return null;
        if(password.length() < 8) return null;

        if(!loginDatabaseOperation.checkIfUserExits(username)) return null;

        encryptPassword(password.getBytes(StandardCharsets.UTF_8));

        if(!loginDatabaseOperation.checkIfCorrectLoginData(username, encryptedPassword)) return null;

        User user = loginDatabaseOperation.getUserByUsername(username);

        loginDatabaseOperation.updateLastLogin(username);

        return user;
    }

    public static void changePassword(String username, String password) {
        encryptPassword(password.getBytes(StandardCharsets.UTF_8));

        loginDatabaseOperation.changeUsersPassword(username, encryptedPassword);
    }

    public static void encryptPassword(byte[] password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            BigInteger bigInt = new BigInteger(1, messageDigest.digest(password));
            encryptedPassword = bigInt.toString(16);

            while(encryptedPassword.length() < 32 ){
                encryptedPassword = "0" + encryptedPassword;
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getEncryptedPassword() {
        return encryptedPassword;
    }
}
