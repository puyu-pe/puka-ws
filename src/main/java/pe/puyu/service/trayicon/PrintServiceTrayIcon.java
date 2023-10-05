package pe.puyu.service.trayicon;

import com.dustinredmond.fxtrayicon.FXTrayIcon;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import pe.puyu.controller.ActionPanelController;
import pe.puyu.service.bifrost.BifrostService;
import pe.puyu.util.PukaUtil;

public class PrintServiceTrayIcon {
  private FXTrayIcon trayIcon;
  private BifrostService bifrostService;
  private Stage parentStage;

  public PrintServiceTrayIcon(BifrostService bifrostService) throws Exception {
    this.bifrostService = bifrostService;
    loadStage();
    loadTrayIcon();
    this.bifrostService.setListenerInfoNotification(this::onInfoMessageBifrost);
    this.bifrostService.setListenerErrorNotification(this::onErrorMessageBifrost);
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
        .menuItem("Portapapeles Logs", this::onClickCopyLogsDirectoryToClipboard)
        .build();
  }

  private void onClickCopyLogsDirectoryToClipboard(ActionEvent event) {
    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    content.putString(PukaUtil.getLogsDirectory());
    clipboard.setContent(content);
  }

  private void onInfoMessageBifrost(String title, String message) {
    trayIcon.showInfoMessage(title, message);
  }

  private void onErrorMessageBifrost(String title, String message) {
    trayIcon.showErrorMessage(title, message);
  }

}
