package com.company.soldatowa.services;


import com.company.soldatowa.controllers.Main;
import com.company.soldatowa.database.DatabaseUtils;
import com.company.soldatowa.model.Metric;
import com.company.soldatowa.model.TransitionMatrix;
import com.company.soldatowa.model.TransitionMatrixForComplexInd;
import javafx.scene.control.Slider;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Анализ введенных пользователем данных:
 */
public final class Recognition {

    private static Map<String, String> indicesList = new HashMap<>();

    public static boolean recognition(String data) {
        indicesList = createIndices();
        DataComparator dataComparator = new DataComparator();
        List<String> allData = DatabaseUtils.selectAllData();

        Optional<String> equalRow = allData.stream()
                .filter(row -> dataComparator.compare(row.split("/")[0], data) == 0)
                .findFirst();

        return equalRow.map(s -> s.split("/")[1].equals("TRUE")).orElseGet(Recognition::smartRecognition);

    }

    private static boolean smartRecognition() {
        System.out.println(filteringByBetta());
        return true;
    }

    private static List<TransitionMatrixForComplexInd> filteringByBetta() {
        List<TransitionMatrixForComplexInd> matrices = createTransitionMatricesForComplexInd();
        double bettaPercent = CalculateInformative.informativeY * Main.getBettaValue() / 100;
        return matrices.stream()
                .filter(matrix -> matrix.getInformative() >= bettaPercent)
                .collect(Collectors.toList());
    }

    private static List<TransitionMatrixForComplexInd> createTransitionMatricesForComplexInd() {
        Set<String> complexIndices = createComplexIndices();
        List<TransitionMatrixForComplexInd> matrices = new ArrayList<>();
        complexIndices.forEach(alias -> {
            int[] transitions = createTransitionsArrayForComplexInd(alias);
            matrices.add(new TransitionMatrixForComplexInd(transitions, alias, Parser.getBinaryValueForComplexInd(alias)));
        });
        return matrices;
    }

    private static Set<String> createComplexIndices() {
        List<List<String>> clusters = clustering(.7);
        Set<String> complexIndices = new HashSet<>();
        createBinaryComplexIndices(clusters, complexIndices);
        createTripleComplexIndices(clusters, complexIndices);
        return complexIndices;
    }

    private static void createTripleComplexIndices(List<List<String>> clusters, Set<String> complexIndices) {
        clusters.forEach(cluster -> {
            cluster.forEach(simpleInd -> {
                cluster.forEach(otherSimpleInd -> {
                    var simpleIndices = new Object() {
                        String binarySimpleInd = "";
                        String reversedBinarySimpleInd = "";
                    };
                    if (!Objects.equals(simpleInd, otherSimpleInd)) {
                        simpleIndices.binarySimpleInd = simpleInd + "|" + otherSimpleInd;
                        simpleIndices.reversedBinarySimpleInd = otherSimpleInd + "|" + simpleInd;
                    }
                    clusters.forEach(otherCluster -> {
                        if (cluster != otherCluster) {
                            otherCluster.forEach(simpleIndInOtherCluster -> {
                                String tripleComplexInd1 = simpleIndices.binarySimpleInd + "|" + simpleIndInOtherCluster;
                                String tripleComplexInd2 = simpleIndInOtherCluster + "|" + simpleIndices.binarySimpleInd;
                                String tripleComplexInd3 = simpleIndices.reversedBinarySimpleInd + "|" + simpleIndInOtherCluster;
                                String tripleComplexInd4 = simpleIndInOtherCluster + "|" + simpleIndices.reversedBinarySimpleInd;
                                if (!simpleIndices.binarySimpleInd.isEmpty() &&
                                        !simpleIndices.reversedBinarySimpleInd.isEmpty() &&
                                        !complexIndices.contains(tripleComplexInd1) &&
                                        !complexIndices.contains(tripleComplexInd2) &&
                                        !complexIndices.contains(tripleComplexInd3) &&
                                        !complexIndices.contains(tripleComplexInd4)) {
                                    complexIndices.add(tripleComplexInd1);
                                }
                            });
                        }
                    });
                });
            });
        });
    }

    private static void createBinaryComplexIndices(List<List<String>> clusters, Set<String> complexIndices) {
        clusters.forEach(cluster -> {
            cluster.forEach(simpleInd -> {
                clusters.forEach(otherCluster -> {
                    if (cluster != otherCluster) {
                        otherCluster.forEach(otherSimpleInd -> {
                            String complexInd = simpleInd + "|" + otherSimpleInd;
                            String reversedComplexInd = otherSimpleInd + "|" + simpleInd;
                            if (!complexIndices.contains(complexInd) && !complexIndices.contains(reversedComplexInd)) {
                                complexIndices.add(complexInd);
                            }
                        });
                    }
                });
            });
        });
    }

