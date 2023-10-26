package pe.puyu.pukafx.services.printer;

import ch.qos.logback.classic.Logger;
import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;


public class SerialStream extends PipedOutputStream {

    private final PipedInputStream pipedInputStream;
    private final Thread threadPrint;
		protected final Logger logger = (Logger) LoggerFactory.getLogger("pe.puyu.puka.service.printer");

		/*
		 * portDescriptor example "com6"
		 */
    public SerialStream(String portDescriptor) throws IOException {
        pipedInputStream = new PipedInputStream();
        super.connect(pipedInputStream);
        Thread.UncaughtExceptionHandler uncaughtException = (t, e) -> logger.error("Excepcion no controlada en SambaOuputStream: {}", e.getMessage(), e);

        Runnable runnablePrint = () -> {
            try {
                SerialPort comPort = SerialPort.getCommPort(portDescriptor);
                if(!comPort.openPort()){
                    throw new IOException("Error on comPort.openPort call");
                }

	            try (OutputStream outputStream = comPort.getOutputStream()) {
		            byte[] buf = new byte[1024];
		            while (true) {
			            int n = pipedInputStream.read(buf);
			            if (n < 0) {
				            break;
			            }
			            outputStream.write(buf, 0, n);
		            }

	            } finally {
		            comPort.closePort();
	            }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        };

        threadPrint = new Thread(runnablePrint);
        threadPrint.setUncaughtExceptionHandler(uncaughtException);
        threadPrint.start();
    }
    /**
     * Set UncaughtExceptionHandler to make special error treatment.
     * <p>
     * Make special treatment of errors on your code.
     *
     * @param uncaughtException used on (another thread) print.
     */
    public void setUncaughtException(Thread.UncaughtExceptionHandler uncaughtException) {
        threadPrint.setUncaughtExceptionHandler(uncaughtException);
    }
}
