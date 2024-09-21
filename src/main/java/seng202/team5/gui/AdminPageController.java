package seng202.team5.gui;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import seng202.team5.models.User;
import seng202.team5.repository.UserDAO;

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
    private UserDAO userDAO;

    @FXML
    private void initialize() {
        userDAO = new UserDAO();
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        //actionColumn.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        actionColumn.setCellFactory(new ActionCellFactory());
        actionColumn.setSortable(false);

        ObservableList<User> users = FXCollections.observableArrayList(userDAO.getAll());
        userTable.setItems(users);
    }

    @FXML
    private void done(ActionEvent event) {
        swapPage("/fxml/AccountManagePage.fxml");
    }

    @FXML
    private void enterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            searchPressed();
        }
    }

    @FXML
    private void searchPressed() {
        List<User> results = userDAO.getMatchingUserName(searchField.getText());
        ObservableList<User> users = FXCollections.observableArrayList(results);
        userTable.setItems(users);
    }

}
