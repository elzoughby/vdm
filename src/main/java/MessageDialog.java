import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;


public class MessageDialog implements Initializable {

    public enum Buttons {
        OK,
        CLOSE,
        YES_AND_NO
    }

    private String messageTitle;
    private Buttons actionButtons;
    private double xOffset = 0;
    private double yOffset = 0;

    private Stage messageStage;
    private Button okButton;
    private Button closeButton;
    private Button yesButton;
    private Button noButton;
    @FXML
    private VBox errorDialogPane;
    @FXML
    private Pane dragPane;
    @FXML
    private Text titleText;
    @FXML
    private VBox messageOptionPane;
    @FXML
    private HBox messageActionPane;



    public MessageDialog(String messageTitle, Buttons actionButtons) {

        this.messageTitle = messageTitle;
        this.actionButtons = actionButtons;

        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("windows/MessageDialog.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.load();

        } catch (Exception e) {
            new ErrorDialog("Error in loading the MessageDialog!" +
                    "Restart program and try again", e.getStackTrace());
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        titleText.setText(messageTitle);

        switch(actionButtons) {
            case OK:
                okButton = new Button("OK");
                okButton.setPrefWidth(100);
                messageActionPane.getChildren().add(okButton);
                break;
            case CLOSE:
                closeButton = new Button("Close");
                closeButton.setPrefWidth(100);
                messageActionPane.getChildren().add(closeButton);
                break;
            case YES_AND_NO:
                yesButton = new Button("Yes");
                yesButton.setPrefWidth(100);
                noButton = new Button("No");
                noButton.setPrefWidth(100);
                messageActionPane.getChildren().addAll(noButton, yesButton);
                break;
        }

        dragPane.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        dragPane.setOnMouseDragged(event -> {
            messageStage.setX(event.getScreenX() - xOffset);
            messageStage.setY(event.getScreenY() - yOffset);
        });

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
        return okButton;
    }

    public Button getCloseButton() {
        return closeButton;
    }

    public Button getYesButton() {
        return yesButton;
    }

    public Button getNoButton() {
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

        Scene scene = new Scene(errorDialogPane);
        messageStage = new Stage();
        messageStage.setScene(scene);
        messageStage.setResizable(false);
        messageStage.initStyle(StageStyle.UNDECORATED);
        messageStage.initModality(Modality.APPLICATION_MODAL);
        messageStage.setOnCloseRequest(Event::consume);
        messageStage.setMinWidth(500);
        messageStage.setMaxWidth(500);

        if(messageOptionPane.getChildren().size() == 0) {
            errorDialogPane.getChildren().remove(messageOptionPane);
            messageStage.setMinHeight(170);
            messageStage.setMaxHeight(170);
        } else {
            messageStage.setMinHeight(196);
            messageStage.setMaxHeight(196);
        }

    }

}
