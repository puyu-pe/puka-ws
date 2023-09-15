package pe.puyu.service;

import java.net.URI;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;

//TODO: Implementar logs
public class BifrostService {
  Socket socket;
  URI uriBifrost;
  int attempsConnection;

  public BifrostService(URI uri) {
    IO.Options options = IO.Options.builder().build();
    uriBifrost = uri;
    attempsConnection = 0;
    socket = IO.socket(uri, options);
    startListiningEvents();
  }

  public void start() {
    socket.connect();
  }

  private void startListiningEvents() {
    socket.on("printer:send-printing-queue", this::onSendPrintingQueue);
    socket.on("printer:emit-item", this::onEmitItem);
    socket.on("printer:send-number-items-queue", this::onSendNumberItemsQueue);
    socket.on(Socket.EVENT_CONNECT, this::onConnected);
    socket.on(Socket.EVENT_CONNECT_ERROR, this::onConnectedError);
    socket.on(Socket.EVENT_DISCONNECT, this::onDisconnect);
  }

  private void onSendPrintingQueue(Object... args) {
    try {
      var response = new BifrostResponse((JSONObject) args[0]);
      if (response.getStatus().equalsIgnoreCase("success")) {
        System.out.println(response.getMessage());
        printItems(response.getData());
      }
    } catch (JSONException e) {
      System.out.println(String.format("Excepción al obtener cola de impresión: %s", e.getMessage()));
    }
  }

  private void onEmitItem(Object... args) {
    try {
      var response = new BifrostResponse((JSONObject) args[0]);
      System.out.println(response.getMessage());
      printItems(response.getData());
    } catch (JSONException e) {
      System.out.println(e.getMessage());
    }
  }

  private void onConnected(Object... args) {
    // TODO: Notify onConnectedSuccess
    attempsConnection = 0;
    System.out.println(String.format("Se establecio conexión con: %s", uriBifrost));
    requestToGetPrintingQueue();
  }

  private void onConnectedError(Object... args) {
    ++attempsConnection;
    System.out.println(
        String.format("Ocurrio un error en la conexión , reitento n° %d ..., excepcion: %s", attempsConnection,
            args[0]));
  }

  private void onDisconnect(Object... args) {
    System.out.println("El servicio ha sido desconectado");
  }

  private void onSendNumberItemsQueue(Object... args) {
    int numberItemsQueue = (int) args[0];
    // TODO: Notify onChangeNumberItemsQueue
    System.out.println(String.format("Se modifico el numero de elementos en cola: %d", numberItemsQueue));
  }

  private void emitPrintItem(String itemId) {
    try {
      JSONObject obj = new JSONObject();
      obj.put("key", itemId);
      socket.emit("printer:print-item", obj);
    } catch (JSONException e) {
      System.out.println(String.format("JSONException al emitir un elemento para imprimir: %s", e.getMessage()));
    }
  }

  public void requestToGetPrintingQueue() {
    socket.emit("printer:get-printing-queue");
  }

  public void requestToReleaseQueue() {
    socket.emit("printer:release-queue");
  }

  public void printItems(Map<String, JSONObject> data) {
    System.out.println(String.format("Se recibe %d elemento(s) para imprimir", data.size()));
    for (Map.Entry<String, JSONObject> entry : data.entrySet()) {
      var key = entry.getKey();
      var value = entry.getValue();
      try {
        if (!value.has("tickets")) {
          throw new Exception(String.format("La propiedad tickets no existe para el itemId %d", key));
        }
        var tickets = new JSONArray(value.getString("tickets"));
        for (int i = 0; i < tickets.length(); ++i) {
          var ticket = tickets.get(i);
          System.out.println(ticket);
          emitPrintItem(key);
        }
      } catch (JSONException e) {
        System.out.println(String.format("JSONException al imprimir un ticket: %s", e.getMessage()));
      } catch (Exception e) {
        System.out.println(String.format("Exception al imprimir un ticket: %s", e.getMessage()));
      }
    }
  }

}
