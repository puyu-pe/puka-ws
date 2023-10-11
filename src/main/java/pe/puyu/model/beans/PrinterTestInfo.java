package pe.puyu.model.beans;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PrinterTestInfo {
  private BooleanProperty backgroundInverted;
  private BooleanProperty nativeQR;
  private StringProperty charCodeTable;
  private StringProperty charSetName;
  private IntegerProperty width;
  private StringProperty type;
  private StringProperty name_system;
  private IntegerProperty port;

  public PrinterTestInfo() {
    backgroundInverted = new SimpleBooleanProperty();
    nativeQR = new SimpleBooleanProperty();
    charCodeTable = new SimpleStringProperty();
    charSetName = new SimpleStringProperty();
    width = new SimpleIntegerProperty();
    type = new SimpleStringProperty();
    port = new SimpleIntegerProperty();
    name_system = new SimpleStringProperty();
  }

  public void setBackgroundInverted(boolean backgroundInverted) {
    this.backgroundInverted.set(backgroundInverted);
  }

  public boolean getBackgroundInverted() {
    return backgroundInverted.get();
  }

  public BooleanProperty backgroundInvertedProperty() {
    return backgroundInverted;
  }

  public void setNativeQR(boolean nativeQR) {
    this.nativeQR.set(nativeQR);
  }

  public boolean getNativeQR() {
    return nativeQR.get();
  }

  public BooleanProperty nativeQRProperty() {
    return nativeQR;
  }

  public void setCharCodeTable(String charCodeTable) {
    this.charCodeTable.set(charCodeTable);
  }

  public String getCharCodeTable() {
    return charCodeTable.get();
  }

  public StringProperty charCodeTableProperty() {
    return charCodeTable;
  }

  public void setCharSetName(String charSetName) {
    this.charSetName.set(charSetName);
  }

  public String getCharSetName() {
    return charSetName.get();
  }

  public StringProperty charSetNameProperty() {
    return charSetName;
  }

  public void setWidth(int width) {
    this.width.set(width);
  }

  public int getWidth() {
    return width.get();
  }

  public IntegerProperty widthProperty() {
    return width;
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
