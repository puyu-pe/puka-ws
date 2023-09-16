package pe.puyu.controller;

import java.net.URI;
import java.net.URISyntaxException;
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
import pe.puyu.service.BifrostService;
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
    errors.addAll(BifrostValidator.validateUrlBifrost(txtUrlBifrost.getText()));
    errors.addAll(BifrostValidator.validateRuc(txtRuc.getText()));
    errors.addAll(BifrostValidator.validateBranch(txtBranch.getText()));
    errors.addAll(BifrostValidator.validateNamespace(txtNamespace.getText()));
    if (errors.isEmpty()) {
      try {
        closeWindow(event);
        BifrostService bifrostService = new BifrostService(new URI("http://localhost:3001/printing-12345678910-8"));
        bifrostService.start();
      } catch (URISyntaxException e) {
      } 
    } else {
      PukaAlerts.showWarning("Configuración invalida detectada.", String.join("\n", errors));
    }
  }

  @FXML
  void onCancel(ActionEvent event) {
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
