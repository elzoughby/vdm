import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;


public class LoadingController implements Initializable {

    @FXML
    AnchorPane pane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    protected void finalize() throws Throwable {
        DbManager.load();
        System.out.println("Finalize");
        close();
        super.finalize();
    }

    public void close() {



    }
}
