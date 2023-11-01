package pe.puyu.pukafx.services.printer.outputstream;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import ch.qos.logback.classic.Logger;
import pe.puyu.pukafx.services.printer.interfaces.Cancelable;
import pe.puyu.pukafx.services.printer.interfaces.Caughtable;

public class SocketOutputStream extends PipedOutputStream implements Cancelable, Caughtable {
	protected final PipedInputStream pipedInputStream;
	protected final Thread threadPrint;
	protected final Logger logger = (Logger) LoggerFactory.getLogger("pe.puyu.puka.service.printer");

	public SocketOutputStream(String host, int port) throws IOException {
		pipedInputStream = new PipedInputStream();
		super.connect(pipedInputStream);
		Thread.UncaughtExceptionHandler uncaughtException = (t, e) ->
			logger.error("Excepcion no controlada en SocketOutputStream: {}", e.getMessage(), e);

		Runnable runnablePrint = () -> {
			try (Socket socket = new Socket()) {
				socket.connect(new InetSocketAddress(host, port), 1600);
				OutputStream outputStream = socket.getOutputStream();
				byte[] buf = new byte[1024];
				while (true) {
					int n = pipedInputStream.read(buf);
					if (n < 0) break;
					outputStream.write(buf, 0, n);
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		};

		threadPrint = new Thread(runnablePrint);
		threadPrint.setUncaughtExceptionHandler(uncaughtException);
		threadPrint.start();
	}

	public void cancel() {
		try {
			this.close();
		} catch (IOException ignored) {
		}
	}

	public void setUncaughtException(Thread.UncaughtExceptionHandler uncaughtException) {
		threadPrint.setUncaughtExceptionHandler(uncaughtException);
	}
}
