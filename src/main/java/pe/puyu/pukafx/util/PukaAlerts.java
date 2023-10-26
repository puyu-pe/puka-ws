package pe.puyu.pukafx.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class PukaAlerts {

  public static void showWarning(String headerText, String contentText) {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle("Advertencia");
    alert.setHeaderText(headerText);
    alert.setContentText(contentText);
    alert.showAndWait();
  }

  public static boolean showConfirmation(String headerText, String contentText) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirmar acción");
    alert.setHeaderText(headerText);
    alert.setContentText(contentText);
    var result = alert.showAndWait();
    return result.isPresent() && result.get() == ButtonType.OK;
  }
}
