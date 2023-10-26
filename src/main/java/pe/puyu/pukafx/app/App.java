package pe.puyu.pukafx.app;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pe.puyu.pukafx.model.BifrostConfig;
import pe.puyu.pukafx.services.bifrost.BifrostServiceLauncher;
import pe.puyu.pukafx.services.trayicon.PrintServiceTrayIcon;
import pe.puyu.pukafx.util.JsonUtil;
import pe.puyu.pukafx.util.PukaUtil;
import pe.puyu.pukafx.validations.BifrostValidator;

public class App extends Application {
  // Level error : TRACE DEBUG INFO WARN ERROR
  private static final Logger rootLogger = (Logger) LoggerFactory.getLogger("pe.puyu.puka");
  private BifrostConfig bifrostConfig = null;

  @Override
  public void init() {
    rootLogger.setLevel(Level.INFO);
    var config = rebuildBifrostConfig();
	  config.ifPresent(value -> bifrostConfig = value);
  }

  @Override
  public void start(Stage stage) {
    try {
      if (bifrostConfig == null) {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/pe/puyu/pukafx/views/user-config.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Configuración de cliente servicio de impresión");
        stage.show();
      } else {
        var service = new BifrostServiceLauncher(bifrostConfig).buildBifrostService();
        if (service.isPresent()) {
          new PrintServiceTrayIcon(service.get()).show();
          service.get().start();
        }
      }
    } catch (Exception e) {
      rootLogger.error("Excepción al iniciar puka: {}", e.getMessage(), e);
    }
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