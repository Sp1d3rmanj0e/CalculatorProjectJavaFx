package hellofx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static String roomName = "Degrees";

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("calcDegrees.fxml"));
        primaryStage.setTitle("Unit Circle Calculator: " + roomName);
        primaryStage.setScene(new Scene(root, 777, 331));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void setRoomName(String name) {
        roomName = name;
    }

    public static String getRoomName() {
        return roomName;
    }

    public static void main(String[] args) {
        launch(args);
    }
}