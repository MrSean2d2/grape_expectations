package seng202.team5.gui;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import seng202.team5.models.User;
import seng202.team5.repository.UserDAO;
import seng202.team5.services.OpenWindowsService;

/**
 * Controller for the admin page. This page allows the admin to manage users.
 *
 * @author Sean Reitsma
 */
public class AdminPageController extends PageController {

    @FXML
    private TableColumn<User, Boolean> actionColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private Label resultsLabel;

    private UserDAO userDAO;

    /**
     * Initialise columns.
     */
    @FXML
    private void initialize() {
        userDAO = new UserDAO();
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        //actionColumn.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        actionColumn.setCellFactory(new ManageUserActionCellFactory(this));
        actionColumn.setSortable(false);

        ObservableList<User> users = FXCollections.observableList(userDAO.getAll(),
                User.extractor());
        userTable.setItems(users);
        resultsLabel.setText(String.format("Found %d users", users.size()));
        userTable.setPlaceholder(new Label("No matching users found"));
    }

    /**
     * Closes admin page, swap to account manage page.
     */
    @FXML
    private void done() {
        OpenWindowsService.getInstance().closeAllWindows();
        swapPage("/fxml/AccountManagePage.fxml");
    }

    /**
     * Handles event of search after enter pressed.
     *
     * @param event key event
     */
    @FXML
    private void enterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            searchPressed();
        }
    }

    /**
     * Handles event of search pressed, searches users for term in search field.
     */
    @FXML
    private void searchPressed() {
        List<User> results = userDAO.getMatchingUserName(searchField.getText());
        ObservableList<User> users = FXCollections.observableList(results, User.extractor());
        userTable.setItems(users);
        resultsLabel.setText(String.format("Found %d users with name '%s'",
                users.size(), searchField.getText()));
    }

}
