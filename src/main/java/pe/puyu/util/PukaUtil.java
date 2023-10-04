package pe.puyu.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

public class PukaUtil {

  public static String getUserDataDir() {
    AppDirs appDirs = AppDirsFactory.getInstance();
    String userDataDir = appDirs.getUserDataDir("puka", "0.1.0", "puyu");
    File file = new File(userDataDir);
    if (!file.exists()) {
      file.mkdirs();
    }
    return userDataDir;
  }

  public static String getBifrostConfigFileDir() throws IOException {
    return getConfigFileDir("bifrost.json");
  }

  public static String getUserConfigFileDir() throws IOException {
    return getConfigFileDir("user.json");
  }

  public static String getConfigFileDir(String jsonFileName) throws IOException {
    File file = new File(Path.of(getUserDataDir(), jsonFileName).toString());
    if (!file.exists()) {
      file.createNewFile();
    }
    return file.getAbsolutePath();
  }

  public static String getPukaVersion() {
    try {
      BufferedReader reader = new BufferedReader(new FileReader("./VERSION"));
      String version = reader.readLine();
      reader.close();
      return version;
    } catch (IOException e) {
      return "0.1.0";
    }
  }

  public static Optional<File> showPngFileChooser(Stage parent) {
    FileChooser fileChooser = new FileChooser();
    FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
    fileChooser.getExtensionFilters().add(pngFilter);
    File selectFile = fileChooser.showOpenDialog(parent);
    return Optional.of(selectFile);
  }

  public static void toast(Stage stage, String text) {
    Popup popup = new Popup();
    Label message = new Label(text);
    message.setStyle("-fx-text-fill: #2cfc03; -fx-font-weight: bold; -fx-font-size: 16px");
    message.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.0), CornerRadii.EMPTY, Insets.EMPTY)));

    StackPane pane = new StackPane(message);
    pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 10px; -fx-background-radius: 5px;");
    popup.getContent().add(pane);
    popup.show(stage);
    PauseTransition delay = new PauseTransition(Duration.seconds(2));
    delay.setOnFinished(e -> popup.hide());
    delay.play();
  }

}
