package pe.puyu.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pe.puyu.service.bifrost.BifrostService;
import pe.puyu.util.PukaAlerts;

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
  }

  @FXML
  void onReprint(ActionEvent event) {
    try {
      boolean result = PukaAlerts.showConfirmation("¿Seguro que deseas reemprimir estos tickets?",
          "Esta accion no es reversible");
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

  private void onUpdateNumberItemsQueue(int numberItemsQueue) {
    Platform.runLater(() -> {
      lblNumberItemsQueue.setText("" + numberItemsQueue);
    });
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

}
