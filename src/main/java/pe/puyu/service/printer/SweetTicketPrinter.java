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

  private JSONObject printerInfo = new JSONObject();
  private JSONObject ticket = new JSONObject();

  public SweetTicketPrinter(JSONObject ticket) {
    this.ticket = ticket;
    this.printerInfo = ticket.getJSONObject("printer");
    this.onSuccess = () -> System.out.println("on success not implemented: SweetTicketPrinter");
    this.onError = (error) -> System.out.println(error);
  }

  public void printTicket() {
    try {
      var outputStream = getOutputStreamByPrinterType();
      loadMetadata();
      outputStream.write(new SweetTicketDesing(ticket).getBytes());
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
    var metadata = new JSONObject();
    if (ticket.has("metadata") && !ticket.isNull("metadata")) {
      metadata = ticket.getJSONObject("metadata");
    }
    var userConfig = JsonUtil.convertFromJson(PukaUtil.getUserConfigFileDir(), UserConfig.class);
    if ((!metadata.has("logoPath") || metadata.isNull("logoPath")) && userConfig.isPresent()) {
      metadata.put("logoPath", userConfig.get().getLogoPath());
      ticket.put("metadata", metadata);
    }
  }

  private OutputStream getOutputStreamByPrinterType() throws Exception {
    if (this.printerInfo.isNull("name_system"))
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
