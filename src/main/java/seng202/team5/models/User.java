package seng202.team5.models;

import java.util.Objects;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

/**
 * Representation of user.
 *
 * @author Martyn Gascoigne
 */
public class User {
    private int id;
    private StringProperty username;
    private StringProperty password;
    private ObjectProperty<Role> role;
    private IntegerProperty icon;

    /**
     * Initialise a new User without id.
     *
     * @param username user's unique username
     * @param password user's (hashed) password
     * @param role     user's role
     * @param icon     user's icon number
     */
    public User(String username, String password, Role role, int icon) {
        setUsername(username);
        setPassword(password);
        setRole(role);
        iconProperty().set(icon);
    }

    /**
     * Initialise a new User.
     *
     * @param id       user's unique identification number
     * @param username user's unique username
     * @param password user's (hashed) password
     * @param role     user's role
     * @param icon     user's icon number
     */
    public User(int id, String username, String password, Role role, int icon) {
        this.id = id;
        setUsername(username);
        setPassword(password);
        setRole(role);
        iconProperty().set(icon);
    }

    /**
     * Property getter for name.
     *
     * @return the StringProperty for the username
     */
    public final StringProperty usernameProperty() {
        if (username == null) {
            username = new SimpleStringProperty();
        }
        return username;
    }

    /**
     * Property getter for (hashed) password.
     *
     * @return the StringProperty for the password
     */
    public final StringProperty passwordProperty() {
        if (password == null) {
            password = new SimpleStringProperty();
        }
        return password;
    }

    /**
     * Property getter for the user's role.
     *
     * @return the ObjectProperty for the Role enum type
     */
    public final ObjectProperty<Role> roleProperty() {
        if (role == null) {
            role = new SimpleObjectProperty<>();
        }
        return role;
    }

    /**
     * Property getter for the user's icon number.
     *
     * @return the IntegerProperty of the icon number
     */
    public final IntegerProperty iconProperty() {
        if (icon == null) {
            icon = new SimpleIntegerProperty();
        }
        return icon;
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
    public final String getUsername() {
        return usernameProperty().get();
    }

    /**
     * Gets the file path of the user's given icon.
     *
     * @return a file path to the user's icon
     */
    public String getIcon() {
        String iconPath = switch (iconProperty().get()) {
            case 0, 1, 2, 3, 4 -> "/images/user_profile" + iconProperty().get() + ".png";
            default -> "/images/user_profile0.png";
        };
        return iconPath;
    }


    /**
     * Gets the user's icon number.
     *
     * @return the user's icon number
     */
    public final int getIconNumber() {
        return iconProperty().get();
    }


    /**
     * Gets the user's password
     * This should be hashed, as the user object will be
     * created using the values in the database.
     *
     * @return the user's (potentially hashed) password
     */
    public final String getPassword() {
        return passwordProperty().get();
    }


    /**
     * Gets the user's role.
     *
     * @return the user's role
     */
    public final Role getRole() {
        return roleProperty().get();
    }


    /**
     * Check for if the user is an admin.
     *
     * @return true if the user is an admin, false otherwise
     */
    public boolean getIsAdmin() {
        return (roleProperty().get() == Role.ADMIN);
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
    public void setRole(Role role) {
        roleProperty().set(role);
    }


    /**
     * Sets the user's username.
     */
    public void setUsername(String username) {
        usernameProperty().set(username);
    }

    /**
     * Set the password for the user. This should not be a plaintext password and
     * should be set by
     * {@link seng202.team5.services.UserService#updateUserPassword(User, String)}
     *
     * @param password the user's new hashed password
     */
    public void setPassword(String password) {
        passwordProperty().set(password);
    }

    /**
     * Defines extractor callback to be used by Observable lists.
     *
     * @return a Callback containing an array of Observable properties of this Object
     */
    public static Callback<User, Observable[]> extractor() {
        return (User u) -> new Observable[]{u.usernameProperty(), u.passwordProperty(),
        u.roleProperty(), u.iconProperty()};
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
        return id == user.id && getIconNumber() == user.getIconNumber()
                && Objects.equals(getUsername(), user.getUsername())
                && Objects.equals(getPassword(), user.getPassword())
                && Objects.equals(getRole(), user.getRole());
    }
}
