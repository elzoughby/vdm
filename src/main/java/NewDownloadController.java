import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;


public class NewDownloadController {

    @FXML
    private BorderPane newDownloadWindowPane;
    @FXML
    private TextField urlTextField;
    @FXML
    private TextField locationTextField;
    @FXML
    private CheckBox customNameChkBox;
    @FXML
    private TextField customNameTextField;
    @FXML
    private ChoiceBox<?> qualityComboBox;
    @FXML
    private CheckBox embeddedSubtitleChkBox;
    @FXML
    private CheckBox autoGenSubtitleChkBox;
    @FXML
    private ChoiceBox<?> subtitleLanguageChoiceBox;
    @FXML
    private RadioButton allItemsRadioBtn;
    @FXML
    private ToggleGroup playlistToggleGroup;
    @FXML
    private RadioButton indexRangeRadioBtn;
    @FXML
    private TextField startIndexTextField;
    @FXML
    private TextField endIndexTextField;
    @FXML
    private RadioButton specificItemsRadioBtn;
    @FXML
    private TextField playlistItemsTextField;
    @FXML
    private CheckBox needLoginCheckBox;
    @FXML
    private TextField userNameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private Spinner<?> limitSpinner;
    @FXML
    private CheckBox shutdownCheckBox;




    @FXML
    void startBtnAction(ActionEvent event) {

    }

    @FXML
    void scheduleBtnAction(ActionEvent event) {

    }

    @FXML
    void cancelBtnAction(ActionEvent event) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("windows/HomeWindow.fxml"));
            Stage stage = (Stage) newDownloadWindowPane.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Error Loading Home Window!");
        }

    }

    @FXML
    void browseBtnAction(ActionEvent event) {

    }



}
