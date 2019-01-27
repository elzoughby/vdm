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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


public class TrayHandler {

    private static final String OS_NAME = VDM.OS_NAME;
    private static final String SEPARATOR = VDM.SEPARATOR;
    private static final String USER_HOME = VDM.USER_HOME;
    private static final String EXECUTABLE_PATH = VDM.EXECUTABLE_PATH;
    private static final String INSTALL_PATH = VDM.INSTALL_PATH;

    private static SystemTray systemTray;
    private static Stage appStage = VDM.getAppStage();
    private static Stage notificationStage;
    private static int numOfRunningDownloads = 0;
    private static boolean moveToNewDownload = false;
    private static Timeline checkClipboardTask;
    private static String startupDirectoryPath;
    static {
        // set user startup directory path based on the user OS
        if (OS_NAME.contains("win"))
            startupDirectoryPath = USER_HOME + SEPARATOR + "AppData" + SEPARATOR + "Roaming" + SEPARATOR + "Microsoft" +
                    SEPARATOR + "Windows" + SEPARATOR + "Start Menu" + SEPARATOR + "Programs" + SEPARATOR + "Startup";
        else if (OS_NAME.contains("mac"))
            startupDirectoryPath = USER_HOME + SEPARATOR + "Library" + SEPARATOR + "LaunchAgents";
        else
            startupDirectoryPath = USER_HOME + SEPARATOR + ".config" + SEPARATOR + "autostart";
    }



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

