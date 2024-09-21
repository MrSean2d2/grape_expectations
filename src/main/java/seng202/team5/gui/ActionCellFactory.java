package seng202.team5.gui;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import seng202.team5.models.User;
import seng202.team5.repository.UserDAO;

/**
 * A cell factory class for putting the action buttons in the action column
 * of the user table.
 *
 * @author Sean Reitsma
 */
public class ActionCellFactory implements
        Callback<TableColumn<User, Boolean>, TableCell<User, Boolean>> {

    @Override
    public TableCell<User, Boolean> call(TableColumn<User, Boolean> userStringTableColumn) {
        return new TableCell<>() {
            final Button deleteUserButton = new Button("Delete");

            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    deleteUserButton.setOnAction(event -> {
                        User user = getTableView().getItems().get(getIndex());
                        UserDAO userDAO = new UserDAO();
                        userDAO.delete(user.getId());
                        getTableView().getItems().remove(getIndex());
                    });
                    setGraphic(deleteUserButton);
                    setText(null);
                }
            }
        };
    }
}
