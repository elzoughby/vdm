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

    @Override
    public void start(Stage primaryStage) {

        try {


            Parent root = FXMLLoader.load(getClass().getResource("windows/LoadingPage.fxml"));
            System.out.println("loading first ");
            primaryStage.setTitle("Nazel Video Downloader");
            primaryStage.getIcons().add(0, new Image(getClass().getResource("icon/icon.png").toString()));
            primaryStage.setScene(new Scene(root));
            System.out.println("after scene ");
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(500);
            primaryStage.show();


            primaryStage.setOnCloseRequest(event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Exit");
                alert.setContentText("Are you sure you want to exit ? ");
                alert.showAndWait().ifPresent(response -> {
                    if(response == ButtonType.OK)
                           goodbye();
                    else
                        event.consume();
                });

            });

        }catch (IOException i )
        {
            System.out.println("not load ");
        }
        catch (Exception e) {
            new ErrorDialog("Error loading the LoadingPage window! \n" +
                    "Restart program and try again.", e.getStackTrace()).showAndWait();
        }

    }

    private void goodbye() {

        for(Item i : HomeController.getItemList()) {
            i.stopDownload();

        }
       // System.out.println("a");
        DatabaseManager.close();
        System.out.println("after close");
        Platform.exit();

    }

}
