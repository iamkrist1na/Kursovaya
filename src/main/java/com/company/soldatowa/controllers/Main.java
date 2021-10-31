package com.company.soldatowa.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import com.company.soldatowa.services.Parser;
import com.company.soldatowa.services.Recognition;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private final List<CheckBox> tags = new ArrayList<>();
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
    public Slider alpha;
    @FXML
    public Slider betta;
    @FXML
    public Slider gamma;
    @FXML
    private TextField alphaField;
    @FXML
    private TextField bettaField;
    @FXML
    private TextField gammaField;
    @FXML
    private Button closeButton;

    private static int alphaValue = 25;
    private static int bettaValue = 60;
    private static int gammaValue = 2;

    public static int getAlphaValue() {
        return alphaValue;
    }
    public static int getBettaValue() {
        return bettaValue;
    }
    public static int getGammaValue() {
        return gammaValue;
    }

    @FXML
    void initialize() {
        setListenersForSliders();
        alphaField.setText("25");
        bettaField.setText("60");
        gammaField.setText("2");
        analyse.setOnMouseClicked(event -> doAnalyse());
    }

    public void doAnalyse() {
        loadCheckBoxList();
        String data = parseTags();
        String recognition = Recognition.recognition(data);
    }

    private String parseTags() {
        String result = Parser.parse(tags);
        Logger.getGlobal().log(Level.INFO, "Parser result: " + result);
        return result;
    }

    private void loadCheckBoxList() {
        if (tags.size() < 10) {
            tags.clear();
            tags.add(tag1);
            tags.add(tag2);
            tags.add(tag3);
            tags.add(tag4);
            tags.add(tag5);
            tags.add(tag6);
            tags.add(tag7);
            tags.add(tag8);
            tags.add(tag9);
            tags.add(tag10);
        }
    }

    private void setListenersForSliders() {
        alpha.valueProperty().addListener((observableValue, number, t1) -> setTextForFields(alphaField, t1, "alphaValue"));
        betta.valueProperty().addListener((observableValue, number, t1) -> setTextForFields(bettaField, t1, "bettaValue"));
        gamma.valueProperty().addListener((observableValue, number, t1) -> setTextForFields(gammaField, t1, "gammaValue"));
    }

    private void setTextForFields(TextField textField, Number number, String numberValue) {
        String stringValue = String.valueOf(number.intValue());
        textField.setText(stringValue);
        switch (numberValue) {
            case "alphaValue":
                alphaValue = Integer.parseInt(stringValue);
                break;
            case "bettaValue":
                bettaValue = Integer.parseInt(stringValue);
                break;
            case "gammaValue":
                gammaValue = Integer.parseInt(stringValue);
                break;
        }
    }

    public void closeWindow(ActionEvent event) {
        closeButton.getScene().getWindow().hide();
    }
}
