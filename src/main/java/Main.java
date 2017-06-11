import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("windows/LoadingPage.fxml"));
        primaryStage.setTitle("Nazel Video Downloader");
        primaryStage.getIcons().add(0, new Image(getClass().getResource("icons/icon.png").toString()));
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(500);
        primaryStage.show();


        primaryStage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit");
            alert.setContentText("Are you sure you want to exit?");
            alert.showAndWait().ifPresent(response -> {
                if(response == ButtonType.OK)
                    goodbye();
                else
                    event.consume();
            });

        });

    }

    private void goodbye() {

        for(Item i : HomeController.getItemList()) {
            try {
                i.stopDownload();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        DbManager.close();
        Platform.exit();
    }

}
