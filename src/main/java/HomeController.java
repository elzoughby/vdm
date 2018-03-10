import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;


public class HomeController implements Initializable {

    // Table Progress bar cell class
    private class ProgressBarCell extends ProgressBarTableCell<Item> {

        StackPane stackPane = new StackPane();

        ProgressBar progressBar = new ProgressBar();
        Label label = new Label();
        ProgressBarCell() {
            progressBar.setMaxWidth(Double.MAX_VALUE);
            label.getStyleClass().add("progress-label");
            stackPane.getChildren().addAll(progressBar, label);
        }

        @Override
        public void updateItem(Double item, boolean empty) {
            super.updateItem(item, empty);
            if(!empty) {
                progressBar.setProgress(item / 100);
                label.setText(BigDecimal.valueOf(item).setScale(1,BigDecimal.ROUND_HALF_UP).toString() + " %");
                setGraphic(stackPane);
            } else
                setGraphic(null);
        }

    }


    private static final String HOME_PAGE_NODE = "Home";
    private static final String DIVIDER_POSITION = "dividerPosition";
    private static final String HIDE_LOG = "hideLog";
    private Preferences programData = Preferences.userRoot().node(HOME_PAGE_NODE);

    private static ObservableList<Item> itemList = FXCollections.observableArrayList();

    private static ObservableList<Item> queueItemList = FXCollections.observableArrayList();

    @FXML
    private BorderPane homeWindowPane;
    @FXML
    private SplitPane homeSplitPane;
    @FXML
    private ToggleButton queueBtn;
    @FXML
    private ListView<String> consoleListView;
    @FXML
    private TableView<Item> itemsTableView;
    @FXML
    private TableColumn<Item, Double> itemsProgressColumn;
    @FXML
    private TableColumn<Item, Boolean> itemsTypeColumn;
    @FXML
    private TableColumn<Item, String> itemsStatusColumn;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // draw ImageView in the type column
        itemsTypeColumn.setCellFactory(param -> {

            final Image fileImage = new Image(getClass().getResource("theme/imgs/file.png").toString(), 16, 16, true, true);
            final Image playlistImage = new Image(getClass().getResource("theme/imgs/playlist.png").toString(), 16, 16, true, true);

            TableCell<Item, Boolean> cell = new TableCell<Item, Boolean>() {
                ImageView imageView = new ImageView();
                @Override
                protected void updateItem(Boolean isPlaylist, boolean empty) {
                    super.updateItem(isPlaylist, empty);
                    if(!empty) {
                        if(isPlaylist)
                            imageView.setImage(playlistImage);
                        else
                            imageView.setImage(fileImage);
                        setGraphic(imageView);
                    } else
                        setGraphic(null);
                }
            };

            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        // color status based on its value
        itemsStatusColumn.setCellFactory(param -> {

            return new TableCell<Item, String>() {

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {

                        setText(item);
                        setAlignment(Pos.CENTER_LEFT);
                        Item currentItem = getTableView().getItems().get(getIndex());
                        switch (currentItem.getStatus()) {
                            case "Stopped":
                                setTextFill(Color.GRAY);
                                setGraphic(new ImageView(new Image(getClass().getResource("status/stop.png").toString())));
                                break;
                            case "Running":
                                setTextFill(Color.valueOf("#009128"));
                                setGraphic(new ImageView(new Image(getClass().getResource("status/run.png").toString())));
                                break;
                            case "Finished":
                                setTextFill(Color.BLACK);
                                setGraphic(new ImageView(new Image(getClass().getResource("status/finish.png").toString())));
                                break;
                            case "Waiting":
                                setTextFill(Color.valueOf("#00918a"));
                                setGraphic(new ImageView(new Image(getClass().getResource("status/wait.png").toString())));
                                break;
                            case "Starting":
                                setTextFill(Color.valueOf("#005491"));
                                setGraphic(new ImageView(new Image(getClass().getResource("status/start.png").toString())));
                                break;
                            case "Error":
                                setTextFill(Color.CRIMSON);
                                setGraphic(new ImageView(new Image(getClass().getResource("status/error.png").toString())));
                                break;
                        }

                    }

                }

            };

        });

        // draw progress bar in the progress columns
        itemsProgressColumn.setCellFactory(param -> new ProgressBarCell());

        // context menu construction
        ContextMenu rowContextMenu = getRowContextMenu();

        // filling the table with download items
        itemsTableView.setItems(itemList);

        // Table row factory with context menu
        itemsTableView.setRowFactory(param -> {

            return new TableRow<Item>() {

                @Override
                protected void updateItem(Item item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setContextMenu(null);
                    } else {
                        setContextMenu(rowContextMenu);
                    }
                }

            };

        });

