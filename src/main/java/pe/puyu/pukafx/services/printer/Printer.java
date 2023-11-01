package pe.puyu.pukafx.services.printer;

import pe.puyu.pukafx.services.printer.interfaces.Caughtable;
import pe.puyu.pukafx.services.printer.outputstream.SambaOutputStream;
import pe.puyu.pukafx.services.printer.outputstream.SerialStream;
import pe.puyu.pukafx.services.printer.outputstream.ServiceOutputStream;
import pe.puyu.pukafx.services.printer.outputstream.SocketOutputStream;

import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;

public class Printer {

  public enum Type {
    WINDOWS_USB("windows-usb"),
    LINUX_USB("linux-usb"),
    SAMBA("samba"),
    SERIAL("serial"),
    CUPS("cups"),
    SMBFILE("smbfile"),
    ETHERNET("ethernet");

    private final String value;

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
      throw new IllegalArgumentException(String.format("Tipo de conexión: %s, no soportado", value));
    }
  }

  public static OutputStream getOutputStreamFor(String name_system, int port, String typeConnection) throws Exception {
    var type = Type.fromValue(typeConnection);
	  return switch (type) {
		  case SMBFILE -> new SambaOutputStream(name_system);
		  case SERIAL -> new SerialStream(name_system);
		  case WINDOWS_USB, LINUX_USB, SAMBA, CUPS -> {
			  var printService = ServiceOutputStream.getPrintServiceByName(name_system);
			  yield new ServiceOutputStream(printService);
		  }
		  case ETHERNET -> new SocketOutputStream(name_system, port);
	  };
  }

  public static void setOnUncaughtExceptionFor(OutputStream outputStream, UncaughtExceptionHandler uncaughtException) {
		if(outputStream instanceof Caughtable){
			((Caughtable) outputStream).setUncaughtException(uncaughtException);
		}
  }

}
