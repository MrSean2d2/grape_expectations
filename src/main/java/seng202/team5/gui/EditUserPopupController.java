package seng202.team5.gui;

import java.util.Optional;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seng202.team5.models.Role;
import seng202.team5.models.User;
import seng202.team5.repository.UserDAO;
import seng202.team5.services.OpenWindowsService;
import seng202.team5.services.UserService;


/**
 * A controller for the edit user window.
 *
 * @author Sean Reitsma
 */
public class EditUserPopupController extends PageController implements ClosableWindow {

    @FXML
    private ComboBox<Role> roleComboBox;

    @FXML
    private TextField nameField;

    @FXML
    private Label usernameLabel;

    @FXML
    private Button closeButton;

    @FXML
    private Button deleteButton;
    private User curUser;
    private boolean editingCurrentUser;

    private UserDAO userDAO;

    private ObservableList<User> userList;

    public void setDataList(ObservableList<User> list) {
        userList = list;
    }

    /**
     * Initialize the user property page.
     */
    @FXML
    private void initialize() {
        OpenWindowsService.getInstance().addWindow(this);

        User loggedInUser = UserService.getInstance().getCurrentUser();

        curUser = UserService.getInstance().getSelectedUser();
        editingCurrentUser = curUser.equals(loggedInUser);
        usernameLabel.setText("Editing User " + curUser.getUsername());
        nameField.setText(curUser.getUsername());
        roleComboBox.setDisable(editingCurrentUser);
        roleComboBox.getItems().setAll(Role.values());
        roleComboBox.getSelectionModel().select(curUser.getRole());

        userDAO = new UserDAO();

        boolean isAdmin = true;
        if (loggedInUser != null) {
            isAdmin = loggedInUser.getIsAdmin();
        }

        /*
         The user shouldn't be able to delete users if they aren't an admin.
         They shouldn't be able to see this page if they aren't an admin,
         but this is an extra safety measure.
         */
        if (editingCurrentUser || !isAdmin) {
            deleteButton.setVisible(false);
        }
    }

    /**
     * Close the window.
     */
    @FXML
    @Override
    public void closeWindow() {
        OpenWindowsService.getInstance().closeWindow(this);
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Submit the changes to the user.
     */
    @FXML
    private void submit() {
        curUser.setUsername(nameField.getText());
        curUser.setRole(roleComboBox.getSelectionModel().getSelectedItem());
        if (editingCurrentUser) {
            UserService.getInstance().setCurrentUser(curUser);
        }
        userDAO.update(curUser);
        addNotification("User updated", "#d5e958");
        closeWindow();
    }

    /**
     * Delete the tag we're editing.
     * We can only do this if the current user isn't the logged-in user.
     */
    @FXML
    private void delete() {
        // Check if tag exists
        if (!editingCurrentUser) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete User " + curUser.getUsername() + "?");
            alert.setHeaderText("Are you sure you want to delete this user?");
            alert.setContentText("This action is irreversible!");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Close and delete
                addNotification(String.format("Deleted user '%s'",
                        curUser.getUsername()), "#d5e958");
                UserService.getInstance().deleteUser(curUser);
                userList.remove(curUser);
                closeWindow();
            }
        }
    }

    /**
     * Open the edit password window.
     */
    @FXML
    private void openEditPassword() {
        openEditPasswordPopup(true, closeButton.getScene().getWindow());
    }

}
