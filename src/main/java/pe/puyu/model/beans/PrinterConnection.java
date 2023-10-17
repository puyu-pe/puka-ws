package pe.puyu.model.beans;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PrinterConnection {

  private StringProperty type;
  private StringProperty name_system;
  private IntegerProperty port;

  public PrinterConnection() {
    type = new SimpleStringProperty();
    port = new SimpleIntegerProperty();
    name_system = new SimpleStringProperty();
  }

  public void setType(String type) {
    this.type.set(type);
  }

  public String getType() {
    return this.type.get();
  }

  public StringProperty typeProperty() {
    return type;
  }

  public void setName_system(String name_system) {
    this.name_system.set(name_system);
  }

  public String getName_system() {
    return this.name_system.get();
  }

  public StringProperty name_systemProperty() {
    return this.name_system;
  }

  public void setPort(int port) {
    this.port.set(port);
  }

  public int getPort() {
    return this.port.get();
  }

  public IntegerProperty portProperty() {
    return this.port;
  }
}
