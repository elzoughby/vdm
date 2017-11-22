import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
    private TableView<Item> itemsTableView;
    @FXML
    private TableColumn<Item, Integer> itemsIdColumn;
    @FXML
    private TableColumn<Item, String> itemsTitleColumn;
    @FXML
    private TableColumn<Item, String> itemsStatusColumn;
    @FXML
    private TableColumn<Item, String> itemsSizeColumn;
    @FXML
    private TableColumn<Item, String> itemsSpeedColumn;
    @FXML
    private TableColumn<Item, String> itemsDoneColumn;
    @FXML
    private TableColumn<Item, String> itemsEtaColumn;




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
            if(newValue == null)
                consoleListView.setItems(null);
            else
                consoleListView.setItems(newValue.getLogList());
        });


        itemsTableView.setRowFactory(param -> {

            TableRow<Item> row = new TableRow<>();
            ContextMenu rowContextMenu = new ContextMenu();

            MenuItem startMenuItem = new MenuItem("Start");
            startMenuItem.setOnAction(event -> startBtnAction());

            MenuItem pauseMenuItem = new MenuItem("Pause");
            pauseMenuItem.setOnAction(event -> stopBtnAction());

            MenuItem deleteMenuItem = new MenuItem("Delete");
            deleteMenuItem.setOnAction(event -> removeBtnAction());

            MenuItem clearMenuItem = new MenuItem("Clear Logs");
            clearMenuItem.setOnAction(event -> clearLogsMenuAction());

            MenuItem openFolderMenuItem = new MenuItem("Open Location");
            openFolderMenuItem.setOnAction(event -> openFolderMenuAction());

            MenuItem infoMenuItem = new MenuItem("Properties");
            infoMenuItem.setOnAction(event -> infoBtnAction());

            rowContextMenu.getItems().addAll(startMenuItem, pauseMenuItem, deleteMenuItem,
                    clearMenuItem, openFolderMenuItem, infoMenuItem);

            row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty()))
                            .then(rowContextMenu)
                            .otherwise((ContextMenu) null));

            return row;
        });

    }

    @FXML
    void addBtnAction() {

        try {

            Parent root = FXMLLoader.load(getClass().getResource("windows/AddDownloadWindow.fxml"));
            Stage addWindowStage = new Stage();
            addWindowStage.setScene(new Scene(root));
            addWindowStage.setTitle("Add Download");
            addWindowStage.initOwner(homeWindowPane.getScene().getWindow());
            addWindowStage.initModality(Modality.APPLICATION_MODAL);
            addWindowStage.setResizable(false);
            addWindowStage.show();

        } catch (IOException e) {
            System.err.println("Error Loading NewDownload Window!");
        }

    }

    @FXML
    void startBtnAction() {

        Item selectedItem = itemsTableView.getSelectionModel().getSelectedItem();
        if( selectedItem != null && selectedItem.getStatus().equals("Stopped"))
            selectedItem.startDownload();

    }

    @FXML
    void stopBtnAction() {

        Item selectedItem = itemsTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null)
            selectedItem.stopDownload();

    }

    @FXML
    void removeBtnAction() {

        Item selectedItem = itemsTableView.getSelectionModel().getSelectedItem();
        if( selectedItem != null) {
            selectedItem.stopDownload();
            itemList.remove(selectedItem);
            queueItemList.remove(selectedItem);
            DbManager.delete(selectedItem);
        }

    }

    @FXML
    void infoBtnAction() {

        Item selectedItem = itemsTableView.getSelectionModel().getSelectedItem();
        if( selectedItem != null) {

            ObservableList<String> infoList = FXCollections.observableArrayList();

            infoList.add("title : \t" + selectedItem.getTitle());
            infoList.add("url : \t" + selectedItem.getUrl());
            infoList.add("location : \t" + selectedItem.getLocation());
            infoList.add("format : \t" + selectedItem.getFormat());
            infoList.add("quality : \t" + selectedItem.getVideoQuality());
            infoList.add("limit : \t" + selectedItem.getSpeedLimit());

            ListView<String> infoListView = new ListView<>(infoList);
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
    void settingBtnAction() {

        try {

            Parent root = FXMLLoader.load(getClass().getResource("windows/SettingWindow.fxml"));
            Stage settingStage = new Stage();
            settingStage.setScene(new Scene(root));
            settingStage.setTitle("Setting");
            settingStage.show();

        } catch (IOException e) {
            System.err.println("Error Loading Settings Window!");
        }

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

    @FXML
    void clearLogsMenuAction() {

        itemsTableView.getSelectionModel().getSelectedItem().getLogList().clear();

    }

    @FXML
    void openFolderMenuAction() {

        new Runnable() {

            @Override
            public void run() {

                try {

                    String location = itemsTableView.getSelectionModel().getSelectedItem().getLocation();
                    Desktop desktop = null;
                    File file = new File(location);
                    if (Desktop.isDesktopSupported())
                        desktop = Desktop.getDesktop();
                    if (desktop != null)
                        desktop.open(file);

                } catch (IOException e) {
                    System.err.println("Error Opening Location Folder");
                }

            }

        };

    }

}
