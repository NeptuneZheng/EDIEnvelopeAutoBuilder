package scene.service;

import javafx.beans.InvalidationListener;
import javafx.beans.NamedArg;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import scene.utilFunction.StageSingleCase;

/**
 * Created by ZHENGNE on 11/1/2017.
 */
public class ModalDialog {
    Button btn;
    public ModalDialog(Stage stg, String message) {

        Stage stage = new Stage();
//Initialize the Stage with type of modal
        stage.initModality(Modality.APPLICATION_MODAL);
//Set the owner of the Stage
        stage.initOwner(stg);
        stage.setTitle("Warrning!!");
        Group root = new Group();
//        Scene scene = new Scene(root, 320, 200, Color.BISQUE);
        Scene scene = new Scene(root, 320, 200);

        btn = new Button();

        Image image = new Image("/images/awkward.jpg", 60, 60,false,true);
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setX(200);
        imageView.setY(100);

        Text text = new Text(10,50,message);
        text.setWrappingWidth(300);
        text.setFont(new Font(15));
        text.setFill(Color.RED);

        root.getChildren().add(text);
        root.getChildren().add(imageView);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                stage.hide();

            }
        });

        btn.setLayoutX(100);
        btn.setLayoutY(80);
        btn.setText("OK");

//        root.getChildren().add(btn);
        stage.setScene(scene);
        stage.show();

    }
}
