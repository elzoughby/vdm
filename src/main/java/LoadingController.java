import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.toilelibre.libe.curl.Curl;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;


public class LoadingController implements Initializable {

    @FXML private VBox loadingPane;
    @FXML private Label statusLabel;

    private Stage appStage = Main.getAppStage();



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> appStage.setOnCloseRequest(Event::consume));
    }

    @Override
    protected void finalize() {

        // Load previous download items data
        Platform.runLater(() -> statusLabel.setText("loading data"));
        DataHandler.load();

        try {

            // getting the online youtube-dl version
            Platform.runLater(() -> statusLabel.setText("checking fro updates"));
            Document document = Jsoup.connect("http://yt-dl.org/").get();
            Elements divs = document.select("div");
            Element latestVersionDiv = divs.get(1);
            String latestVersionText = latestVersionDiv.text();
            String serverVersion = latestVersionText.split("\\(")[1].split("\\)")[0].substring(1);

            // getting the local youtube-dl version
            Process ytdlProcess = new ProcessBuilder("python", "youtube-dl", "--version").start();
            InputStream inputStream = ytdlProcess.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String localVersion = bufferedReader.readLine();

            // Checking for youtube-dl updates
            if(serverVersion != null && ! serverVersion.equals(localVersion)) {

                // downloading youtube-dl updates
                Platform.runLater(() -> statusLabel.setText("updating youtube-dl"));
                File tempFile = new File("youtube-dl.tmp");
                if(tempFile.exists())
                    tempFile.delete();
                Curl.curl("-L http://yt-dl.org/downloads/latest/youtube-dl -o youtube-dl.tmp");

                File file = new File("youtube-dl");
                if(file.delete()) {
                    tempFile.renameTo(file);
                }

            }

        } catch (Exception e) {
            System.err.println("Error updating youtube-dl : " + e.getMessage());
        }

        // Go to home page
        Platform.runLater(() -> {
            close();
            appStage.setOnCloseRequest(event -> appStage.close());
        });

    }

    private void close() {

        try {

            Parent newRoot = FXMLLoader.load(getClass().getResource("windows/HomeWindow.fxml"));

            FadeTransition fadeIn = new FadeTransition(new Duration(500), newRoot);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setCycleCount(1);
            fadeIn.play();

            loadingPane.getScene().setRoot(newRoot);

        } catch (Exception e) {
            new MessageDialog("Error Loading the Home Window! \n" +
                    "Restart program and try again.", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(e.getStackTrace()).showAndWait();
        }

    }

}
