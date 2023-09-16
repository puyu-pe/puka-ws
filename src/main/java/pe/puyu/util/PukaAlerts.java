package pe.puyu.util;

import javafx.scene.control.Alert;

public class PukaAlerts {

  public static void showWarning(String headerText, String contentText) {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle("Advertencia");
    alert.setHeaderText(headerText);
    alert.setContentText(contentText);
    alert.showAndWait();
  }
}
