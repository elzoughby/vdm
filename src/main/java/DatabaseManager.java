import com.google.gson.Gson;
import org.hildan.fxgson.FxGson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;


public class DatabaseManager {

    private static final String DATABASE_NODE = "DatabaseManager";
    private static final String NEXT_ID = "nextID";

    private static Preferences programData = Preferences.userRoot().node(DATABASE_NODE);
    private static Gson gson = FxGson.coreBuilder().excludeFieldsWithoutExposeAnnotation().create();


    public static void load() {

        try {
            File[] dataFiles = new File("data").listFiles();

            if(dataFiles != null) {

                for(File file : dataFiles) {
                    if(file.getName().endsWith(".json")) {

                        Item item = read(file);

                        if(item != null) {

                            if (item.getDone() == 100.0)
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
                    "Restart program and try again.", MessageDialog.Type.ERROR,
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

            Path path = Paths.get("data/" + item.getId() + ".json");
            Files.write(path, gson.toJson(item).getBytes());

        } catch (IOException e) {
            e.printStackTrace();
            new MessageDialog("Error saving item data! \n" +
                    "Restart program and try again.", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(e.getMessage()).showAndWait();
        }

    }

    public static void delete(Item item) {

        try {

            String path = "data/" + item.getId() + ".json";
            File file = new File(path);
            if(! file.delete())
                throw new Exception("file" + item.getId() + ".json could not be deleted");

        } catch (Exception e) {
            new MessageDialog("Error deleting item from database! \n" +
                    "Restart program and try again.", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(e.getMessage()).showAndWait();
        }

    }

    public static int getNextId() {

        int nextID = programData.getInt(NEXT_ID, 0);
        programData.putInt(NEXT_ID, nextID + 1);
        return nextID;

    }

}
