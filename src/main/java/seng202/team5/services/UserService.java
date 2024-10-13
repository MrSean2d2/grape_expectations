package seng202.team5.services;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.exceptions.InvalidUserIdException;
import seng202.team5.exceptions.NotFoundException;
import seng202.team5.exceptions.PasswordIncorrectException;
import seng202.team5.models.Role;
import seng202.team5.models.User;
import seng202.team5.repository.UserDAO;

/**
 * Class to store the current user and actions regarding user account
 * Such as registering, logging in, signing out.
 *
 * @author Martyn Gascoigne
 */
public class UserService {
    private static final Logger log = LogManager.getLogger(UserService.class);

    // Hashing
    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 64;
    private static final int ITERATIONS = 10000;

    // DAO
    private final UserDAO userDAO;
    private static UserService instance;

    private static final ObjectProperty<User> currentUser = new SimpleObjectProperty<>(null);

    private User selectedUser;
    private boolean adminDefaultPassword;

    /**
     * Constructor for UserService.
     */
    private UserService() {
        this.userDAO = new UserDAO();
        adminDefaultPassword = false;
        if (userDAO.getAdminCount() == 0) {
            User admin = registerUser("admin", "admin");
            if (admin != null) {
                admin.setRole(Role.ADMIN);
                userDAO.update(admin);
                adminDefaultPassword = true;
            }
        }
    }

    /**
     * Returns true if the admin user has not changed the default password.
     *
     * @return true if the admin password needs changing, false otherwise
     */
    public boolean isAdminDefaultPassword() {
        return this.adminDefaultPassword;
    }

    /**
     * Sets the admin password as changed and no longer needing changing.
     */
    public void changedAdminPassword() {
        this.adminDefaultPassword = false;
    }

    /**
     * Singleton method to get the current instance if it exists. Otherwise,
     * create one.
     *
     * @return the singleton instance of {@link UserService}
     */
    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    /**
     * WARNING: Sets the current singleton instance to null! Should only be used
     * for testing.
     */
    public static void removeInstance() {
        instance = null;
    }

    /**
     * Gets the current user property.
     * This uses an ObjectProperty type as it is observable,
     * and this is used for getting the user's name.
     *
     * @return the current user
     */
    public ObjectProperty<User> getUserProperty() {
        return currentUser;
    }

    /**
     * Set the current user to the passed in user.
     *
     * @param user to set current user to
     */
    public void setCurrentUser(User user) {
        currentUser.set(user);
    }

    /**
     * Gets current user.
     *
     * @return current user
     */
    public User getCurrentUser() {
        return currentUser.get();
    }

    /**
     * Gets selected user.
     *
     * @return selected user
     */
    public User getSelectedUser() {
        return selectedUser;
    }

    /**
     * Sets selected user.
     *
     * @param user selected user
     */
    public void setSelectedUser(User user) {
        this.selectedUser = user;
    }

    /**
     * Checks the username and returns an appropriate error message, or null if
     * everything is all good.
     *
     * @param name the username to check
     * @return an error message or null if valid
     */
    public String checkName(String name) {
        if (name.isEmpty()) {
            return "Username cannot be empty!";
        } else if (name.length() < 4 || name.length() > 20) {
            return "Username must be between 4 and 20 characters!";
        } else {
            return null;
        }
    }

    /**
     * Check the given password and return a suitable error message or null if
     * all valid.
     *
     * @param password the password to check
     * @return an error message or null if valid
     */
    public String checkPassword(String password) {
        if (password.isEmpty()) {
            return "Password cannot be empty!";
        } else if (password.length() < 8) {
            return "Password must contain at least 8 characters!";
        } else if (password.length() > 20) {
            return "Password cannot exceed 20 characters!";
        } else {
            Pattern letter = Pattern.compile("[a-zA-z]");
            Matcher hasLetter = letter.matcher(password);
            Pattern digit = Pattern.compile("[0-9]");
            Matcher hasDigit = digit.matcher(password);
            Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
            Matcher hasSpecial = special.matcher(password);
            if (!hasLetter.find()) {
                return "Password must contain alphanumeric characters!";
            } else if (!hasDigit.find()) {
                return "Password must contain a numeric character!";
            } else if (!hasSpecial.find()) {
                return "Password must contain a special character!";
            } else {
                return null;
            }
        }
    }


