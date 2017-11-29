import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class NewDownloadController implements Initializable{

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
    private ChoiceBox<String> qualityComboBox;
    @FXML
    private CheckBox embeddedSubtitleChkBox;
    @FXML
    private CheckBox autoGenSubtitleChkBox;
    @FXML
    private ChoiceBox<String> subtitleLanguageChoiceBox;
    @FXML
    private TitledPane playlistPane;
    @FXML
    private RadioButton allItemsRadioBtn;
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
    private Spinner<Integer> limitSpinner;
    @FXML
    private CheckBox shutdownCheckBox;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String urlRegex = "(https?:\\/\\/)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
        Clipboard systemClipboard = Clipboard.getSystemClipboard();

        urlTextField.textProperty().addListener((obs, oldText, newText) -> {
            if(newText.matches(urlRegex)) {
                urlTextField.setStyle(null);
            } else {
                urlTextField.setStyle("-fx-background-color: crimson, white;");
            }

            if(newText.contains("playlist?list="))
                playlistPane.setDisable(false);
            else
                playlistPane.setDisable(true);
        });

        String clipboardText = systemClipboard.getString();
        if(clipboardText != null && clipboardText.matches(urlRegex))
            urlTextField.setText(clipboardText);

        locationTextField.setText(System.getProperty("user.home") + "/Downloads");
        subtitleLanguageChoiceBox.setItems(FXCollections.observableArrayList("Arabic", "English", "French", "Italian", "spanish", "German", "Russian"));
        subtitleLanguageChoiceBox.setValue("English");
        qualityComboBox.setItems(FXCollections.observableArrayList("Best", "1080p - mp4 video", "720p - mp4 video", "480p - mp4 video", "360p - mp4 video", "240p - mp4 video", "144p - mp4 video", "48K - m4a audio only"));
        qualityComboBox.setValue("Best");

    }

    private Item createItem() {

        Item item = new Item();

        item.setItemId(DbManager.getNextId());
        item.setUrl(urlTextField.getText());
        item.setTitle(urlTextField.getText());
        item.setLocation(locationTextField.getText());
        if(customNameChkBox.isSelected())
            item.setCustomName(customNameTextField.getText());

        if(!playlistPane.isDisable()) {
            item.setIsPlaylist(true);
            item.setAllItems(false);
            if(allItemsRadioBtn.isSelected())
                item.setAllItems(true);
            else if(indexRangeRadioBtn.isSelected()) {
                item.setStartIndex(Integer.parseInt(startIndexTextField.getText()));
                item.setEndIndex(Integer.parseInt(endIndexTextField.getText()));
            } else
                item.setItems(playlistItemsTextField.getText());
        }

        item.setIsVideo(true);
        item.setFormat("mp4");

        if (qualityComboBox.getSelectionModel().getSelectedItem().equals("1080p - mp4 video")) {
            item.setVideoQuality(137);
            item.setAudioQuality(141);
        } else if (qualityComboBox.getSelectionModel().getSelectedItem().equals("720p - mp4 video")) {
            item.setVideoQuality(22);
            item.setAudioQuality(0);
        } else if (qualityComboBox.getSelectionModel().getSelectedItem().equals("480p - mp4 video")) {
            item.setVideoQuality(135);
            item.setAudioQuality(140);
        } else if (qualityComboBox.getSelectionModel().getSelectedItem().equals("360p - mp4 video")) {
            item.setVideoQuality(18);
            item.setAudioQuality(0);
        } else if (qualityComboBox.getSelectionModel().getSelectedItem().equals("240p - mp4 video")) {
            item.setVideoQuality(133);
            item.setAudioQuality(139);
        } else if (qualityComboBox.getSelectionModel().getSelectedItem().equals("144p - mp4 video")) {
            item.setVideoQuality(17);
            item.setAudioQuality(0);
        } else if (qualityComboBox.getSelectionModel().getSelectedItem().equals("48K - m4a audio only")) {
            item.setFormat("mp3");
            item.setIsVideo(false);
            item.setVideoQuality(0);
            item.setAudioQuality(139);
        }

        item.setEmbeddedSubtitle(embeddedSubtitleChkBox.isSelected());
        item.setSubtitleLanguage(subtitleLanguageChoiceBox.getValue());
        item.setAutoGeneratedSubtitle(autoGenSubtitleChkBox.isSelected());
        item.setSpeedLimit(limitSpinner.getValue());
        item.setShutdownAfterFinish(shutdownCheckBox.isSelected());

        return item;
    }

    @FXML
    void browseBtnAction() {

        String saveLocation = new DirectoryChooser().showDialog(newDownloadWindowPane.getScene().getWindow()).getPath();
        locationTextField.setText(saveLocation);

    }

    @FXML
    void startBtnAction() {

        Item item = createItem();
        item.setAddToQueue(false);
        HomeController.getItemList().add(item);
        DbManager.insert(item);
        item.startDownload();
        cancelBtnAction();

    }

    @FXML
    void scheduleBtnAction() {

        Item item = createItem();
        item.setAddToQueue(true);
        HomeController.getQueueItemList().add(item);
        DbManager.insert(item);
        cancelBtnAction();

    }

    @FXML
    void cancelBtnAction() {

        try {

            Parent root = FXMLLoader.load(getClass().getResource("windows/HomeWindow.fxml"));
            newDownloadWindowPane.getScene().setRoot(root);

        } catch (IOException e) {
            System.err.println("Error Loading Home Window!");
        }

    }

}