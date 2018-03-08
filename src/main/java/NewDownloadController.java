import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
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
    private HBox subtitleLanguagePane;
    @FXML
    private ChoiceBox<String> subtitleLanguageChoiceBox;
    @FXML
    private CheckBox isPlaylistChkBox;
    @FXML
    private VBox playlistPane;
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

    private boolean isQueueBtnSelected;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String urlRegex = "(https?:\\/\\/)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
        Clipboard systemClipboard = Clipboard.getSystemClipboard();

        urlTextField.textProperty().addListener((obs, oldText, newText) -> {
            if(newText.contains("playlist?list="))
                isPlaylistChkBox.setSelected(true);
        });

        String clipboardText = systemClipboard.getString();
        if(clipboardText != null && clipboardText.matches(urlRegex))
            urlTextField.setText(clipboardText);

        if(System.getProperty("os.name").toLowerCase().contains("win"))
            locationTextField.setText(System.getProperty("user.home") + "\\Downloads");
        else
            locationTextField.setText(System.getProperty("user.home") + "/Downloads");

        subtitleLanguageChoiceBox.setItems(FXCollections.observableArrayList("Arabic", "English", "French", "Italian", "spanish", "German", "Russian"));
        subtitleLanguageChoiceBox.setValue("English");
        embeddedSubtitleChkBox.selectedProperty().not().and(autoGenSubtitleChkBox.selectedProperty().not()).addListener((observable, oldValue, newValue) -> subtitleLanguagePane.setDisable(newValue));
        qualityComboBox.setItems(FXCollections.observableArrayList("Best", "1080p - mp4 video", "720p - mp4 video", "480p - mp4 video", "360p - mp4 video", "240p - mp4 video", "144p - mp4 video", "48K - m4a audio only"));
        qualityComboBox.setValue("Best");

        newDownloadWindowPane.setOnKeyPressed((KeyEvent keyEvent) -> {
            if(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN).match(keyEvent))
                startBtnAction();
            else if(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN).match(keyEvent))
                scheduleBtnAction();
            else if(new KeyCodeCombination(KeyCode.ESCAPE).match(keyEvent))
                cancelBtnAction();
        });

    }

    public void setQueueBtnSelected(boolean selected) {
        isQueueBtnSelected = selected;
    }


    @FXML
    private void browseBtnAction() {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(locationTextField.getText()));
        directoryChooser.setTitle("Choose save location");

        File selectedDirectory = directoryChooser.showDialog(newDownloadWindowPane.getScene().getWindow());
        if(selectedDirectory != null)
            locationTextField.setText(selectedDirectory.getPath());

    }

    @FXML
    private void startBtnAction() {

        if(isValidInfo()) {
            Item item = createItem();
            ////////////////////////////////////
            System.out.println(item.toString());
            ////////////////////////////////////
            item.setIsAddedToQueue(false);
            HomeController.getItemList().add(item);
            DataHandler.save(item);
            item.startDownload();
            cancelAndSetQueueBtn(false);
        }

    }

    @FXML
    private void scheduleBtnAction() {

        if(isValidInfo()) {
            Item item = createItem();
            ////////////////////////////////////
            System.out.println(item.toString());
            ////////////////////////////////////
            item.setIsAddedToQueue(true);

            if(queueIsRunningBefore(item))
                item.setStatus("Waiting");
            else
                item.setStatus("Stopped");

            HomeController.getQueueItemList().add(item);
            DataHandler.save(item);
            cancelAndSetQueueBtn(true);
        }

    }

    @FXML
    private void cancelBtnAction() {
        cancelAndSetQueueBtn(isQueueBtnSelected);
    }

    private boolean isValidInfo() {

        boolean result = true;
        String urlRegex = "(https?:\\/\\/)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";

        // Check if the url is valid
        if(urlTextField.getText().matches(urlRegex)) {
            urlTextField.getStyleClass().clear();
            urlTextField.getStyleClass().add("text-field");
        } else {
            urlTextField.getStyleClass().add("text-field-error");
            result = false;
        }

        // Check if the save location is not empty
        if(locationTextField.getText().equals("")) {
            locationTextField.getStyleClass().add("text-field-error");
            result = false;
        } else {
            locationTextField.getStyleClass().clear();
            locationTextField.getStyleClass().add("text-field");
        }

        // Check if the custom name is not empty
        if(customNameChkBox.isSelected()) {
            if(customNameTextField.getText().equals("")) {
                customNameTextField.getStyleClass().add("text-field-error");
                result = false;
            } else {
                customNameTextField.getStyleClass().clear();
                customNameTextField.getStyleClass().add("text-field");
            }
        }

        // Check if the specific playlist items are written correctly
        if(isPlaylistChkBox.isSelected()) {
            if(indexRangeRadioBtn.isSelected()) {
                if(startIndexTextField.getText().matches("[1-9]*")) {
                    startIndexTextField.getStyleClass().clear();
                    startIndexTextField.getStyleClass().add("text-field");
                } else {
                    startIndexTextField.getStyleClass().add("text-field-error");
                    result = false;
                }

                if(endIndexTextField.getText().matches("[1-9]*")) {
                    endIndexTextField.getStyleClass().clear();
                    endIndexTextField.getStyleClass().add("text-field");
                } else {
                    endIndexTextField.getStyleClass().add("text-field-error");
                    result = false;
                }
            }

            if(specificItemsRadioBtn.isSelected()) {
                if(playlistItemsTextField.getText().matches("[0-9,]+")) {
                    playlistItemsTextField.getStyleClass().clear();
                    playlistItemsTextField.getStyleClass().add("text-field");
                } else {
                    playlistItemsTextField.getStyleClass().add("text-field-error");
                    result = false;
                }
            }
        }

        // Check if the username and password are not empty
        if(needLoginCheckBox.isSelected()) {
            if(userNameTextField.getText().equals("")) {
                userNameTextField.getStyleClass().add("text-field-error");
                result = false;
            } else {
                userNameTextField.getStyleClass().clear();
                userNameTextField.getStyleClass().add("text-field");
            }

            if(passwordTextField.getText().equals("")) {
                passwordTextField.getStyleClass().add("text-field-error");
                result = false;
            } else {
                passwordTextField.getStyleClass().clear();
                passwordTextField.getStyleClass().add("text-field");
            }
        }

        // Check if the speed limit is valid
        if(limitSpinner.getEditor().getText().matches("[0-9]+")) {
            limitSpinner.getStyleClass().clear();
            limitSpinner.getStyleClass().add("spinner");
        } else {
            limitSpinner.getStyleClass().add("spinner-error");
            result = false;
        }

        return result;
    }

    private Item createItem() {

        Item item = new Item();

        item.setId(DataHandler.getNextId());
        item.setUrl(urlTextField.getText());
        item.setTitle(urlTextField.getText());
        item.setLocation(locationTextField.getText().replaceAll("[/\\\\]$",""));
        if(customNameChkBox.isSelected())
            item.setCustomName(customNameTextField.getText());

        if(isPlaylistChkBox.isSelected()) {
            item.setIsPlaylist(true);
            item.setNeedAllPlaylistItems(false);
            if(allItemsRadioBtn.isSelected()) {
                item.setNeedAllPlaylistItems(true);
            } else if(indexRangeRadioBtn.isSelected()) {

                if(startIndexTextField.getText().equals("")) {
                    item.setPlaylistStartIndex(0);
                } else {
                    item.setPlaylistStartIndex(Integer.parseInt(startIndexTextField.getText()));
                }

                if(endIndexTextField.getText().equals("")) {
                    item.setPlaylistEndIndex(-1);
                } else {
                    item.setPlaylistEndIndex(Integer.parseInt(endIndexTextField.getText()));
                }

            } else {
                item.setPlaylistItems(playlistItemsTextField.getText());
            }
        }

        item.setIsVideo(true);
        item.setFormat("mp4");
        String selectedQuality = qualityComboBox.getSelectionModel().getSelectedItem();

        if (selectedQuality.equals("1080p - mp4 video")) {
            item.setVideoQuality(137);
            item.setAudioQuality(141);
        } else if (selectedQuality.equals("720p - mp4 video")) {
            item.setVideoQuality(22);
            item.setAudioQuality(0);
        } else if (selectedQuality.equals("480p - mp4 video")) {
            item.setVideoQuality(135);
            item.setAudioQuality(140);
        } else if (selectedQuality.equals("360p - mp4 video")) {
            item.setVideoQuality(18);
            item.setAudioQuality(0);
        } else if (selectedQuality.equals("240p - mp4 video")) {
            item.setVideoQuality(133);
            item.setAudioQuality(139);
        } else if (selectedQuality.equals("144p - mp4 video")) {
            item.setVideoQuality(17);
            item.setAudioQuality(0);
        } else if (selectedQuality.equals("48K - m4a audio only")) {
            item.setFormat("mp3");
            item.setIsVideo(false);
            item.setVideoQuality(0);
            item.setAudioQuality(139);
        }

        item.setNeedEmbeddedSubtitle(embeddedSubtitleChkBox.isSelected());
        item.setSubtitleLanguage(subtitleLanguageChoiceBox.getValue());
        item.setNeedAutoGeneratedSubtitle(autoGenSubtitleChkBox.isSelected());

        if(needLoginCheckBox.isSelected()) {
            item.setUserName(userNameTextField.getText());
            item.setPassword(passwordTextField.getText());
        }

        item.setSpeedLimit(Integer.parseInt(limitSpinner.getEditor().getText()));
        item.setShutdownAfterFinish(shutdownCheckBox.isSelected());

        return item;
    }

    private void cancelAndSetQueueBtn(boolean queueBtnSelected) {

        try {

            FXMLLoader homeWindowLoader = new FXMLLoader(getClass().getResource("windows/HomeWindow.fxml"));
            homeWindowLoader.load();
            ((HomeController) homeWindowLoader.getController()).getQueueBtn().setSelected(queueBtnSelected);
            Parent root = homeWindowLoader.getRoot();
            newDownloadWindowPane.getScene().setRoot(root);

        } catch (Exception e) {
            newDownloadWindowPane.setOpacity(0.30);
            new MessageDialog("Error Loading the Home Window! \n" +
                    "Restart program and try again.", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(e.getStackTrace()).showAndWait();
            newDownloadWindowPane.setOpacity(1);
        }

    }

    private boolean queueIsRunningBefore(Item currentItem) {
        for(Item item : HomeController.getQueueItemList()) {
            if(item.getStatus().equals("Starting") || item.getStatus().equals("Running"))
                return true;
        }
        return false;
    }

}