    /**
     * Attempt to register a user, if they don't already exist.
     *
     * @param username the user's username
     * @param password the user's password
     * @return user if they register
     */
    public User registerUser(String username, String password) {
        try {
            String hashedPassword = hashPassword(password, UserService.generateSalt());
            if (username.isEmpty() || password.isEmpty()) {

                return null;
            }

            if (!userDAO.userIsUnique(username)) {
                return null;
            }

            Random rand = new Random();
            int iconNum = rand.nextInt(0, 5); // Generate a new (random) icon number

            User user = new User(username, hashedPassword, Role.USER, iconNum);

            // Get the user id (autoincremented by database)
            int userId = userDAO.add(user);

            // Check if the user id is valid
            if (userId == -1) {
                throw new InvalidUserIdException("User id is invalid! = -1");
            }

            // Update user ID
            user.setId(userId);

            return user;
        } catch (DuplicateEntryException
                 | NoSuchAlgorithmException
                 | InvalidKeySpecException
                 | InvalidUserIdException e) {
            log.error(e);
            return null;
        }
    }

    /**
     * Update the user's password. This updates the user in place as well as
     * returning the altered user.
     *
     * @param user the user to change
     * @param password the user's new password
     * @return the altered user
     */
    public User updateUserPassword(User user, String password) {
        String oldPassword = user.getPassword();
        try {
            String hashedPassword = hashPassword(password, generateSalt());
            user.setPassword(hashedPassword);
            return user;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error(e);
            /* Reset the password to ensure that the exception thrown didn't
            leave the user in an inconsistent state. */
            user.setPassword(oldPassword);
            return user;
        }
    }


    /**
     * Delete the specified user from the database. The user object must be
     * able to identify itself.
     *
     * @param user the user to delete
     */
    public void deleteUser(User user) {
        if (!user.getIsAdmin() || userDAO.getAdminCount() > 1) {
            userDAO.delete(user.getId());
        }
    }

    /**
     * Attempt to sign in to an account if they exist.
     *
     * @param password of user signing in
     * @param username of user signing in
     * @return user if they sign in
     * @throws NotFoundException exception for if user not found
     * @throws PasswordIncorrectException exception if entered password incorrect
     */
    public User signinUser(String username, String password) throws
            NotFoundException, PasswordIncorrectException {
        // Get password from database if user exists
        try {
            User userAccount = userDAO.getFromUserName(username);
            String hashPassword = userAccount.getPassword();

            // Check if user's password matches
            if (verifyPassword(password, hashPassword)) {
                return userAccount;
            }
        } catch (NotFoundException | InvalidKeySpecException e) {
            throw new NotFoundException(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            log.error(e);
        }
        return null;
    }

    /**
     * Sign out of account.
     */
    public void signOut() {
        currentUser.set(null);
    }

    /**
     * Generate a salt for the hashed password.
     *
     * @return bytearray for the salt
     */
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Generate a hashed version of the password.
     *
     * @param salt to hash password
     * @param password the password to hash
     * @return hashed password as a string
     * @throws NoSuchAlgorithmException if no algorithm found
     * @throws InvalidKeySpecException if invalid key enterd
     */
    public static String hashPassword(String password, byte[] salt)
            throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, HASH_LENGTH * 8);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();

        // Combine salt and hash for storage in the DB
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashBase64 = Base64.getEncoder().encodeToString(hash);

        return saltBase64 + ":" + hashBase64;
    }

    /**
     * Verify a password.
     *
     * @param password the password to check
     * @param hashedPassword the hashed password
     * @return hashed password as a string
     * @throws NoSuchAlgorithmException if password hashing fails
     * @throws InvalidKeySpecException if password hashing fails
     * @throws PasswordIncorrectException if the password is incorrect
     */
    public static boolean verifyPassword(String password, String hashedPassword)
            throws NoSuchAlgorithmException,
            InvalidKeySpecException, PasswordIncorrectException {
        String[] parts = hashedPassword.split(":");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        String storedHashBase64 = parts[1];

        String inputHash = hashPassword(password, salt).split(":")[1];

        boolean equals = storedHashBase64.equals(inputHash);

        if (!equals) {
            throw new PasswordIncorrectException("Password incorrect");
        }
        return equals;
    }

}
