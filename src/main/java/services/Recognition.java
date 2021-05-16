package services;


import database.DatabaseUtils;
import java.util.List;
import java.util.Optional;


public final class Recognition {
    public static boolean recognition(String data, int alpha, int betta, int gamma) {
        DataComparator dataComparator = new DataComparator();
        List<String> allData = DatabaseUtils.selectAllData();

        Optional<String> equalRow = allData
                .stream()
                .filter(row -> dataComparator.compare(row.split("/")[0], data) == 0)
                .findFirst();

        return equalRow
                .map(s -> s.split("/")[1].equals("TRUE"))
                .orElseGet(() -> smartRecognition(data, alpha, betta, gamma));

    }

    private static boolean smartRecognition(String data, int alpha, int betta, int gamma) {

        return true;
    }
}