    private static List<List<String>> clustering(double minMetricPercent) {
        List<List<String>> clusters = new ArrayList<>();
        AtomicReference<Metric> maxMetric = new AtomicReference<>();
        List<Metric> metrics = calculateMetrics();
        Map<String, String> copy = new HashMap<>(Map.copyOf(indicesList));
        copy.remove("Y");

        metrics.stream()
                .max(Comparator.comparingDouble(Metric::getMetric))
                .ifPresentOrElse(maxMetric::set, () -> maxMetric.set(null));

        double maxMetricValue = maxMetric.get() != null ? maxMetric.get().getMetric() * minMetricPercent : 1.0;
        while (copy.size() > 0) {
            indicesList.forEach((k, v) -> {
                if (clusters.size() < 1) {
                    List<String> list = new ArrayList<>();
                    list.add(k);
                    clusters.add(list);
                    copy.remove(k);
                } else {
                    AtomicBoolean indicesIsAdded = new AtomicBoolean(false);
                    AtomicBoolean keyContains = new AtomicBoolean(false);
                    for (List<String> cluster : clusters) {
                        if (cluster.contains(k)) {
                            keyContains.set(true);
                            break;
                        }
                    }
                    if (!keyContains.get()) {
                        for (int indInCluster = 0; indInCluster < clusters.size(); indInCluster++) {
                            int finalIndInCluster = indInCluster;
                            metrics.forEach(metric -> {
                                if (!clusters.get(finalIndInCluster).contains(k) && copy.containsKey(k)) {
                                    if (k.equals(metric.getIndicesNumbers().split("\\|")[1])
                                            || k.equals(metric.getIndicesNumbers().split("\\|")[0])) {
                                        if (metric.getMetric() < maxMetricValue) {
                                            clusters.get(finalIndInCluster).add(k);
                                            copy.remove(k);
                                            indicesIsAdded.set(true);
                                        }
                                    }
                                }
                            });
                            if (!indicesIsAdded.get() && copy.containsKey(k)) {
                                List<String> list = new ArrayList<>();
                                list.add(k);
                                clusters.add(list);
                                copy.remove(k);
                                indicesIsAdded.set(true);
                            }
                        }
                    } else {
                        copy.remove(k);
                    }
                }
            });
        }

        return clusters;
    }

    private static List<Metric> calculateMetrics() {
        List<Metric> metrics = new ArrayList<>();
        List<TransitionMatrix> matrices = filteringIndicesByAlpha();
        List<TransitionMatrix> transitionMatrices = new ArrayList<>();

        matrices.forEach(matrix1 -> {
            matrices.forEach(matrix2 -> {
                if (!matrix1.getIndicesX().equals(matrix2.getIndicesX())) {
                    transitionMatrices.add(createTransitionMatrix(matrix1.getIndicesValue(), matrix2.getIndicesValue(),
                            matrix1.getIndicesX() + "|" + matrix2.getIndicesX()));
                }
            });
        });

        transitionMatrices.forEach(transitionMatrix1 -> {
            transitionMatrices.forEach(transitionMatrix2 -> {
                String[] indicesNumber1 = transitionMatrix1.getIndicesX().split("\\|");
                String[] indicesNumber2 = transitionMatrix2.getIndicesX().split("\\|");
                if (indicesNumber1[0].equals(indicesNumber2[1]) && indicesNumber1[1].equals(indicesNumber2[0])) {
                    double metric = (double) 1 / 2 * (transitionMatrix1.getInformative() + transitionMatrix2.getInformative());
                    Metric m = new Metric(transitionMatrix2.getIndicesX(), metric);
                    if (!metrics.contains(m)) {
                        metrics.add(m);
                    }
                }
            });
        });

        return metrics;
    }

    private static List<TransitionMatrix> filteringIndicesByAlpha() {
        List<TransitionMatrix> matrices = createTransitionMatrices();
        Map<String, String> map = new TreeMap<>();
        double alphaPercent = CalculateInformative.informativeY * Main.getAlphaValue() / 100;

        List<TransitionMatrix> filteredList = matrices
                .stream()
                .filter(matrix -> matrix.getInformative() > alphaPercent)
                .collect(Collectors.toList());

        filteredList.forEach(matrix -> {
            map.put(matrix.getIndicesX(), matrix.getIndicesValue());
        });

        indicesList = map;

        return filteredList;
    }

    private static TransitionMatrix createTransitionMatrix(String indices1, String indices2, String concatenationNumberOfIndices) {
        int[] transitionsXiXj = createTransitionsArray(indices1, indices2);
        int[] transitionsXjXi = createTransitionsArray(indices2, indices1);

        return new TransitionMatrix(transitionsXiXj, transitionsXjXi, concatenationNumberOfIndices,
                indices1 + "," + indices2);
    }

    private static int[] createTransitionsArray(String indices1, String indices2) {
        int[] transitionsXiXj = new int[4];
        for (int ind2 = 0; ind2 < indices2.length(); ind2++) {
            if (indices1.charAt(ind2) == '1' && indices2.charAt(ind2) == '1') {
                transitionsXiXj[3] = transitionsXiXj[3] + 1;
            }
            if (indices1.charAt(ind2) == '1' && indices2.charAt(ind2) == '0') {
                transitionsXiXj[2] = transitionsXiXj[2] + 1;
            }
            if (indices1.charAt(ind2) == '0' && indices2.charAt(ind2) == '1') {
                transitionsXiXj[1] = transitionsXiXj[1] + 1;
            }
            if (indices1.charAt(ind2) == '0' && indices2.charAt(ind2) == '0') {
                transitionsXiXj[0] = transitionsXiXj[0] + 1;
            }
        }
        return transitionsXiXj;
    }

