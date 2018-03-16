import dorkbox.systemTray.Menu;
import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.Separator;
import dorkbox.systemTray.SystemTray;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.prefs.Preferences;


public class Main extends Application {

    private static final String MAIN_WINDOW_NODE = "Main";
    private static final String STAGE_HEIGHT = "height";
    private static final String STAGE_WIDTH = "width";
    private Preferences programData = Preferences.userRoot().node(MAIN_WINDOW_NODE);
    private Stage appStage;


    public static void main(String args[]) {
        launch(args);
    }

    public static void saveAndExit() {
        stopAllDownloads();
        Platform.exit();
        System.exit(0);
    }

    private static void stopAllDownloads() {
        for(Item item : HomeController.getItemList())
            item.stopDownload();
        for(Item item : HomeController.getQueueItemList())
            item.stopDownload();
    }


    @Override
    public void start(Stage primaryStage) {

        // stores a reference to the stage.
        this.appStage = primaryStage;

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
            primaryStage.setOnCloseRequest(event -> appStage.hide());
            primaryStage.show();

            // instructs the javafx system not to exit implicitly when the last application window is shut.
            Platform.setImplicitExit(false);
            initSystemTray();

        } catch (Exception e) {
            new MessageDialog("Error loading the LoadingPage window! \n" +
                    "Restart program and try again.", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(e.getStackTrace()).showAndWait();
        }

    }

    private void initSystemTray() {

        SystemTray systemTray = SystemTray.get();
        if(systemTray == null)
            return;
        systemTray.setTooltip("Nazel Video Downloader");
        systemTray.setImage(Main.class.getResource("icon/icon.png"));
        systemTray.setStatus("No Running Downloads");

        MenuItem showWindowMenuItem = new MenuItem("Show Window");
        showWindowMenuItem.setCallback(e -> Platform.runLater(() -> {
            try {
                FXMLLoader newDownloadWindowLoader = new FXMLLoader(getClass().getResource("windows/HomeWindow.fxml"));
                newDownloadWindowLoader.load();
                Parent root = newDownloadWindowLoader.getRoot();
                appStage.getScene().setRoot(root);
            } catch (Exception ex) {
                System.err.println("Error returning to Home Window");
            } finally {
                showWindow();
            }
        }));
        showWindowMenuItem.setImage(Main.class.getResource("icon/icon.png"));
        showWindowMenuItem.setShortcut('w');

        MenuItem newDownloadMenuItem = new MenuItem("New Download");
        newDownloadMenuItem.setCallback(e -> Platform.runLater(() -> {
            try {
                FXMLLoader newDownloadWindowLoader = new FXMLLoader(getClass().getResource("windows/NewDownloadWindow.fxml"));
                newDownloadWindowLoader.load();
                Parent root = newDownloadWindowLoader.getRoot();
                appStage.getScene().setRoot(root);
                showWindow();
            } catch (Exception ex) {
                new MessageDialog("Couldn't load the New Download page!\n" +
                        "Restart the program and try again.", MessageDialog.Type.ERROR,
                        MessageDialog.Buttons.CLOSE).createErrorDialog(ex.getStackTrace()).showAndWait();
            }
        }));
        newDownloadMenuItem.setImage(Main.class.getResource("theme/imgs/add.png"));
        newDownloadMenuItem.setShortcut('n');

        MenuItem startQueueMenuItem = new MenuItem("Start Queue");
        startQueueMenuItem.setCallback(e -> {
            for(int i = 0; i < HomeController.getQueueItemList().size(); i++) {
                if(HomeController.getQueueItemList().get(i).getStatus().equals("Finished"))
                    continue;
                if(HomeController.getQueueItemList().get(i).getStatus().equals("Stopped") || HomeController.getQueueItemList().get(i).getStatus().equals("Error")) {
                    HomeController.getQueueItemList().get(i).startDownload();
                    break;
                }
                if(HomeController.getQueueItemList().get(i).getStatus().equals("Running") || HomeController.getQueueItemList().get(i).getStatus().equals("Starting"))
                    break;
            }
        });
        startQueueMenuItem.setImage(Main.class.getResource("theme/imgs/start.png"));
        startQueueMenuItem.setShortcut('s');

        MenuItem pauseQueueMenuItem = new MenuItem("Pause Queue");
        pauseQueueMenuItem.setCallback(e -> {
            for(Item item : HomeController.getQueueItemList())
                item.stopDownload();
        });
        pauseQueueMenuItem.setImage(Main.class.getResource("theme/imgs/pause.png"));
        pauseQueueMenuItem.setShortcut('u');

        MenuItem pauseAllMenuItem = new MenuItem("Pause All");
        pauseAllMenuItem.setCallback(e -> stopAllDownloads());
        pauseAllMenuItem.setImage(Main.class.getResource("theme/imgs/pause.png"));
        pauseAllMenuItem.setShortcut('p');

        MenuItem aboutMenuItem = new MenuItem("About");
        aboutMenuItem.setCallback(e -> {

        });
        aboutMenuItem.setImage(Main.class.getResource("theme/imgs/about.png"));
        aboutMenuItem.setShortcut('a');

        MenuItem quitMenuItem = new MenuItem("Quit");
        quitMenuItem.setCallback(c -> Platform.runLater(() -> {
            MessageDialog exitDialog = new MessageDialog("It seems you clicked the exit button right now,\n" +
                    "Are you sure you want to exit?", MessageDialog.Type.INFO, MessageDialog.Buttons.YES_AND_NO);
            exitDialog.getYesButton().setOnAction(e -> {
                exitDialog.close();
                saveAndExit();
                systemTray.shutdown();
            });
            exitDialog.getNoButton().setOnAction(e -> exitDialog.close());
            exitDialog.showAndWait();
        }));
        quitMenuItem.setImage(Main.class.getResource("theme/imgs/cancel.png"));
        quitMenuItem.setShortcut('q');


        Menu trayMenu = systemTray.getMenu();
        trayMenu.add(showWindowMenuItem);
        trayMenu.add(new Separator());
        trayMenu.add(newDownloadMenuItem);
        trayMenu.add(startQueueMenuItem);
        trayMenu.add(pauseQueueMenuItem);
        trayMenu.add(pauseAllMenuItem);
        trayMenu.add(new Separator());
        trayMenu.add(aboutMenuItem);
        trayMenu.add(quitMenuItem);

    }

    private void showWindow() {
        appStage.show();
        appStage.toFront();
    }

}
