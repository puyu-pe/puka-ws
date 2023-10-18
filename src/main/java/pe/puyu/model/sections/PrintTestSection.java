package pe.puyu.model.sections;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.json.JSONObject;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPos.CharacterCodeTable;
import com.github.anastaciocintra.output.PrinterOutputStream;

import pe.puyu.model.beans.TicketInfo;
import pe.puyu.service.printer.Printer;
import pe.puyu.util.JsonUtil;

public class PrintTestSection {

  public static String[] getPrintServices() {
    return PrinterOutputStream.getListPrintServicesNames();
  }

  public static List<String> getTypesConnection() {
    var types = Printer.Type.values();
    var list = new ArrayList<String>();
    for (var type : types) {
      list.add(type.getValue());
    }
    return list;
  }

  public static List<String> getCharSetNameList() {
    var list = new LinkedList<String>();
    var charsets = Charset.availableCharsets().keySet();
    for (var charset : charsets) {
      list.add(charset);
    }
    return list;
  }

  public static List<String> getCharCodeTableList() {
    var list = new LinkedList<String>();
    var charCodeTableList = CharacterCodeTable.values();
    for (var item : charCodeTableList) {
      list.add(item.toString());
    }
    return list;
  }

  public static byte[] customDesing(Consumer<EscPos> consumer) throws Exception {
    var buffer = new ByteArrayOutputStream();
    EscPos escPos = new EscPos(buffer);
    consumer.accept(escPos);
    escPos.close();
    return buffer.toByteArray();
  }

  public static Map<String, String> getTypeDocumentsMap() {
    var map = new LinkedHashMap<String, String>();
    map.put("COMANDA - SIMPLE", "comanda_simple.json");
    map.put("COMANDA - ADICIONAL", "comanda_adicional.json");
    map.put("ANULACION - SIMPLE", "anulacion_simple.json");
    map.put("ANULACION - ADICIONAL", "anulacion_adicional.json");
    map.put("BOLETA RESTAURANTE", "boleta_restaurante.json");
    map.put("BOLETA TRANSPORTE", "boleta_transporte.json");
    map.put("ENCOMIENDA", "encomienda.json");
    map.put("EXTRA RESTAURANTE", "extra_restaurante.json");
    map.put("EXTRAS", "extras.json");
    map.put("PRECUENTA", "precuenta.json");
    return map;
  }

  public static JSONObject getTicketByTypeDocument(String typeDocument) throws Exception {
    var typeDocumentList = getTypeDocumentsMap();
    var pathToJson = "/testPrinter/" + typeDocumentList.get(typeDocument);
    var jsonURL = TicketInfo.class.getResource(pathToJson);
    return JsonUtil.getJsonFrom(jsonURL);
  }

  public static void addLogoToTicket(JSONObject ticket) {
    if (!ticket.has("data"))
      return;
    if (!ticket.getJSONObject("data").has("business"))
      return;
    var business = ticket.getJSONObject("data").getJSONObject("business");
    if (!business.has("comercialDescription"))
      return;
    var comercialDescription = business.getJSONObject("comercialDescription");
    comercialDescription.put("type", "img");
  }

  public static void removeQRToTicket(JSONObject ticket) {
    if (!ticket.has("data"))
      return;
    ticket.getJSONObject("data").remove("stringQR");
  }

}
