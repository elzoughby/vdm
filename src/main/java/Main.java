import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URISyntaxException;


public class Main extends Application {

    private static Stage appStage;
    private static boolean startMinimized = false;

    public static final String VERSION = "1.0.0";
    public static final String WEBSITE = "https://elzoughby.github.io/vdm";
    public static final String PATREON = "https://www.patreon.com/bePatron?c=1746384";

    public static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    public static final String SEPARATOR = System.getProperty("file.separator");
    public static final String SYSTEM_TEMP_PATH = System.getProperty("java.io.tmpdir").replaceAll("[/\\\\]$", "");
    public static final String USER_HOME = System.getProperty("user.home").replaceAll("[/\\\\]$", "");
    public static final String APP_DATA_DIRECTORY = DataHandler.getAppDataDirectory();

    private static String rawJarPath;
    static {
        try {
            rawJarPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
    public static final String JAR_PATH = rawJarPath.replaceAll("[/\\\\]$", "");
    public static final String INSTALL_PATH = JAR_PATH.replace(SEPARATOR + "app" + SEPARATOR + "vdm.jar", "");
    public static final String EXECUTABLE_NAME = OS_NAME.contains("win")? "vdm.exe" : "vdm";
    public static final String EXECUTABLE_PATH = INSTALL_PATH + SEPARATOR + EXECUTABLE_NAME;


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
        File tempDirectory = new File(SYSTEM_TEMP_PATH + SEPARATOR + "vdm");
        if(tempDirectory.exists() && tempDirectory.isDirectory()) {
            File[] tempFiles = tempDirectory.listFiles();
            if(tempFiles != null) {
                for (File tempFile : tempFiles)
                    tempFile.delete();
            }
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
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Video Download Manager");
            primaryStage.getIcons().add(0, new Image(getClass().getResource("icon/icon.png").toString()));
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
            appStage.setOnCloseRequest(event -> appStage.close());

            TrayHandler.initSystemTray();
            TrayHandler.initNotifications();
            TrayHandler.initClipboardMonitor();
            AES.initKey();

            // instructs the javafx system not to exit implicitly when the last application window is shut.
            Platform.setImplicitExit(false);
            if(! startMinimized) {
                primaryStage.show();
            }

            Task<Void> loadingTask = new Task<Void>() {
                @Override
                protected Void call() {

                    Label statusLabel = (Label) loader.getNamespace().get("statusLabel");

                    // Load previous download items data
                    Platform.runLater(() -> statusLabel.setText("Loading data"));
                    DataHandler.loadSavedItems();

                    try {

                        // getting the online youtube-dl version
                        Platform.runLater(() -> statusLabel.setText("checking for updates"));
                        Document document = Jsoup.connect("http://yt-dl.org/").get();
                        Elements divs = document.select("div");
                        Element latestVersionDiv = divs.get(1);
                        String latestVersionText = latestVersionDiv.text();
                        String serverVersion = latestVersionText.split("\\(")[1].split("\\)")[0].substring(1);

                        // getting the local youtube-dl version
                        Process ytdlProcess = new ProcessBuilder("python", APP_DATA_DIRECTORY + SEPARATOR + "youtube-dl", "--version").start();
                        InputStream inputStream = ytdlProcess.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String localVersion = bufferedReader.readLine();

                        // Checking for youtube-dl updates
                        if(serverVersion != null && ! serverVersion.equals(localVersion)) {

                            // downloading youtube-dl updates
                            Platform.runLater(() -> statusLabel.setText("updating youtube-dl"));
                            File tempFile = new File(APP_DATA_DIRECTORY + SEPARATOR + "youtube-dl.tmp");
                            if(tempFile.exists())
                                tempFile.delete();

                            HttpDownloader httpDownloader = new HttpDownloader("http://yt-dl.org/downloads/latest/youtube-dl", APP_DATA_DIRECTORY);
                            httpDownloader.setCustomName("youtube-dl.tmp");
                            httpDownloader.readDownloadInfo();
                            httpDownloader.start();

                            if(httpDownloader.getDownloaded() != httpDownloader.getFileSize())
                                throw new Exception("couldn't complete downloading youtube-dl successfully");

                            File file = new File(APP_DATA_DIRECTORY + SEPARATOR + "youtube-dl");
                            if(file.exists()) {
                                if(file.delete()) {
                                    tempFile.renameTo(file);
                                }
                            } else {
                                tempFile.renameTo(file);
                            }

                        }

                    } catch (Exception e) {
                        System.err.println("Error updating youtube-dl : " + e.getMessage());
                        File file = new File(APP_DATA_DIRECTORY + SEPARATOR + "youtube-dl");
                        if(! file.exists()) {
                            new MessageDialog("Couldn't download youtube-dl\n" +
                                    "Try again later or report this issue.", MessageDialog.Type.ERROR,
                                    MessageDialog.Buttons.CLOSE).createErrorDialog(e.getStackTrace()).showAndWait();
                        }
                    }

                    // Go to home page
                    Platform.runLater(Main::goForward);

                    return null;
                }
            };

            Thread loadingThread = new Thread(loadingTask);
            loadingThread.start();

        } catch (Exception e) {
            e.printStackTrace();
            new MessageDialog("Error loading the LoadingPage window! \n" +
                    "Try again later or report this issue", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(e.getStackTrace()).showAndWait();
        }

    }

    private static void goForward() {

        try {

            Parent newRoot = null;
            FXMLLoader loader = null;

            if(TrayHandler.isMoveToNewDownload()) {
                loader = new FXMLLoader(Main.class.getResource("windows/NewDownloadWindow.fxml"));
                newRoot = loader.load();
            } else {
                loader = new FXMLLoader(Main.class.getResource("windows/HomeWindow.fxml"));
                newRoot = loader.load();
            }


            FadeTransition fadeIn = new FadeTransition(new Duration(1250), newRoot);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setCycleCount(1);
            fadeIn.play();

            appStage.getScene().setRoot(newRoot);
            if(TrayHandler.isMoveToNewDownload()) {
                NewDownloadController controller = loader.getController();
                controller.showUrlDialog();
            }

        } catch (Exception e) {
            new MessageDialog("Error Loading the Home Window! \n" +
                    "Try again later or report this issue", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(e.getStackTrace()).showAndWait();
        }

    }

}
