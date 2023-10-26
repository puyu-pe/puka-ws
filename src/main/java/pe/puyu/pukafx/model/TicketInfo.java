package pe.puyu.pukafx.model;

import javafx.beans.property.*;

public class TicketInfo {
  private final BooleanProperty backgroundInverted;
  private final BooleanProperty nativeQR;
  private final BooleanProperty textNormalize;
  private final StringProperty charCodeTable;
  private final IntegerProperty width;
  private final IntegerProperty fontSizeCommand;

  public TicketInfo() {
    backgroundInverted = new SimpleBooleanProperty();
    nativeQR = new SimpleBooleanProperty();
    textNormalize = new SimpleBooleanProperty();
    charCodeTable = new SimpleStringProperty();
    width = new SimpleIntegerProperty();
    fontSizeCommand = new SimpleIntegerProperty();
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

  public void setTextNormalize(boolean textNormalize) {
    this.textNormalize.set(textNormalize);
  }

  public boolean getTextNormalize() {
    return textNormalize.get();
  }

  public BooleanProperty textNormalizeProperty() {
    return textNormalize;
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

  public void setWidth(int width) {
    this.width.set(width);
  }

  public int getWidth() {
    return width.get();
  }

  public IntegerProperty widthProperty() {
    return width;
  }

  public void setFontSizeCommand(int fontsize) {
    this.fontSizeCommand.set(fontsize);
  }

  public int getFonSizeCommand() {
    return fontSizeCommand.get();
  }

  public IntegerProperty fontSizeCommandProperty() {
    return fontSizeCommand;
  }

}
