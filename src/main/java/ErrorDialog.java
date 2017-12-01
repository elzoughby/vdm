import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ErrorDialog implements Initializable {

    private Text text;
    private String details;

    @FXML
    private VBox errorDialogPane;
    @FXML
    private TextFlow messageTextFlow;
    @FXML
    private TitledPane detailsTitledPane;
    @FXML
    private TextArea detailsTextArea;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        text.setFont(Font.font(15));
        messageTextFlow.getChildren().add(text);
        detailsTextArea.setText(details);

        detailsTitledPane.expandedProperty().addListener((observable, wasExpanded, nowExpanded) -> {
            if(nowExpanded) {
                errorDialogPane.setPrefHeight(errorDialogPane.getMaxHeight());
                errorDialogPane.getScene().getWindow().setHeight(errorDialogPane.getMaxHeight());
            } else {
                errorDialogPane.setPrefHeight(errorDialogPane.getMinHeight());
                errorDialogPane.getScene().getWindow().setHeight(errorDialogPane.getMinHeight());
            }
        });

    }

    public ErrorDialog(String title, StackTraceElement[] stackTrace) {

        StringBuilder stringBuilder = new StringBuilder();
        for(StackTraceElement ste : stackTrace)
            stringBuilder.append(ste.toString()).append("\n");
        text = new Text(title);
        details = stringBuilder.toString();

    }

    public ErrorDialog(String title, String errorDetails) {

        text = new Text(title);
        details = errorDetails;

    }

    public void showAndWait() {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("windows/errorDialog.fxml"));
            fxmlLoader.setController(this);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            System.out.println("Unknown Error!");
            e.printStackTrace();
        }

    }

    @FXML
    void closeBtnAction() {
        ((Stage) errorDialogPane.getScene().getWindow()).close();
    }

}
