package pe.puyu.pukafx.services.bifrost;

import ch.qos.logback.classic.Logger;
import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import pe.puyu.pukafx.services.printer.SweetTicketPrinter;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BifrostService {
	private final Logger logger = (Logger) LoggerFactory.getLogger("pe.puyu.puka.service.bifrost");

	Socket socket;
	URI uriBifrost;
	int attemptsConnection;

	private final List<Consumer<Integer>> updateItemsQueueListeners;
	private final Executor executor;
	private BiConsumer<String, String> listenerInfo = (title, message) -> logger.info(String.format("%s: %s", title, message));
	private BiConsumer<String, String> listenerError = (title, message) -> logger.error(String.format("%s: %s", title, message));

	public BifrostService(URI uri) {
		updateItemsQueueListeners = new LinkedList<>();
		uriBifrost = uri;
		int numberOfThreads = Runtime.getRuntime().availableProcessors() * 2;
		listenerInfo.accept("Numero de hilos", "" + numberOfThreads);
		executor = Executors.newFixedThreadPool(numberOfThreads);
		reloadSocket();
	}

	public void reloadSocket() {
		attemptsConnection = 0;
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
			.readTimeout(8, TimeUnit.HOURS)
			.build();
		IO.Options options = new IO.Options();
		options.callFactory = okHttpClient;
		options.webSocketFactory = okHttpClient;
		if (socket != null) {
			socket.close();
		}
		socket = IO.socket(uriBifrost, options);
		startListeningEvents();
	}

	public void start() {
		socket.connect();
		logger.info("Se inicia el servicio de bifrost");
	}

	public void addUpdateItemsQueueListener(Consumer<Integer> callback) {
		this.updateItemsQueueListeners.add(callback);
	}

	private void startListeningEvents() {
		socket.on("printer:send-printing-queue", this::onSendPrintingQueue);
		socket.on("printer:emit-item", this::onEmitItem);
		socket.on("printer:send-number-items-queue", this::onSendNumberItemsQueue);
		socket.on(Socket.EVENT_CONNECT, this::onConnected);
		socket.on(Socket.EVENT_CONNECT_ERROR, this::onConnectedError);
		socket.on(Socket.EVENT_DISCONNECT, this::onDisconnect);
	}

	private void onSendPrintingQueue(Object... args) {
		CompletableFuture.runAsync(() -> {
			try {
				var response = new BifrostResponse((JSONObject) args[0]);
				if (response.getStatus().equalsIgnoreCase("success")) {
					logger.info("Llego cola de impresión de bifrost con el siguiente mensaje: {}", response.getMessage());
					printItems(response.getData());
				}
			} catch (JSONException e) {
				logger.error("Excepción al obtener cola de impresión: {}", e.getMessage(), e);
			}
		}, executor);
	}

	private void onEmitItem(Object... args) {
		CompletableFuture.runAsync(() -> {
			try {
				var response = new BifrostResponse((JSONObject) args[0]);
				logger.debug("Llego un item de bifrost para imprimir con el siguiente mensaje: {}", response.getMessage());
				printItems(response.getData());
			} catch (JSONException e) {
				logger.error("Excepción al lanzar el evento para emitir un item emit-item: {}", e.getMessage(), e);
			}
		}, executor);
	}

	private void onConnected(Object... args) {
		try {
			attemptsConnection = 0;
			requestToGetPrintingQueue();
			logger.info("Se establecio conexión con {}", uriBifrost);
			listenerInfo.accept("Conexión exitosa", "Puka recupero la conexión con el servidor");
		} catch (Exception e) {
			logger.error("Excepcion en onConnected: {}", e.getMessage(), e);
		}
	}

	private void onConnectedError(Object... args) {
		if (attemptsConnection % 5 == 0) {
			listenerError.accept("Conexión perdida", String.format("%s. Intentando conectarse al servidor intento: %s", args[0], attemptsConnection));
		}
		++attemptsConnection;
		logger.error("Ocurrio un error al intentar conectarse: {}, ...reintentando n° {}", args[0], attemptsConnection);
	}

	private void onDisconnect(Object... args) {
		var message = "";
		if (args.length > 0) message = args[0].toString();
		logger.info("El servicio a sido desconectado: {}", message);
	}

	private void onSendNumberItemsQueue(Object... args) {
		int numberItemsQueue = (int) args[0];
		for (var listener : updateItemsQueueListeners) {
			listener.accept(numberItemsQueue);
		}
		logger.debug("Se actualizo el numero de elementos en cola: {}", numberItemsQueue);
	}

	private void emitPrintItem(String itemId) {
		CompletableFuture.runAsync(() -> {
			try {
				JSONObject obj = new JSONObject();
				obj.put("key", itemId);
				socket.emit("printer:print-item", obj);
			} catch (JSONException e) {
				logger.error("Ocurrio una excepción en emit printer:print-item: {}", e.getMessage(), e);
			}
		}, executor);
	}

	public void requestToGetPrintingQueue() {
		socket.emit("printer:get-printing-queue");
		logger.info("Se solicita cola de impresión a bifrost");
	}

	public void requestToReleaseQueue() {
		socket.emit("printer:release-queue");
		logger.debug("Se solicita liberar cola de impresión a bifrost");
	}

	public void requestItemsQueue() {
		socket.emit("printer:request-items-queue");
		logger.debug("Se solicita items en cola");
	}

	public void setListenerInfoNotification(BiConsumer<String, String> callback) {
		this.listenerInfo = callback;
	}

	public void setListenerErrorNotification(BiConsumer<String, String> callback) {
		this.listenerError = callback;
	}

	private void printItems(Map<String, JSONObject> queue) {
		logger.debug("Se recibe {} items de bifrost", queue.size());
		for (Map.Entry<String, JSONObject> entry : queue.entrySet()) {
			var id = entry.getKey();
			var item = entry.getValue();
			if (!item.has("tickets"))
				continue;
			var tickets = new JSONArray(item.getString("tickets"));
			for (int i = 0; i < tickets.length(); ++i) {
				try {
					var ticket = tickets.getJSONObject(i);
					logger.trace("Se imprimira el siguiente ticket con id {}: {}", id, ticket);
					var sweetTicketPrinter = new SweetTicketPrinter(ticket);
					sweetTicketPrinter.setOnUncaughtException(error -> {
						logger.warn("UncaughtException al imprimir ticket con id: {}, error: {}", id, error);
						listenerError.accept("Error interno, hilo de impresión: ", error);
					});
					sweetTicketPrinter.printTicket();
					emitPrintItem(id);
				} catch (Exception e) {
					var message = String.format("Excepción al intentar imprimir ticket con id %s: %s", id, e.getMessage());
					logger.error(message, e);
					listenerError.accept("Fallo al imprimir un ticket", message);
				}
			}
		}
	}

}
