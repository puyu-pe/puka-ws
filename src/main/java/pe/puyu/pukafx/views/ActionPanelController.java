package pe.puyu.pukafx.views;

import ch.qos.logback.classic.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import pe.puyu.pukafx.model.BifrostConfig;
import pe.puyu.pukafx.model.UserConfig;
import pe.puyu.pukafx.services.bifrost.BifrostService;
import pe.puyu.pukafx.util.JsonUtil;
import pe.puyu.pukafx.util.PukaAlerts;
import pe.puyu.pukafx.util.PukaUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ActionPanelController implements Initializable {
  private static final Logger logger = (Logger) LoggerFactory.getLogger("pe.puyu.puka.views.actionPanel");
  private BifrostService bifrostService;

  public void initBifrostService(BifrostService bifrostService) {
    try {
      if (this.bifrostService == null) {
        this.bifrostService = bifrostService;
        this.bifrostService.addUpdateItemsQueueListener(this::onUpdateNumberItemsQueue);
        this.bifrostService.requestItemsQueue();
      }
    } catch (Exception e) {
      logger.error("Excepcion al inicilizar BifrostService: {}", e.getMessage(), e);
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initPerfilTab();
    lblVersion.setText(PukaUtil.getPukaVersion());
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
  private Label lblVersion;

  @FXML
  private Label lblBranch;

  @FXML
  private Label lblRuc;

  @FXML
  private ImageView imgLogo;

}
