import com.google.gson.Gson;
import org.hildan.fxgson.FxGson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;


public class DataHandler {

    private static final String DATABASE_NODE = "DataHandler";
    private static final String NEXT_ID = "nextID";
    private static String dataDirectory;
    private static Preferences programData = Preferences.userRoot().node(DATABASE_NODE);
    private static Gson gson = FxGson.coreBuilder().excludeFieldsWithoutExposeAnnotation().create();

    static {
        if(System.getProperty("os.name").toLowerCase().contains("win"))
            dataDirectory = System.getenv("AppData") + System.getProperty("file.separator") + "nazel" +
                    System.getProperty("file.separator") + "data";
        else if(System.getProperty("os.name").toLowerCase().contains("mac"))
            dataDirectory = System.getProperty("user.home") + System.getProperty("file.separator") + "Library" +
                    System.getProperty("file.separator") + "Preferences" + System.getProperty("file.separator") +
                    "nazel" + System.getProperty("file.separator") + "data";
        else
            dataDirectory = System.getProperty("user.home") + System.getProperty("file.separator") + ".nazel" +
                    System.getProperty("file.separator") + "data";
    }


    public static void load() {

        try {

            if(Files.notExists(Paths.get(dataDirectory)) || (! Files.isDirectory(Paths.get(dataDirectory))))
                Files.createDirectory(Paths.get(dataDirectory));
            File[] dataFiles = new File(dataDirectory).listFiles();

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

            if(Files.notExists(Paths.get(dataDirectory)) || (! Files.isDirectory(Paths.get(dataDirectory))))
                Files.createDirectory(Paths.get(dataDirectory));

            Path path = Paths.get(dataDirectory + System.getProperty("file.separator") + item.getId() + ".json");
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

            String path = dataDirectory + System.getProperty("file.separator") + item.getId() + ".json";
            File file = new File(path);
            if(! file.delete())
                throw new Exception("file" + item.getId() + ".json could not be deleted");

        } catch (Exception e) {
            new MessageDialog("Error deleting item from database! \n" +
                    "Try again later or report this issue", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(e.getMessage()).showAndWait();
        }

    }

    public static int getNextId() {

        int nextID = programData.getInt(NEXT_ID, 0);
        programData.putInt(NEXT_ID, nextID + 1);
        return nextID;

    }

}