    public static void addToStartup() {

        // check the running os
        if (OS_NAME.contains("win")) {

            final String REG_ADD_CMD = "reg add \"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run\" /v \"vdm\" /d \"" + EXECUTABLE_PATH + " -s\" /t REG_EXPAND_SZ";
            try {
                Runtime.getRuntime().exec(REG_ADD_CMD);
            } catch (Exception ex) {
                System.err.println("Couldn't create a startup config entry! Try again later.");
            }

        } else if (OS_NAME.contains("mac")) {

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(startupDirectoryPath + SEPARATOR + "VDM.plist"));
                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                        "<plist version=\"1.0\">\n" +
                        "<dict>\n" +
                        "<key>Label</key>\n" +
                        "<string>elzoughby.vdm</string>\n" +
                        //"<key>Program</key>\n" +
                        //"<string>" + executablePath + SEPARATOR + "vdm" + "</string>\n" +
                        "<key>ProgramArguments</key>\n" +
                        "<array>\n" +
                        "<string>" + EXECUTABLE_PATH + "</string>\n" +
                        "<string>-s</string>\n" +
                        "</array>\n" +
                        "<key>RunAtLoad</key>\n" +
                        "<true/>\n" +
                        "</dict>\n" +
                        "</plist>"
                );
                writer.close();
            } catch (Exception ex) {
                System.err.println("Couldn't create a startup configuration file! Try again later.");
            }

        } else {

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(startupDirectoryPath + SEPARATOR + "VDM.desktop"));
                writer.write("[Desktop Entry]\n" +
                        "Name=Video Download Manager\n" +
                        "Version=1.0.0\n" +
                        "Exec=" + EXECUTABLE_PATH + " -s\n" +
                        "Comment=Free, Open Source, Cross-platform video downloader.\n" +
                        "Icon=" + INSTALL_PATH + SEPARATOR + "icon.png\n" +
                        "Type=Application\n" +
                        "Terminal=false\n" +
                        "StartupNotify=true\n" +
                        "Encoding=UTF-8\n" +
                        "Categories=Video;Network;\n"
                );
                writer.close();
            } catch (Exception ex) {
                System.err.println("Couldn't create a shortcut in the startup folder! Try again later.");
            }

        }

    }

    public static void removeFromStartup() {

        // check the running os
        if (OS_NAME.contains("win")) {
            final String REG_DELETE_CMD = "reg delete \"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run\" /v \"vdm\" /f";
            try {
                Runtime.getRuntime().exec(REG_DELETE_CMD);
            } catch (Exception ex) {
                System.err.println("Couldn't delete the startup config entry! Try again later.");
            }
        } else {
            final String SHORTCUT_NAME = OS_NAME.contains("mac")? "VDM.plist" : "VDM.desktop";
            // delete shortcut from the startup folder
            File startupShortcut = new File(startupDirectoryPath, SHORTCUT_NAME);
            if(startupShortcut.exists())
                startupShortcut.delete();
        }

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
        showWindowMenuItem.setImage(VDM.class.getResource("icon/icon.png"));
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
        pauseAllMenuItem.setCallback(e -> VDM.stopAllDownloads());
        pauseAllMenuItem.setImage(TrayHandler.class.getResource("theme/imgs/pause.png"));
        pauseAllMenuItem.setShortcut('p');

        MenuItem clipboardMenuItem = new MenuItem("Clipboard Monitor");
        clipboardMenuItem.setCallback(e -> {
            if( (Boolean) DataHandler.getAppPreferences().get("TrayHandled.clipboardMonitor")) {
                DataHandler.getAppPreferences().replace("TrayHandled.clipboardMonitor", false);
                DataHandler.writeAppPreferences();
                clipboardMenuItem.setImage(VDM.class.getResource("menu/unchecked.png"));
                checkClipboardTask.pause();
            } else {
                DataHandler.getAppPreferences().replace("TrayHandled.clipboardMonitor", true);
                DataHandler.writeAppPreferences();
                clipboardMenuItem.setImage(VDM.class.getResource("menu/checked.png"));
                checkClipboardTask.play();
            }
        });
        if( (Boolean) DataHandler.getAppPreferences().get("TrayHandled.clipboardMonitor"))
            clipboardMenuItem.setImage(VDM.class.getResource("menu/checked.png"));
        else
            clipboardMenuItem.setImage(VDM.class.getResource("menu/unchecked.png"));
        clipboardMenuItem.setShortcut('c');

        MenuItem startupMenuItem = new MenuItem("Run At Startup");
        startupMenuItem.setCallback(e -> {
            if( (Boolean) DataHandler.getAppPreferences().get("TrayHandled.runAtStartup")) {
                DataHandler.getAppPreferences().replace("TrayHandled.runAtStartup", false);
                DataHandler.writeAppPreferences();
                startupMenuItem.setImage(VDM.class.getResource("menu/unchecked.png"));
                removeFromStartup();
            } else {
                DataHandler.getAppPreferences().replace("TrayHandled.runAtStartup", true);
                DataHandler.writeAppPreferences();
                startupMenuItem.setImage(VDM.class.getResource("menu/checked.png"));
                addToStartup();
            }
        });
        if( (Boolean) DataHandler.getAppPreferences().get("TrayHandled.runAtStartup"))
            startupMenuItem.setImage(VDM.class.getResource("menu/checked.png"));
        else
            startupMenuItem.setImage(VDM.class.getResource("menu/unchecked.png"));
        startupMenuItem.setShortcut('r');

        MenuItem aboutMenuItem = new MenuItem("About");
        aboutMenuItem.setCallback(e -> Platform.runLater(AboutController::showAboutDialog));
        aboutMenuItem.setImage(VDM.class.getResource("theme/imgs/about.png"));
        aboutMenuItem.setShortcut('a');

        MenuItem quitMenuItem = new MenuItem("Quit");
        quitMenuItem.setCallback(c -> Platform.runLater(() -> {
            MessageDialog exitDialog = new MessageDialog("It seems you clicked the exit button right now,\n" +
                    "Are you sure you want to exit?", MessageDialog.Type.INFO, MessageDialog.Buttons.YES_AND_NO);
            exitDialog.getYesButton().setOnAction(e -> {
                exitDialog.close();
                VDM.saveAndExit();
                systemTray.shutdown();
            });
            exitDialog.getNoButton().setOnAction(e -> exitDialog.close());
            exitDialog.showAndWait();
        }));
        quitMenuItem.setImage(VDM.class.getResource("theme/imgs/cancel.png"));
        quitMenuItem.setShortcut('q');


        Menu trayMenu = systemTray.getMenu();
        trayMenu.add(showWindowMenuItem);
        trayMenu.add(new Separator());
        trayMenu.add(newDownloadMenuItem);
        trayMenu.add(startQueueMenuItem);
        trayMenu.add(pauseQueueMenuItem);
        trayMenu.add(pauseAllMenuItem);
        trayMenu.add(new Separator());
        trayMenu.add(clipboardMenuItem);
        trayMenu.add(startupMenuItem);
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

    public static void initClipboardMonitor() {

        final Clipboard systemClipboard = Clipboard.getSystemClipboard();
        final StringBuilder lastClipboardText = new StringBuilder(systemClipboard.hasString() && systemClipboard.getString() != null? systemClipboard.getString() : "zox");
        String urlRegex = "(https?:\\/\\/)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";

        checkClipboardTask = new Timeline(new KeyFrame(Duration.millis(250), event -> {
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
        checkClipboardTask.setCycleCount(Timeline.INDEFINITE);
        if( (Boolean) DataHandler.getAppPreferences().get("TrayHandled.clipboardMonitor"))
            checkClipboardTask.play();

    }

}
