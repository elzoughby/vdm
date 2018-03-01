import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;


public class LoadingController implements Initializable {

    @FXML
    private StackPane loadingPane;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadingPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                DataHandler.load();
                Platform.runLater(this::close);
            }
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
