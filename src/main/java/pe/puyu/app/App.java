package pe.puyu.app;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pe.puyu.model.BifrostConfig;
import pe.puyu.service.bifrost.BifrostServiceLauncher;
import pe.puyu.service.trayicon.PrintServiceTrayIcon;
import pe.puyu.util.JsonUtil;
import pe.puyu.util.PukaUtil;
import pe.puyu.validations.BifrostValidator;

public class App extends Application {

  private static final Logger rootLogger = (Logger) LoggerFactory.getLogger("pe.puyu");
  private Optional<BifrostConfig> bifrostConfig = Optional.empty();

  @Override
  public void init() {
    rootLogger.setLevel(Level.TRACE);
    var config = rebuildBifrostConfig();
    if (config.isPresent()) {
      bifrostConfig = config;
    }
  }

  @Override
  public void start(Stage stage) {
    try {
      if (bifrostConfig.isEmpty()) {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/user-config.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Configuración de cliente servicio de impresión");
        stage.show();
      } else {
        var service = new BifrostServiceLauncher(bifrostConfig.get()).tryStart();
        if (service.isPresent()) {
          new PrintServiceTrayIcon(service.get()).show();
        }
      }
    } catch (Exception e) {
      rootLogger.error("Excepción al iniciar puka: {}", e.getMessage(), e);
    }
  }

  @Override
  public void stop() {

  }

  public static void main(String[] args) {
    launch(args);
  }

  private Optional<BifrostConfig> rebuildBifrostConfig() {
    try {
      Optional<BifrostConfig> config = JsonUtil.convertFromJson(PukaUtil.getBifrostConfigFileDir(),
          BifrostConfig.class);
      if (config.isEmpty())
        return Optional.empty();
      List<String> errors = new LinkedList<>();
      errors.addAll(BifrostValidator.validateUrlBifrost(config.get().getUrlBifrost()));
      errors.addAll(BifrostValidator.validateNamespace(config.get().getNamespace()));
      errors.addAll(BifrostValidator.validateRuc(config.get().getRuc()));
      errors.addAll(BifrostValidator.validateBranch(config.get().getBranch()));
      if (!errors.isEmpty())
        return Optional.empty();
      return config;
    } catch (IOException e) {
      rootLogger.error("Excepción al reconstruir la configuración de bifrost: {}", e.getMessage(), e);
      return Optional.empty();
    }
  }
}
