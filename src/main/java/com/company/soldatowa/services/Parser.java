package com.company.soldatowa.services;

import com.company.soldatowa.database.DatabaseUtils;
import javafx.scene.control.CheckBox;

import java.util.List;

public abstract class Parser {
    public static String parse(List<CheckBox> tags) {
        StringBuilder sb = new StringBuilder();
        tags.forEach(tag -> {
            if (tag.isSelected()) {
                sb.append(1);
            } else {
                sb.append(0);
            }
        });
        return sb.toString();
    }

    public static String getBinaryValueForComplexInd(String alias) {
        String[] split = alias.split("\\|");
        StringBuilder result = new StringBuilder();
        for (int k = 0; k < getBinaryValueForSimpleInd(split[0]).length(); k++) {
            result.append(getBinaryValueForSimpleInd(split[0]).charAt(k));
            result.append(getBinaryValueForSimpleInd(split[1]).charAt(k));
            if (split.length == 3) {
                result.append(getBinaryValueForSimpleInd(split[2]).charAt(k));
            }
            if (k != getBinaryValueForSimpleInd(split[0]).length() - 1) {
                result.append(",");
            }
        }
        return result.toString();
    }

    public static String getBinaryValueForSimpleInd(String alias) {
        String[] split = alias.split("X");
        StringBuilder numberOfInd = new StringBuilder();
        for (int digitIndex = 1; digitIndex < split.length; digitIndex++) {
            numberOfInd.append(split[digitIndex]);
        }
        StringBuilder result = new StringBuilder();
        List<String> data = DatabaseUtils.selectAllData();
        for (String datum : data) {
            result.append(datum.charAt(Integer.parseInt(numberOfInd.toString()) - 1));
        }
        return result.toString();
    }
}
