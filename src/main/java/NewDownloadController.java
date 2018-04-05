import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;


public class NewDownloadController implements Initializable{

    @FXML private BorderPane newDownloadPane;
    @FXML private ToolBar toolbar;
    @FXML private Button startBtn;
    @FXML private Button scheduleBtn;
    @FXML private VBox scrollPaneVBox;
    @FXML private ImageView thumbnailImageView;
    @FXML private Label titleLabel;
    @FXML private Label urlLabel;
    @FXML private Label descriptionLabel;
    @FXML private TitledPane artifactsTitledPane;
    @FXML private HBox artifactsSaveLocationHBox;
    @FXML private TextField locationTextField;
    @FXML private Button browseBtn;
    @FXML private CheckBox customNameChkBox;
    @FXML private TextField customNameTextField;
    @FXML private TitledPane websiteTitledPane;
    @FXML private ChoiceBox<Quality> videoQualityChoiceBox;
    @FXML private ChoiceBox<Quality> audioQualityChoiceBox;
    @FXML private ChoiceBox<String> formatChoiceBox;
    @FXML private CheckBox embeddedSubtitleChkBox;
    @FXML private CheckBox autoGenSubtitleChkBox;
    @FXML private HBox subtitleLanguagePane;
    @FXML private ChoiceBox<String> subtitleLanguageChoiceBox;
    @FXML private TitledPane playlistTitledPane;
    @FXML private CheckBox isPlaylistChkBox;
    @FXML private VBox playlistPane;
    @FXML private RadioButton allItemsRadioBtn;
    @FXML private RadioButton indexRangeRadioBtn;
    @FXML private TextField startIndexTextField;
    @FXML private TextField endIndexTextField;
    @FXML private RadioButton specificItemsRadioBtn;
    @FXML private TextField playlistItemsTextField;
    @FXML private TitledPane authenticationTitledPane;
    @FXML private CheckBox needLoginCheckBox;
    @FXML private TextField userNameTextField;
    @FXML private TextField passwordTextField;
    @FXML private TitledPane othersTitledPane;
    @FXML private Spinner<Integer> limitSpinner;
    @FXML private CheckBox shutdownCheckBox;

    private boolean isQueueBtnSelected;
    private Stage appStage;
    private Stage urlDialogStage;



    public BorderPane getNewDownloadPane() {
        return newDownloadPane;
    }

    public ToolBar getToolbar() {
        return toolbar;
    }

    public Button getStartBtn() {
        return startBtn;
    }

    public Button getScheduleBtn() {
        return scheduleBtn;
    }

    public VBox getScrollPaneVBox() {
        return scrollPaneVBox;
    }

