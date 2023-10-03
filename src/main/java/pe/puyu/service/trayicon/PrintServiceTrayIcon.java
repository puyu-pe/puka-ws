package pe.puyu.service.trayicon;

import com.dustinredmond.fxtrayicon.FXTrayIcon;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pe.puyu.controller.ActionPanelController;
import pe.puyu.service.bifrost.BifrostService;

public class PrintServiceTrayIcon {
  private FXTrayIcon trayIcon;
  private BifrostService bifrostService;
  private Stage parentStage;

  public PrintServiceTrayIcon(BifrostService bifrostService) throws Exception {
    this.bifrostService = bifrostService;
    loadStage();
    loadTrayIcon();
  }

  public void show() {
    trayIcon.show();
  }

  private void loadStage() throws Exception {
    this.parentStage = new Stage();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/actions-panel.fxml"));
    Parent root = loader.load();
    ActionPanelController controller = loader.getController();
    controller.initBifrostService(this.bifrostService);
    Scene scene = new Scene(root);
    parentStage.setScene(scene);
    parentStage.setTitle("Panel de acciones de puka");
  }

  private void loadTrayIcon() {
    trayIcon = new FXTrayIcon.Builder(parentStage, getClass().getResource("/assets/icon.png"))
        .build();
  }

}
