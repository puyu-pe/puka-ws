package pe.puyu.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.LoggerFactory;
import com.github.anastaciocintra.output.PrinterOutputStream;

import ch.qos.logback.classic.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pe.puyu.model.BifrostConfig;
import pe.puyu.model.UserConfig;
import pe.puyu.service.bifrost.BifrostService;
import pe.puyu.util.JsonUtil;
import pe.puyu.util.PukaAlerts;
import pe.puyu.util.PukaUtil;
import pe.puyu.util.SweetTicketPrinter;
import pe.puyu.util.TestPrinter;

public class ActionPanelController implements Initializable {
  private static final Logger logger = (Logger) LoggerFactory.getLogger("pe.puyu.controller.actionPanel");
  private BifrostService bifrostService;

  public void initBifrostService(BifrostService bifrostService) {
    try {
      if (this.bifrostService == null) {
        this.bifrostService = bifrostService;
        this.bifrostService.addUpdateItemsQueueListener(this::onUpdateNumberItemsQueue);
        this.bifrostService.requestItemsQueue();
      }
    } catch (Exception e) {
      logger.error(String.format("Excepcion al inicilizar BifrostService: %s", e.getMessage(), e));
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initPerfilTab();
    initCmbTypeConnection();
    lblVersion.setText(PukaUtil.getPukaVersion());
    reloadPrintServices();
  }

  @FXML
  void onReprint(ActionEvent event) {
    try {
      boolean result = PukaAlerts.showConfirmation("¿Seguro que deseas reemprimir estos tickets?",
          "");
      if (result) {
        bifrostService.requestToGetPrintingQueue();
      }
    } catch (Exception e) {
      logger.error("Excepcion al reemprimir elementos en cola: {}", e.getMessage(), e);
    }
  }

  @FXML
  void onReleaseQueue(ActionEvent event) {
    try {
      boolean result = PukaAlerts.showConfirmation("¿Seguro que deseas liberar los tickets?",
          "Esta accion no es reversible");
      if (result) {
        this.bifrostService.requestToReleaseQueue();
      }
    } catch (Exception e) {
      logger.error("Excepcion al liberar cola de impresion: {}", e.getMessage(), e);
    }
  }

  @FXML
  void onHideWindow(ActionEvent event) {
    this.getStage().hide();
  }

  @FXML
  void onReloadPrintServices(ActionEvent event) {
    reloadPrintServices();
  }

  @FXML
  void onClickListView(MouseEvent event) {
    if (event.getClickCount() == 1) {
      String selectedItem = listViewServices.getSelectionModel().getSelectedItem();
      if (selectedItem != null) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(selectedItem);
        clipboard.setContent(content);
        PukaUtil.toast(getStage(), String.format("Se copio %s", selectedItem));
      }
    }
  }

  @FXML
  void onTestPrintService(ActionEvent event) {
    var name_system = cmbPrintService.getValue();
    var type = cmbTypeConnection.getValue();
    var port = txtPort.getText();
    try {
      txtInfoTestPrintService.setText("La prueba se ejecuto sin complicaciones.");
      if (name_system == null) {
        throw new Exception("El servicio de impresion es un campo obligatorio");
      }
      if (port.trim().isEmpty() && type.equalsIgnoreCase(SweetTicketPrinter.Type.ETHERNET.getValue())) {
        throw new Exception("El puerto es un campo obligatorio en ethernet");
      }
      txtInfoTestPrintService.setStyle("-fx-text-fill: #2cfc03;");
      txtInfoTestPrintService.setText("La prueba no lanzo una excepcion.");
      TestPrinter.setRuntimeError((error) -> {
        txtInfoTestPrintService.setStyle("-fx-text-fill: red;");
        txtInfoTestPrintService.setText(error);
        cmbPrintService.getItems().removeIf(value -> value == name_system);
      });
      cmbPrintService.getItems().add(name_system);
      TestPrinter.runTest(name_system, port, type);
    } catch (Exception e) {
      txtInfoTestPrintService.setStyle("-fx-text-fill: red;");
      txtInfoTestPrintService.setText(e.getMessage());
      cmbPrintService.getItems().removeIf(value -> value == name_system);
    }
  }

  private void onUpdateNumberItemsQueue(int numberItemsQueue) {
    Platform.runLater(() -> {
      lblNumberItemsQueue.setText("" + numberItemsQueue);
      btnReprint.setDisable(numberItemsQueue == 0);
      btnRelease.setDisable(numberItemsQueue == 0);
    });
  }

  private void initPerfilTab() {
    Platform.runLater(() -> {
      try {
        var userConfig = JsonUtil.convertFromJson(PukaUtil.getUserConfigFileDir(), UserConfig.class);
        var bifrostConfig = JsonUtil.convertFromJson(PukaUtil.getBifrostConfigFileDir(), BifrostConfig.class);
        if (userConfig.isPresent()) {
          File logoFile = new File(userConfig.get().getLogoPath());
          if (logoFile.exists()) {
            String imgUrl = logoFile.toURI().toURL().toString();
            imgLogo.setImage(new Image(imgUrl));
          }
        }

        if (bifrostConfig.isPresent()) {
          lblRuc.setText(bifrostConfig.get().getRuc());
          lblBranch.setText(bifrostConfig.get().getBranch());
        }

      } catch (IOException e) {
        lblRuc.setText("----------");
        lblBranch.setText("-");
        logger.error("Excepcion al iniacilizar la pestaña de perfil: {}", e.getMessage(), e);
      }
    });
  }

  private void reloadPrintServices() {
    String[] printServicesNames = PrinterOutputStream.getListPrintServicesNames();
    listViewServices.getItems().clear();
    for (String printServiceName : printServicesNames) {
      listViewServices.getItems().add(printServiceName);
    }
  }

  private void initCmbTypeConnection() {
    cmbTypeConnection.getItems().add(SweetTicketPrinter.Type.WINDOWS_USB.getValue());
    cmbTypeConnection.getItems().add(SweetTicketPrinter.Type.LINUX_USB.getValue());
    cmbTypeConnection.getItems().add(SweetTicketPrinter.Type.SAMBA.getValue());
    cmbTypeConnection.getItems().add(SweetTicketPrinter.Type.SERIAL.getValue());
    cmbTypeConnection.getItems().add(SweetTicketPrinter.Type.CUPS.getValue());
    cmbTypeConnection.getItems().add(SweetTicketPrinter.Type.ETHERNET.getValue());
    cmbTypeConnection.setValue(SweetTicketPrinter.Type.ETHERNET.getValue());
  }

  private Stage getStage() {
    return (Stage) root.getScene().getWindow();
  }

  @FXML
  private Label lblNumberItemsQueue;

  @FXML
  private GridPane root;

  @FXML
  private Button btnReprint;

  @FXML
  private Button btnRelease;

  @FXML
  private Button btnHide;

  @FXML
  private Label lblVersion;

  @FXML
  private Label lblBranch;

  @FXML
  private Label lblRuc;

  @FXML
  private ImageView imgLogo;

  @FXML
  private ListView<String> listViewServices;

  @FXML
  private ComboBox<String> cmbPrintService;

  @FXML
  private ComboBox<String> cmbTypeConnection;

  @FXML
  private TextArea txtInfoTestPrintService;

  @FXML
  private TextField txtPort;
}
