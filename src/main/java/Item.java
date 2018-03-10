import com.google.gson.annotations.Expose;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Item {

    @Expose private IntegerProperty id;
    @Expose private StringProperty url;
    @Expose private StringProperty location;
    @Expose private StringProperty title;
    @Expose private StringProperty customName;
    @Expose private IntegerProperty speedLimit;
    @Expose private BooleanProperty shutdownAfterFinish;
    @Expose private BooleanProperty isAddedToQueue;
    @Expose private BooleanProperty isVideo;
    @Expose private StringProperty format;
    @Expose private IntegerProperty videoQuality;
    @Expose private IntegerProperty audioQuality;
    @Expose private StringProperty subtitleLanguage;
    @Expose private BooleanProperty needEmbeddedSubtitle;
    @Expose private BooleanProperty needAutoGeneratedSubtitle;
    @Expose private BooleanProperty isPlaylist;
    @Expose private IntegerProperty playlistStartIndex;
    @Expose private IntegerProperty playlistEndIndex;
    @Expose private StringProperty playlistItems;
    @Expose private BooleanProperty needAllPlaylistItems;
    @Expose private StringProperty userName;
    @Expose private StringProperty password;
    @Expose private StringProperty status;
    @Expose private DoubleProperty done;
    @Expose private StringProperty size;
    private StringProperty speed;
    private StringProperty eta;
    private ObservableList<String> logList;
    private Process ytdlProcess;
    private boolean errorFlag;


    public Item() {

        id = new SimpleIntegerProperty();
        url = new SimpleStringProperty();
        location = new SimpleStringProperty("");
        title = new SimpleStringProperty("");
        customName = new SimpleStringProperty("");
        speedLimit = new SimpleIntegerProperty(0);
        shutdownAfterFinish = new SimpleBooleanProperty(false);
        isAddedToQueue = new SimpleBooleanProperty(false);
        isVideo = new SimpleBooleanProperty();
        format = new SimpleStringProperty();
        videoQuality = new SimpleIntegerProperty();
        audioQuality = new SimpleIntegerProperty();
        subtitleLanguage = new SimpleStringProperty();
        needEmbeddedSubtitle = new SimpleBooleanProperty();
        needAutoGeneratedSubtitle = new SimpleBooleanProperty();
        isPlaylist = new SimpleBooleanProperty();
        playlistStartIndex = new SimpleIntegerProperty(0);
        playlistEndIndex = new SimpleIntegerProperty(-1);
        playlistItems = new SimpleStringProperty("");
        needAllPlaylistItems = new SimpleBooleanProperty();
        userName = new SimpleStringProperty("");
        password = new SimpleStringProperty("");
        status = new SimpleStringProperty("");
        done = new SimpleDoubleProperty(0);
        size = new SimpleStringProperty("");
        speed = new SimpleStringProperty("");
        eta = new SimpleStringProperty("");
        logList = FXCollections.observableArrayList();
        errorFlag = false;

    }

    @Override
    public String toString() {

        return new StringBuilder("Item{")
                .append("id=").append(id.get())
                .append(", url=").append(url.get())
                .append(", location=").append(location.get())
                .append(", title=").append(title.get())
                .append(", customName=").append(customName.get())
                .append(", speedLimit=").append(speedLimit.get())
                .append(", shutdownAfterFinish=").append(shutdownAfterFinish.get())
                .append(", isAddedToQueue=").append(isAddedToQueue.get())
                .append(", isVideo=").append(isVideo.get())
                .append(", format=").append(format.get())
                .append(", videoQuality=").append(videoQuality.get())
                .append(", audioQuality=").append(audioQuality.get())
                .append(", subtitleLanguage=").append(subtitleLanguage.get())
                .append(", needEmbeddedSubtitle=").append(needEmbeddedSubtitle.get())
                .append(", needAutoGeneratedSubtitle=").append(needAutoGeneratedSubtitle.get())
                .append(", isPlaylist=").append(isPlaylist.get())
                .append(", playlistStartIndex=").append(playlistStartIndex.get())
                .append(", playlistEndIndex=").append(playlistEndIndex.get())
                .append(", playlistItems=").append(playlistItems.get())
                .append(", needAllPlaylistItems=").append(needAllPlaylistItems.get())
                .append(", userName=").append(userName.get())
                .append(", password=").append(password.get())
                .append(", status=").append(status.get())
                .append(", done=").append(done.get())
                .append(", size=").append(size.get())
                .append("}").toString();

    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getUrl() {
        return url.get();
    }

    public StringProperty urlProperty() {
        return url;
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public String getLocation() {
        return location.get();
    }

    public StringProperty locationProperty() {
        return location;
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getCustomName() {
        return customName.get();
    }

    public StringProperty customNameProperty() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName.set(customName);
    }

    public int getSpeedLimit() {
        return speedLimit.get();
    }

    public IntegerProperty speedLimitProperty() {
        return speedLimit;
    }

    public void setSpeedLimit(int speedLimit) {
        this.speedLimit.set(speedLimit);
    }

    public boolean getShutdownAfterFinish() {
        return shutdownAfterFinish.get();
    }

    public BooleanProperty shutdownAfterFinishProperty() {
        return shutdownAfterFinish;
    }

    public void setShutdownAfterFinish(boolean shutdownAfterFinish) {
        this.shutdownAfterFinish.set(shutdownAfterFinish);
    }

    public boolean getIsAddedToQueue() {
        return isAddedToQueue.get();
    }

    public BooleanProperty isAddedToQueueProperty() {
        return isAddedToQueue;
    }

    public void setIsAddedToQueue(boolean isAddedToQueue) {
        this.isAddedToQueue.set(isAddedToQueue);
    }

    public boolean getIsVideo() {
        return isVideo.get();
    }

    public BooleanProperty isVideoProperty() {
        return isVideo;
    }

    public void setIsVideo(boolean isVideo) {
        this.isVideo.set(isVideo);
    }

    public String getFormat() {
        return format.get();
    }

    public StringProperty formatProperty() {
        return format;
    }

    public void setFormat(String format) {
        this.format.set(format);
    }

    public int getVideoQuality() {
        return videoQuality.get();
    }

    public IntegerProperty videoQualityProperty() {
        return videoQuality;
    }

    public void setVideoQuality(int videoQuality) {
        this.videoQuality.set(videoQuality);
    }

    public int getAudioQuality() {
        return audioQuality.get();
    }

    public IntegerProperty audioQualityProperty() {
        return audioQuality;
    }

    public void setAudioQuality(int audioQuality) {
        this.audioQuality.set(audioQuality);
    }

    public String getSubtitleLanguage() {
        return subtitleLanguage.get();
    }

    public StringProperty subtitleLanguageProperty() {
        return subtitleLanguage;
    }

    public void setSubtitleLanguage(String subtitleLanguage) {
        this.subtitleLanguage.set(subtitleLanguage);
    }

    public boolean getNeedEmbeddedSubtitle() {
        return needEmbeddedSubtitle.get();
    }

    public BooleanProperty needEmbeddedSubtitleProperty() {
        return needEmbeddedSubtitle;
    }

    public void setNeedEmbeddedSubtitle(boolean needEmbeddedSubtitle) {
        this.needEmbeddedSubtitle.set(needEmbeddedSubtitle);
    }

    public boolean getNeedAutoGeneratedSubtitle() {
        return needAutoGeneratedSubtitle.get();
    }

    public BooleanProperty needAutoGeneratedSubtitleProperty() {
        return needAutoGeneratedSubtitle;
    }

    public void setNeedAutoGeneratedSubtitle(boolean needAutoGeneratedSubtitle) {
        this.needAutoGeneratedSubtitle.set(needAutoGeneratedSubtitle);
    }

    public boolean getIsPlaylist() {
        return isPlaylist.get();
    }

    public BooleanProperty isPlaylistProperty() {
        return isPlaylist;
    }

    public void setIsPlaylist(boolean isPlaylist) {
        this.isPlaylist.set(isPlaylist);
    }

    public int getPlaylistStartIndex() {
        return playlistStartIndex.get();
    }

    public IntegerProperty playlistStartIndexProperty() {
        return playlistStartIndex;
    }

    public void setPlaylistStartIndex(int playlistStartIndex) {
        this.playlistStartIndex.set(playlistStartIndex);
    }

    public int getPlaylistEndIndex() {
        return playlistEndIndex.get();
    }

    public IntegerProperty playlistEndIndexProperty() {
        return playlistEndIndex;
    }

    public void setPlaylistEndIndex(int playlistEndIndex) {
        this.playlistEndIndex.set(playlistEndIndex);
    }

    public String getPlaylistItems() {
        return playlistItems.get();
    }

    public StringProperty playlistItemsProperty() {
        return playlistItems;
    }

    public void setPlaylistItems(String playlistItems) {
        this.playlistItems.set(playlistItems);
    }

    public boolean getNeedAllPlaylistItems() {
        return needAllPlaylistItems.get();
    }

    public BooleanProperty needAllPlaylistItemsProperty() {
        return needAllPlaylistItems;
    }

    public void setNeedAllPlaylistItems(boolean needAllPlaylistItems) {
        this.needAllPlaylistItems.set(needAllPlaylistItems);
    }

    public String getUserName() {
        return userName.get();
    }

    public StringProperty userNameProperty() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public double getDone() {
        return done.get();
    }

    public DoubleProperty doneProperty() {
        return done;
    }

    public void setDone(double done) {
        this.done.set(done);
    }

    public String getSize() {
        return size.get();
    }

    public StringProperty sizeProperty() {
        return size;
    }

    public void setSize(String size) {
        this.size.set(size);
    }

    public String getSpeed() {
        return speed.get();
    }

    public StringProperty speedProperty() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed.set(speed);
    }

    public String getEta() {
        return eta.get();
    }

    public StringProperty etaProperty() {
        return eta;
    }

    public void setEta(String eta) {
        this.eta.set(eta);
    }

    public ObservableList<String> getLogList() {
        return logList;
    }

    public void setLogList(ObservableList<String> logList) {
        this.logList = logList;
    }

    public Process getYtdlProcess() {
        return ytdlProcess;
    }

    public void setYtdlProcess(Process ytdlProcess) {
        this.ytdlProcess = ytdlProcess;
    }

    public boolean getErrorFlag() {
        return errorFlag;
    }

    public void setErrorFlag(boolean errorFlag) {
        this.errorFlag = errorFlag;
    }


    public void startDownload() {

        Task<Void> downloadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                List<String> cmd = commandBuilder();
                System.out.println(cmd.toString().replace(",", ""));
                ytdlProcess = new ProcessBuilder(cmd).redirectErrorStream(true).start();
                setStatus("Starting");
                setErrorFlag(false);

                InputStream inputStream = ytdlProcess.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String downloadRegex = "\\[download\\]\\s*(\\d+\\.\\d+)%\\s*of\\s*(~?\\d+\\.\\d+)([MKG]?i?B)\\s*at\\s*(\\d+\\.\\d+)([MKG]?i?B/s)\\s*ETA\\s*(.*)";
                String fileFinishRegex = "\\[download\\]\\s*100%\\s*of\\s*(~?\\d+\\.\\d+)([MKG]?i?B).*";

                Pattern downloadPattern = Pattern.compile(downloadRegex);
                Pattern fileFinishPattern = Pattern.compile(fileFinishRegex);
                String buff;


                while ((buff = bufferedReader.readLine()) != null &&
                        (getStatus().equals("Starting") || getStatus().equals("Running"))) {

                    final String line = buff;
                    final Matcher downloadMatcher = downloadPattern.matcher(line);
                    final Matcher fileFinishMatcher = fileFinishPattern.matcher(line);

                    Platform.runLater( () -> {

                        //parsing download status info
                        if (downloadMatcher.find()) {
                            setStatus("Running");

                            //combine the download messages in one line
                            if (logList.size() > 0 && logList.get(logList.size() - 1).matches(downloadRegex)) {
                                logList.remove(logList.size() - 1);
                                logList.add(line);
                            } else
                                logList.add(line);

                            setDone(Double.parseDouble(downloadMatcher.group(1)));
                            setSize(downloadMatcher.group(2) + " " + downloadMatcher.group(3));
                            setSpeed(downloadMatcher.group(4) + " " + downloadMatcher.group(5));
                            setEta(downloadMatcher.group(6));
                            DataHandler.save(getThisItem());

                        } else if (! line.equals("")) {

                            logList.add(line);

                            if (getIsPlaylist()) {
                                //parse the title of download playlist item and add it to the database
                                if (line.matches("\\[download\\]\\s*Downloading playlist:\\s*.+")) {
                                    String title = line.split(":\\s+")[1];
                                    setTitle(title);
                                    DataHandler.save(getThisItem());
                                //Check If download is completed and set Finished status
                                } else if (line.matches("\\[download\\]\\s*Finished\\s*downloading\\s*playlist:.*")) {
                                    setDone(100);
                                    setSize("");
                                //Check if there is an error and set Failed status
                                } else if (line.startsWith("ERROR:")) {
                                    setErrorFlag(true);
                                }
                            } else {
                                //parse the title of download item and add it to the database
                                String regexLocationString = System.getProperty("os.name").toLowerCase().contains("win")? getLocation().replace("\\", "\\\\") : getLocation();
                                if (line.matches("\\[download\\]\\s*(Destination:\\s*)?" + regexLocationString + "[/\\\\]?.+")) {
                                    String title = line.split(regexLocationString + "[/\\\\]?")[1].split("\\s*has already been downloaded")[0];
                                    setTitle(title);
                                    DataHandler.save(getThisItem());
                                //Check If download is completed and set Finished status
                                } else if(fileFinishMatcher.find()) {
                                    setDone(100);
                                    setSize(fileFinishMatcher.group(1) + " " + fileFinishMatcher.group(2));
                                //Check if there is an error and set Failed status
                                } else if(line.startsWith("ERROR:")) {
                                    setErrorFlag(true);
                                }
                            }

                        }

                    });

                }

                if(getStatus().equals("Stopped")) {
                    DataHandler.save(getThisItem());
                } else {
                    if(getErrorFlag()) {
                        setStatus("Error");
                    } else {
                        if(getDone() == 100.0)
                            setStatus("Finished");
                    }
                    DataHandler.save(getThisItem());
                    finishDownload();
                }

                return null;
            }

        };

        Thread backgroundThread = new Thread(downloadTask);
        backgroundThread.start();

    }

    public void stopDownload() {
        setStatus("Stopped");
        if(ytdlProcess != null)
            ytdlProcess.destroy();
        setSpeed("");
        setEta("");
    }

    public void finishDownload() {

        ytdlProcess.destroy();
        setSpeed("");
        setEta("");

        if(getShutdownAfterFinish()) {
            shutdownAfter(60);
        }

        if(getIsAddedToQueue()) {
            Item nextQueueItem = getNextQueueItemTo(this);
            while(nextQueueItem != null && (nextQueueItem.getStatus().equals("Stopped") || nextQueueItem.getStatus().equals("Finished")))
                nextQueueItem = getNextQueueItemTo(this);
            if(nextQueueItem != null && (nextQueueItem.getStatus().equals("Waiting") || nextQueueItem.getStatus().equals("Failed")))
                nextQueueItem.startDownload();
        }

    }


    private List<String> commandBuilder() {

        List<String> cmdList = new ArrayList<>(Arrays.asList("python", "youtube-dl", "-i", "-c", "--no-part"));

        if (getSpeedLimit() != 0) {
            cmdList.add("-r");
            cmdList.add(String.valueOf(getSpeedLimit() + "K"));
        }

        if (audioQuality.getValue() == 0 && videoQuality.getValue() != 0) {
            cmdList.add("-f");
            cmdList.add(videoQuality.getValue().toString());
        } else if (audioQuality.getValue() != 0 && videoQuality.getValue() != 0) {
            cmdList.add("-f");
            cmdList.add(videoQuality.getValue().toString() + "+" + audioQuality.getValue().toString());
        } else if (audioQuality.getValue() != 0 && videoQuality.getValue() == 0) {
            cmdList.add("-f");
            cmdList.add(audioQuality.getValue().toString());
        }

        if (getIsVideo()) {
            cmdList.add("--merge-output-format");
            cmdList.add("mp4");
        }

        if (needEmbeddedSubtitle.getValue()) {
            cmdList.add("--write-sub");
            cmdList.add("--sub-lang");
            cmdList.add(subtitleLanguage.getValue());
        }

        if (needAutoGeneratedSubtitle.getValue())
            cmdList.add("--write-auto-sub");

        if (isPlaylist.getValue()) {
            cmdList.add("--yes-playlist");
            if (customName.get().equals("")) {
                cmdList.add("-o");
                if(System.getProperty("os.name").toLowerCase().contains("win"))
                    cmdList.add(location.getValue() + "\\%(playlist_title)s\\%(playlist_index)s - %(title)s.%(ext)s");
                else
                cmdList.add(location.getValue() + "/%(playlist_title)s/%(playlist_index)s - %(title)s.%(ext)s");
            } else {
                cmdList.add("-o");
                if(System.getProperty("os.name").toLowerCase().contains("win"))
                    cmdList.add(location.getValue() + "\\" + customName.getValue() + "\\%(playlist_index)s - %(title)s.%(ext)s");
                else
                    cmdList.add(location.getValue() + "/" + customName.getValue() + "/%(playlist_index)s - %(title)s.%(ext)s");
            }
        } else {
            cmdList.add("--no-playlist");
            if (customName.get().equals("")) {
                cmdList.add("-o");
                if(System.getProperty("os.name").toLowerCase().contains("win"))
                    cmdList.add(location.getValue() + "\\" + "%(title)s.%(ext)s");
                else
                    cmdList.add(location.getValue() + "/" + "%(title)s.%(ext)s");
            } else {
                cmdList.add("-o");
                if(System.getProperty("os.name").toLowerCase().contains("win"))
                    cmdList.add(location.getValue() + "\\" + customName.getValue() + ".%(ext)s");
                else
                    cmdList.add(location.getValue() + "/" + customName.getValue() + ".%(ext)s");
            }
        }

        if(!getUserName().equals("") && !getPassword().equals("")) {
            cmdList.add("-u");
            cmdList.add(userName.get());
            cmdList.add("-p");
            cmdList.add(password.get());
        }

        cmdList.add(url.getValue());

        return cmdList;
    }

    private Item getThisItem() {
        return this;
    }

    private void shutdownAfter(int seconds) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Main.saveAndExit();

                try {

                    String os = System.getProperty("os.name").toLowerCase();

                    if (os.contains("win"))
                        Runtime.getRuntime().exec("shutdown.exe -s -t 0");
                    else
                        Runtime.getRuntime().exec("shutdown -h now");

                } catch(Exception e) {

                    new MessageDialog("Error executing shutdown command!\n" +
                            "Restart program and try again.", MessageDialog.Type.ERROR,
                            MessageDialog.Buttons.CLOSE).createErrorDialog(e.getStackTrace()).show();
                }

                System.exit(0);
            }
        }, seconds * 1000);

        Platform.runLater(() -> {
            MessageDialog messageDialog = new MessageDialog("Attention, Computer will shutdown after " +
                    seconds + " seconds !\nSave your work, or click cancel to stop.", MessageDialog.Type.INFO, MessageDialog.Buttons.OK_AND_CANCEL);
            messageDialog.getOkButton().setOnAction(actionEvent -> messageDialog.close());
            messageDialog.getCancelButton().setOnAction(actionEvent -> {
                timer.cancel();
                messageDialog.close();
            });
            messageDialog.show();
        });


    }

    private Item getNextQueueItemTo(Item currentItem) {

        int itemIndex = HomeController.getQueueItemList().indexOf(currentItem);
        int queueItemListSize = HomeController.getQueueItemList().size();
        boolean thereIsAnotherItem = itemIndex != queueItemListSize - 1;

        return thereIsAnotherItem? HomeController.getQueueItemList().get(itemIndex + 1) : null;
    }

}
