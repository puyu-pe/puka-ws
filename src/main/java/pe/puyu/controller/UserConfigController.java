package pe.puyu.controller;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import pe.puyu.service.BifrostServiceLauncher;
import pe.puyu.util.PukaAlerts;
import pe.puyu.validations.BifrostValidator;

public class UserConfigController implements Initializable {

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    txtUrlBifrost.setText("https://bifrost-io.puyu.pe");
    txtNamespace.setText("printing");
  }

  @FXML
  void onAccept(ActionEvent event) {
    List<String> errors = new LinkedList<>();
    var urlBifrost = txtUrlBifrost.getText().trim();
    var namespace = txtNamespace.getText().trim();
    var ruc = txtRuc.getText().trim();
    var branch = txtBranch.getText().trim();
    errors.addAll(BifrostValidator.validateUrlBifrost(urlBifrost));
    errors.addAll(BifrostValidator.validateNamespace(namespace));
    errors.addAll(BifrostValidator.validateRuc(ruc));
    errors.addAll(BifrostValidator.validateBranch(branch));
    if (errors.isEmpty()) {
      closeWindow(event);
      new BifrostServiceLauncher(urlBifrost, namespace, ruc, branch).tryStart();
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

  private void closeWindow(ActionEvent event) {
    Node source = (Node) event.getSource();
    Stage stage = (Stage) source.getScene().getWindow();
    stage.close();
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
