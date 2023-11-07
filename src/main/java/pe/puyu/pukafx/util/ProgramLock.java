package pe.puyu.pukafx.util;

import ch.qos.logback.classic.Logger;
import javafx.application.Platform;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;

public class ProgramLock {
	private static File file = null;
	private static FileChannel channel = null;
	private static FileLock lock = null;

	private static final Logger logger = (Logger) LoggerFactory.getLogger("pe.puyu.puka.lock");

	public static void lock() {
		if (lock != null) return;
		try {
			file = new File(Path.of(PukaUtil.getUserDataDir(), ".lockuyupakup").toString());
			//noinspection resource
			channel = new RandomAccessFile((file), "rw").getChannel();
			lock = channel.tryLock();
			if (lock == null) {
				logger.info("Intento de una segunda instancia de puka.");
				channel.close();
				Platform.exit();
			}

			Runtime.getRuntime().addShutdownHook(new Thread(ProgramLock::unLock));

		} catch (Exception e) {
			logger.error("Excepcion en lock puka: {}", e.getMessage(), e);
		}
	}

	public static void unLock() {
		try {
			if (lock != null) {
				lock.release();
				channel.close();
				var ignored = file.delete();
				lock = null;
			}
		} catch (Exception e) {
			logger.error("Excepcion en unlock puka: {}", e.getMessage(), e);
		}
	}
}