    public ImageView getThumbnailImageView() {
        return thumbnailImageView;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public Label getUrlLabel() {
        return urlLabel;
    }

    public Label getDescriptionLabel() {
        return descriptionLabel;
    }

    public TitledPane getArtifactsTitledPane() {
        return artifactsTitledPane;
    }

    public HBox getArtifactsSaveLocationHBox() {
        return artifactsSaveLocationHBox;
    }

    public TextField getLocationTextField() {
        return locationTextField;
    }

    public Button getBrowseBtn() {
        return browseBtn;
    }

    public CheckBox getCustomNameChkBox() {
        return customNameChkBox;
    }

    public TextField getCustomNameTextField() {
        return customNameTextField;
    }

    public TitledPane getWebsiteTitledPane() {
        return websiteTitledPane;
    }

    public ChoiceBox<Quality> getVideoQualityChoiceBox() {
        return videoQualityChoiceBox;
    }

    public ChoiceBox<Quality> getAudioQualityChoiceBox() {
        return audioQualityChoiceBox;
    }

    public ChoiceBox<String> getFormatChoiceBox() {
        return formatChoiceBox;
    }

    public CheckBox getEmbeddedSubtitleChkBox() {
        return embeddedSubtitleChkBox;
    }

    public CheckBox getAutoGenSubtitleChkBox() {
        return autoGenSubtitleChkBox;
    }

    public HBox getSubtitleLanguagePane() {
        return subtitleLanguagePane;
    }

    public ChoiceBox<String> getSubtitleLanguageChoiceBox() {
        return subtitleLanguageChoiceBox;
    }

    public TitledPane getPlaylistTitledPane() {
        return playlistTitledPane;
    }

    public CheckBox getIsPlaylistChkBox() {
        return isPlaylistChkBox;
    }

    public VBox getPlaylistPane() {
        return playlistPane;
    }

    public RadioButton getAllItemsRadioBtn() {
        return allItemsRadioBtn;
    }

    public RadioButton getIndexRangeRadioBtn() {
        return indexRangeRadioBtn;
    }

    public TextField getStartIndexTextField() {
        return startIndexTextField;
    }

    public TextField getEndIndexTextField() {
        return endIndexTextField;
    }

    public RadioButton getSpecificItemsRadioBtn() {
        return specificItemsRadioBtn;
    }

    public TextField getPlaylistItemsTextField() {
        return playlistItemsTextField;
    }

    public TitledPane getAuthenticationTitledPane() {
        return authenticationTitledPane;
    }

    public CheckBox getNeedLoginCheckBox() {
        return needLoginCheckBox;
    }

    public TextField getUserNameTextField() {
        return userNameTextField;
    }

    public TextField getPasswordTextField() {
        return passwordTextField;
    }

    public TitledPane getOthersTitledPane() {
        return othersTitledPane;
    }

    public Spinner<Integer> getLimitSpinner() {
        return limitSpinner;
    }

    public CheckBox getShutdownCheckBox() {
        return shutdownCheckBox;
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // stores a reference to the stage
        appStage = Main.getAppStage();

        urlLabel.textProperty().addListener((obs, oldText, newText) -> {
            if(newText.contains("playlist?list="))
                isPlaylistChkBox.setSelected(true);
        });

        if(System.getProperty("os.name").toLowerCase().contains("win"))
            locationTextField.setText(System.getProperty("user.home") + "\\Downloads");
        else
            locationTextField.setText(System.getProperty("user.home") + "/Downloads");

        subtitleLanguageChoiceBox.setItems(FXCollections.observableArrayList("Arabic", "English", "French", "Italian", "spanish", "German", "Russian"));
        subtitleLanguageChoiceBox.setValue("English");
        embeddedSubtitleChkBox.selectedProperty().not().and(autoGenSubtitleChkBox.selectedProperty().not()).addListener((observable, oldValue, newValue) -> subtitleLanguagePane.setDisable(newValue));
        //qualityComboBox.setItems(FXCollections.observableArrayList("Best", "1080p - mp4 video", "720p - mp4 video", "480p - mp4 video", "360p - mp4 video", "240p - mp4 video", "144p - mp4 video", "48K - m4a audio only"));
        //qualityComboBox.setValue("Best");

        newDownloadPane.setOnKeyPressed((KeyEvent keyEvent) -> {
            if(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN).match(keyEvent)) {
                if(startBtn.isVisible())
                    startBtnAction();
                keyEvent.consume();
            } else if(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN).match(keyEvent)) {
                if(scheduleBtn.isVisible())
                    scheduleBtnAction();
                keyEvent.consume();
            } else if(new KeyCodeCombination(KeyCode.ESCAPE).match(keyEvent)) {
                cancelBtnAction();
                keyEvent.consume();
            }
        });

