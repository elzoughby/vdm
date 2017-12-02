import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
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

        DatabaseManager.initialize();
        DatabaseManager.load();
        Platform.runLater(this::close);
        super.finalize();

    }

    private void close() {

        try {

            Parent newRoot = FXMLLoader.load(getClass().getResource("windows/HomeWindow.fxml"));

            FadeTransition fadeIn = new FadeTransition(new Duration(500), newRoot);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setCycleCount(1);
            fadeIn.play();

            pane.getScene().setRoot(newRoot);

        } catch (IOException e) {
            new ErrorDialog("Error Loading the Home Window! \n" +
                    "Restart program and try again.", e.getStackTrace()).showAndWait();
        }

    }

}