    private static int[] createTransitionsArrayForComplexInd(String complexIndAlias) {
        List<String> isTrueData = DatabaseUtils.selectIsTrueData();
        String[] split = Parser.getBinaryValueForComplexInd(complexIndAlias).split(",");
        int[] transitions = split[0].length() == 2 ? new int[8] : new int[16];
        for (int i = 0; i < split.length; i++) {
            switch (split[i]) {
                case "00":
                case "000": {
                    switch (isTrueData.get(i)) {
                        case "TRUE": {
                            int tmp = transitions[1];
                            transitions[1] = ++tmp;
                            break;
                        }
                        case "FALSE": {
                            int tmp = transitions[0];
                            transitions[0] = ++tmp;
                            break;
                        }
                    }
                    break;
                }
                case "01":
                case "001": {
                    switch (isTrueData.get(i)) {
                        case "TRUE": {
                            int tmp = transitions[3];
                            transitions[3] = ++tmp;
                            break;
                        }
                        case "FALSE": {
                            int tmp = transitions[2];
                            transitions[2] = ++tmp;
                            break;
                        }
                    }
                    break;
                }
                case "10":
                case "010": {
                    switch (isTrueData.get(i)) {
                        case "TRUE": {
                            int tmp = transitions[5];
                            transitions[5] = ++tmp;
                            break;
                        }
                        case "FALSE": {
                            int tmp = transitions[4];
                            transitions[4] = ++tmp;
                            break;
                        }
                    }
                    break;
                }
                case "011": {
                    switch (isTrueData.get(i)) {
                        case "TRUE": {
                            int tmp = transitions[7];
                            transitions[7] = ++tmp;
                            break;
                        }
                        case "FALSE": {
                            int tmp = transitions[6];
                            transitions[6] = ++tmp;
                            break;
                        }
                    }
                    break;
                }
                case "100": {
                    switch (isTrueData.get(i)) {
                        case "TRUE": {
                            int tmp = transitions[9];
                            transitions[9] = ++tmp;
                            break;
                        }
                        case "FALSE": {
                            int tmp = transitions[8];
                            transitions[8] = ++tmp;
                            break;
                        }
                    }
                    break;
                }
                case "101": {
                    switch (isTrueData.get(i)) {
                        case "TRUE": {
                            int tmp = transitions[11];
                            transitions[11] = ++tmp;
                            break;
                        }
                        case "FALSE": {
                            int tmp = transitions[10];
                            transitions[10] = ++tmp;
                            break;
                        }
                    }
                    break;
                }
                case "110": {
                    switch (isTrueData.get(i)) {
                        case "TRUE": {
                            int tmp = transitions[13];
                            transitions[13] = ++tmp;
                            break;
                        }
                        case "FALSE": {
                            int tmp = transitions[12];
                            transitions[12] = ++tmp;
                            break;
                        }
                    }
                    break;
                }
                case "111": {
                    switch (isTrueData.get(i)) {
                        case "TRUE": {
                            int tmp = transitions[15];
                            transitions[15] = ++tmp;
                            break;
                        }
                        case "FALSE": {
                            int tmp = transitions[14];
                            transitions[14] = ++tmp;
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return transitions;
    }

    private static List<TransitionMatrix> createTransitionMatrices() {
        List<TransitionMatrix> resultList = new ArrayList<>();
        String indicesY = indicesList.get("Y");

        for (int k = 1; k < indicesList.size(); k++) {
            int[] transitions = createTransitionsArray(indicesList.get("X" + k), indicesY);
            TransitionMatrix transitionMatrix = new TransitionMatrix(transitions, "X" + k, indicesList.get("X" + k));
            resultList.add(transitionMatrix);

        }
        return resultList;
    }

    /**
     * Инициализация признаков:
     *
     * @return Map<String, String>, где key - номер признака, value - признак
     */
    private static Map<String, String> createIndices() {
        List<String> allData = DatabaseUtils.selectAllData();
        Map<String, String> indices = new LinkedHashMap<>();

        int sizeOfRow = allData.get(0).length();
        StringBuilder indicesY = new StringBuilder();

        for (int i = 0; i < sizeOfRow; i++) {
            int finalI = i;
            StringBuilder indicesXBuilder = new StringBuilder();
            allData.forEach(row -> {
                char c = row.charAt(finalI);
                if (c == '0' || c == '1') {
                    indicesXBuilder.append(c);
                } else {
                    if (row.split("/")[1].equals("TRUE") && indicesY.length() < allData.size()) {
                        indicesY.append(1);
                    } else if (row.split("/")[1].equals("FALSE") && indicesY.length() < allData.size()) {
                        indicesY.append(0);
                    }
                }
            });
            if (indicesXBuilder.length() > 0) {
                indices.put("X" + (i + 1), indicesXBuilder.toString());
            }
        }
        indices.put("Y", indicesY.toString());

        return indices;
    }
}