        // center the urlDialogStage in the appStage
        appStage.xProperty().addListener((observableValue, oldValue, newValue) -> {
            if(urlDialogStage != null && urlDialogStage.isShowing())
                urlDialogStage.setX(newValue.doubleValue() + appStage.getWidth()/2d - urlDialogStage.getWidth()/2d);
        });
        appStage.yProperty().addListener((observableValue, oldValue, newValue) -> {
            if(urlDialogStage != null && urlDialogStage.isShowing())
                urlDialogStage.setY(newValue.doubleValue() + appStage.getHeight()/2d - urlDialogStage.getHeight()/2d);
        });

    }

    public void setQueueBtnSelected(boolean selected) {
        isQueueBtnSelected = selected;
    }

    public void showUrlDialog() {

        final double URL_DIALOG_WIDTH = 650;
        final double URL_DIALOG_HEIGHT = 370;

        Effect blurEffect = new BoxBlur(10, 10, 3);
        newDownloadPane.setEffect(blurEffect);

        try {

            String urlRegex = "(https?:\\/\\/)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
            Clipboard systemClipboard = Clipboard.getSystemClipboard();
            String clipboardText = systemClipboard.getString();

            FXMLLoader urlDialogLoader = new FXMLLoader(getClass().getResource("windows/URLDialog.fxml"));
            VBox vBox = urlDialogLoader.load();
            Scene urlDialogScene = new Scene(vBox, URL_DIALOG_WIDTH, URL_DIALOG_HEIGHT, Color.TRANSPARENT);
            urlDialogStage = new Stage();
            urlDialogStage.setScene(urlDialogScene);

            TextField urlTextFieldOnUrlDialog = (TextField) urlDialogLoader.getNamespace().get("urlTextFieldOnUrlDialog");
            Button addBtnOnUrlDialog = (Button) urlDialogLoader.getNamespace().get("addBtnOnUrlDialog");
            Button cancelBtnOnUrlDialog = (Button) urlDialogLoader.getNamespace().get("cancelBtnOnUrlDialog");
            CheckBox needLoginCheckBoxOnUrlDialog = (CheckBox) urlDialogLoader.getNamespace().get("needLoginCheckBoxOnUrlDialog");
            TextField userNameTextFieldOnUrlDialog = (TextField) urlDialogLoader.getNamespace().get("userNameTextFieldOnUrlDialog");
            TextField passwordTextFieldOnUrlDialog = (TextField) urlDialogLoader.getNamespace().get("passwordTextFieldOnUrlDialog");

            if(clipboardText != null && clipboardText.matches(urlRegex))
                urlTextFieldOnUrlDialog.setText(clipboardText);

            addBtnOnUrlDialog.setOnAction((ActionEvent actionEvent) -> {

                boolean validUserInputs = true;

                if(urlTextFieldOnUrlDialog.getText().matches(urlRegex)) {
                    urlTextFieldOnUrlDialog.getStyleClass().add("text-field");
                    urlLabel.setText(urlTextFieldOnUrlDialog.getText());
                } else {
                    urlTextFieldOnUrlDialog.getStyleClass().add("text-field-error");
                    validUserInputs = false;
                }

                if(needLoginCheckBoxOnUrlDialog.isSelected()) {

                    needLoginCheckBox.setSelected(true);

                    if(userNameTextFieldOnUrlDialog.getText().equals("")) {
                        userNameTextFieldOnUrlDialog.getStyleClass().add("text-field-error");
                        validUserInputs = false;
                    } else {
                        userNameTextFieldOnUrlDialog.getStyleClass().add("text-field");
                        userNameTextField.setText(userNameTextFieldOnUrlDialog.getText());
                    }

                    if(passwordTextFieldOnUrlDialog.getText().equals("")) {
                        passwordTextFieldOnUrlDialog.getStyleClass().add("text-field-error");
                        validUserInputs = false;
                    } else {
                        passwordTextFieldOnUrlDialog.getStyleClass().add("text-field");
                        passwordTextField.setText(passwordTextFieldOnUrlDialog.getText());
                    }

                }

                if(! validUserInputs)
                    return;

                vBox.getChildren().clear();
                vBox.setAlignment(Pos.CENTER);
                vBox.setStyle(vBox.getStyle().replace("darkturquoise", "transparent"));
                ProgressIndicator progressIndicator = new ProgressIndicator();
                progressIndicator.setPrefHeight(48);
                progressIndicator.setPrefWidth(48);
                vBox.getChildren().add(progressIndicator);
                urlDialogStage.setWidth(200);
                urlDialogStage.setHeight(200);
                urlDialogStage.setX(appStage.getX() + appStage.getWidth()/2d - urlDialogStage.getWidth()/2d);
                urlDialogStage.setY(appStage.getY() + appStage.getHeight()/2d - urlDialogStage.getHeight()/2d);

                List<String> argsList = new ArrayList<>();
                argsList.add("python");
                argsList.add("youtube-dl");
                if(needLoginCheckBox.isSelected()) {
                    argsList.add("-u");
                    argsList.add(userNameTextField.getText());
                    argsList.add("-p");
                    argsList.add(passwordTextField.getText());
                }
                CountDownLatch latch = new CountDownLatch(3);

                // for parsing the download thumbnail image
                Task<Void> thumbnailLoader = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {

                        List<String> cmdList = new ArrayList<>(argsList);
                        cmdList.add("--get-thumbnail");
                        cmdList.add(urlLabel.getText());

                        Process ytdlProcess = new ProcessBuilder(cmdList).redirectErrorStream(true).start();

                        InputStream inputStream = ytdlProcess.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String thumbnailUrl = bufferedReader.readLine();
                        ytdlProcess.destroy();
                        if(thumbnailUrl.matches(urlRegex)) {
                            Platform.runLater(() -> {
                                thumbnailImageView.setImage(new Image(thumbnailUrl, true));
                                thumbnailImageView.setAccessibleText(thumbnailUrl);
                            });
                        }

                        synchronized (latch) {
                            latch.countDown();
                            System.out.println("thumbnail done  -> " + latch.getCount());
                            if(latch.getCount() == 0)
                                Platform.runLater(() -> closeUrlDialog());
                        }

                        return null;
                    }

                };

                // for parsing the download title and description
                Task<Void> titleParser = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {

                        List<String> cmdList = new ArrayList<>(argsList);
                        cmdList.add("-o");
                        cmdList.add("temp/%(title)s");
                        cmdList.add(urlLabel.getText());

                        Process ytdlProcess = new ProcessBuilder(cmdList).redirectErrorStream(true).start();

                        InputStream inputStream = ytdlProcess.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                        // parse the download description
                        String line = bufferedReader.readLine();
                        if(line.matches("\\[.+\\].+")) {
                            String description = line.split("\\[")[1].split("\\]")[0].replace(':', ' ');
                            Platform.runLater(() -> descriptionLabel.setText(description));
                            System.out.println("description = " + description);
                        }

                        //parse the download title
                        while((line = bufferedReader.readLine()) != null) {

                            if(line.startsWith("[download]") && line.contains(":")) {
                                ytdlProcess.destroy();
                                String title = line.split(":")[1].split("\\.f\\d{1,4}")[0].replace("temp/", "");
                                Platform.runLater(() -> titleLabel.setText(title));
                                System.out.println("title = " + title);
                                break;
                            }

                        }

                        synchronized (latch) {
                            latch.countDown();
                            System.out.println("title done -> " + latch.getCount());
                            if(latch.getCount() == 0)
                                Platform.runLater(() -> closeUrlDialog());
                        }

                        return null;
                    }

                };

                // for parsing the download qualities
                Task<Void> qualityParser = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {

                        List<String> cmdList = new ArrayList<>(argsList);
                        cmdList.add("-F");
                        cmdList.add(urlLabel.getText());

                        Process ytdlProcess = new ProcessBuilder(cmdList).start();

                        InputStream inputStream = ytdlProcess.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line = bufferedReader.readLine();

                        while(line != null && !line.startsWith("format code")) {
                            System.out.println(line);
                            line = bufferedReader.readLine();
                        }

                        //parsing the qualities and formats
                        while(line != null && !(line = bufferedReader.readLine()).startsWith("[")) {
                            System.out.println(line);
                            // parse the quality line
                        }

                        System.out.println("line = " + line);

                        ytdlProcess.destroy();

                        synchronized (latch) {
                            latch.countDown();
                            System.out.println("quality done -> " + latch.getCount());
                            if(latch.getCount() == 0)
                                Platform.runLater(() -> closeUrlDialog());
                        }

                        return null;
                    }

                };

                Thread thumbnailLoaderThread = new Thread(thumbnailLoader);
                Thread qualityParserThread = new Thread(qualityParser);
                Thread titleParserThread = new Thread(titleParser);
                thumbnailLoaderThread.start();
                qualityParserThread.start();
                titleParserThread.start();

            });
            cancelBtnOnUrlDialog.setOnAction(actionEvent -> {
                urlDialogStage.close();
                cancelBtnAction();
            });

            urlDialogStage.setResizable(false);
            urlDialogStage.initStyle(StageStyle.TRANSPARENT);
            urlDialogStage.initModality(Modality.APPLICATION_MODAL);
            urlDialogStage.getIcons().add(0, new Image(getClass().getResource("icon/icon.png").toString()));
            urlDialogStage.setOnCloseRequest(Event::consume);
            urlDialogStage.setOpacity(0.75);
            urlDialogStage.setX(appStage.getX() + appStage.getWidth()/2d - URL_DIALOG_WIDTH/2d);
            urlDialogStage.setY(appStage.getY() + appStage.getHeight()/2d - URL_DIALOG_HEIGHT/2d);
            urlDialogStage.show();
            urlDialogStage.toFront();

        } catch (Exception ex) {
            new MessageDialog("Error Loading the Home Window! \n" +
                    "Restart program and try again.", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(ex.getStackTrace()).showAndWait();
        }

    }

    public void closeUrlDialog() {

        if(urlDialogStage != null) {
            urlDialogStage.close();
            newDownloadPane.setEffect(null);
        }

    }


    @FXML
    private void browseBtnAction() {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(locationTextField.getText()));
        directoryChooser.setTitle("Choose save location");

        File selectedDirectory = directoryChooser.showDialog(newDownloadPane.getScene().getWindow());
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

        // Check if the save location is not empty
        if(locationTextField.getText().equals("")) {
            locationTextField.getStyleClass().add("text-field-error");
            result = false;
        } else {
            locationTextField.getStyleClass().add("text-field");
        }

        // Check if the custom name is not empty
        if(customNameChkBox.isSelected()) {
            if(customNameTextField.getText().equals("")) {
                customNameTextField.getStyleClass().add("text-field-error");
                result = false;
            } else {
                customNameTextField.getStyleClass().add("text-field");
            }
        }

        // Check if the specific playlist items are written correctly
        if(isPlaylistChkBox.isSelected()) {
            if(indexRangeRadioBtn.isSelected()) {
                if(startIndexTextField.getText().matches("[1-9]*")) {
                    startIndexTextField.getStyleClass().add("text-field");
                } else {
                    startIndexTextField.getStyleClass().add("text-field-error");
                    result = false;
                }

                if(endIndexTextField.getText().matches("[1-9]*")) {
                    endIndexTextField.getStyleClass().add("text-field");
                } else {
                    endIndexTextField.getStyleClass().add("text-field-error");
                    result = false;
                }
            }

            if(specificItemsRadioBtn.isSelected()) {
                if(playlistItemsTextField.getText().replaceAll("\\s","").matches("[0-9,]+")) {
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
                userNameTextField.getStyleClass().add("text-field");
            }

            if(passwordTextField.getText().equals("")) {
                passwordTextField.getStyleClass().add("text-field-error");
                result = false;
            } else {
                passwordTextField.getStyleClass().add("text-field");
            }
        }

        // Check if the speed limit is valid
        if(limitSpinner.getEditor().getText().matches("[0-9]+")) {
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
        item.setUrl(urlLabel.getText());
        item.setTitle(urlLabel.getText());
        item.setDescription(descriptionLabel.getText());
        item.setThumbnailUrl(thumbnailImageView.getAccessibleText());
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
                item.setPlaylistItems(playlistItemsTextField.getText().replaceAll("\\s",""));
            }
        }

        item.setIsVideo(true);
        item.setFormat("mp4");
