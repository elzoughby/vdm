import com.google.gson.Gson;
import javafx.application.Platform;
import org.hildan.fxgson.FxGson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;


public class DataHandler {

    private static final String OS_NAME = Main.OS_NAME;
    private static final String SEPARATOR = Main.SEPARATOR;
    private static final String USER_HOME = Main.USER_HOME;

    private static Gson gson = FxGson.coreBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private static String appDataDirectory;
    private static HashMap<String, Object> appPreferences ;
    static {

        // set VDM AppData directory path based on the user OS
        if(OS_NAME.contains("win"))
            appDataDirectory = System.getenv("AppData").replaceAll("[/\\\\]$", "") + SEPARATOR + "vdm";
        else if(OS_NAME.contains("mac"))
            appDataDirectory = USER_HOME + SEPARATOR + "Library" + SEPARATOR + "Preferences" + SEPARATOR + "vdm";
        else
            appDataDirectory = USER_HOME + SEPARATOR + ".vdm";

        // Fill the appPreferences with default values
        appPreferences = new HashMap<>();
        appPreferences.put("Main.width", 800d);                         //Double
        appPreferences.put("Main.height", 500d);                        //Double
        appPreferences.put("Home.hideLog", Boolean.FALSE);                 //Boolean
        appPreferences.put("Home.dividerPosition", 0.8d);               //Double
        appPreferences.put("Data.nextID", 0L);                          //Long
        appPreferences.put("AES.date", new Date().toString());             //String
        appPreferences.put("AES.nanoTime", System.nanoTime());             //Long
        appPreferences.put("TrayHandled.clipboardMonitor", Boolean.TRUE);  //Boolean
        appPreferences.put("TrayHandled.runAtStartup", Boolean.FALSE);     //Boolean

    }
    private static final String DATA_DIRECTORY = appDataDirectory + SEPARATOR + "data";
    private static final String CONFIG_FILE = appDataDirectory + SEPARATOR + "config.dat";


    public static String getAppDataDirectory() {
        return appDataDirectory;
    }

    public static HashMap<String, Object> getAppPreferences() {
        return appPreferences;
    }

    public static void readAppPreferences() {

        File configFile = new File(CONFIG_FILE);

        if(configFile.exists()) {

            try {
                FileInputStream fileInputStream = new FileInputStream(configFile);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);

                if(objectInputStream.readUTF().equals("_VDM_")) {

                    appPreferences.replace("Main.width", objectInputStream.readDouble());
                    appPreferences.replace("Main.height", objectInputStream.readDouble());
                    appPreferences.replace("Home.hideLog", objectInputStream.readBoolean());
                    appPreferences.replace("Home.dividerPosition", objectInputStream.readDouble());
                    appPreferences.replace("Data.nextID", objectInputStream.readLong());
                    appPreferences.replace("AES.date", objectInputStream.readUTF());
                    appPreferences.replace("AES.nanoTime", objectInputStream.readLong());
                    appPreferences.replace("TrayHandled.clipboardMonitor", objectInputStream.readBoolean());
                    appPreferences.replace("TrayHandled.runAtStartup", objectInputStream.readBoolean());

                } else {
                    throw new Exception("configuration file is corrupted");
                }

                objectInputStream.close();
                bufferedInputStream.close();
                fileInputStream.close();

            } catch (Exception e) {
                Platform.runLater(() -> new MessageDialog("Error loading configuration file\n" +
                        "Try again later or report this issue", MessageDialog.Type.ERROR,
                        MessageDialog.Buttons.CLOSE).createErrorDialog(e.getStackTrace()).showAndWait());

            }

        }

    }

    public  static void writeAppPreferences() {

        File configFile = new File(CONFIG_FILE);

        try {

            FileOutputStream fileOutputStream = new FileOutputStream(configFile);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream);

            objectOutputStream.writeUTF("_VDM_");

            objectOutputStream.writeDouble( (Double) appPreferences.get("Main.width"));
            objectOutputStream.writeDouble( (Double) appPreferences.get("Main.height"));
            objectOutputStream.writeBoolean( (Boolean) appPreferences.get("Home.hideLog"));
            objectOutputStream.writeDouble( (Double) appPreferences.get("Home.dividerPosition"));
            objectOutputStream.writeLong( (Long) appPreferences.get("Data.nextID"));
            objectOutputStream.writeUTF( (String) appPreferences.get("AES.date"));
            objectOutputStream.writeLong( (Long) appPreferences.get("AES.nanoTime"));
            objectOutputStream.writeBoolean( (Boolean) appPreferences.get("TrayHandled.clipboardMonitor"));
            objectOutputStream.writeBoolean( (Boolean) appPreferences.get("TrayHandled.runAtStartup"));

            objectOutputStream.close();
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            fileOutputStream.close();

        } catch (Exception e) {
            Platform.runLater(() -> new MessageDialog("Error writing the configuration file\n" +
                    "Try again later or report this issue", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(e.getStackTrace()).showAndWait());
        }

    }

    public static void loadSavedItems() {

        try {

            File dataDirectory = new File(DATA_DIRECTORY);

            if(! dataDirectory.exists())
                dataDirectory.mkdirs();
            File[] dataFiles = dataDirectory.listFiles();

            if(dataFiles != null) {

                for(File file : dataFiles) {
                    if(file.getName().endsWith(".json")) {

                        Item item = read(file);

                        if(item != null) {

                            if (item.getDone() == 100.0 && item.getStatus().equals("Finished"))
                                item.setStatus("Finished");
                            else
                                item.setStatus("Stopped");

                            if (item.getIsAddedToQueue())
                                HomeController.getQueueItemList().add(item);
                            else
                                HomeController.getItemList().add(item);

                        }

                    }
                }
            }

        } catch (Exception e) {
            new MessageDialog("Error loading download items! \n" +
                    "Try again later or report this issue", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(e.getStackTrace()).showAndWait();
        }

    }

    public static Item read(File jsonFile) throws Exception {

        FileReader fileReader = new FileReader(jsonFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        return gson.fromJson(bufferedReader, Item.class);

    }

    public static void save(Item item) {

        try {

            File dataDirectory = new File(DATA_DIRECTORY);
            if(! dataDirectory.exists())
                dataDirectory.mkdirs();

            Path path = Paths.get(DATA_DIRECTORY + SEPARATOR + item.getId() + ".json");
            Files.write(path, gson.toJson(item).getBytes());

        } catch (IOException e) {
            e.printStackTrace();
            new MessageDialog("Error saving item data! \n" +
                    "Try again later or report this issue", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(e.getMessage()).showAndWait();
        }

    }

    public static void delete(Item item) {

        try {

            String path = DATA_DIRECTORY + SEPARATOR + item.getId() + ".json";
            File file = new File(path);
            if(! file.delete())
                throw new Exception("file" + item.getId() + ".json could not be deleted");

        } catch (Exception e) {
            new MessageDialog("Error deleting item from database! \n" +
                    "Try again later or report this issue", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(e.getMessage()).showAndWait();
        }

    }

    public static long getNextId() {

        Long nextID = (Long) getAppPreferences().get("Data.nextID");
        getAppPreferences().replace("Data.nextID", nextID + 1);
        writeAppPreferences();
        return nextID;

    }

}