        // listening for table row selection, to show the log and change status color of the selected item
        itemsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null)
                consoleListView.setItems(null);
            else
                consoleListView.setItems(newValue.getLogList());
        });

        // adding contextmenu to the log list
        consoleListView.setCellFactory(stringListView -> {

            ListCell<String> cell = new ListCell<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem copyMenuItem = new MenuItem("Copy");
            copyMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));
            copyMenuItem.setGraphic(new ImageView(new Image(getClass().getResource("menu/copy.png").toString())));
            copyMenuItem.setOnAction(actionEvent -> copyMenuAction());

            MenuItem saveMenuItem = new MenuItem("Save as");
            saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN));
            saveMenuItem.setGraphic(new ImageView(new Image(getClass().getResource("menu/save.png").toString())));
            saveMenuItem.setOnAction(actionEvent -> saveMenuAction());

            MenuItem clearMenuItem = new MenuItem("Clear all");
            clearMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN));
            clearMenuItem.setGraphic(new ImageView(new Image(getClass().getResource("menu/clear.png").toString())));
            clearMenuItem.setOnAction(actionEvent -> clearLogsMenuAction());

            contextMenu.getItems().addAll(copyMenuItem, saveMenuItem, clearMenuItem);
            cell.textProperty().bind(cell.itemProperty());
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {

                    if(cell.getItem().startsWith("ERROR"))
                        cell.getStyleClass().add("error-list-cell");
                    else if(cell.getItem().startsWith("WARNING"))
                        cell.getStyleClass().add("warning-list-cell");

                    cell.setContextMenu(contextMenu);

                }
            });
            return cell;
        });

        // Show/Hide Queue list and change context menu queue option with selecting queueBtn
        queueBtn.selectedProperty().addListener((observableValue, wasSelected, nowSelected) -> {
            if(nowSelected) {
                itemsTableView.setItems(queueItemList);
                rowContextMenu.getItems().get(2).setDisable(false);
                rowContextMenu.getItems().get(6).setText("Remove from Queue");
                rowContextMenu.getItems().get(6).setOnAction(event -> removeFromQueueMenuAction());
            } else {
                itemsTableView.setItems(itemList);
                rowContextMenu.getItems().get(2).setDisable(true);
                rowContextMenu.getItems().get(6).setText("Add to Queue");
                rowContextMenu.getItems().get(6).setOnAction(event -> addToQueueMenuAction());
            }
        });

        // Keyboard shortcuts for non-contextmenu options
        homeWindowPane.setOnKeyPressed((KeyEvent keyEvent) -> {
            if(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN).match(keyEvent))
                addBtnAction();
            else if(new KeyCodeCombination(KeyCode.F6).match(keyEvent))
                queueBtn.fire();
            else if(new KeyCodeCombination(KeyCode.F7).match(keyEvent))
                logBtnAction();
            else if(new KeyCodeCombination(KeyCode.F10).match(keyEvent))
                settingBtnAction();
            else if(new KeyCodeCombination(KeyCode.F1).match(keyEvent))
                helpBtnAction();
        });

        // save and restore SplitPane divider position
        homeSplitPane.setDividerPositions(programData.getDouble(DIVIDER_POSITION, 0.8));
        homeSplitPane.getDividers().get(0).positionProperty().addListener((observableValue, oldValue, newValue) ->
                programData.putDouble(DIVIDER_POSITION, newValue.doubleValue()));

        // hide split pane if it was hidden last time
        if(programData.getBoolean(HIDE_LOG, false))
            homeSplitPane.getItems().remove(consoleListView);

    }

    public static List<Item> getItemList() {
        return itemList;
    }

    public static List<Item> getQueueItemList() {
        return queueItemList;
    }

    public ToggleButton getQueueBtn() {
        return queueBtn;
    }

    public ListView<String> getConsoleListView() {
        return consoleListView;
    }

    public TableView<Item> getItemsTableView() {
        return itemsTableView;
    }


    private void addToQueueMenuAction() {

        Item selectedItem = itemsTableView.getSelectionModel().getSelectedItem();
        if(selectedItem != null) {
            selectedItem.setIsAddedToQueue(true);
            DataHandler.save(selectedItem);
            stopBtnAction();
            itemList.remove(selectedItem);
            queueItemList.add(selectedItem);
            if (queueIsRunningBefore(selectedItem) && !selectedItem.getStatus().equals("Finished"))
                selectedItem.setStatus("Waiting");
        }

    }

    private void removeFromQueueMenuAction() {

        Item selectedItem = itemsTableView.getSelectionModel().getSelectedItem();
        if(selectedItem != null) {
            selectedItem.setIsAddedToQueue(false);
            DataHandler.save(selectedItem);

            if (selectedItem.getStatus().equals("Waiting")) {
                selectedItem.setStatus("Stopped");
            } else if (selectedItem.getStatus().equals("Running") || selectedItem.getStatus().equals("Starting")) {
                if (!queueIsRunningBefore(selectedItem))
                    setNextQueueItemsStatus(selectedItem, "Stopped");
            }

            itemList.add(selectedItem);
            queueItemList.remove(selectedItem);
        }

    }

    private void clearLogsMenuAction() {

        Item selectedItem = itemsTableView.getSelectionModel().getSelectedItem();
        if(selectedItem != null)
            selectedItem.getLogList().clear();

    }

    private void openFolderMenuAction() {

        Item selectedItem = itemsTableView.getSelectionModel().getSelectedItem();

        if(selectedItem != null) {

            String location = selectedItem.getLocation();
            String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

            try {

                if (os.contains("win")) {
                    new ProcessBuilder("explorer", location).start();
                } else if (os.contains("mac")) {
                    new ProcessBuilder("open", location).start();
                } else {
                    new ProcessBuilder("xdg-open", location).start();
                }

            } catch (Exception e) {
                new MessageDialog("Error opening save location! \n" +
                        "Restart program and try again.", MessageDialog.Type.ERROR,
                        MessageDialog.Buttons.CLOSE).createErrorDialog(e.getStackTrace()).showAndWait();
            }

        }

    }

    private void waitMenuAction() {
        Item selectedItem = itemsTableView.getSelectionModel().getSelectedItem();

        if(queueIsRunningBefore(selectedItem) && (selectedItem.getStatus().equals("Stopped") || selectedItem.getStatus().equals("Error")))
            selectedItem.setStatus("Waiting");
    }

    private void upMenuAction() {
        Item selectedItem = itemsTableView.getSelectionModel().getSelectedItem();
        int oldIndex = itemsTableView.getSelectionModel().getSelectedIndex();
        int newIndex = oldIndex - 1;
        if(oldIndex > 0) {
            itemsTableView.getItems().remove(oldIndex);
            itemsTableView.getItems().add(newIndex, selectedItem);
            itemsTableView.getSelectionModel().clearAndSelect(newIndex);

            if(selectedItem.getIsAddedToQueue()) {
                if(selectedItem.getStatus().equals("Starting") || selectedItem.getStatus().equals("Running")) {
                    if(itemsTableView.getItems().get(oldIndex).getStatus().equals("Stopped"))
                        itemsTableView.getItems().get(oldIndex).setStatus("Waiting");
                } else if(selectedItem.getStatus().equals("Waiting") || selectedItem.getStatus().equals("Stopped") || selectedItem.getStatus().equals("Error")) {
                    if(queueIsRunningBefore(selectedItem))
                        itemsTableView.getItems().get(newIndex).setStatus("Waiting");
                    else
                        itemsTableView.getItems().get(newIndex).setStatus("Stopped");
                }
            }
        }
    }

    private void downMenuAction() {
        Item selectedItem = itemsTableView.getSelectionModel().getSelectedItem();
        int oldIndex = itemsTableView.getSelectionModel().getSelectedIndex();
        int newIndex = oldIndex + 1;
        if(newIndex < itemsTableView.getItems().size()) {
            itemsTableView.getItems().remove(oldIndex);
            itemsTableView.getItems().add(newIndex, selectedItem);
            itemsTableView.getSelectionModel().clearAndSelect(newIndex);

            if(selectedItem.getIsAddedToQueue()) {
                if(selectedItem.getStatus().equals("Starting") || selectedItem.getStatus().equals("Running")) {
                    if(itemsTableView.getItems().get(oldIndex).getStatus().equals("Waiting")) {
                        if(!queueIsRunningBefore(itemsTableView.getItems().get(oldIndex)))
                            itemsTableView.getItems().get(oldIndex).setStatus("Stopped");
                    }
                } else if(selectedItem.getStatus().equals("Waiting") || selectedItem.getStatus().equals("Stopped") || selectedItem.getStatus().equals("Error")) {
                    if(queueIsRunningBefore(selectedItem))
                        itemsTableView.getItems().get(newIndex).setStatus("Waiting");
                    else
                        itemsTableView.getItems().get(newIndex).setStatus("Stopped");
                }
            }
        }
    }

    private void copyMenuAction() {

        String selectedLine = consoleListView.getSelectionModel().getSelectedItem();
        Clipboard systemClipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(selectedLine);
        systemClipboard.setContent(clipboardContent);

    }

    private void saveMenuAction() {

        Item selectedItem = itemsTableView.getSelectionModel().getSelectedItem();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save log file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialFileName("NVD-item-" + selectedItem.getId() + ".log");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.log", "*.txt"));
        File file = fileChooser.showSaveDialog(homeWindowPane.getScene().getWindow());
        if(file != null) {
            StringBuilder sb = new StringBuilder();
            selectedItem.getLogList().forEach(line -> sb.append(line).append('\n'));
            try {
                if(! file.exists())
                    Files.createFile(file.toPath());
                Files.write(file.toPath(), sb.toString().getBytes());
            } catch (Exception e) {
                homeWindowPane.setOpacity(0.30);
                new MessageDialog("Error Saving log file! \n" +
                        "Restart program and try again.", MessageDialog.Type.ERROR,
                        MessageDialog.Buttons.CLOSE).createErrorDialog(e.getStackTrace()).showAndWait();
                homeWindowPane.setOpacity(1);
            }
        }

    }


    @FXML
    private void addBtnAction() {

        try {

            FXMLLoader newDownloadWindowLoader = new FXMLLoader(getClass().getResource("windows/NewDownloadWindow.fxml"));
            newDownloadWindowLoader.load();
            ((NewDownloadController) newDownloadWindowLoader.getController()).setQueueBtnSelected(queueBtn.isSelected());
            Parent root = newDownloadWindowLoader.getRoot();
            homeWindowPane.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
            homeWindowPane.setOpacity(0.30);
            new MessageDialog("Error Loading NewDownload Window! \n" +
                    "Restart program and try again.", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(e.getStackTrace()).showAndWait();
            homeWindowPane.setOpacity(1);
        }

    }

    @FXML
    private void startBtnAction() {

        Item selectedItem = itemsTableView.getSelectionModel().getSelectedItem();
        boolean isValidItem = selectedItem != null &&
                (selectedItem.getStatus().equals("Stopped") || selectedItem.getStatus().equals("Waiting") ||
                        selectedItem.getStatus().equals("Error"));

        if(isValidItem) {
            selectedItem.setStatus("Starting");
            selectedItem.startDownload();
            if(selectedItem.getIsAddedToQueue())
                setNextQueueItemsStatus(selectedItem, "Waiting");
        }

    }

    @FXML
    private void stopBtnAction() {

        Item selectedItem = itemsTableView.getSelectionModel().getSelectedItem();
        boolean isValidItem = selectedItem != null
                && (selectedItem.getStatus().equals("Starting") || selectedItem.getStatus().equals("Running")
                || selectedItem.getStatus().equals("Waiting") || selectedItem.getStatus().equals("Error"));

        if (isValidItem) {
            if(selectedItem.getIsAddedToQueue() && (selectedItem.getStatus().equals("Starting")
                    || selectedItem.getStatus().equals("Running"))) {
                setNextQueueItemsStatus(selectedItem, "Stopped");
            }
            selectedItem.stopDownload();
        }

    }

    @FXML
    private void removeBtnAction() {

        Item selectedItem = itemsTableView.getSelectionModel().getSelectedItem();
        if( selectedItem != null) {

            MessageDialog deleteDialog = new MessageDialog("Are you sure you want to delete this item?\n" +
                    "You cannot undo this step", MessageDialog.Type.OPTION, MessageDialog.Buttons.OK_AND_CANCEL);
            CheckBox checkBox = new CheckBox("Delete files from the disk");
            checkBox.setTextFill(Color.CRIMSON);
            deleteDialog.addCheckBox(checkBox);
            deleteDialog.getNoButton().setOnAction(event -> deleteDialog.close());
            deleteDialog.getYesButton().setOnAction(event -> {
                if(checkBox.isSelected()) {

                    stopBtnAction();
                    boolean result;

                    if(selectedItem.getIsPlaylist()) {

                        if(System.getProperty("os.name").toLowerCase().contains("win"))
                            result = deleteItemFiles(selectedItem.getLocation() + "\\" +
                                    (selectedItem.getCustomName().equals("")? selectedItem.getTitle() : selectedItem.getCustomName()));
                        else
                            result = deleteItemFiles(selectedItem.getLocation() + "/" +
                                    (selectedItem.getCustomName().equals("")? selectedItem.getTitle() : selectedItem.getCustomName()));

                    } else {

                        if(System.getProperty("os.name").toLowerCase().contains("win"))
                            result = deleteItemFiles(selectedItem.getLocation() + "\\" +
                                    selectedItem.getTitle());
                        else
                            result = deleteItemFiles(selectedItem.getLocation() + "/" +
                                    selectedItem.getTitle());

                    }

                    if (result) {
                        itemList.remove(selectedItem);
                        queueItemList.remove(selectedItem);
                        DataHandler.delete(selectedItem);
                        deleteDialog.close();
                    }

                } else {

                    stopBtnAction();
                    itemList.remove(selectedItem);
                    queueItemList.remove(selectedItem);
                    DataHandler.delete(selectedItem);
                    deleteDialog.close();

                }
            });
            deleteDialog.showAndWait();

        }

    }

    @FXML
    private void infoBtnAction() {

        Item selectedItem = itemsTableView.getSelectionModel().getSelectedItem();
        if( selectedItem != null) {

            try {

                FXMLLoader infoWindowLoader = new FXMLLoader(getClass().getResource("windows/NewDownloadWindow.fxml"));
                infoWindowLoader.load();
                NewDownloadController infoWindowController = infoWindowLoader.getController();

                infoWindowController.setQueueBtnSelected(queueBtn.isSelected());
                infoWindowController.getStartBtn().setVisible(false);
                infoWindowController.getScheduleBtn().setVisible(false);
                infoWindowController.getToolbar().getItems().remove(infoWindowController.getStartBtn());
                infoWindowController.getToolbar().getItems().remove(infoWindowController.getScheduleBtn());
                infoWindowController.getArtifactsTitledPane().setExpanded(true);
                infoWindowController.getArtifactsTitledPane().setCollapsible(false);
                infoWindowController.getWebsiteTitledPane().setExpanded(true);
                infoWindowController.getWebsiteTitledPane().setCollapsible(false);
                infoWindowController.getPlaylistTitledPane().setExpanded(true);
                infoWindowController.getPlaylistTitledPane().setCollapsible(false);
                infoWindowController.getAuthenticationTitledPane().setExpanded(true);
                infoWindowController.getAuthenticationTitledPane().setCollapsible(false);
                infoWindowController.getOthersTitledPane().setExpanded(true);
                infoWindowController.getOthersTitledPane().setCollapsible(false);

                infoWindowController.getUrlTextField().setText(selectedItem.getUrl());
                infoWindowController.getUrlTextField().setEditable(false);
                infoWindowController.getLocationTextField().setText(selectedItem.getLocation());
                infoWindowController.getLocationTextField().setEditable(false);
                infoWindowController.getArtifactsSaveLocationHBox().getChildren().remove(infoWindowController.getBrowseBtn());
                if(! selectedItem.getCustomName().equals("")) {
                    infoWindowController.getCustomNameChkBox().setSelected(true);
                    infoWindowController.getCustomNameTextField().setText(selectedItem.getCustomName());
                    infoWindowController.getCustomNameTextField().setEditable(false);
                }
                infoWindowController.getCustomNameChkBox().setDisable(true);
                // Parsing the quality and format attributes
                infoWindowController.getQualityComboBox().setDisable(true);
                infoWindowController.getEmbeddedSubtitleChkBox().setSelected(selectedItem.getNeedEmbeddedSubtitle());
                infoWindowController.getEmbeddedSubtitleChkBox().setDisable(true);
                infoWindowController.getAutoGenSubtitleChkBox().setSelected(selectedItem.getNeedAutoGeneratedSubtitle());
                infoWindowController.getAutoGenSubtitleChkBox().setDisable(true);
                infoWindowController.getSubtitleLanguageChoiceBox().setValue(selectedItem.getSubtitleLanguage());
                infoWindowController.getSubtitleLanguageChoiceBox().setDisable(true);
                if(selectedItem.getIsPlaylist()) {
                    infoWindowController.getIsPlaylistChkBox().setSelected(true);
                    infoWindowController.getIsPlaylistChkBox().setDisable(true);
                    if(! selectedItem.getPlaylistItems().equals("")) {
                        infoWindowController.getSpecificItemsRadioBtn().setSelected(true);
                        infoWindowController.getPlaylistItemsTextField().setText(selectedItem.getPlaylistItems());
                        infoWindowController.getPlaylistItemsTextField().setEditable(false);
                    } else if(selectedItem.getPlaylistStartIndex() != 0 || selectedItem.getPlaylistEndIndex() != -1) {
                        infoWindowController.getIndexRangeRadioBtn().setSelected(true);
                        infoWindowController.getStartIndexTextField().setText(String.valueOf(selectedItem.getPlaylistStartIndex()));
                        infoWindowController.getStartIndexTextField().setEditable(false);
                        infoWindowController.getEndIndexTextField().setText(String.valueOf(selectedItem.getPlaylistEndIndex()));
                        infoWindowController.getEndIndexTextField().setEditable(false);
                    } else {
                        infoWindowController.getAllItemsRadioBtn().setSelected(true);
                    }
                    infoWindowController.getAllItemsRadioBtn().setDisable(true);
                    infoWindowController.getIndexRangeRadioBtn().setDisable(true);
                    infoWindowController.getSpecificItemsRadioBtn().setDisable(true);
                } else {
                    infoWindowController.getScrollPaneVBox().getChildren().remove(infoWindowController.getPlaylistTitledPane());
                }

                if(! selectedItem.getUserName().equals("") && ! selectedItem.getPassword().equals("")) {
                    infoWindowController.getNeedLoginCheckBox().setSelected(true);
                    infoWindowController.getNeedLoginCheckBox().setDisable(true);
                    infoWindowController.getUserNameTextField().setText(selectedItem.getUserName());
                    infoWindowController.getUserNameTextField().setEditable(false);
                    infoWindowController.getPasswordTextField().setText(selectedItem.getPassword());
                    infoWindowController.getPasswordTextField().setEditable(false);
                } else {
                    infoWindowController.getScrollPaneVBox().getChildren().remove(infoWindowController.getAuthenticationTitledPane());
                }

                infoWindowController.getLimitSpinner().getEditor().setText(String.valueOf(selectedItem.getSpeedLimit()));
                infoWindowController.getLimitSpinner().setDisable(true);
                infoWindowController.getShutdownCheckBox().setSelected(selectedItem.getShutdownAfterFinish());
                infoWindowController.getShutdownCheckBox().setDisable(true);

                Parent root = infoWindowLoader.getRoot();
                homeWindowPane.getScene().setRoot(root);

            } catch (Exception e) {
                e.printStackTrace();
                homeWindowPane.setOpacity(0.30);
                new MessageDialog("Error Loading NewDownload Window! \n" +
                        "Restart program and try again.", MessageDialog.Type.ERROR,
                        MessageDialog.Buttons.CLOSE).createErrorDialog(e.getStackTrace()).showAndWait();
                homeWindowPane.setOpacity(1);
            }

        }

    }

    @FXML
    private void settingBtnAction() {

        try {

            Parent root = FXMLLoader.load(getClass().getResource("windows/SettingWindow.fxml"));
            Stage settingStage = new Stage();
            settingStage.setScene(new Scene(root));
            settingStage.setTitle("Setting");
            settingStage.show();

        } catch (Exception e) {
            homeWindowPane.setOpacity(0.30);
            new MessageDialog("Error Loading Settings Window! \n" +
                    "Restart program and try again.", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(e.getStackTrace()).showAndWait();
            homeWindowPane.setOpacity(1);
        }

    }

    @FXML
    private void helpBtnAction() {

        MessageDialog messageDialog = new MessageDialog("Ooh, you need help in this simple program?\n" +
                "Sorry, no help in this version. help yourself", MessageDialog.Type.INFO, MessageDialog.Buttons.OK);
        messageDialog.getOkButton().setOnAction(event -> messageDialog.close());
        messageDialog.showAndWait();

    }

    @FXML
    private void logBtnAction() {

        boolean isLogVisible = homeSplitPane.getItems().size() == 2;

        if(isLogVisible) {
            homeSplitPane.getItems().remove(consoleListView);
        } else {
            homeSplitPane.getItems().add(1, consoleListView);
            homeSplitPane.setDividerPosition(0, programData.getDouble(DIVIDER_POSITION, 0.8));
        }

        programData.putBoolean(HIDE_LOG, isLogVisible);

    }


    private ContextMenu getRowContextMenu() {

        ContextMenu rowContextMenu = new ContextMenu();

        MenuItem startMenuItem = new MenuItem("Start");
        startMenuItem.setOnAction(event -> startBtnAction());
        startMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        startMenuItem.setGraphic(new ImageView(new Image(getClass().getResource("menu/start.png").toString())));

        MenuItem pauseMenuItem = new MenuItem("Pause");
        pauseMenuItem.setOnAction(event -> stopBtnAction());
        pauseMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN));
        pauseMenuItem.setGraphic(new ImageView(new Image(getClass().getResource("menu/pause.png").toString())));

        MenuItem waitMenuItem = new MenuItem("Set Waiting");
        waitMenuItem.setDisable(true);
        waitMenuItem.setOnAction(event -> waitMenuAction());
        waitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN));
        waitMenuItem.setGraphic(new ImageView(new Image(getClass().getResource("menu/wait.png").toString())));

        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(event -> removeBtnAction());
        deleteMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
        deleteMenuItem.setGraphic(new ImageView(new Image(getClass().getResource("menu/delete.png").toString())));

        MenuItem upeMenuItem = new MenuItem("Up");
        upeMenuItem.setOnAction(event -> upMenuAction());
        upeMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.UP, KeyCombination.SHORTCUT_DOWN));
        upeMenuItem.setGraphic(new ImageView(new Image(getClass().getResource("menu/up.png").toString())));

        MenuItem downMenuItem = new MenuItem("Down");
        downMenuItem.setOnAction(event -> downMenuAction());
        downMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DOWN, KeyCombination.SHORTCUT_DOWN));
        downMenuItem.setGraphic(new ImageView(new Image(getClass().getResource("menu/down.png").toString())));

        MenuItem queueMenuItem = new MenuItem("Add to Queue");
        queueMenuItem.setOnAction(event -> addToQueueMenuAction());
        queueMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN));
        queueMenuItem.setGraphic(new ImageView(new Image(getClass().getResource("menu/queue.png").toString())));

        MenuItem clearMenuItem = new MenuItem("Clear Logs");
        clearMenuItem.setOnAction(event -> clearLogsMenuAction());
        clearMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN));
        clearMenuItem.setGraphic(new ImageView(new Image(getClass().getResource("menu/clear.png").toString())));

        MenuItem openFolderMenuItem = new MenuItem("Open Location");
        openFolderMenuItem.setOnAction(event -> openFolderMenuAction());
        openFolderMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        openFolderMenuItem.setGraphic(new ImageView(new Image(getClass().getResource("menu/folder.png").toString())));

        MenuItem infoMenuItem = new MenuItem("Properties");
        infoMenuItem.setOnAction(event -> infoBtnAction());
        infoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.ENTER, KeyCombination.ALT_DOWN));
        infoMenuItem.setGraphic(new ImageView(new Image(getClass().getResource("menu/details.png").toString())));

        rowContextMenu.getItems().addAll(startMenuItem, pauseMenuItem, waitMenuItem, deleteMenuItem, upeMenuItem,
                downMenuItem, queueMenuItem, clearMenuItem, openFolderMenuItem, infoMenuItem);

        return rowContextMenu;
    }

    private boolean deleteItemFiles(String path) {

        File file = new File(path);

        if (file.exists()) {

            if (file.isDirectory()) {
                String[] directoryFiles = file.list();
                if (directoryFiles != null && directoryFiles.length > 0) {
                    for(String s : directoryFiles)
                        deleteItemFiles(new File(path, s).getPath());
                }
            }

            boolean result = file.delete();

            // test if delete of file is success or not
            if (! result) {
                new MessageDialog("File cannot be deleted! \n" +
                        "Restart program and try again.", MessageDialog.Type.ERROR, MessageDialog.Buttons.CLOSE)
                        .createErrorDialog("The file may be in use by another program").showAndWait();
            }

            return result;

        } else {

            new MessageDialog("File delete failed, file does not exist! \n" +
                    "Restart program and try again.", MessageDialog.Type.ERROR, MessageDialog.Buttons.CLOSE)
                    .createErrorDialog("file is not exist").showAndWait();
            return false;

        }

    }

    private void setNextQueueItemsStatus(Item currentItem, String newStatus) {

        for(int i = queueItemList.indexOf(currentItem) + 1; i < queueItemList.size(); i++) {

            if(queueItemList.get(i).getStatus().equals("Stopped") || queueItemList.get(i).getStatus().equals("Waiting") || queueItemList.get(i).getStatus().equals("Error"))
                queueItemList.get(i).setStatus(newStatus);
            else if(queueItemList.get(i).getStatus().equals("Starting") || queueItemList.get(i).getStatus().equals("Running"))
                break;
        }

    }

    private boolean queueIsRunningBefore(Item currentItem) {

        for(int i = 0; i < queueItemList.indexOf(currentItem); i++) {
            if(queueItemList.get(i).getStatus().equals("Starting") || queueItemList.get(i).getStatus().equals("Running"))
                return true;
        }
        return false;

    }

}
