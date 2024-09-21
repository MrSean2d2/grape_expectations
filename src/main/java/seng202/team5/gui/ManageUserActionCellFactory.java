package seng202.team5.gui;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import seng202.team5.models.User;
import seng202.team5.repository.UserDAO;
import seng202.team5.services.UserService;

/**
 * A cell factory class for putting the action buttons in the action column
 * of the user table.
 *
 * @author Sean Reitsma
 */
public class ManageUserActionCellFactory implements
        Callback<TableColumn<User, Boolean>, TableCell<User, Boolean>> {

    @Override
    public TableCell<User, Boolean> call(TableColumn<User, Boolean> userStringTableColumn) {
        return new TableCell<>() {
            final HBox buttonsHbox = new HBox();
            final Button deleteUserButton = new Button("Delete");
            final Button editUserButton = new Button("Edit");
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    buttonsHbox.getChildren().clear();
                    buttonsHbox.getChildren().addAll(deleteUserButton, editUserButton);
                    User user = getTableView().getItems().get(getIndex());
                    User curUser = UserService.getInstance().getCurrentUser();
                    UserDAO userDAO = new UserDAO();
                    deleteUserButton.setDisable(user.equals(curUser));
                    deleteUserButton.setOnAction(event -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Delete User?");
                        alert.setHeaderText("Are you sure you want to delete this user?");
                        alert.setContentText("This action cannot be undone.");
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            UserService.getInstance().deleteUser(user);
                            getTableView().getItems().remove(getIndex());
                        }
                    });
                    setGraphic(buttonsHbox);
                    setText(null);
                }
            }
        };
    }
}
