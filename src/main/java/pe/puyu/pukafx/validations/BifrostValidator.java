package pe.puyu.pukafx.validations;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class BifrostValidator {

  public static List<String> validateUrlBifrost(String urlBifrost) {
    List<String> errors = new LinkedList<>();
    urlBifrost = urlBifrost.trim();
    if (urlBifrost.isEmpty()) {
      errors.add("La url no debe ser un campo vacio");
    }
    var urlPattern = Pattern.compile("^(https?://).+[^/]$", Pattern.MULTILINE);
    var urlMatcher = urlPattern.matcher(urlBifrost);
    boolean isValidUrl = urlMatcher.matches();
    if (!isValidUrl) {
      errors.add("La url no tiene un valor valido correcto");
    }
    try {
      new URI(urlBifrost);
    } catch (URISyntaxException e) {
      errors.add("La url tiene errores de sintaxis");
    }
    return errors;
  }

  public static List<String> validateRuc(String ruc) {
    List<String> errors = new LinkedList<>();
    ruc = ruc.trim();
    if (ruc.isEmpty()) {
      errors.add("El ruc no debe ser un campo vacio");
    }
    var rucPattern = Pattern.compile("^\\d{11}$", Pattern.MULTILINE);
    var rucMatcher = rucPattern.matcher(ruc);
    boolean isValidRuc = rucMatcher.matches();
    if (!isValidRuc) {
      errors.add("El ruc debe tener 11 digitos numericos");
    }
    return errors;
  }

  public static List<String> validateNamespace(String namespace) {
    List<String> errors = new LinkedList<>();
    namespace = namespace.trim();
    if (namespace.isEmpty()) {
      errors.add("El namespace no debe ser un campo vacio");
    }
    var namespacePattern = Pattern.compile("^[a-zA-Z]+:?[a-zA-Z]+$", Pattern.MULTILINE);
    var namespaceMatcher = namespacePattern.matcher(namespace);
    boolean isValidNamespace = namespaceMatcher.matches();
    if (!isValidNamespace) {
      errors.add("El namespace no tiene un formato correcto");
    }
    return errors;
  }

  public static List<String> validateBranch(String branch) {
    List<String> errors = new LinkedList<>();
    branch = branch.trim();
    var branchPattern = Pattern.compile("^\\d$", Pattern.MULTILINE);
    var branchMatcher = branchPattern.matcher(branch);
    boolean isValidBranch = branchMatcher.matches();
    if (!branch.isEmpty() && !isValidBranch) {
      errors.add("El número de sucursal solo debe contener un digito numerico");
    }
    return errors;
  }
}
