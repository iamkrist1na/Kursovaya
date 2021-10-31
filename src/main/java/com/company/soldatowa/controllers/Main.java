package com.company.soldatowa.controllers;

import com.company.soldatowa.config.application.ApplicationConfig;
import com.company.soldatowa.database.ScriptsUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.company.soldatowa.services.Parser;
import com.company.soldatowa.services.Recognition;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    private static String recognitionResult = "";

    private static String userData = "";

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
        Main.userData = parseTags();
        Main.recognitionResult = Recognition.recognition(Main.userData);
        Optional<ButtonType> selectedButton = Optional.empty();
        switch (recognitionResult) {
            case "0":
                selectedButton = createRecognitionDialog("Recognition result is FALSE (0)", false);
                break;
            case "1":
                selectedButton = createRecognitionDialog("Recognition result is TRUE (1)", false);
                break;
            case "-":
                selectedButton = createRecognitionDialog("Failed to recognize the object", true);
                break;
        }
        selectedButton.ifPresent(this::setActionAfterButtonClick);
    }

    private void setActionAfterButtonClick(ButtonType selectedButton) {
        if (selectedButton.equals(ButtonType.OK)) {
            // do nothing
            return;
        }
        String result = "";
        if (Main.recognitionResult.equals("0")) {
            result = "FALSE";
        } else if (Main.recognitionResult.equals("1")) {
            result = "TRUE";
        }
        try {
            if (selectedButton.getText().equals(ButtonType.YES.getText())) {
                ScriptsUtils.writeNewLine(Main.userData, result.toLowerCase());
            } else if (selectedButton.getText().equalsIgnoreCase(ButtonType.NO.getText())) {
                ScriptsUtils.deleteLine(Main.userData, result.toLowerCase());
            }
        } catch (IOException writeException) {
            Logger.getGlobal().log(
                    Level.SEVERE,
                    "Can't write or delete row in" + ApplicationConfig.pathToUpdateScript + writeException.getMessage()
            );
        }
    }

    private Optional<ButtonType> createRecognitionDialog(String recognitionResultMessage, boolean recognitionFailed) {
        Dialog<ButtonType> responseAlert = new Dialog<>();
        responseAlert.setTitle("Recognition Result");
        responseAlert.setContentText(recognitionResultMessage);
        if (recognitionFailed) {
            responseAlert.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
        } else {
            setDefaultButtonsFor(responseAlert);
        }
        return responseAlert.showAndWait();
    }

    private void setDefaultButtonsFor(Dialog<ButtonType> dialog) {
        ButtonType YES = new ButtonType(ButtonType.YES.getText(), ButtonBar.ButtonData.YES);
        ButtonType NO = new ButtonType(ButtonType.NO.getText(), ButtonBar.ButtonData.NO);
        dialog.getDialogPane().getButtonTypes().add(YES);
        dialog.getDialogPane().getButtonTypes().add(NO);
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