//        String selectedQuality = qualityComboBox.getSelectionModel().getSelectedItem();
//
//        if (selectedQuality.equals("1080p - mp4 video")) {
//            item.setVideoQuality(137);
//            item.setAudioQuality(141);
//        } else if (selectedQuality.equals("720p - mp4 video")) {
//            item.setVideoQuality(22);
//            item.setAudioQuality(0);
//        } else if (selectedQuality.equals("480p - mp4 video")) {
//            item.setVideoQuality(135);
//            item.setAudioQuality(140);
//        } else if (selectedQuality.equals("360p - mp4 video")) {
//            item.setVideoQuality(18);
//            item.setAudioQuality(0);
//        } else if (selectedQuality.equals("240p - mp4 video")) {
//            item.setVideoQuality(133);
//            item.setAudioQuality(139);
//        } else if (selectedQuality.equals("144p - mp4 video")) {
//            item.setVideoQuality(17);
//            item.setAudioQuality(0);
//        } else if (selectedQuality.equals("48K - m4a audio only")) {
//            item.setFormat("mp3");
//            item.setIsVideo(false);
//            item.setVideoQuality(0);
//            item.setAudioQuality(139);
//        }

        item.setNeedEmbeddedSubtitle(embeddedSubtitleChkBox.isSelected());
        item.setSubtitleLanguage(subtitleLanguageChoiceBox.getValue());
        item.setNeedAutoGeneratedSubtitle(autoGenSubtitleChkBox.isSelected());

        if(needLoginCheckBox.isSelected()) {
            item.setUserName(userNameTextField.getText());
            item.setPassword(AES.encrypt(passwordTextField.getText()));
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
            newDownloadPane.getScene().setRoot(root);

        } catch (Exception e) {
            newDownloadPane.setOpacity(0.30);
            new MessageDialog("Error Loading the Home Window! \n" +
                    "Restart program and try again.", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(e.getStackTrace()).showAndWait();
            newDownloadPane.setOpacity(1);
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
