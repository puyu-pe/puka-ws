package pe.puyu.util;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import org.hildan.fxgson.FxGson;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import ch.qos.logback.classic.Logger;

public class JsonUtil {
  private static final Logger logger = (Logger) LoggerFactory.getLogger("pe.puyu.util");

  public static <T> void saveJson(String jsonFileDir, T beanSource) {
    Gson gson = FxGson.create();
    try (FileWriter writer = new FileWriter(jsonFileDir)) {
      String jsonSource = gson.toJson(beanSource);
      writer.write(jsonSource);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static <T> Optional<T> convertFromJson(String jsonFileDir, Class<T> objectClass) {
    Gson gson = new Gson();
    try {
      String jsonFromFile = new String(Files.readAllBytes(Paths.get(jsonFileDir)));
      return Optional.of(gson.fromJson(jsonFromFile, objectClass));
    } catch (IOException e) {
      logger.error("Excepción al convertir un objeto a json: {}", e.getMessage(), e);
      return Optional.empty();
    }
  }
}
