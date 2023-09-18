package pe.puyu.app;

import java.io.IOException;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

  private static final Logger rootLogger = (Logger) LoggerFactory.getLogger("pe.puyu");

  @Override
  public void init() {
    rootLogger.setLevel(Level.TRACE);
  }

  @Override
  public void start(Stage stage) {
    try {
      Parent root = FXMLLoader.load(getClass().getResource("/fxml/user-config.fxml"));
      Scene scene = new Scene(root);
      stage.setScene(scene);
      stage.setTitle("Configuración de cliente servicio de impresión");
      stage.show();
    } catch (IOException e) {
    }
  }

  @Override
  public void stop() {

  }

  public static void main(String[] args) {
    launch(args);
  }
}
