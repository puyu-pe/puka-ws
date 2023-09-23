package pe.puyu.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
    if(!file.exists()){
      file.createNewFile();
    }
    return file.getAbsolutePath();
  }

  public static Optional<File> showPngFileChooser(Stage parent) {
    FileChooser fileChooser = new FileChooser();
    FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
    fileChooser.getExtensionFilters().add(pngFilter);
    File selectFile = fileChooser.showOpenDialog(parent);
    return Optional.of(selectFile);
  }

}
