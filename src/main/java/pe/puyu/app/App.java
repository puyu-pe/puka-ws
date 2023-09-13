package pe.puyu.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {

  @Override
  public void init() {

  }

  @Override
  public void start(Stage stage) {
    Button btn = new Button("Hello word");

    StackPane root = new StackPane();
    root.getChildren().add(btn);

    Scene scene = new Scene(root, 600, 600);

    stage.setScene(scene);

    stage.setTitle("Hola Mundo JavaFX");

    stage.show();
  }

  @Override
  public void stop() {

  }

  public static void main(String[] args) {
    launch(args);
  }
}
