import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Item {

    private IntegerProperty itemId = new SimpleIntegerProperty();
    private StringProperty url = new SimpleStringProperty();
    private StringProperty location = new SimpleStringProperty();
    private StringProperty title = new SimpleStringProperty("");
    private StringProperty customName = new SimpleStringProperty("");
    private IntegerProperty speedLimit = new SimpleIntegerProperty();
    private BooleanProperty shutdownAfterFinish = new SimpleBooleanProperty();
    private BooleanProperty addToQueue = new SimpleBooleanProperty();
    private BooleanProperty isVideo = new SimpleBooleanProperty();
    private StringProperty format = new SimpleStringProperty();
    private IntegerProperty videoQuality = new SimpleIntegerProperty();
    private IntegerProperty audioQuality = new SimpleIntegerProperty();
    private StringProperty subtitleLanguage = new SimpleStringProperty();
    private BooleanProperty embeddedSubtitle = new SimpleBooleanProperty();
    private BooleanProperty autoGeneratedSubtitle = new SimpleBooleanProperty();
    private BooleanProperty isPlaylist = new SimpleBooleanProperty();
    private IntegerProperty startIndex = new SimpleIntegerProperty();
    private IntegerProperty endIndex = new SimpleIntegerProperty();
    private StringProperty items = new SimpleStringProperty();
    private BooleanProperty allItems = new SimpleBooleanProperty();
    private StringProperty status = new SimpleStringProperty();
    private FloatProperty done = new SimpleFloatProperty();
    private FloatProperty size = new SimpleFloatProperty();
    private FloatProperty speed = new SimpleFloatProperty();
    private StringProperty eta = new SimpleStringProperty();
    private StringProperty speedString = new SimpleStringProperty();
    private StringProperty sizeString = new SimpleStringProperty();
    private DoubleProperty progress = new SimpleDoubleProperty();
    private String sizeUnit = "";
    private String speedUnit = "";
    private ObservableList<String> logList = FXCollections.observableArrayList();
    private Process ytdlProcess = null;



    public int getItemId() {
        return itemId.get();
    }

    public IntegerProperty itemIdProperty() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId.set(itemId);
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

    public boolean isAddToQueue() {
        return addToQueue.get();
    }

    public BooleanProperty addToQueueProperty() {
        return addToQueue;
    }

    public void setAddToQueue(boolean addToQueue) {
        this.addToQueue.set(addToQueue);
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

    public boolean isEmbeddedSubtitle() {
        return embeddedSubtitle.get();
    }

    public BooleanProperty embeddedSubtitleProperty() {
        return embeddedSubtitle;
    }

    public void setEmbeddedSubtitle(boolean embeddedSubtitle) {
        this.embeddedSubtitle.set(embeddedSubtitle);
    }

    public boolean isAutoGeneratedSubtitle() {
        return autoGeneratedSubtitle.get();
    }

    public BooleanProperty autoGeneratedSubtitleProperty() {
        return autoGeneratedSubtitle;
    }

    public void setAutoGeneratedSubtitle(boolean autoGeneratedSubtitle) {
        this.autoGeneratedSubtitle.set(autoGeneratedSubtitle);
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

    public int getStartIndex() {
        return startIndex.get();
    }

    public IntegerProperty startIndexProperty() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex.set(startIndex);
    }

    public int getEndIndex() {
        return endIndex.get();
    }

    public IntegerProperty endIndexProperty() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex.set(endIndex);
    }

    public String getItems() {
        return items.get();
    }

    public StringProperty itemsProperty() {
        return items;
    }

    public void setItems(String items) {
        this.items.set(items);
    }

    public boolean isAllItems() {
        return allItems.get();
    }

    public BooleanProperty allItemsProperty() {
        return allItems;
    }

    public void setAllItems(boolean allItems) {
        this.allItems.set(allItems);
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

    public float getDone() {
        return done.get();
    }

    public FloatProperty doneProperty() {
        return done;
    }

    public void setDone(float done) {
        this.done.set(done);
        this.progress.set((this.done.get() / 100.0f));
    }

    public float getSize() {
        return size.get();
    }

    public FloatProperty sizeProperty() {
        return size;
    }

    public void setSize(float size) {
        this.size.set(size);
        if(size == 0)
            this.sizeString.set("");
        else
            this.sizeString.set(this.size.get() + " " + this.sizeUnit);
    }

    public float getSpeed() {
        return speed.get();
    }

    public FloatProperty speedProperty() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed.set(speed);
        if (speed == 0)
            this.speedString.set("");
        else
            this.speedString.set(this.speed.get() + " " + this.getSpeedUnit());
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

    public String getSpeedString() {
        return speedString.get();
    }

    public StringProperty speedStringProperty() {
        return speedString;
    }

    public String getSizeString() {
        return sizeString.get();
    }

    public StringProperty sizeStringProperty() {
        return sizeString;
    }

    public double getProgress() {
        return progress.get();
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress.set(progress);
    }

    public String getSizeUnit() {
        return sizeUnit;
    }

    public void setSizeUnit(String sizeUnit) {
        this.sizeUnit = sizeUnit;
    }

    public String getSpeedUnit() {
        return speedUnit;
    }

    public void setSpeedUnit(String speedUnit) {
        this.speedUnit = speedUnit;
    }

    public ObservableList<String> getLogList() {
        return logList;
    }

    // the magic method
    private Item getThisItem() {
        return this;
    }



    private List<String> commandBuilder() {

        List<String> cmdList = new ArrayList<>(Arrays.asList("python", "youtube-dl", "-i", "-c", "--no-part"));

        if (speedLimit.getValue() != 0) {
            cmdList.add("-r");
            cmdList.add(speedLimit.getValue().toString());
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

        if (embeddedSubtitle.getValue()) {
            cmdList.add("--write-sub");
            cmdList.add("--sub-lang");
            cmdList.add(subtitleLanguage.getValue());
        }

        if (autoGeneratedSubtitle.getValue())
            cmdList.add("--write-auto-sub");

        if (isPlaylist.getValue()) {
            cmdList.add("--yes-playlist");
            if (customName.get().equals("")) {
                cmdList.add("-o");
                cmdList.add(location.getValue() + "/%(playlist_title)s/%(playlist_index)s - %(title)s.%(ext)s");
            } else {
                cmdList.add("-o");
                cmdList.add(location.getValue() + "/" + customName.getValue() + "/%(playlist_index)s - %(title)s.%(ext)s");
            }
        } else {
            cmdList.add("--no-playlist");
            if (customName.get().equals("")) {
                cmdList.add("-o");
                cmdList.add(location.getValue() + "/" + "%(title)s.%(ext)s");
            } else {
                cmdList.add("-o");
                cmdList.add(location.getValue() + "/" + customName.getValue() + ".%(ext)s");
            }
        }

        cmdList.add(url.getValue());

        if (shutdownAfterFinish.getValue().equals("Shutdown")) {
            cmdList.add("&");
            cmdList.add("shutdown");
            cmdList.add("-H");
        } else if (shutdownAfterFinish.getValue().equals("Sleep")) {
            cmdList.add("&");
            cmdList.add("shutdown");
            cmdList.add("-s");
        }

        return cmdList;
    }

    public void startDownload() {

        Task<Void> downloadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                List<String> cmd = commandBuilder();
                System.out.println(cmd.toString().replace(",", ""));
                ytdlProcess = new ProcessBuilder(cmd).start();
                setStatus("Running");

                InputStream inputStream = ytdlProcess.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String downloadRegex = "\\[download\\]\\s*(\\d+\\.\\d+)%\\s*of\\s*~?(\\d+\\.\\d+)([MKG]?i?B)\\s*at\\s*(\\d+\\.\\d+)([MKG]?i?B/s)\\s*ETA\\s*(.*)";
                String fileFinishRegex = "\\[download\\]\\s*100%\\s*of\\s*~?(\\d+\\.\\d+)\\s*([MKG]?i?B).*";

                Pattern downloadPattern = Pattern.compile(downloadRegex);
                Pattern fileFinishPattern = Pattern.compile(fileFinishRegex);
                String buff;


                while ((buff = bufferedReader.readLine()) != null && getStatus().equals("Running")) {

                    final String line = buff;
                    final Matcher downloadMatcher = downloadPattern.matcher(line);
                    final Matcher fileFinishMatcher = fileFinishPattern.matcher(line);

                    Platform.runLater( () -> {

                        //parsing download status info
                        if (downloadMatcher.find()) {
                            //combine the download messages in one line
                            if (logList.size() > 0 && logList.get(logList.size() - 1).matches(downloadRegex))
                                logList.set(logList.size() - 1, line);
                            else
                                logList.add(line);

                            String x = downloadMatcher.group(1);
                            setDone(Float.parseFloat(x));
                            DbManager.updateFloat(getThisItem(), "done", getDone());  //update done percent in database
                            x = downloadMatcher.group(3);
                            setSizeUnit(x);
                            DbManager.updateString(getThisItem(), "sizeUnit", x);
                            x = downloadMatcher.group(2);
                            setSize(Float.parseFloat(x));
                            DbManager.updateFloat(getThisItem(), "size", getSize());
                            x = downloadMatcher.group(4);
                            setSpeed(Float.parseFloat(x));
                            x = downloadMatcher.group(5);
                            setSpeedUnit(x);
                            x = downloadMatcher.group(6);
                            setEta(x);

                        } else if (!line.equals("")) {

                            logList.add(line);

                            if (getIsPlaylist()) {
                                //parse the title of download playlist item and add it to the database
                                if (line.matches("\\[download\\]\\s*Downloading playlist:\\s*.+")) {
                                    String title = line.split(":\\s+")[1];
                                    setTitle(title);
                                    DbManager.updateString(getThisItem(), "title", title);
                                //Check If download is completed and set Finished status
                                } else if (line.matches("\\[download\\]\\s*Finished\\s*downloading\\s*playlist:.*")) {
                                    setDone(100);
                                    setSize(0);
                                    finishDownload();
                                }
                            } else {
                                //parse the title of download playlist item and add it to the database
                                if (line.matches("\\[download\\]\\s*(Destination:\\s*)?" + getLocation() + "[/\\\\]?.+")) {
                                    String title = line.split(getLocation() + "[/\\\\]?")[1].split("\\s*has already been downloaded")[0];
                                    setTitle(title);
                                    DbManager.updateString(getThisItem(), "title", title);
                                //Check If download is completed and set Finished status
                                } else if(!getIsPlaylist() && fileFinishMatcher.find()) {
                                    setDone(100);
                                    setSizeUnit(fileFinishMatcher.group(2));
                                    setSize(Float.parseFloat(fileFinishMatcher.group(1)));
                                    finishDownload();
                                }
                            }

                        } else {

                        }

                    });
                }

                return null;
            }

        };

        Thread backgroundThread = new Thread(downloadTask);
        backgroundThread.start();

    }

    public void stopDownload() {

        if (! getStatus().equals("Stopped")) {
            setStatus("Stopped");
            ytdlProcess.destroy();
            setSpeed(0);
            setEta("");
        }
    }

    public void finishDownload() {

        setStatus("Finished");
        ytdlProcess.destroy();
        setSpeed(0);
        setEta("");
    }

}
