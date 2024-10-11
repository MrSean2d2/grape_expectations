package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    private TextField usernameField;

    @FXML
    private Label usernameLabel;

    @FXML
    private Button closeButton;
    private User curUser;
    private boolean editingCurrentUser;

    /**
     * Initialises page based on selected user.
     */
    @FXML
    private void initialize() {
        OpenWindowsService.getInstance().addWindow(this);

        curUser = UserService.getInstance().getSelectedUser();
        editingCurrentUser = curUser.equals(UserService.getInstance().getCurrentUser());
        usernameLabel.setText(curUser.getUsername());
        usernameField.setText(curUser.getUsername());
        roleComboBox.setDisable(editingCurrentUser);
        roleComboBox.getItems().setAll(Role.values());
        roleComboBox.getSelectionModel().select(curUser.getRole());
    }

    /**
     * Closes window.
     */
    @FXML
    @Override
    public void closeWindow() {
        OpenWindowsService.getInstance().closeWindow(this);
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Saves changes made and closes the window.
     */
    @FXML
    private void submit() {
        curUser.setUsername(usernameField.getText());
        curUser.setRole(roleComboBox.getSelectionModel().getSelectedItem());
        if (editingCurrentUser) {
            UserService.getInstance().setCurrentUser(curUser);
        }
        UserDAO userDAO = new UserDAO();
        userDAO.update(curUser);
        addNotification("User updated", "#d5e958");
        closeWindow();
    }

    /**
     * Opens edit password popup.
     */
    @FXML
    private void openEditPassword() {
        openEditPasswordPopup(true, closeButton.getScene().getWindow());
    }

}
