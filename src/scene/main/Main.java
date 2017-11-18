package scene.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import scene.controller.Controller;
import scene.utilFunction.StageSingleCase;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scene/fxml/Scene.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1000, 800);
        primaryStage.setTitle("EDI Envelope Auto Builder System");
        primaryStage.setScene(scene);
        // Set the application icon.
        primaryStage.getIcons().add(new Image("file:resource/images/title.png"));
        scene.getStylesheets().add(getClass().getResource("/css/stage.css").toExternalForm());
        primaryStage.show();
        StageSingleCase.setPrimaryStage(primaryStage);

        Controller controller = loader.getController();
        controller.initFormateBox();
        controller.setPrimaryStage(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
