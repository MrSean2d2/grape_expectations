package seng202.team5.gui;

import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import seng202.team5.models.User;
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
                    editUserButton.setOnAction(event -> {
                        try {
                            UserService.getInstance().setSelectedUser(user);
                            FXMLLoader editUserLoader = new FXMLLoader(getClass()
                                    .getResource("/fxml/EditUserPopup.fxml"));
                            Parent root = editUserLoader.load();
                            Scene scene = new Scene(root);
                            Stage stage = new Stage();
                            stage.setScene(scene);
                            stage.setTitle(String.format("Edit user %s", user.getUsername()));
                            stage.setResizable(false);
                            stage.initModality(Modality.WINDOW_MODAL);
                            stage.showAndWait();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    });
                    setGraphic(buttonsHbox);
                    setText(null);
                }
            }
        };
    }
}
