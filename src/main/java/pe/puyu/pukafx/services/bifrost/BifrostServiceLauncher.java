package pe.puyu.pukafx.services.bifrost;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import pe.puyu.pukafx.model.BifrostConfig;

import java.net.URI;
import java.util.Optional;

public class BifrostServiceLauncher {
  private final String uriStr;
  private final Logger logger = (Logger) LoggerFactory.getLogger("pe.puyu.puka.service.bifrost");

  public BifrostServiceLauncher(BifrostConfig bifrostConfig) {
    uriStr = makeUriConnection(bifrostConfig);
    logger.trace("BifrostServiceLauncher genero la siguiente uri de conección: {}", uriStr);
  }

  private String makeUriConnection(BifrostConfig config) {
    var uri = config.getUrlBifrost() + "/" + config.getNamespace() + "-" + config.getRuc();
    if (!config.getBranch().isEmpty())
      uri += ("-" + config.getBranch());
    return uri;
  }

  public Optional<BifrostService> buildBifrostService() {
    try {
      var bifrostService = new BifrostService(new URI(uriStr));
      return Optional.of(bifrostService);
    } catch (Exception e) {
      logger.error("Excepción al lanzar el servicio de bifrost: {}", e.getMessage(), e);
      return Optional.empty();
    }
  }
}
