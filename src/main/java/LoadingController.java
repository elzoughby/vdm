import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoadingController implements Initializable {

    @FXML
    private StackPane pane;



    @Override
    public void initialize(URL location, ResourceBundle resources) {



    }

    @Override
    protected void finalize() throws Throwable {

        DbManager.initialize();
        DbManager.load();
        Platform.runLater(this::close);
        super.finalize();

    }

    private void close() {

        try {
            Parent newRoot = FXMLLoader.load(getClass().getResource("windows/HomeWindow.fxml"));
            Parent oldRoot = pane.getScene().getRoot();
            Stage stage = (Stage) pane.getScene().getWindow();
            Scene scene = new Scene(newRoot);

            FadeTransition fadeOut = new FadeTransition(new Duration(500), oldRoot);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);
            fadeOut.play();

            stage.setScene(scene);

            FadeTransition fadeIn = new FadeTransition(new Duration(500), newRoot);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setCycleCount(1);
            fadeIn.play();

        } catch (IOException e) {
            System.out.println("Error Loading the Home Window");
        }

    }

}
