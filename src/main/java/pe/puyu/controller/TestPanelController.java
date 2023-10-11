package pe.puyu.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import org.slf4j.LoggerFactory;

import com.github.anastaciocintra.escpos.EscPos.CutMode;
import com.github.anastaciocintra.escpos.EscPosConst.Justification;
import com.github.anastaciocintra.escpos.Style.FontSize;

import ch.qos.logback.classic.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import pe.puyu.model.beans.PrinterTestInfo;
import pe.puyu.model.sections.PrintTestSection;
import pe.puyu.service.printer.Printer;
import pe.puyu.service.printer.SweetTicketPrinter;
import pe.puyu.util.JsonUtil;
import pe.puyu.util.PukaUtil;

public class TestPanelController implements Initializable {
  private final Logger logger = (Logger) LoggerFactory.getLogger("pe.puyu.controller.testpanel");
  private final PrinterTestInfo printerInfo = new PrinterTestInfo();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    printerInfo.widthProperty().bind(Bindings.createIntegerBinding(() -> {
      try {
        return Integer.parseInt(txtCharacterPerLine.getText());
      } catch (Exception e) {
        return 42;
      }
    }, txtCharacterPerLine.textProperty()));

    printerInfo.portProperty().bind(Bindings.createIntegerBinding(() -> {
      try {
        return Integer.parseInt(txtPort.getText());
      } catch (Exception e) {
        return 9100;
      }
    }, txtPort.textProperty()));

    printerInfo.charSetNameProperty().bind(Bindings.createStringBinding(() -> {
      if (checkBoxCharSetName.isSelected())
        return cmbCharSetName.getValue();
      return null;
    }, cmbCharSetName.valueProperty(), checkBoxCharSetName.selectedProperty()));

    printerInfo.charCodeTableProperty().bind(Bindings.createStringBinding(() -> {
      if (checkBoxCharCodeTable.isSelected())
        return cmbCharCodeTable.getValue();
      return null;
    }, cmbCharCodeTable.valueProperty(), checkBoxCharCodeTable.selectedProperty()));

