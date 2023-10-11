package pe.puyu.service.printer;

import java.io.OutputStream;
import java.util.function.Consumer;

import org.json.JSONObject;

import pe.puyu.jticketdesing.core.SweetTicketDesing;
import pe.puyu.model.beans.UserConfig;
import pe.puyu.util.JsonUtil;
import pe.puyu.util.PukaUtil;

public class SweetTicketPrinter {
  private Runnable onSuccess;
  private Consumer<String> onError;

  private JSONObject ticket = new JSONObject();
  private JSONObject printerInfo = new JSONObject();
  private JSONObject data = new JSONObject();
  private JSONObject metadata = new JSONObject();

  public SweetTicketPrinter(JSONObject data) {
    this.data = data;
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
      onSuccess.run();
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
    if (printerInfo.has("backgroundInverted") && !printerInfo.isNull("backgroundInverted")) {
      metadata.put("backgroundInverted", printerInfo.getBoolean("backgroundInverted"));
    }
    if (printerInfo.has("nativeQR") && !printerInfo.isNull("nativeQR")) {
      metadata.put("nativeQR", printerInfo.getBoolean("nativeQR"));
    }
    if (printerInfo.has("charCodeTable") && !printerInfo.isNull("charCodeTable")) {
      metadata.put("charCodeTable", printerInfo.getString("charCodeTable"));
    }
    if (printerInfo.has("charSetName") && !printerInfo.isNull("charSetName")) {
      metadata.put("charSetName", printerInfo.getString("charSetName"));
    }
  }

  private OutputStream getOutputStreamByPrinterType() throws Exception {
    if(this.printerInfo.isNull("name_system"))
      throw new Exception("name_system esta vacio");
    var name_system = this.printerInfo.getString("name_system");
    var port = this.printerInfo.getInt("port");
    var outputStream = Printer.getOutputStreamFor(name_system, port, this.printerInfo.getString("type"));
    Printer.setOnUncaughtExceptionFor(outputStream,
        error -> onError.accept(makeErrorMessageForException(new Exception(error))));
    return outputStream;
  }

  private String makeErrorMessageForException(Exception e) {
    return String.format("Error al imprimir un ticket, name_system: %s, port: %d, type: %s, mensaje error: %s",
        printerInfo.getString("name_system"), printerInfo.getInt("port"), printerInfo.getString("type"),
        e.getMessage());
  }

}
