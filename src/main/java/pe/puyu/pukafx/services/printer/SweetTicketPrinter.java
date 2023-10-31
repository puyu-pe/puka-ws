package pe.puyu.pukafx.services.printer;

import ch.qos.logback.classic.Logger;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import pe.puyu.jticketdesing.core.SweetTicketDesign;
import pe.puyu.pukafx.model.UserConfig;
import pe.puyu.pukafx.util.JsonUtil;
import pe.puyu.pukafx.util.PukaUtil;

import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class SweetTicketPrinter {
	private final JSONObject printerInfo;
	private final JSONObject ticket;
	private Consumer<String> onUncaughtException;
	private static final Logger logger = (Logger) LoggerFactory.getLogger("pe.puyu.sweetticketprinter");

	public SweetTicketPrinter(JSONObject ticket) {
		this.ticket = ticket;
		this.printerInfo = ticket.getJSONObject("printer");
		this.onUncaughtException = logger::error;
	}

	public boolean printTicket() throws Exception{
		return CompletableFuture.supplyAsync(() -> {
			try (OutputStream outputStream = getOutputStreamByPrinterType()) {
				loadMetadata();
				var bytes = new SweetTicketDesign(ticket).getBytes();
				outputStream.write(bytes);
				return true;
			} catch (Exception e) {
				onUncaughtException.accept(makeErrorMessage(e.getMessage()));
				return false;
			}
		}).orTimeout(1600, TimeUnit.MILLISECONDS).get();
	}

	public void setOnUncaughtException(Consumer<String> onUncaughtException) {
		this.onUncaughtException = onUncaughtException;
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
		Printer.setOnUncaughtExceptionFor(outputStream, makeUncaughtException());
		return outputStream;
	}

	private Thread.UncaughtExceptionHandler makeUncaughtException() {
		return (t, e) -> {
			logger.error("UncaughtException SweetTicketPrinter: {}, thread_name: {}, thread_state: {}",
				e.getMessage(), t.getName(), t.getState().name(), e);
			onUncaughtException.accept(makeErrorMessage(e.getMessage()));
		};
	}

	private String makeErrorMessage(String message) {
		return String.format("UncaughtException en name_system: %s, type: %s, port: %d, message: %s",
			printerInfo.getString("name_system"), printerInfo.getString("type"),
			printerInfo.getInt("port"), message);
	}

}
