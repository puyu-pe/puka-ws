package pe.puyu.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class UserConfigController {

    @FXML
    private Button btnAccept;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSelectLogo;

    @FXML
    private CheckBox checkBoxConfigServer;

    @FXML
    private ImageView imgViewLogo;

    @FXML
    private TextField txtBranch;

    @FXML
    private TextField txtNamespace;

    @FXML
    private TextField txtRuc;

    @FXML
    private TextField txtUrlBifrost;

    @FXML
    void onAccept(ActionEvent event) {

    }

    @FXML
    void onCancel(ActionEvent event) {
      System.exit(0);
    }

    @FXML
    void onSelectLogo(ActionEvent event) {

    }

}

