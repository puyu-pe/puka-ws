package pe.puyu.pukafx.services.trayicon;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import pe.puyu.pukafx.services.bifrost.BifrostService;
import pe.puyu.pukafx.util.PukaUtil;
import pe.puyu.pukafx.views.ActionPanelController;

import java.io.IOException;
import java.util.Objects;

public class PrintServiceTrayIcon {
  private FXTrayIcon trayIcon;
  private final BifrostService bifrostService;
  private final MenuItem enableLogsMenuItem;
  private Stage parentStage;
  private final Stage testPanelStage;
  private final Logger logger = (Logger) LoggerFactory.getLogger("pe.puyu.puka.service.trayicon");

  public PrintServiceTrayIcon(BifrostService bifrostService) throws Exception {
    this.enableLogsMenuItem = new MenuItem("Activar Logs");
    this.enableLogsMenuItem.setOnAction(this::onClickEnableLogs);
    this.bifrostService = bifrostService;
    this.testPanelStage = new Stage();
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
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/pe/puyu/pukafx/views/actions-panel.fxml"));
    Parent root = loader.load();
    ActionPanelController controller = loader.getController();
    controller.initBifrostService(this.bifrostService);
    Scene scene = new Scene(root);
    parentStage.setScene(scene);
    parentStage.setTitle("Panel de acciones de puka");
  }

  private void loadTrayIcon() {
    var logs = new MenuItem("Logs");
    logs.setOnAction(this::onClickLogs);
    var test = new MenuItem("Pruebas de impresión");
    test.setOnAction(this::onClickMenuItemTestPrinter);

    trayIcon = new FXTrayIcon.Builder(parentStage, Objects.requireNonNull(getClass().getResource("/pe/puyu/pukafx/assets/icon.png")))
        .menuItem("Refrescar servicio", this::onRefreshService)
        .menu("Mantenimiento", enableLogsMenuItem, logs, test)
        .menuItem("Configuración", this::onClickCopyUserDirectoryToClipboard)
        .build();
  }

  private void onRefreshService(ActionEvent event) {
    bifrostService.reloadSocket();
    bifrostService.start();
  }

  private void onClickLogs(ActionEvent event) {
    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    content.putString(PukaUtil.getLogsDirectory());
    clipboard.setContent(content);
  }

  private void onClickCopyUserDirectoryToClipboard(ActionEvent event) {
    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    content.putString(PukaUtil.getUserDataDir());
    clipboard.setContent(content);
  }

  private void onClickEnableLogs(ActionEvent event) {
    Logger rootLogger = (Logger) LoggerFactory.getLogger("pe.puyu.puka");
    var logsAreEnabled = rootLogger.getLevel() == Level.TRACE;
    var menuItemText = logsAreEnabled ? "Activar Logs" : "Desactivar Logs";
    var level = logsAreEnabled ? Level.INFO : Level.TRACE;
    enableLogsMenuItem.setText(menuItemText);
    rootLogger.setLevel(level);
  }

  private void onClickMenuItemTestPrinter(ActionEvent event) {
    try {
      Parent root = new FXMLLoader(getClass().getResource("/pe/puyu/pukafx/views/test-panel.fxml")).load();
      testPanelStage.setScene(new Scene(root));
      testPanelStage.setTitle("Pruebas de impresion PUKA");
      testPanelStage.show();
    } catch (IOException e) {
      logger.error("Error al abrir stage test printer: {}", e.getMessage(), e);
    }
  }

  private void onInfoMessageBifrost(String title, String message) {
    trayIcon.showInfoMessage(title, message);
  }

  private void onErrorMessageBifrost(String title, String message) {
    trayIcon.showErrorMessage(title, message);
  }

}
