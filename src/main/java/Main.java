import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        try {

            Parent root = FXMLLoader.load(getClass().getResource("windows/LoadingPage.fxml"));
            primaryStage.setTitle("Nazel Video Downloader");
            primaryStage.getIcons().add(0, new Image(getClass().getResource("icon/icon.png").toString()));
            primaryStage.setScene(new Scene(root));
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(500);
            primaryStage.show();

            primaryStage.setOnCloseRequest(event -> {
                MessageDialog exitDialog = new MessageDialog("It seems you clicked the exit button right now,\n" +
                        "Are you sure you want to exit?", MessageDialog.Buttons.YES_AND_NO);
                exitDialog.getYesButton().setOnAction(e -> {
                    exitDialog.close();
                    goodbye();
                });
                exitDialog.getNoButton().setOnAction(e -> {
                    exitDialog.close();
                    event.consume();
                });
                exitDialog.showAndWait();
            });

        } catch (Exception e) {
            new ErrorDialog("Error loading the LoadingPage window! \n" +
                    "Restart program and try again.", e.getStackTrace()).showAndWait();
        }

    }

    private void goodbye() {

        for(Item i : HomeController.getItemList())
            i.stopDownload();
        DatabaseManager.close();
        Platform.exit();

    }

}
