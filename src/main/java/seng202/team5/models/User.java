package seng202.team5.models;

import java.util.Objects;

/**
 * Representation of user.
 *
 * @author Martyn Gascoigne
 */
public class User {
    private int id;
    private String username;
    private final String password;
    private String role;
    private int icon;

    /**
     * Initialise a new User without id.
     *
     * @param username user's unique username
     * @param password user's (hashed) password
     * @param role user's role
     * @param icon user's icon number
     */
    public User(String username, String password, String role, int icon) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.icon = icon;
    }

    /**
     * Initialise a new User.
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

    /**
     * Gets the user's id.
     *
     * @return the user's id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the user's username.
     *
     * @return the user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the file path of the user's given icon.
     *
     * @return a file path to the user's icon
     */
    public String getIcon() {
        String iconPath = switch (icon) {
            case 0, 1, 2, 3, 4 ->
                    "/images/user_profile" + icon + ".png";
            default -> "/images/user_profile0.png";
        };
        return iconPath;
    }


    /**
     * Gets the user's icon number.
     *
     * @return the user's icon number
     */
    public int getIconNumber() {
        return icon;
    }


    /**
     * Gets the user's password
     * This should be hashed, as the user object will be
     * created using the values in the database.
     *
     * @return the user's (potentially hashed) password
     */
    public String getPassword() {
        return password;
    }


    /**
     * Gets the user's role.
     *
     * @return the user's role
     */
    public String getRole() {
        return role;
    }


    /**
     * Check for if the user is an admin.
     *
     * @return true if the user is an admin, false otherwise
     */
    public boolean getIsAdmin() {
        return (Objects.equals(role, "admin"));
    }


    /**
     * Sets the user's ID.
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Sets the user's role.
     */
    public void setRole(String role) {
        this.role = role;
    }


    /**
     * Sets the user's username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return id == user.id && icon == user.icon &&
                Objects.equals(username, user.username) &&
                Objects.equals(password, user.password) &&
                Objects.equals(role, user.role);
    }
}
