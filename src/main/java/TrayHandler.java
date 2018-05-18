import dorkbox.systemTray.Menu;
import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.Separator;
import dorkbox.systemTray.SystemTray;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;


public class TrayHandler {

    private static SystemTray systemTray;
    private static Stage appStage = Main.getAppStage();
    private static Stage notificationStage;
    private static int numOfRunningDownloads = 0;
    private static boolean moveToNewDownload = false;



    private static void showHomeWindow() {

        Platform.runLater(() -> {
            try {
                if(! appStage.isShowing()) {
                    if(! appStage.getScene().getRoot().getId().equals("loadingPane")) {
                        FXMLLoader newDownloadWindowLoader = new FXMLLoader(TrayHandler.class.getResource("windows/HomeWindow.fxml"));
                        Parent root = newDownloadWindowLoader.load();
                        appStage.getScene().setRoot(root);
                    }
                }
                appStage.show();
                appStage.toFront();
            } catch (Exception ex) {
                new MessageDialog("Error returning to the home window! \n" +
                        "Try again later or report this issue", MessageDialog.Type.ERROR,
                        MessageDialog.Buttons.CLOSE).createErrorDialog(ex.getStackTrace()).showAndWait();
            }
        });

    }

    private static void showNewDownloadWindow() {

        Platform.runLater(() -> {
            try {
                if(! appStage.getScene().getRoot().getId().equals("newDownloadPane")) {
                    if(! appStage.getScene().getRoot().getId().equals("loadingPane")) {
                        FXMLLoader newDownloadWindowLoader = new FXMLLoader(TrayHandler.class.getResource("windows/NewDownloadWindow.fxml"));
                        Parent root = newDownloadWindowLoader.load();
                        NewDownloadController controller = newDownloadWindowLoader.getController();
                        appStage.getScene().setRoot(root);
                        appStage.show();
                        controller.showUrlDialog();
                    } else {
                        moveToNewDownload = true;
                    }
                }
                appStage.show();
                appStage.toFront();
            } catch (Exception ex) {
                new MessageDialog("Couldn't load the New Download page!\n" +
                        "Try again later or report this issue", MessageDialog.Type.ERROR,
                        MessageDialog.Buttons.CLOSE).createErrorDialog(ex.getStackTrace()).showAndWait();
            }
        });

    }

    public static int getNumOfRunningDownloads() {
        return numOfRunningDownloads;
    }

    public static void setNumOfRunningDownloads(int numOfRunningDownloads) {

        TrayHandler.numOfRunningDownloads = numOfRunningDownloads;
        if(numOfRunningDownloads == 0)
            systemTray.setStatus("No Running Downloads");
        else
            systemTray.setStatus(String.valueOf(numOfRunningDownloads) + "Running Downloads");
    }

    public static void incrementNumOfRunningDownloads() {

        TrayHandler.numOfRunningDownloads++;
        if(numOfRunningDownloads == 0)
            systemTray.setStatus("No Running Downloads");
        else
            systemTray.setStatus(String.valueOf(numOfRunningDownloads) + " Running Downloads");
    }

    public static void decrementNumOfRunningDownloads() {

        TrayHandler.numOfRunningDownloads--;
        if(numOfRunningDownloads == 0)
            systemTray.setStatus("No Running Downloads");
        else
            systemTray.setStatus(String.valueOf(numOfRunningDownloads) + " Running Downloads");
    }

    public static boolean isMoveToNewDownload() {
        return moveToNewDownload;
    }

