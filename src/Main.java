import controller.ControllerUI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/ui.fxml"));
        Parent root = loader.load();

        ControllerUI controller = loader.getController();
        controller.setHostServices(getHostServices());

        primaryStage.setTitle("AstroBOT");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);

        primaryStage.setOnCloseRequest(event -> {
            controller.stopBot();
            Platform.exit();
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
