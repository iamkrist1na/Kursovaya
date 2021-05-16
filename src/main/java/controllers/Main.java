package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import services.Parser;
import services.Recognition;

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

    public void doAnalyse(ActionEvent event) throws IOException {
        loadCheckBoxList();
        String data = parseTags();
        boolean recognition = Recognition.recognition(data, 0, 0, 0);
    }

    private String parseTags() {
        Parser parser = new Parser();
        String result = parser.parse(tags);
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

    public void closeWindow(ActionEvent event) {
        closeButton.getScene().getWindow().hide();
    }
}
