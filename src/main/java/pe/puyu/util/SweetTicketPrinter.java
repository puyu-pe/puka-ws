package pe.puyu.util;

import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;

import org.json.JSONObject;

import com.github.anastaciocintra.output.TcpIpOutputStream;

import pe.puyu.jticketdesing.core.SweetTicketDesing;
import pe.puyu.model.UserConfig;

public class SweetTicketPrinter {

  private JSONObject ticket = new JSONObject();
  private JSONObject printerInfo = new JSONObject();
  private JSONObject data = new JSONObject();
  private JSONObject metadata = new JSONObject();

  public SweetTicketPrinter(JSONObject data) throws Exception {
    this.data = data;
    this.ticket = data.getJSONObject("data");
    this.printerInfo = data.getJSONObject("printer");
    loadMetadata();
  }

  public void printTicket() throws Exception {
    try {
      var outputStream = getOutputStreamByPrinterType();
      outputStream.write(new SweetTicketDesing(ticket, metadata).getBytes());
      outputStream.close();
    } catch (Exception e) {
      var error = String.format("Error al imprimir un ticket, name_system: %s, port: %d, type: %s, mensaje error: %s",
          printerInfo.getString("name_system"), printerInfo.getInt("port"), printerInfo.getString("type"),
          e.getMessage());
      throw new Exception(error);
    }
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
    var typeConnection = this.printerInfo.getString("type");
    switch (typeConnection) {
      case "ethernet":
        return new TcpIpOutputStream(this.printerInfo.getString("name_system"), this.printerInfo.getInt("port"));
      default:
        throw new Exception(String.format("Tipo de conexión: %s, no soportado", typeConnection));
    }
  }
}
