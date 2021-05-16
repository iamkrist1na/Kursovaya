package services;

import javafx.scene.control.CheckBox;

import java.util.List;

public class Parser {
    public String parse(List<CheckBox> tags) {
        StringBuilder sb = new StringBuilder();
        tags.forEach(tag -> {
            if(tag.isSelected()) {
                sb.append(1);
            } else {
                sb.append(0);
            }
        });
        return sb.toString();
    }
}
