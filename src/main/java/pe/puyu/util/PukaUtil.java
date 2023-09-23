package pe.puyu.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

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
    File file = new File(Path.of(getUserDataDir(), "config.json").toString());
    if (!file.exists()) {
      file.createNewFile();
    }
    return file.getAbsolutePath();
  }
}
