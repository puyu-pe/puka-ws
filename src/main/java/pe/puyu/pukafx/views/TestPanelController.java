package pe.puyu.pukafx.views;

import ch.qos.logback.classic.Logger;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPos.CharacterCodeTable;
import com.github.anastaciocintra.escpos.EscPos.CutMode;
import com.github.anastaciocintra.escpos.EscPosConst.Justification;
import com.github.anastaciocintra.escpos.Style.FontSize;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import pe.puyu.jticketdesing.util.escpos.EscPosWrapper;
import pe.puyu.jticketdesing.util.escpos.StyleWrapper;
import pe.puyu.pukafx.model.PrinterConnection;
import pe.puyu.pukafx.model.TicketInfo;
import pe.puyu.pukafx.services.printer.Printer;
import pe.puyu.pukafx.services.printer.SweetTicketPrinter;
import pe.puyu.pukafx.services.printingtest.PrintTestService;
import pe.puyu.pukafx.util.JsonUtil;
import pe.puyu.pukafx.util.PukaUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class TestPanelController implements Initializable {
	private final Logger logger = (Logger) LoggerFactory.getLogger("pe.puyu.puka.views.testpanel");
	private final TicketInfo ticketInfo = new TicketInfo();
	private final PrinterConnection printerConnection = new PrinterConnection();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ticketInfo.widthProperty().bind(Bindings.createIntegerBinding(() -> {
			try {
				return Integer.parseInt(txtCharacterPerLine.getText());
			} catch (Exception e) {
				return 42;
			}
		}, txtCharacterPerLine.textProperty()));

		ticketInfo.fontSizeCommandProperty().bind(Bindings.createIntegerBinding(() -> {
			try {
				return Integer.parseInt(txtFontSizeCommand.getText());
			} catch (Exception e) {
				return 2;
			}
		}, txtFontSizeCommand.textProperty()));

		printerConnection.portProperty().bind(Bindings.createIntegerBinding(() -> {
			try {
				return Integer.parseInt(txtPort.getText());
			} catch (Exception e) {
				return 9100;
			}
		}, txtPort.textProperty()));

		ticketInfo.charCodeTableProperty().bind(Bindings.createStringBinding(() -> {
			if (checkBoxCharCodeTable.isSelected())
				return cmbCharCodeTable.getValue();
			return null;
		}, cmbCharCodeTable.valueProperty(), checkBoxCharCodeTable.selectedProperty()));

		ticketInfo.nativeQRProperty().bind(checkBoxNativeQR.selectedProperty());
		ticketInfo.backgroundInvertedProperty().bind(checkBoxInvertedText.selectedProperty());
		ticketInfo.textNormalizeProperty().bind(checkBoxNormalize.selectedProperty());
		printerConnection.name_systemProperty().bind(txtNameSystem.textProperty());
		printerConnection.typeProperty().bind(cmbTypeConnection.valueProperty());
		reloadPrintServices();
		initTypesConnectionList();
		initCharCodeTableList();
		initTypeDocumentList();
		initDefaultValues();
	}

	@FXML
	void onClickBtnTest() {
		Platform.runLater(() -> {
			var name_system = printerConnection.getName_system();
			var port = printerConnection.getPort();
			var type = printerConnection.getType();
			try (var outputStream = Printer.getOutputStreamFor(name_system, port, type)) {
				Printer.setOnUncaughtExceptionFor(outputStream, (t, e) -> showMessageAreaError(e.getMessage(), "error"));
				var escpos = new EscPos(outputStream);
				var width = ticketInfo.getWidth();
				escpos.setCharacterCodeTable(CharacterCodeTable.WPC1252);
				var escposWrapper = new EscPosWrapper(escpos, StyleWrapper.textBold());
				escposWrapper.toCenter("-- PUYU - PUKA --", width, FontSize._2);
				escposWrapper.toCenter("Esta es una prueba de impresión", width);
				escposWrapper.removeStyleBold();
				escposWrapper.printLine('-', width);
				escposWrapper.toLeft(String.format("name_system: %s", name_system), width);
				escposWrapper.toLeft(String.format("port:        %s", port), width);
				escposWrapper.toLeft(String.format("type:        %s", type), width);
				escposWrapper.toLeft(String.format("width:       %s", width), width);
				escposWrapper.printLine('-', width);
				escposWrapper.addStyleBold();
				escposWrapper.toCenter("Gracias, que tenga un buen dia.", width);
				escpos.feed(4);
				escpos.cut(CutMode.PART);
				showMessageAreaError("La prueba no lanzo ninguna excepcion.", "info");
			} catch (Exception e) {
				showMessageAreaError(String.format("Fallo la prueba: %s", e.getMessage()), "error");
				logger.error("Ocurrio una excepcion al realizar el test basico: {}", e.getMessage(), e);
			}
		});
	}

	@FXML
	void onClickCheckBoxCharCodeTable() {
		cmbCharCodeTable.setDisable(!checkBoxCharCodeTable.isSelected());
	}

	@FXML
	void onClickBtnPrint() {
		Platform.runLater(() -> {
			try {
				var ticket = PrintTestService.getTicketByTypeDocument(cmbTypeDocument.getValue());
				var printer = JsonUtil.toJSONObject(printerConnection);
				var properties = JsonUtil.toJSONObject(ticketInfo);
				ticket.put("printer", printer);
				ticket.getJSONObject("printer").put("properties", properties);
				if (checkBoxLogo.isSelected()) {
					PrintTestService.addLogoToTicket(ticket);
				}
				if (!checkBoxQrCode.isSelected()) {
					PrintTestService.removeQRToTicket(ticket);
				}
				var sweetTicketPrinter = new SweetTicketPrinter(ticket);
				sweetTicketPrinter.setOnUncaughtException(error -> showMessageAreaError(error, "error"));
				sweetTicketPrinter.printTicket();
				showMessageAreaError("La prueba no lanzo una excepcion.", "info");
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
				txtNameSystem.setText(selectedItem);
			}
		}
	}

	@FXML
	void onReloadPrintServices() {
		reloadPrintServices();
	}

	private void reloadPrintServices() {
		listViewServices.getItems().clear();
		listViewServices.getItems().addAll(PrintTestService.getPrintServices());
	}

	private void initTypesConnectionList() {
		var typesConnection = PrintTestService.getTypesConnection();
		cmbTypeConnection.getItems().addAll(typesConnection);
		cmbTypeConnection.setValue(typesConnection.get(0));
	}

	private void initCharCodeTableList() {
		var charCodeTableList = PrintTestService.getCharCodeTableList();
		cmbCharCodeTable.getItems().addAll(charCodeTableList);
		cmbCharCodeTable.setValue(charCodeTableList.get(0));
	}

	private void initTypeDocumentList() {
		var typeDocumentList = PrintTestService.getTypeDocumentsMap().keySet();
		cmbTypeDocument.getItems().addAll(typeDocumentList);
		var firstItem = typeDocumentList.stream().findFirst();
		firstItem.ifPresent(s -> cmbTypeDocument.setValue(s));
	}

	private void initDefaultValues() {
		txtPort.setText("9100");
		txtCharacterPerLine.setText("42");
		checkBoxInvertedText.setSelected(true);
		checkBoxNativeQR.setSelected(false);
		checkBoxCharCodeTable.setSelected(false);
		checkBoxNormalize.setSelected(false);
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
	private CheckBox checkBoxCharCodeTable;

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
	private TextField txtNameSystem;

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

	@FXML
	private CheckBox checkBoxNormalize;

	@FXML
	private TextField txtFontSizeCommand;
}
