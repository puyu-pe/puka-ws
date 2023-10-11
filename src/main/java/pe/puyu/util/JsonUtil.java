package pe.puyu.util;

import java.io.FileWriter;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.hildan.fxgson.FxGson;
import org.json.JSONObject;
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
    } catch (Exception e) {
      logger.error("Excepción al guardar un json: {}", e.getMessage(), e);
    }
  }

  public static <T> Optional<T> convertFromJson(String jsonFileDir, Class<T> objectClass) {
    Gson gson = FxGson.create();
    try {
      String jsonFromFile = new String(Files.readAllBytes(Paths.get(jsonFileDir)));
      return Optional.ofNullable(gson.fromJson(jsonFromFile, objectClass));
    } catch (Exception e) {
      logger.error("Excepción al convertir un objeto a json: {}", e.getMessage(), e);
      return Optional.empty();
    }
  }

  public static <T> JSONObject toJSONObject(T beanSource) {
    Gson gson = FxGson.create();
    return new JSONObject(gson.toJson(beanSource));
  }

  public static JSONObject getJsonFrom(URL jsonURL) throws Exception {
    var json = new String(Files.readAllBytes(Path.of(jsonURL.toURI())));
    return new JSONObject(json);
  }
}
