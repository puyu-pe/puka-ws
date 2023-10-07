package pe.puyu.service.trayicon;

import org.slf4j.LoggerFactory;

import com.dustinredmond.fxtrayicon.FXTrayIcon;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import pe.puyu.controller.ActionPanelController;
import pe.puyu.service.bifrost.BifrostService;
import pe.puyu.util.PukaUtil;

public class PrintServiceTrayIcon {
  private FXTrayIcon trayIcon;
  private BifrostService bifrostService;
  private MenuItem enableLogsMenuItem;
  private Stage parentStage;

  public PrintServiceTrayIcon(BifrostService bifrostService) throws Exception {
    this.enableLogsMenuItem = new MenuItem("Activar Logs");
    this.enableLogsMenuItem.setOnAction(this::onClickEnableLogs);
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
        .menuItem(enableLogsMenuItem)
        .build();
  }

  private void onClickCopyLogsDirectoryToClipboard(ActionEvent event) {
    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    content.putString(PukaUtil.getLogsDirectory());
    clipboard.setContent(content);
  }

  private void onClickEnableLogs(ActionEvent event) {
    Logger rootLogger = (Logger) LoggerFactory.getLogger("pe.puyu");
    var logsAreEnabled = rootLogger.getLevel() == Level.TRACE;
    var menuItemText = logsAreEnabled ? "Activar Logs" : "Desactivar Logs";
    var level = logsAreEnabled ? Level.INFO : Level.TRACE;
    enableLogsMenuItem.setText(menuItemText);
    rootLogger.setLevel(level);
  }

  private void onInfoMessageBifrost(String title, String message) {
    trayIcon.showInfoMessage(title, message);
  }

  private void onErrorMessageBifrost(String title, String message) {
    trayIcon.showErrorMessage(title, message);
  }

}
