import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.util.prefs.Preferences;


public class Main extends Application {

    private static Stage appStage;
    private static boolean startMinimized = false;
    private static final String MAIN_WINDOW_NODE = "Main";
    private static final String STAGE_HEIGHT = "height";
    private static final String STAGE_WIDTH = "width";
    private Preferences programData = Preferences.userRoot().node(MAIN_WINDOW_NODE);


    public static void main(String args[]) {

        boolean errorFlag = false;

        if(args.length == 1)
            if(args[0].equals("-m"))
                startMinimized = true;
            else
                errorFlag = true;
        else if(args.length == 0)
            startMinimized = false;
        else
            errorFlag = true;


        if(errorFlag)
            System.err.println("invalid arguments");
        else
            launch(args);

    }

    public static Stage getAppStage() {
        return appStage;
    }

    public static void saveAndExit() {
        stopAllDownloads();

        // Delete temp files
        File tempDirectory = new File("temp");
        if(tempDirectory.exists() && tempDirectory.isDirectory()) {
            File[] tempFiles = tempDirectory.listFiles();
            for (File tempFile : tempFiles)
                tempFile.delete();
        }

        Platform.exit();
        System.exit(0);
    }

    public static void stopAllDownloads() {
        for(Item item : HomeController.getItemList())
            item.stopDownload();
        for(Item item : HomeController.getQueueItemList())
            item.stopDownload();
    }


    @Override
    public void start(Stage primaryStage) {

        // stores a reference to the stage.
        appStage = primaryStage;

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("windows/LoadingPage.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("Nazel Video Downloader");
            primaryStage.getIcons().add(0, new Image(getClass().getResource("icon/icon.png").toString()));
            primaryStage.setScene(new Scene(root));
            primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(400);
            primaryStage.setWidth(programData.getDouble(STAGE_WIDTH, 800));
            primaryStage.setHeight(programData.getDouble(STAGE_HEIGHT, 500));
            primaryStage.widthProperty().addListener((observableValue, oldValue, newValue) -> programData.putDouble(STAGE_WIDTH, newValue.doubleValue()));
            primaryStage.heightProperty().addListener((observableValue, oldValue, newValue) -> programData.putDouble(STAGE_HEIGHT, newValue.doubleValue()));
            primaryStage.setOnCloseRequest(event -> appStage.close());

            TrayHandler.initSystemTray();
            TrayHandler.initNotifications();
            TrayHandler.startClipboardMonitor();
            AES.initKey();

            // instructs the javafx system not to exit implicitly when the last application window is shut.
            Platform.setImplicitExit(false);
            if(! startMinimized)
                primaryStage.show();

        } catch (Exception e) {
            new MessageDialog("Error loading the LoadingPage window! \n" +
                    "Restart program and try again.", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(e.getStackTrace()).showAndWait();
        }

    }

}