    public static void initSystemTray() {

        systemTray = SystemTray.get();
        if(systemTray == null)
            return;
        systemTray.setTooltip("Video Download Manager");
        systemTray.setImage(TrayHandler.class.getResource("icon/icon.png"));
        systemTray.setStatus("No Running Downloads");

        MenuItem showWindowMenuItem = new MenuItem("Show Window");
        showWindowMenuItem.setCallback(e -> showHomeWindow());
        showWindowMenuItem.setImage(Main.class.getResource("icon/icon.png"));
        showWindowMenuItem.setShortcut('w');

        MenuItem newDownloadMenuItem = new MenuItem("New Download");
        newDownloadMenuItem.setCallback(e -> showNewDownloadWindow());
        newDownloadMenuItem.setImage(TrayHandler.class.getResource("theme/imgs/add.png"));
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
        startQueueMenuItem.setImage(TrayHandler.class.getResource("theme/imgs/start.png"));
        startQueueMenuItem.setShortcut('s');

        MenuItem pauseQueueMenuItem = new MenuItem("Pause Queue");
        pauseQueueMenuItem.setCallback(e -> {
            for(Item item : HomeController.getQueueItemList())
                item.stopDownload();
        });
        pauseQueueMenuItem.setImage(TrayHandler.class.getResource("theme/imgs/pause.png"));
        pauseQueueMenuItem.setShortcut('u');

        MenuItem pauseAllMenuItem = new MenuItem("Pause All");
        pauseAllMenuItem.setCallback(e -> Main.stopAllDownloads());
        pauseAllMenuItem.setImage(TrayHandler.class.getResource("theme/imgs/pause.png"));
        pauseAllMenuItem.setShortcut('p');

        MenuItem aboutMenuItem = new MenuItem("About");
        aboutMenuItem.setCallback(e -> Platform.runLater(AboutController::showAboutDialog));
        aboutMenuItem.setImage(Main.class.getResource("theme/imgs/about.png"));
        aboutMenuItem.setShortcut('a');

        MenuItem quitMenuItem = new MenuItem("Quit");
        quitMenuItem.setCallback(c -> Platform.runLater(() -> {
            MessageDialog exitDialog = new MessageDialog("It seems you clicked the exit button right now,\n" +
                    "Are you sure you want to exit?", MessageDialog.Type.INFO, MessageDialog.Buttons.YES_AND_NO);
            exitDialog.getYesButton().setOnAction(e -> {
                exitDialog.close();
                Main.saveAndExit();
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

    public static void initNotifications() {

        final int STAGE_WIDTH = 1;
        final int STAGE_HEIGHT = 1;

        notificationStage = new Stage();
        notificationStage.setScene(new Scene(new Pane()));
        notificationStage.getScene().setFill(Color.TRANSPARENT);
        notificationStage.initStyle(StageStyle.UTILITY);
        notificationStage.setOpacity(0);
        notificationStage.setMaxWidth(STAGE_WIDTH);
        notificationStage.setMinWidth(STAGE_WIDTH);
        notificationStage.setMaxHeight(STAGE_HEIGHT);
        notificationStage.setMinHeight(STAGE_HEIGHT);
        notificationStage.setResizable(false);
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        notificationStage.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth());
        notificationStage.setY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight());
        notificationStage.show();
        notificationStage.toBack();

    }

    public static void showDownloadFinishNotification(Item item) {

        Notifications notification = Notifications.create();
        notification.title("Download Complete");
        String title = item.getTitle().length() <= 40? item.getTitle() : item.getTitle().substring(0, 40).concat("...");
        notification.text("Finished downloading \n" + title);
        ImageView imageView = new ImageView(new Image(TrayHandler.class.getResource("theme/imgs/done.png").toString()));
        imageView.setFitHeight(64);
        imageView.setFitWidth(64);
        notification.graphic(imageView);
        notification.hideAfter(Duration.seconds(4));
        notification.owner(notificationStage);
        notification.onAction(actionEvent -> showHomeWindow());
        Platform.runLater(notification::show);

    }

    public static void showDownloadErrorNotification(Item item) {

        Notifications notification = Notifications.create();
        notification.title("Download Error");
        String title = item.getTitle().length() <= 40? item.getTitle() : item.getTitle().substring(0, 40).concat("...");
        notification.text("Error in downloading \n" + title);
        Image image = new Image(TrayHandler.class.getResource("theme/imgs/cancel.png").toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(64);
        imageView.setFitWidth(64);
        notification.graphic(imageView);
        notification.hideAfter(Duration.seconds(4));
        notification.owner(notificationStage);
        notification.onAction(actionEvent -> showHomeWindow());
        Platform.runLater(notification::show);

    }

    public static void showNewDownloadNotification(String url) {

        Notifications notification = Notifications.create();
        notification.title("New URL detected");
        String copiedLink = url.length() <= 40? url : url.substring(0, 40).concat("...");
        notification.text(copiedLink + "\nclick to start a new download");
        ImageView imageView = new ImageView(new Image(TrayHandler.class.getResource("theme/imgs/link.png").toString()));
        imageView.setFitHeight(64);
        imageView.setFitWidth(64);
        notification.graphic(imageView);
        notification.hideAfter(Duration.seconds(5));
        notification.owner(notificationStage);
        notification.onAction(actionEvent -> showNewDownloadWindow());
        Platform.runLater(notification::show);

    }

    public static void startClipboardMonitor() {

        final Clipboard systemClipboard = Clipboard.getSystemClipboard();
        final StringBuilder lastClipboardText = new StringBuilder(systemClipboard.hasString() && systemClipboard.getString() != null? systemClipboard.getString() : "zox");
        String urlRegex = "(https?:\\/\\/)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";

        Timeline repeatTask = new Timeline(new KeyFrame(Duration.millis(250), event -> {
            if(systemClipboard.hasString()) {
                String newClipboardString = systemClipboard.getString();
                if(! lastClipboardText.toString().equals(newClipboardString)) {
                    if(newClipboardString != null && newClipboardString.matches(urlRegex)) {
                        showNewDownloadNotification(newClipboardString);
                    }
                    lastClipboardText.delete(0, lastClipboardText.length());
                    lastClipboardText.append(newClipboardString);
                }
            }
        }));
        repeatTask.setCycleCount(Timeline.INDEFINITE);
        repeatTask.play();

    }

}
