import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.prefs.Preferences;


public class Main extends Application {

    private static final String MAIN_WINDOW_NODE = "Main";
    private static final String STAGE_HEIGHT = "height";
    private static final String STAGE_WIDTH = "width";
    private Preferences programData = Preferences.userRoot().node(MAIN_WINDOW_NODE);

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
            primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(400);
            primaryStage.setWidth(programData.getDouble(STAGE_WIDTH, 800));
            primaryStage.setHeight(programData.getDouble(STAGE_HEIGHT, 500));
            primaryStage.widthProperty().addListener((observableValue, oldValue, newValue) -> programData.putDouble(STAGE_WIDTH, newValue.doubleValue()));
            primaryStage.heightProperty().addListener((observableValue, oldValue, newValue) -> programData.putDouble(STAGE_HEIGHT, newValue.doubleValue()));
            primaryStage.setOnCloseRequest(this::close);
            primaryStage.show();

        } catch (Exception e) {
            new MessageDialog("Error loading the LoadingPage window! \n" +
                    "Restart program and try again.", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(e.getStackTrace()).showAndWait();
        }

    }

    public void close(WindowEvent event) {

        MessageDialog exitDialog = new MessageDialog("It seems you clicked the exit button right now,\n" +
                "Are you sure you want to exit?", MessageDialog.Type.INFO, MessageDialog.Buttons.YES_AND_NO);
        exitDialog.getYesButton().setOnAction(e -> {
            exitDialog.close();
            saveAndExit();
        });
        exitDialog.getNoButton().setOnAction(e -> {
            exitDialog.close();
            event.consume();
        });
        exitDialog.showAndWait();

    }

    public static void saveAndExit() {

        for(Item i : HomeController.getItemList())
            i.stopDownload();
        for(Item i : HomeController.getQueueItemList())
            i.stopDownload();
        DatabaseManager.closeConnection();
        Platform.exit();

    }

}
