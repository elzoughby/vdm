import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;


public class AboutController implements Initializable {

    private double xOffset = 0;
    private double yOffset = 0;
    private static Stage aboutStage;

    @FXML private VBox aboutWindowVBox;
    @FXML private Label versionLabel;



    public static void showAboutDialog() {

        try {

            FXMLLoader aboutWindowLoader = new FXMLLoader(AboutController.class.getResource("windows/aboutWindow.fxml"));
            Parent root = aboutWindowLoader.load();
            Scene scene = new Scene(root);
            aboutStage = new Stage();
            aboutStage.setTitle("About");
            aboutStage.setMinWidth(450);
            aboutStage.setMaxWidth(450);
            aboutStage.setWidth(450);
            aboutStage.setMinHeight(480);
            aboutStage.setMaxHeight(480);
            aboutStage.setHeight(480);
            aboutStage.setResizable(false);
            aboutStage.initStyle(StageStyle.UNDECORATED);
            aboutStage.initModality(Modality.APPLICATION_MODAL);
            aboutStage.getIcons().add(0, new Image(AboutController.class.getResource("icon/icon.png").toString()));
            aboutStage.setScene(scene);
            aboutStage.show();

        } catch (Exception ex) {
            new MessageDialog("Error loading the about window! \n" +
                    "Restart program and try again.", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(ex.getStackTrace()).showAndWait();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        versionLabel.setText(Main.getVersion());

        aboutWindowVBox.setOnKeyPressed((KeyEvent keyEvent) -> {
            if(new KeyCodeCombination(KeyCode.ESCAPE).match(keyEvent))
                closeBtnAction();
        });

        aboutWindowVBox.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        aboutWindowVBox.setOnMouseDragged(event -> {
            aboutStage.setX(event.getScreenX() - xOffset);
            aboutStage.setY(event.getScreenY() - yOffset);
        });

    }

    @FXML
    private void closeBtnAction() {

        Stage aboutStage = (Stage) aboutWindowVBox.getScene().getWindow();
        aboutStage.close();
    }

}
