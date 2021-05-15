package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Main {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private CheckBox tag1;

    @FXML
    private CheckBox tag2;

    @FXML
    private CheckBox tag3;

    @FXML
    private CheckBox tag4;

    @FXML
    private CheckBox tag5;

    @FXML
    private CheckBox tag6;

    @FXML
    private CheckBox tag7;

    @FXML
    private CheckBox tag8;

    @FXML
    private CheckBox tag9;

    @FXML
    private CheckBox tag10;

    @FXML
    private Button analyse;

    @FXML
    private Slider alpha;

    @FXML
    private Slider betta;

    @FXML
    private Slider gamma;

    @FXML
    private TextField alphaField;

    @FXML
    private TextField bettaField;

    @FXML
    private TextField gammaField;

    @FXML
    private Button closeButton;
    public void doAnalyse(ActionEvent event) {

    }

    public void closeWindow(ActionEvent event) {
        closeButton.getScene().getWindow().hide();
    }
}
