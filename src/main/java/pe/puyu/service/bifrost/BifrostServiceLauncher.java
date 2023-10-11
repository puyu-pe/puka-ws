package pe.puyu.service.bifrost;

import java.net.URI;
import java.util.Optional;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import pe.puyu.model.beans.BifrostConfig;

public class BifrostServiceLauncher {
  private String uriStr;
  private final Logger logger = (Logger) LoggerFactory.getLogger("pe.puyu.service.bifrost");

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
      return Optional.ofNullable(bifrostService);
    } catch (Exception e) {
      logger.error("Excepción al lanzar el servicio de bifrost: {}", e.getMessage(), e);
      return Optional.empty();
    }
  }
}