    printerInfo.nativeQRProperty().bind(checkBoxNativeQR.selectedProperty());
    printerInfo.backgroundInvertedProperty().bind(checkBoxInvertedText.selectedProperty());
    printerInfo.name_systemProperty().bind(cmbPrintService.valueProperty());
    printerInfo.typeProperty().bind(cmbTypeConnection.valueProperty());
    reloadPrintServices();
    initTypesConnectionList();
    initCharCodeTableList();
    initCharSetNameList();
    initTypeDocumentList();
    initDefaultValues();
  }

  @FXML
  void onClickBtnTest(ActionEvent event) {
    CompletableFuture.runAsync(() -> {
      try {
        var name_system = cmbPrintService.getValue();
        var port = Integer.parseInt(txtPort.getText());
        var type = cmbTypeConnection.getValue();
        var outputStream = Printer.getOutputStreamFor(name_system, port, type);
        Printer.setOnUncaughtExceptionFor(outputStream, (error) -> {
          showMessageAreaError(error, "error");
        });
        var bytes = PrintTestSection.customDesing(escpos -> {
          try {
            escpos.getStyle().setBold(true).setFontSize(FontSize._2, FontSize._2)
                .setJustification(Justification.Center);
            escpos.writeLF("-- PUYU - PUKA --");
            escpos.getStyle().setFontSize(FontSize._1, FontSize._1);
            escpos.writeLF("Esta es una prueba de impresion");
            escpos.getStyle().reset();
            escpos.writeLF(String.format("name_system: %s", name_system));
            escpos.writeLF(String.format("port: %s", type));
            escpos.writeLF(String.format("type: %s", port));
            escpos.getStyle().setJustification(Justification.Center);
            escpos.writeLF("Gracias, que tenga un buen dia.");
            escpos.feed(4);
            escpos.cut(CutMode.PART);
          } catch (Exception e) {
            showMessageAreaError(e.getMessage(), "error");
          }
        });
        outputStream.write(bytes);
        outputStream.close();
        cmbPrintService.getItems().removeIf(item -> item.equalsIgnoreCase(printerInfo.getName_system()));
        cmbPrintService.getItems().add(printerInfo.getName_system());
        showMessageAreaError("La prueba no lanzo ninguna excepcion.", "info");
      } catch (Exception e) {
        showMessageAreaError(e.getMessage(), "error");
        logger.error("Ocurrio una excepcion al realizar el test basico: {}", e.getMessage(), e);
      }
    });
  }

  @FXML
  void onClickCheckBoxCharCodeTable(ActionEvent event) {
    if (checkBoxCharCodeTable.isSelected()) {
      cmbCharCodeTable.setDisable(false);
    } else {
      cmbCharCodeTable.setDisable(true);
    }
  }

  @FXML
  void onClickCheckBoxCharSetName(ActionEvent event) {
    if (checkBoxCharSetName.isSelected()) {
      cmbCharSetName.setDisable(false);
    } else {
      cmbCharSetName.setDisable(true);
    }
  }

  @FXML
  void onClickBtnPrint(ActionEvent event) {
    CompletableFuture.runAsync(() -> {
      try {
        var ticket = PrintTestSection.getTicketByTypeDocument(cmbTypeDocument.getValue());
        var printer = JsonUtil.toJSONObject(printerInfo);
        ticket.put("printer", printer);
        if (checkBoxLogo.isSelected()) {
          PrintTestSection.addLogoToTicket(ticket);
        }
        if (!checkBoxQrCode.isSelected()) {
          PrintTestSection.removeQRToTicket(ticket);
        }
        var sweetTicketPrinter = new SweetTicketPrinter(ticket);
        sweetTicketPrinter
            .setOnSuccess(() -> {
              showMessageAreaError("La prueba no lanzo una excepcion.", "info");
            })
            .setOnError(error -> {
              showMessageAreaError(error, "error");
            })
            .printTicket();
        cmbPrintService.getItems().removeIf(item -> item.equalsIgnoreCase(printerInfo.getName_system()));
        cmbPrintService.getItems().add(printerInfo.getName_system());
      } catch (Exception e) {
        showMessageAreaError(e.getMessage(), "error");
        logger.error("Excepcion al imprimir, pruebas avanzadas: {}", e.getMessage(), e);
      }
    });
  }

  @FXML
  void onClickListViewServices(MouseEvent event) {
    if (event.getClickCount() == 1) {
      String selectedItem = listViewServices.getSelectionModel().getSelectedItem();
      if (selectedItem != null) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(selectedItem);
        clipboard.setContent(content);
        PukaUtil.toast(getStage(), String.format("Se copio %s", selectedItem));
        cmbPrintService.setValue(selectedItem);
      }
    }
  }

  @FXML
  void onReloadPrintServices(ActionEvent event) {
    reloadPrintServices();
  }

  private void reloadPrintServices() {
    listViewServices.getItems().clear();
    listViewServices.getItems().addAll(PrintTestSection.getPrintServices());
  }

  private void initTypesConnectionList() {
    var typesConnection = PrintTestSection.getTypesConnection();
    cmbTypeConnection.getItems().addAll(typesConnection);
    cmbTypeConnection.setValue(typesConnection.get(0));
  }

  private void initCharCodeTableList() {
    var charCodeTableList = PrintTestSection.getCharCodeTableList();
    cmbCharCodeTable.getItems().addAll(charCodeTableList);
    cmbCharCodeTable.setValue(charCodeTableList.get(0));
  }

  private void initCharSetNameList() {
    var charSetNameList = PrintTestSection.getCharSetNameList();
    cmbCharSetName.getItems().addAll(charSetNameList);
    cmbCharSetName.setValue(charSetNameList.get(0));
  }

  private void initTypeDocumentList() {
    var typeDocumentList = PrintTestSection.getTypeDocumentsMap().keySet();
    cmbTypeDocument.getItems().addAll(typeDocumentList);
    cmbTypeDocument.setValue(typeDocumentList.stream().findFirst().get());
  }

  private void initDefaultValues() {
    txtPort.setText("9100");
    txtCharacterPerLine.setText("42");
    checkBoxInvertedText.setSelected(true);
    checkBoxNativeQR.setSelected(true);
    checkBoxCharCodeTable.setSelected(false);
    checkBoxCharSetName.setSelected(false);
  }

  private void showMessageAreaError(String message, String type) {
    switch (type) {
      case "info":
        txtErrorArea.setStyle("-fx-text-fill: #6afc65;");
        break;
      case "error":
        txtErrorArea.setStyle("-fx-text-fill: #fc8865;");
        break;
    }
    txtErrorArea.setText(message);
  }

  private Stage getStage() {
    return (Stage) root.getScene().getWindow();
  }

  @FXML
  private Button btnPrint;

  @FXML
  private Button btnTest;

  @FXML
  private CheckBox checkBoxCharCodeTable;

  @FXML
  private CheckBox checkBoxCharSetName;

  @FXML
  private CheckBox checkBoxInvertedText;

  @FXML
  private CheckBox checkBoxLogo;

  @FXML
  private CheckBox checkBoxNativeQR;

  @FXML
  private CheckBox checkBoxQrCode;

  @FXML
  private ComboBox<String> cmbCharCodeTable;

  @FXML
  private ComboBox<String> cmbCharSetName;

  @FXML
  private ComboBox<String> cmbPrintService;

  @FXML
  private ComboBox<String> cmbTypeConnection;

  @FXML
  private ComboBox<String> cmbTypeDocument;

  @FXML
  private TextField txtCharacterPerLine;

  @FXML
  private TextArea txtErrorArea;

  @FXML
  private TextField txtPort;

  @FXML
  private ListView<String> listViewServices;

  @FXML
  private TabPane root;
}
