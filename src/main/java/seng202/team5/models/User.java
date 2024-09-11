package seng202.team5.models;

import java.util.Objects;

public class User {
    private int id;
    private String username = null;
    private String password = null;
    private String role = "user";
    private int icon = -1;


    /**
     * Initialise a new User
     *
     * @param id user's unique identification number
     * @param username user's unique username
     * @param password user's (hashed) password
     * @param role user's role
     * @param icon user's icon number
     */
    public User(int id, String username, String password, String role, int icon) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.icon = icon;
    }

    public int getId() {
    return id;
}

    public String getUsername() {
        return username;
    }

    /**
     * Gets the file path of the user's given icon
     *
     * @return a file path to the user's icon
     */
    public String getIcon() {
        String iconPath = switch (icon) {
            case 0, 1, 2, 3, 4 -> // This doesn't need a switch statement, I just couldn't be bothered to check if outside bounds
                    "/images/user_profile" + icon + ".png";
            default -> "/images/user_profile0.png";
        };
        return iconPath;
    }

    public int getIconNumber() {
        return icon;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public boolean getIsAdmin() {
        return (Objects.equals(role, "admin"));
    }
}
