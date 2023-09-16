package pe.puyu.app;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pe.puyu.service.BifrostService;

public class App extends Application {

  private static final Logger rootLogger = (Logger) LoggerFactory.getLogger("pe.puyu");
  private static final Logger logger = (Logger) LoggerFactory.getLogger("pe.puyu.app");

  @Override
  public void init() {
    rootLogger.setLevel(Level.TRACE);
  }

  @Override
  public void start(Stage stage) {
    // try {
    // BifrostService service = new BifrostService(new
    // URI("http://localhost:3001/printing-20605931546-8"));
    // service.start();
    // } catch (URISyntaxException e) {
    // logger.error("Excepción al iniciar BifrostService: {}", e.getMessage(), e);
    // }
    try {
      Parent root = FXMLLoader.load(getClass().getResource("/fxml/user-config.fxml"));
      Scene scene = new Scene(root);
      stage.setScene(scene);
      stage.setTitle("Configuración de cliente servicio de impresión");
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void stop() {

  }

  public static void main(String[] args) {
    launch(args);
  }
}
