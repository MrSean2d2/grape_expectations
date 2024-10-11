package seng202.team5.gui;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Class starts the javaFX application window.
 *
 * @author seng202 teaching team
 */
public class MainWindow extends Application {

    public static String styleSheet = "/fxml/lightMode.css";

    public MainWindow() { }

    /**
     * Opens the gui with the fxml content specified in resources/fxml/Header.fxml.
     * The header file is treated as a "Wrapper" - Content is inserted into a content box
     * contained below the header, so the header will be on every page (persistent).
     *
     * @param primaryStage The current fxml stage, handled by javaFX Application class
     * @throws IOException if there is an issue loading fxml file
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        System.setProperty("prism.lcdtext", "false");

        FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/Header.fxml"));
        Parent root = baseLoader.load();
        Scene scene = new Scene(root, 1200, 800);
        String styleSheetUrl = styleSheet;
        scene.getStylesheets().add(styleSheetUrl);

        HeaderController baseController = baseLoader.getController();
        baseController.init(primaryStage);

        primaryStage.setTitle("Grape Expectations");
        primaryStage.getIcons().add(
                new Image(getClass().getResource("/images/favicon.png").toExternalForm()));

        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    /**
     * Launches the FXML application, this must be called from another
     * class (in this cass App.java) otherwise JavaFX
     * errors out and does not run.
     *
     * @param args command line arguments
     */
    public static void main(String [] args) {
        launch(args);
    }

}
