package pe.puyu.controller;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import pe.puyu.model.BifrostConfig;
import pe.puyu.service.bifrost.BifrostService;
import pe.puyu.service.bifrost.BifrostServiceLauncher;
import pe.puyu.service.trayicon.PrintServiceTrayIcon;
import pe.puyu.util.JsonUtil;
import pe.puyu.util.PukaAlerts;
import pe.puyu.util.PukaUtil;
import pe.puyu.validations.BifrostValidator;

public class UserConfigController implements Initializable {
  private final Logger logger = (Logger) LoggerFactory.getLogger("pe.puyu.controller");
  private final BifrostConfig bifrostConfig = new BifrostConfig();

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    txtUrlBifrost.setText("https://bifrost-io.puyu.pe");
    txtNamespace.setText("printing");
    bifrostConfig.urlBifrostProperty().bind(txtUrlBifrost.textProperty());
    bifrostConfig.rucProperty().bind(txtRuc.textProperty());
    bifrostConfig.namespaceProperty().bind(txtNamespace.textProperty());
    bifrostConfig.branchProperty().bind(txtBranch.textProperty());
  }

  @FXML
  void onAccept(ActionEvent event) {
    List<String> errors = new LinkedList<>();
    errors.addAll(BifrostValidator.validateUrlBifrost(bifrostConfig.getUrlBifrost()));
    errors.addAll(BifrostValidator.validateNamespace(bifrostConfig.getNamespace()));
    errors.addAll(BifrostValidator.validateRuc(bifrostConfig.getRuc()));
    errors.addAll(BifrostValidator.validateBranch(bifrostConfig.getBranch()));
    if (errors.isEmpty()) {
      closeWindow(event);
      persistBifrostConfig();
      Optional<BifrostService> service = new BifrostServiceLauncher(bifrostConfig).tryStart();
      if (service.isPresent()) {
        new PrintServiceTrayIcon(getStageFromEvent(event), service.get()).show();
      }
    } else {
      PukaAlerts.showWarning("Configuración invalida detectada.", String.join("\n", errors));
    }
  }

  @FXML
  void onCancel(ActionEvent event) {
    boolean result = PukaAlerts.showConfirmation("¿Seguro que deseas cancelar la configuración?",
        "No se guardara la configuración y no se iniciara el servicio de bifrost");
    if (result)
      System.exit(0);
  }

  @FXML
  void onSelectLogo(ActionEvent event) {

  }

  private void persistBifrostConfig() {
    try {
      JsonUtil.saveJson(PukaUtil.getBifrostConfigFileDir(), bifrostConfig);
    } catch (IOException e) {
      logger.error("Excepción al persistir la información en el archivo de configuración de bifrost: {}",
          e.getMessage(),
          e);
    }
  }

  private void closeWindow(ActionEvent event) {
    getStageFromEvent(event).close();
  }

  private Stage getStageFromEvent(ActionEvent event) {
    Node source = (Node) event.getSource();
    Stage stage = (Stage) source.getScene().getWindow();
    return stage;
  }

  @FXML
  private Button btnAccept;

  @FXML
  private Button btnCancel;

  @FXML
  private Button btnSelectLogo;

  @FXML
  private CheckBox checkBoxConfigServer;

  @FXML
  private ImageView imgViewLogo;

  @FXML
  private TextField txtBranch;

  @FXML
  private TextField txtNamespace;

  @FXML
  private TextField txtRuc;

  @FXML
  private TextField txtUrlBifrost;

}
