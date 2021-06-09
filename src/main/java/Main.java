import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = new FXMLLoader().load(Objects.requireNonNull(getClass().getResource("sample.fxml")));
        primaryStage.setTitle("Messenger");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setOnCloseRequest(event -> Controller.onStageClose());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
