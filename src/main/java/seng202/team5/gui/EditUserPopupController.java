package seng202.team5.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng202.team5.models.Role;
import seng202.team5.models.User;
import seng202.team5.repository.UserDAO;
import seng202.team5.services.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * A controller for the edit user window.
 *
 * @author Sean Reitsma
 */
public class EditUserPopupController extends PageController {
    @FXML
    private ComboBox<Role> roleComboBox;

    @FXML
    private TextField usernameField;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private Button closeButton;
    private User curUser;
    private static List<EditUserPopupController> openInstances = new ArrayList<>();
    private boolean editingCurrentUser;

    @FXML
    private void initialize() {
        curUser = UserService.getInstance().getSelectedUser();
        editingCurrentUser = curUser.equals(UserService.getInstance().getCurrentUser());
        usernameLabel.setText(curUser.getUsername());
        usernameField.setText(curUser.getUsername());
        roleComboBox.setDisable(editingCurrentUser);
        roleComboBox.getItems().setAll(Role.values());
        roleComboBox.getSelectionModel().select(curUser.getRole());

        openInstances.add(this);
    }

    @FXML
    private void close() {
        openInstances.remove(this);
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void submit() {
        curUser.setUsername(usernameField.getText());
        curUser.setRole(roleComboBox.getSelectionModel().getSelectedItem());
        if (editingCurrentUser) {
            UserService.getInstance().setCurrentUser(curUser);
        }
        UserDAO userDAO = new UserDAO();
        userDAO.update(curUser);
        close();
    }

    @FXML
    private void openEditPassword() {
        openEditPasswordPopup(true, closeButton.getScene().getWindow());
    }

    /**
     * Closes all open instances of detailed view pages.
     */
    @FXML
    public static void closeAll() {
        for (EditUserPopupController instance : new ArrayList<>(openInstances)) {
            instance.close();
        }
    }

}
