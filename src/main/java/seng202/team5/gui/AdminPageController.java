package seng202.team5.gui;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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
    private Button searchButton;
    @FXML
    private Button doneButton;
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
        actionColumn.setCellFactory(new ManageUserActionCellFactory(this));
        actionColumn.setSortable(false);

        ObservableList<User> users = FXCollections.observableList(userDAO.getAll(),
                User.extractor());

        userTable.setItems(users);
        int size = userTable.getItems().size();
        resultsLabel.setText(String.format("Found %d user%s", size, (size == 1 ? "" : "s")));
        userTable.getItems().addListener((ListChangeListener<? super User>) change -> {
            int s = change.getList().size();
            resultsLabel.setText(String.format("Found %d user%s", s, (s == 1 ? "" : "s")));
        });
        userTable.setPlaceholder(new Label("No matching users found"));
        searchButton.setTooltip(new Tooltip("Search for query"));
        doneButton.setTooltip(new Tooltip("Return to account page"));
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
        userTable.getItems().setAll(users);
        int s = users.size();
        String leadingS = ((s == 1) ? "" : "s");
        if (searchField.getText().isEmpty()) {
            resultsLabel.setText(String.format("Found %d user%s", s, leadingS));
        } else {
            resultsLabel.setText(String.format("Found %d user%s with name like '%s'",
                    s, leadingS, searchField.getText()));
        }
    }

}
