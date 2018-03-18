import dorkbox.systemTray.Menu;
import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.Separator;
import dorkbox.systemTray.SystemTray;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;


public class TrayHandler {

    private static SystemTray systemTray;
    private static Stage appStage = Main.getAppStage();
    private static int numOfRunningDownloads = 0;


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

    public static void initSystemTray() {

        systemTray = SystemTray.get();
        if(systemTray == null)
            return;
        systemTray.setTooltip("Nazel Video Downloader");
        systemTray.setImage(TrayHandler.class.getResource("icon/icon.png"));
        systemTray.setStatus("No Running Downloads");

        MenuItem showWindowMenuItem = new MenuItem("Show Window");
        showWindowMenuItem.setCallback(e -> Platform.runLater(() -> {
            try {
                FXMLLoader newDownloadWindowLoader = new FXMLLoader(TrayHandler.class.getResource("windows/HomeWindow.fxml"));
                newDownloadWindowLoader.load();
                Parent root = newDownloadWindowLoader.getRoot();
                appStage.getScene().setRoot(root);
            } catch (Exception ex) {
                System.err.println("Error returning to Home Window");
            } finally {
                appStage.show();
                appStage.toFront();
            }
        }));
        showWindowMenuItem.setImage(Main.class.getResource("icon/icon.png"));
        showWindowMenuItem.setShortcut('w');

        MenuItem newDownloadMenuItem = new MenuItem("New Download");
        newDownloadMenuItem.setCallback(e -> Platform.runLater(() -> {
            try {
                FXMLLoader newDownloadWindowLoader = new FXMLLoader(TrayHandler.class.getResource("windows/NewDownloadWindow.fxml"));
                newDownloadWindowLoader.load();
                Parent root = newDownloadWindowLoader.getRoot();
                appStage.getScene().setRoot(root);
                appStage.show();
                appStage.toFront();
            } catch (Exception ex) {
                new MessageDialog("Couldn't load the New Download page!\n" +
                        "Restart the program and try again.", MessageDialog.Type.ERROR,
                        MessageDialog.Buttons.CLOSE).createErrorDialog(ex.getStackTrace()).showAndWait();
            }
        }));
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



}
