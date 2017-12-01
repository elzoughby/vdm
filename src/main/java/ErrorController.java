import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ErrorController implements Initializable {

    private static Text text = new Text("");
    private static String details = "";


    @FXML
    private VBox errorDialogPane;
    @FXML
    private TextFlow messageTextFlow;
    @FXML
    private TitledPane detailsTitledPane;
    @FXML
    private TextArea detailsTextArea;



    static void showErrorDialog(String title, StackTraceElement[] stackTrace) {

        try {
            text.setText(title);
            StringBuilder stringBuilder = new StringBuilder();
            for(StackTraceElement ste : stackTrace)
                stringBuilder.append(ste.toString()).append("\n");
            details = stringBuilder.toString();
            Parent root = FXMLLoader.load(ErrorController.class.getResource("windows/errorDialog.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            System.out.println("Unknown Error!");
            e.printStackTrace();
        }

    }

    static void showErrorDialog(String title, String errorDetails) {

        try {
            text.setText(title);
            details = errorDetails;
            Parent root = FXMLLoader.load(ErrorController.class.getResource("windows/errorDialog.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            System.out.println("Unknown Error!");
            e.printStackTrace();
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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

    @FXML
    void closeBtnAction() {
        ((Stage) errorDialogPane.getScene().getWindow()).close();
    }

}
