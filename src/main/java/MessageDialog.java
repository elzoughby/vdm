import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;


public class MessageDialog implements Initializable {

    public enum Type {
        ERROR,
        INFO,
        OPTION,
        CUSTOM
    }
    public enum Buttons {
        OK,
        CLOSE,
        YES_AND_NO,
        OK_AND_CANCEL
    }

    private final int DIALOG_WIDTH = 480;
    private final int INFO_DIALOG_HEIGHT = 138;
    private final int OPTION_DIALOG_HEIGHT = 180;
    private final int ERROR_DIALOG_HEIGHT = 300;

    private String messageTitle;
    private Type messageType;
    private Buttons actionButtons;
    private double xOffset = 0;
    private double yOffset = 0;

    private Stage messageStage;
    private Button yesButton;
    private Button noButton;
    @FXML
    private VBox messageDialogPane;
    @FXML
    private Pane dragPane;
    @FXML
    private ImageView messageImageView;
    @FXML
    private Text titleText;
    @FXML
    private VBox messageOptionPane;
    @FXML
    private HBox messageActionPane;



    public MessageDialog(String messageTitle,Type messageType, Buttons actionButtons) {

        this.messageTitle = messageTitle;
        this.messageType = messageType;
        this.actionButtons = actionButtons;

        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("windows/MessageDialog.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.load();

        } catch (Exception e) {
            System.out.println("Error loading message dialog!");
            e.printStackTrace();
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        titleText.textProperty().bind(new SimpleStringProperty(messageTitle));

        switch(messageType) {
            case ERROR:
                messageImageView.setImage(new Image(getClass().getResource("theme/imgs/error.png").toString()));
                messageDialogPane.setStyle(messageDialogPane.getStyle().concat(" -fx-border-color: crimson;"));
                break;
            case INFO:
            case OPTION:
                messageImageView.setImage(new Image(getClass().getResource("theme/imgs/warning.png").toString()));
            case CUSTOM:
                messageDialogPane.setStyle(messageDialogPane.getStyle().concat(" -fx-border-color: darkturquoise;"));
                break;
        }

        switch(actionButtons) {
            case OK:
                yesButton = new Button("OK");
                yesButton.setPrefWidth(100);
                messageActionPane.getChildren().add(yesButton);
                break;
            case CLOSE:
                noButton = new Button("Close");
                noButton.setPrefWidth(100);
                messageActionPane.getChildren().add(noButton);
                break;
            case YES_AND_NO:
                yesButton = new Button("Yes");
                yesButton.setPrefWidth(100);
                noButton = new Button("No");
                noButton.setPrefWidth(100);
                messageActionPane.getChildren().addAll(noButton, yesButton);
                break;
            case OK_AND_CANCEL:
                yesButton = new Button("OK");
                yesButton.setPrefWidth(100);
                noButton = new Button("Cancel");
                noButton.setPrefWidth(100);
                messageActionPane.getChildren().addAll(noButton, yesButton);
                break;
        }

        messageDialogPane.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        messageDialogPane.setOnMouseDragged(event -> {
            messageStage.setX(event.getScreenX() - xOffset);
            messageStage.setY(event.getScreenY() - yOffset);
        });

        messageDialogPane.setOnKeyPressed((KeyEvent keyEvent) -> {
            if(new KeyCodeCombination(KeyCode.ESCAPE).match(keyEvent))
                noButton.fire();
        });

    }

    public MessageDialog createErrorDialog(StackTraceElement[] errorDetails) {

        StringBuilder stringBuilder = new StringBuilder();
        for(StackTraceElement ste : errorDetails)
            stringBuilder.append(ste.toString()).append("\n");

        TextArea textArea = new TextArea(stringBuilder.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(12));
        TitledPane detailsTitledPane = new TitledPane("Details",textArea);
        noButton.setOnAction(event -> close());
        addTitledPane(detailsTitledPane);

        return this;
    }

    public MessageDialog createErrorDialog(String errorDetails) {

        TextArea textArea = new TextArea(errorDetails);
        textArea.setEditable(false);
        textArea.setFont(new Font(12));
        TitledPane detailsTitledPane = new TitledPane("Details",textArea);
        noButton.setOnAction(event -> close());
        addTitledPane(detailsTitledPane);

        return this;
    }

    public void setImage(String path) {
        if(messageType == Type.CUSTOM)
            messageImageView.setImage(new Image(path));
    }

    public void addTitledPane(TitledPane titledPane) {

        if(messageOptionPane.getChildren().size() == 0) {
            titledPane.setExpanded(false);
            titledPane.setAnimated(false);
            titledPane.expandedProperty().addListener((observable, wasExpanded, nowExpanded) -> {
                if(nowExpanded) {
                    messageDialogPane.setPrefHeight(ERROR_DIALOG_HEIGHT);
                    messageStage.setMaxHeight(ERROR_DIALOG_HEIGHT);
                    messageStage.setMinHeight(ERROR_DIALOG_HEIGHT);
                } else {
                    messageDialogPane.setPrefHeight(OPTION_DIALOG_HEIGHT);
                    messageStage.setMaxHeight(OPTION_DIALOG_HEIGHT);
                    messageStage.setMinHeight(OPTION_DIALOG_HEIGHT);
                }
            });
            messageOptionPane.setPadding(new Insets(0, 30, 0, 30));
            messageOptionPane.getChildren().add(titledPane);
        }
    }

    public void addCheckBox(CheckBox checkBox) {
        if(messageOptionPane.getChildren().size() == 0)
            messageOptionPane.getChildren().add(checkBox);
    }

    public void addTextField(TextField textField) {
        if(messageOptionPane.getChildren().size() == 0)
            messageOptionPane.getChildren().add(textField);
    }

    public void addText(Text text) {
        if(messageOptionPane.getChildren().size() == 0)
            messageOptionPane.getChildren().add(text);
    }

    public Button getOkButton() {
        return yesButton;
    }

    public Button getCloseButton() {
        return noButton;
    }

    public Button getYesButton() {
        return yesButton;
    }

    public Button getNoButton() {
        return noButton;
    }

    public Button getCancelButton() {
        return noButton;
    }

    public void show() {

        initMessageStage();
        messageStage.show();
    }

    public void showAndWait() {

        initMessageStage();
        messageStage.showAndWait();
    }

    public void close() {
        messageStage.close();
    }

    private void initMessageStage() {

        Scene scene = new Scene(messageDialogPane);
        messageStage = new Stage();
        messageStage.setScene(scene);
        messageStage.setResizable(false);
        messageStage.initStyle(StageStyle.UNDECORATED);
        messageStage.initModality(Modality.APPLICATION_MODAL);
        messageStage.getIcons().add(0, new Image(getClass().getResource("icon/icon.png").toString()));
        messageStage.setOnCloseRequest(Event::consume);
        messageStage.setMinWidth(DIALOG_WIDTH);
        messageStage.setMaxWidth(DIALOG_WIDTH);
        if(messageType == Type.ERROR)
            messageStage.setTitle("Error");
        else
            messageStage.setTitle("Nazel Video Downloader");

        if(messageOptionPane.getChildren().size() == 0) {
            messageDialogPane.getChildren().remove(messageOptionPane);
            messageDialogPane.setPrefHeight(INFO_DIALOG_HEIGHT);
            messageStage.setMinHeight(INFO_DIALOG_HEIGHT);
            messageStage.setMaxHeight(INFO_DIALOG_HEIGHT);
        } else {
            messageDialogPane.setPrefHeight(OPTION_DIALOG_HEIGHT);
            messageStage.setMinHeight(OPTION_DIALOG_HEIGHT);
            messageStage.setMaxHeight(OPTION_DIALOG_HEIGHT);
        }

    }

}
