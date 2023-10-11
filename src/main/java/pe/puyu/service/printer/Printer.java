package pe.puyu.service.printer;

import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.function.Consumer;

import com.github.anastaciocintra.output.PrinterOutputStream;
import com.github.anastaciocintra.output.TcpIpOutputStream;

public class Printer {

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

  public static OutputStream getOutputStreamFor(String name_system, int port, String typeConnection) throws Exception {
    var type = Type.fromValue(typeConnection);
    switch (type) {
      case WINDOWS_USB:
      case LINUX_USB:
      case SAMBA:
      case SERIAL:
      case CUPS:
        var printService = PrinterOutputStream.getPrintServiceByName(name_system);
        return new PrinterOutputStream(printService);
      case ETHERNET:
        return new TcpIpOutputStream(name_system, port);
      default:
        throw new Exception(String.format("Tipo de conexión: %s, no soportado", typeConnection));
    }
  }

  public static void setOnUncaughtExceptionFor(OutputStream outputStream, Consumer<String> onError) {
    UncaughtExceptionHandler uncaughtException = (Thread t, Throwable e) -> {
      onError.accept(e.getMessage());
    };
    if (outputStream instanceof PrinterOutputStream) {
      ((PrinterOutputStream) outputStream).setUncaughtException(uncaughtException);
      return;
    }
    if (outputStream instanceof TcpIpOutputStream) {
      ((TcpIpOutputStream) outputStream).setUncaughtException(uncaughtException);
      return;
    }
  }

}
