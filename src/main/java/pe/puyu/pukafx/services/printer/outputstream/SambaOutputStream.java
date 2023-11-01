package pe.puyu.pukafx.services.printer.outputstream;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import pe.puyu.pukafx.services.printer.interfaces.Cancelable;
import pe.puyu.pukafx.services.printer.interfaces.Caughtable;

import java.io.*;

public class SambaOutputStream extends PipedOutputStream implements Cancelable, Caughtable {
	protected final PipedInputStream pipedInputStream;
	protected final Thread threadPrint;
	protected final Logger logger = (Logger) LoggerFactory.getLogger("pe.puyu.puka.service.printer");

	/**
	 *resourcePath ejemplo: "\\\\192.168.1.53\\jpuka"
	 */
	public SambaOutputStream(String resourcePath) throws IOException {
		pipedInputStream = new PipedInputStream();
		super.connect(pipedInputStream);
		Thread.UncaughtExceptionHandler uncaughtException = (t, e) -> logger.error("Excepcion no controlada en SambaOuputStream: {}", e.getMessage(), e);

		Runnable runnablePrint = () -> {
			try {
				File smbFile = new File(resourcePath);
				try (OutputStream outputStream = new FileOutputStream(smbFile)) {
					byte[] buf = new byte[1024];
					while (true) {
						int n = pipedInputStream.read(buf);
						if (n < 0)
							break;
						outputStream.write(buf, 0, n);
					}
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}

		};

		threadPrint = new Thread(runnablePrint);
		threadPrint.setUncaughtExceptionHandler(uncaughtException);
		threadPrint.start();

	}

	public void setUncaughtException(Thread.UncaughtExceptionHandler uncaughtException) {
		threadPrint.setUncaughtExceptionHandler(uncaughtException);
	}

	@Override
	public void cancel() {
		try {
			this.close();
		} catch (IOException ignored) {
		}
	}
}
