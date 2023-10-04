package pe.puyu.util;

import java.io.OutputStream;
import java.util.function.Consumer;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPos.CharacterCodeTable;
import com.github.anastaciocintra.escpos.EscPos.CutMode;
import com.github.anastaciocintra.escpos.EscPosConst.Justification;
import com.github.anastaciocintra.escpos.Style.FontSize;
import com.github.anastaciocintra.output.PrinterOutputStream;
import com.github.anastaciocintra.output.TcpIpOutputStream;

public class TestPrinter {
  private static Consumer<String> onRuntimeError = (message) -> System.out
      .println("Error en ejecución en TestPrinter");

  public static void runTest(String name_system, String port, String connection)
      throws Exception {
    var type = SweetTicketPrinter.Type.fromValue(connection);
    OutputStream outputStream;
    switch (type) {
      case WINDOWS_USB:
      case LINUX_USB:
      case SERIAL:
      case CUPS:
      case SAMBA:
        var printService = PrinterOutputStream.getPrintServiceByName(name_system);
        var printerOutputStream = new PrinterOutputStream(printService);
        printerOutputStream.setUncaughtException(TestPrinter::uncaughtException);
        outputStream = printerOutputStream;
        break;
      case ETHERNET:
        var tcpIpOutputStream = new TcpIpOutputStream(name_system, Integer.parseInt(port));
        tcpIpOutputStream.setUncaughtException(TestPrinter::uncaughtException);
        outputStream = tcpIpOutputStream;
        break;
      default:
        throw new Exception("Tipo de conexion no soportado: " + connection);
    }
    printLayoutTest(outputStream, name_system, port, connection);
    outputStream.close();
  }

  private static void printLayoutTest(OutputStream outputStream, String name_system, String port, String connection)
      throws Exception {
    EscPos escPos = new EscPos(outputStream);
    escPos.setCharacterCodeTable(CharacterCodeTable.WPC1252);
    escPos.getStyle().setBold(true).setFontSize(FontSize._2, FontSize._2).setJustification(Justification.Center);
    escPos.writeLF("-- PUYU - PUKA --");
    escPos.getStyle().setFontSize(FontSize._1, FontSize._1);
    escPos.feed(1);
    escPos.writeLF("Esta es una prueba de impresion.");
    escPos.feed(1);
    escPos.getStyle().reset();
    escPos.writeLF(String.format("names_system: %s", name_system));
    escPos.writeLF(String.format("port: %s", port));
    escPos.writeLF(String.format("type: %s", connection));
    escPos.feed(1);
    escPos.getStyle().setJustification(Justification.Center);
    escPos.writeLF("Gracias, que tenga un buen dia.");
    escPos.feed(4);
    escPos.cut(CutMode.PART);
    escPos.close();
  }

  public static void setRuntimeError(Consumer<String> onRuntimeError) {
    TestPrinter.onRuntimeError = onRuntimeError;
  }

  private static void uncaughtException(Thread t, Throwable e) {
    onRuntimeError.accept(e.getMessage());
  }
}
