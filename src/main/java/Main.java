import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;


public class Main extends Application {

    private static Stage appStage;
    private static boolean startMinimized = false;
    public static final String VERSION = "0.9.8";
    public static final String WEBSITE = "https://elzoughby.github.io/vdm";
    public static final String PATREON = "https://www.patreon.com/bePatron?c=1746384";


    public static void main(String args[]) {

        boolean errorFlag = false;

        if(args.length == 1)
            if(args[0].equals("-s"))
                startMinimized = true;
            else
                errorFlag = true;
        else if(args.length == 0)
            startMinimized = false;
        else
            errorFlag = true;


        if(errorFlag) {
            System.err.println("invalid arguments");
            System.exit(1);
        } else {
            DataHandler.readAppPreferences();
            launch(args);
        }

    }

    public static Stage getAppStage() {
        return appStage;
    }

    public static void saveAndExit() {
        stopAllDownloads();

        // Delete temp files
        File tempDirectory = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "vdm");
        if(tempDirectory.exists() && tempDirectory.isDirectory()) {
            File[] tempFiles = tempDirectory.listFiles();
            for (File tempFile : tempFiles)
                tempFile.delete();
        }

        Platform.exit();
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
            primaryStage.setTitle("Video Download Manager");
            primaryStage.getIcons().add(0, new Image(getClass().getResource("icon/icon.png").toString()));
            primaryStage.setScene(new Scene(root));
            primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(400);
            primaryStage.setWidth( (Double) DataHandler.getAppPreferences().get("Main.width"));
            primaryStage.setHeight( (Double) DataHandler.getAppPreferences().get("Main.height"));
            primaryStage.widthProperty().addListener((observableValue, oldValue, newValue) -> {
                DataHandler.getAppPreferences().replace("Main.width", newValue.doubleValue());
                DataHandler.writeAppPreferences();
            });
            primaryStage.heightProperty().addListener((observableValue, oldValue, newValue) -> {
                DataHandler.getAppPreferences().replace("Main.height", newValue.doubleValue());
                DataHandler.writeAppPreferences();
            });
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
            e.printStackTrace();
            new MessageDialog("Error loading the LoadingPage window! \n" +
                    "Try again later or report this issue", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(e.getStackTrace()).showAndWait();
        }

    }

}
