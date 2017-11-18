package scene.utilFunction;

import javafx.stage.Stage;

/**
 * Created by ZHENGNE on 11/8/2017.
 */
public class StageSingleCase {
    private StageSingleCase() {}
    private  static Stage primaryStage;

    public static void setPrimaryStage(Stage primaryStage) {
        StageSingleCase.primaryStage = primaryStage;
    }

    public static Stage getInstance(){
        if(primaryStage==null){
            primaryStage = new Stage();
        }
        return primaryStage;
    }

}
