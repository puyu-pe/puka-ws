package pe.puyu.util;

import java.io.OutputStream;
import java.util.function.Consumer;

import org.json.JSONObject;

import com.github.anastaciocintra.output.PrinterOutputStream;
import com.github.anastaciocintra.output.TcpIpOutputStream;

import pe.puyu.jticketdesing.core.SweetTicketDesing;
import pe.puyu.model.UserConfig;

public class SweetTicketPrinter {
  private Runnable onSuccess;
  private Consumer<String> onError;

  private JSONObject ticket = new JSONObject();
  private JSONObject printerInfo = new JSONObject();
  private JSONObject data = new JSONObject();
  private JSONObject metadata = new JSONObject();

  public enum Type {
    WINDOWS_USB("windows-usb"),
    LINUX_USB("linux-usb"),
    SAMBA("samba"),
    SERIAL("serial"),
    CUPS("cups"),
    ETHERNET("ethernet");

    private String value;

    Type(String value) {
      this.value = value;
    }

    public String getValue() {
      return this.value;
    }

    public static Type fromValue(String value) {
      for (Type type : Type.values()) {
        if (type.value.equals(value)) {
          return type;
        }
      }
      throw new IllegalArgumentException("Tipo invalido de para enum Type: " + value);
    }
  }

  public SweetTicketPrinter(JSONObject data) {
    this.ticket = data.getJSONObject("data");
    this.printerInfo = data.getJSONObject("printer");
    this.onSuccess = () -> System.out.println("on success not implemented: SweetTicketPrinter");
    this.onError = (error) -> System.out.println(error);
  }

  public void printTicket() {
    try {
      loadMetadata();
      var outputStream = getOutputStreamByPrinterType();
      outputStream.write(new SweetTicketDesing(ticket, metadata).getBytes());
      outputStream.close();
      // onSuccess.run();
    } catch (Exception e) {
      onError.accept(makeErrorMessageForException(e));
    }
  }

  public SweetTicketPrinter setOnSuccess(Runnable onSuccess) {
    this.onSuccess = onSuccess;
    return this;
  }

  public SweetTicketPrinter setOnError(Consumer<String> onError) {
    this.onError = onError;
    return this;
  }

  private void loadMetadata() throws Exception {
    this.metadata.put("typeTicket", data.getString("type"));
    this.metadata.put("times", data.getInt("times"));
    if (printerInfo.has("width")) {
      this.metadata.put("maxTicketWidth", printerInfo.getInt("width"));
    }
    var userConfig = JsonUtil.convertFromJson(PukaUtil.getUserConfigFileDir(), UserConfig.class);
    if (userConfig.isPresent()) {
      metadata.put("logoPath", userConfig.get().getLogoPath());
    }
  }

  private OutputStream getOutputStreamByPrinterType() throws Exception {
    var typeConnection = Type.fromValue(this.printerInfo.getString("type"));
    var name_system = this.printerInfo.getString("name_system");
    var port = this.printerInfo.getInt("port");
    switch (typeConnection) {
      case WINDOWS_USB:
      case SAMBA:
      case SERIAL:
      case CUPS:
        var printService = PrinterOutputStream.getPrintServiceByName(name_system);
        var printerOutputStream = new PrinterOutputStream(printService);
        printerOutputStream.setUncaughtException(this::uncaughtException);
        return printerOutputStream;
      case ETHERNET:
        var tcpIpOutputStream = new TcpIpOutputStream(name_system, port);
        tcpIpOutputStream.setUncaughtException(this::uncaughtException);
        return tcpIpOutputStream;
      default:
        throw new Exception(String.format("Tipo de conexión: %s, no soportado", typeConnection));
    }
  }

  private String makeErrorMessageForException(Exception e) {
    return String.format("Error al imprimir un ticket, name_system: %s, port: %d, type: %s, mensaje error: %s",
        printerInfo.getString("name_system"), printerInfo.getInt("port"), printerInfo.getString("type"),
        e.getMessage());
  }

  private void uncaughtException(Thread t, Throwable e) {
    onError.accept(makeErrorMessageForException((Exception) e));
  }
}
