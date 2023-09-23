package pe.puyu.service.trayicon;

import org.slf4j.LoggerFactory;

import com.dustinredmond.fxtrayicon.FXTrayIcon;

import ch.qos.logback.classic.Logger;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import pe.puyu.service.bifrost.BifrostService;

public class PrintServiceTrayIcon {
  private FXTrayIcon trayIcon;
  private BifrostService bifrostService;
  private MenuItem releaseQueueMenuItem;
  private static final Logger logger = (Logger) LoggerFactory.getLogger("pe.puyu.service.trayicon");

  public PrintServiceTrayIcon(Stage stage, BifrostService bifrostService) {
    this.bifrostService = bifrostService;
    this.releaseQueueMenuItem = new MenuItem("liberar cola de impresión: 0");
    this.releaseQueueMenuItem.setOnAction(this::onClickReleaseQueue);
    this.bifrostService.addUpdateItemsQueueListener(this::onUpdateNumberItemsQueue);
    trayIcon = new FXTrayIcon.Builder(stage, getClass().getResource("/assets/icon.png"))
        .menuItem(releaseQueueMenuItem)
        .build();
  }

  public void onUpdateNumberItemsQueue(int numberItemsQueue) {
    releaseQueueMenuItem.setText("liberar cola de impresión: " + numberItemsQueue);
  }

  public void onClickReleaseQueue(ActionEvent e) {
    bifrostService.requestToReleaseQueue();
  }

  public void show() {
    trayIcon.show();
    logger.trace("El trayicon se hace visible");
  }

}
