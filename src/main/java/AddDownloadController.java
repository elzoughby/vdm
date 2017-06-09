import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class AddDownloadController implements Initializable {

    @FXML
    private AnchorPane addWindowPane;
    @FXML
    private TextField urlTextField;
    @FXML
    private TextField locationTextField;
    @FXML
    private CheckBox customNameChkBox;
    @FXML
    private TextField customNameTextField;
    @FXML
    private Pane playlistPane;
    @FXML
    private RadioButton allItemsRadioBtn;
    @FXML
    private RadioButton indexRangeRadioBtn;
    @FXML
    private Pane indexRangePane;
    @FXML
    private TextField startIndexTextField;
    @FXML
    private TextField endIndexTextField;
    @FXML
    private RadioButton specificItemsRadioBtn;
    @FXML
    private TextField playlistItemsTextField;
    @FXML
    private ComboBox qualityComboBox;
    @FXML
    private CheckBox embeddedSubtitleChkBox;
    @FXML
    private ChoiceBox subtitleLanguageChoiceBox;
    @FXML
    private CheckBox autoGenSubtitleChkBox;
    @FXML
    private Spinner limitSpinner;
    @FXML
    private ChoiceBox afterFinishChoiceBox;
    @FXML
    private CheckBox addToQueueChkBox;




    @Override
    public void initialize(URL location, ResourceBundle resources) {

        urlTextField.textProperty().addListener((obs, oldText, newText) -> {

            String urlRegex = "(https?:\\/\\/)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";

            if(newText.matches(urlRegex))
                urlTextField.setStyle("-fx-border-color: #0022");
            else
                urlTextField.setStyle("-fx-border-color: tomato");

            if(newText.contains("playlist?list="))
                playlistPane.setDisable(false);
            else
                playlistPane.setDisable(true);
        });

        String homeFolder = System.getProperty("user.home");
        locationTextField.setText(homeFolder + "/Downloads");

        indexRangeRadioBtn.selectedProperty().addListener((obs, wasPreviouslySelected, isNowSelected) -> {
            if(isNowSelected)
                indexRangePane.setDisable(false);
            else
                indexRangePane.setDisable(true);
        });

        specificItemsRadioBtn.selectedProperty().addListener((obs, wasPreviouslySelected, isNowSelected) -> {
            if(isNowSelected)
                playlistItemsTextField.setDisable(false);
            else
                playlistItemsTextField.setDisable(true);
        });

        afterFinishChoiceBox.setItems(FXCollections.observableArrayList("Do nothing", "Sleep", "Shutdown"));
        afterFinishChoiceBox.setValue("Do nothing");

        subtitleLanguageChoiceBox.setItems(FXCollections.observableArrayList("Arabic", "English", "French", "Italian", "spanish", "German", "Russian"));
        subtitleLanguageChoiceBox.setValue("English");

        qualityComboBox.setItems(FXCollections.observableArrayList("Default", "1080p - mp4 video", "720p - mp4 video", "480p - mp4 video", "360p - mp4 video", "240p - mp4 video", "144p - mp4 video", "48K - m4a audio only"));
        qualityComboBox.setValue("Default");

    }

    @FXML
    private void browseBtnAction() {

        String saveLocation = new DirectoryChooser().showDialog(addWindowPane.getScene().getWindow()).getPath();
        locationTextField.setText(saveLocation);

    }

    @FXML
    private void customNameChkBoxAction() {

        if(customNameChkBox.isSelected())
            customNameTextField.setDisable(false);
        else
            customNameTextField.setDisable(true);

    }

    @FXML
    private void embeddedSubtitleChkBoxAction() {

        if(embeddedSubtitleChkBox.isSelected())
            subtitleLanguageChoiceBox.setDisable(false);
        else
            subtitleLanguageChoiceBox.setDisable(true);

    }

    @FXML
    private void cancelBtnAction() {
        Stage stage = (Stage) addWindowPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void startBtnAction() {

        Item item = new Item();

        item.setItemId(DbManager.getNextId());
        item.setUrl(urlTextField.getText());
        item.setTitle(urlTextField.getText());
        item.setLocation(locationTextField.getText());
        item.setCustomName(customNameTextField.getText());

        if(! playlistPane.isDisable()) {
            item.setIsPlaylist(true);
            item.setAllItems(false);
            if (! allItemsRadioBtn.isDisable())
                item.setAllItems(true);
            else if (! indexRangeRadioBtn.isDisable()) {
                item.setStartIndex(Integer.parseInt(startIndexTextField.getText()));
                item.setEndIndex(Integer.parseInt(endIndexTextField.getText()));
            } else
                item.setItems(playlistItemsTextField.getText());
        }

        item.setIsVideo(true);
        item.setFormat("mp4");

        if (qualityComboBox.getSelectionModel().getSelectedItem() == "1080p - mp4 video") {
            item.setVideoQuality(137);
            item.setAudioQuality(141);
        } else if (qualityComboBox.getSelectionModel().getSelectedItem()=="720p - mp4 video") {
            item.setVideoQuality(22);
            item.setAudioQuality(0);
        } else if (qualityComboBox.getSelectionModel().getSelectedItem()=="480p - mp4 video") {
            item.setVideoQuality(135);
            item.setAudioQuality(140);
        } else if (qualityComboBox.getSelectionModel().getSelectedItem()=="360p - mp4 video") {
            item.setVideoQuality(18);
            item.setAudioQuality(0);
        } else if (qualityComboBox.getSelectionModel().getSelectedItem()=="240p - mp4 video") {
            item.setVideoQuality(133);
            item.setAudioQuality(139);
        } else if (qualityComboBox.getSelectionModel().getSelectedItem()=="144p - mp4 video") {
            item.setVideoQuality(17);
            item.setAudioQuality(0);
        } else if (qualityComboBox.getSelectionModel().getSelectedItem()=="48K - m4a audio only") {
            item.setFormat("mp3");
            item.setIsVideo(false);
            item.setVideoQuality(0);
            item.setAudioQuality(139);
        }


        item.setEmbeddedSubtitle(embeddedSubtitleChkBox.isSelected());
        item.setSubtitleLanguage(subtitleLanguageChoiceBox.getValue().toString());
        item.setAutoGeneratedSubtitle(autoGenSubtitleChkBox.isSelected());
        item.setSpeedLimit((Integer) limitSpinner.getValue());
        item.setActionAfterFinish(afterFinishChoiceBox.getValue().toString());
        item.setAddToQueue(addToQueueChkBox.isSelected());

        HomeController.getItemList().add(item);
        DbManager.insert(item);
        if(addToQueueChkBox.isSelected())
            HomeController.getQueueItemList().add(item);
        else
            item.startDownload();

        Stage stage = (Stage) addWindowPane.getScene().getWindow();
        stage.close();

    }


}
