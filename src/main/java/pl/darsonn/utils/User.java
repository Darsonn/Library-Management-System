package pl.darsonn.utils;

public class User {
    private final int ID;
    private final String username;
    private final int permissions;
    private final String lastLogin;

    public User(int ID, String username, int permissions, String lastLogin) {
        this.ID = ID;
        this.username = username;
        this.permissions = permissions;
        this.lastLogin = lastLogin;
    }

    public int getID() {
        return ID;
    }

    public String getUsername() {
        return username;
    }

    public int getPermissions() {
        return permissions;
    }

    public String getLastLogin() {
        return lastLogin;
    }
}
