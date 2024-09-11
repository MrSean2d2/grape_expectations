package seng202.team5.services;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.exceptions.NotFoundException;
import seng202.team5.models.User;
import seng202.team5.repository.DatabaseService;
import seng202.team5.repository.UserDAO;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

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

    /**
     * Constructor
     */
    public UserService() {
        this.userDAO = new UserDAO();
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

    public ObjectProperty<User> getUserProperty() {
        return currentUser;
    }

    /**
     * Set the current user to the passed in user
     *
     */
    public void setCurrentUser(User user) {
        currentUser.set(user);
    }

    public User getCurrentUser() {
        return currentUser.get();
    }


    /**
     * Register a user if possible
     * @return user if they register
     */
    public User registerUser(String username, String password) {
        try {
            String hashedPassword = hashPassword(password, UserService.generateSalt());;
            if (username.equals("") || password.equals(""))
                return null;
            if (!userDAO.userIsUnique(username))
                return null;
            int userCount = (userDAO.getAll()).size();
            User user = new User(userCount, username, hashedPassword, "user");
            userDAO.add(user);
            return user;
        } catch (DuplicateEntryException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error(e);
            return null;
        }
    }

    /**
     * Sign in to account
     * @return user if they sign in
     */
    public User signinUser(String username, String password) {
        // Get password from database if user exists
        try {
            User userAccount = userDAO.getFromUserName(username);
            String hashPassword = userAccount.getPassword();

            // Check if user's password matches
            if(verifyPassword(password, hashPassword)) {
                return userAccount;
            }
        } catch (NotFoundException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error(e);
        }
        return null;
    }

    /**
     * Sign out of account
     */
    public void signOut() {
        currentUser.set(null);
    }

    /**
     * Generate a salt for the hashed password
     * @return bytearray for the salt
     */
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Generate a hashed version of the password
     * @param password the password to hash
     * @return hashed password as a string
     */
    public static String hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, HASH_LENGTH * 8);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();

        // Combine salt and hash for storage in the DB
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashBase64 = Base64.getEncoder().encodeToString(hash);

        return saltBase64 + ":" + hashBase64;
    }

    /**
     * Verify a password
     * @param password the password to check
     * @param hashedPassword the hashed password
     * @return hashed password as a string
     */
    public static boolean verifyPassword(String password, String hashedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = hashedPassword.split(":");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        String storedHashBase64 = parts[1];

        String inputHash = hashPassword(password, salt).split(":")[1];

        return storedHashBase64.equals(inputHash);
    }
}
