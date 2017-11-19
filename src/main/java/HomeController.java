import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class HomeController implements Initializable {

    private static ObservableList<Item> itemList = FXCollections.observableArrayList();
    private static ObservableList<Item> queueItemList = FXCollections.observableArrayList();


    @FXML
    private BorderPane homeWindowPane;
    @FXML
    private SplitPane homeSplitPane;
    @FXML
    private ListView<String> consoleListView;
    @FXML
    private TableView itemsTableView;
    @FXML
    private TableColumn itemsIdColumn;
    @FXML
    private TableColumn itemsTitleColumn;
    @FXML
    private TableColumn itemsStatusColumn;
    @FXML
    private TableColumn itemsSizeColumn;
    @FXML
    private TableColumn itemsSpeedColumn;
    @FXML
    private TableColumn itemsDoneColumn;
    @FXML
    private TableColumn itemsEtaColumn;




    public static List<Item> getItemList() {
        return itemList;
    }

    public static List<Item> getQueueItemList() {
        return queueItemList;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        itemsIdColumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        itemsTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        itemsStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        itemsSizeColumn.setCellValueFactory(new PropertyValueFactory<>("sizeString"));
        itemsSpeedColumn.setCellValueFactory(new PropertyValueFactory<>("speedString"));
        itemsDoneColumn.setCellValueFactory(new PropertyValueFactory<>("doneString"));
        itemsEtaColumn.setCellValueFactory(new PropertyValueFactory<>("eta"));

        itemsTableView.setItems(itemList);

        itemsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Item selectedItem = (Item) newValue;
            if(selectedItem == null)
                consoleListView.setItems(null);
            else
                consoleListView.setItems(selectedItem.getLogList());
        });

    }

    @FXML
    void addBtnAction() throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("windows/AddDownloadWindow.fxml"));

        Stage addWindowStage = new Stage();
        addWindowStage.setScene(new Scene(root));
        addWindowStage.setTitle("Add Download");
        addWindowStage.initOwner(homeWindowPane.getScene().getWindow());
        addWindowStage.initModality(Modality.APPLICATION_MODAL);
        addWindowStage.setResizable(false);
        addWindowStage.show();

    }

    @FXML
    void startBtnAction() {

        Item selectedItem = (Item) itemsTableView.getSelectionModel().getSelectedItem();
        if( selectedItem != null && selectedItem.getStatus().equals("Stopped"))
            selectedItem.startDownload();

    }

    @FXML
    void stopBtnAction() throws IOException {

        Item selectedItem = (Item) itemsTableView.getSelectionModel().getSelectedItem();
        if( selectedItem != null)
            selectedItem.stopDownload();

    }

    @FXML
    void removeBtnAction() throws IOException {

        Item selectedItem = (Item) itemsTableView.getSelectionModel().getSelectedItem();
        if( selectedItem != null) {
            selectedItem.stopDownload();
            itemList.remove(selectedItem);
            queueItemList.remove(selectedItem);
            DbManager.delete(selectedItem);
        }

    }

    @FXML
    void infoBtnAction() {

        Item selectedItem = (Item) itemsTableView.getSelectionModel().getSelectedItem();
        if( selectedItem != null) {

            ObservableList<String> infoList = FXCollections.observableArrayList();

            infoList.add("title : \t" + selectedItem.getTitle());
            infoList.add("url : \t" + selectedItem.getUrl());
            infoList.add("location : \t" + selectedItem.getLocation());
            infoList.add("format : \t" + selectedItem.getFormat());
            infoList.add("quality : \t" + selectedItem.getVideoQuality());
            infoList.add("limit : \t" + selectedItem.getSpeedLimit());

            ListView infoListView = new ListView();
            infoListView.setItems(infoList);
            Stage stage = new Stage();
            stage.setScene(new Scene(infoListView));
            stage.showAndWait();
        }

    }

    @FXML
    void queueBtnAction() {

        if(itemsTableView.getItems().equals(itemList))
            itemsTableView.setItems(queueItemList);
        else
            itemsTableView.setItems(itemList);
    }

    @FXML
    void settingBtnAction() throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("windows/SettingWindow.fxml"));
        Stage settingStage = new Stage();
        settingStage.setScene(new Scene(root));
        settingStage.setTitle("Setting");
        settingStage.show();

    }

    @FXML
    void helpBtnAction() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Sorry, beta version");
        alert.setContentText("No help in this version. Help yourself!");
        alert.showAndWait();
    }

    @FXML
    void aboutBtnAction() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Nazel Video Downloader Team");
        alert.setContentText("Mohamed Bazazo \nIsmail Elmogy \nAhmed Elzoughby");
        alert.showAndWait();

    }

    @FXML
    void logBtnAction() {

        if(homeSplitPane.getItems().size() == 2)
            homeSplitPane.getItems().remove(consoleListView);
        else {
            homeSplitPane.getItems().add(1, consoleListView);
            homeSplitPane.setDividerPosition(0, 0.7);
        }

    }

}
