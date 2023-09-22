package pe.puyu.service.trayicon;

import com.dustinredmond.fxtrayicon.FXTrayIcon;

import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import pe.puyu.service.bifrost.BifrostService;

public class PrintServiceTrayIcon {
  private FXTrayIcon trayIcon;
  private BifrostService bifrostService;
  private MenuItem releaseQueueMenuItem;

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

  public void onClickReleaseQueue(ActionEvent e){
    bifrostService.requestToReleaseQueue();
  }

  public void show() {
    trayIcon.show();
  }

}
