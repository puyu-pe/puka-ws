package pe.puyu.service.bifrost;

import java.net.URI;
import java.util.Optional;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class BifrostServiceLauncher {
  private String uriStr;
  private final Logger logger = (Logger) LoggerFactory.getLogger("pe.puyu.service");

  public BifrostServiceLauncher(String urlBifrost, String namespace, String ruc, String branch) {
    uriStr = makeUriConnection(urlBifrost, namespace, ruc, branch);
    logger.trace("BifrostServiceLauncher genero la siguiente uri de conección: {}", uriStr);
  }

  private String makeUriConnection(String urlBifrost, String namespace, String ruc, String branch) {
    var uri = urlBifrost + "/" + namespace + "-" + ruc;
    if (!branch.isEmpty())
      uri += ("-" + branch);
    return uri;
  }

  public Optional<BifrostService> tryStart() {
    try {
      var bifrostService = new BifrostService(new URI(uriStr));
      bifrostService.start();
      return Optional.ofNullable(bifrostService);
    } catch (Exception e) {
      logger.error("Excepción al lanzar el servicio de bifrost: {}", e.getMessage(), e);
      return Optional.empty();
    }
  }
}
