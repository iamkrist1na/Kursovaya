package services;


import database.DatabaseUtils;
import entities.Metric;
import entities.TransitionMatrix;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Анализ введенных пользователем данных:
 */
public final class Recognition {

    private static Map<String, String> indicesList;

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
        clustering();

        return true;
    }

    private static List<List<String>> clustering() {
        List<List<String>> clusters = new ArrayList<>();
        List<Metric> metrics = calculateMetrics();
        double minMetric = 0.3;
        AtomicReference<Metric> maxMetric = new AtomicReference<>();
        metrics
                .stream()
                .max(Comparator.comparingDouble(Metric::getMetric))
                .ifPresentOrElse(maxMetric::set, () -> maxMetric.set(null));

        double maxMetricValue = maxMetric.get() != null ? maxMetric.get().getMetric() * minMetric : 1.0;
        Map<String, String> copy = new HashMap<>(Map.copyOf(indicesList));
        while (copy.size() > 0) {
            indicesList.forEach((k, v) -> {
                if (clusters.size() < 1) {
                    List<String> list = new ArrayList<>();
                    list.add(k);
                    clusters.add(list);
                    copy.remove(k);

                } else {
                    for (int indInCluster = 0; indInCluster < clusters.size(); indInCluster++) {
                        int finalIndInCluster = indInCluster;
                        metrics.forEach(metric -> {
                            if (clusters.get(finalIndInCluster).contains(metric.getIndicesNumbers().split("\\|")[0])
                                    && !clusters.get(finalIndInCluster).contains(metric.getIndicesNumbers().split("\\|")[1])) {
                                if (metric.getMetric() < maxMetricValue) {
                                    clusters.get(finalIndInCluster).add(k);
                                } else {
                                    List<String> list = new ArrayList<>();
                                    list.add(metric.getIndicesNumbers().split("\\|")[1]);
                                    clusters.add(list);
                                }
                                copy.remove(k);
                            }

                            if (clusters.get(finalIndInCluster).contains(metric.getIndicesNumbers().split("\\|")[1])
                                    && !clusters.get(finalIndInCluster).contains(metric.getIndicesNumbers().split("\\|")[0])) {
                                if (metric.getMetric() < maxMetricValue) {
                                    clusters.get(finalIndInCluster).add(k);
                                } else {
                                    List<String> list = new ArrayList<>();
                                    list.add(metric.getIndicesNumbers().split("\\|")[0]);
                                    clusters.add(list);
                                }
                                copy.remove(k);
                            }
                        });
                    }
                }
            });
        }
        System.out.println(clusters);

        return clusters;
    }

    private static List<Metric> calculateMetrics() {
        List<Metric> metrics = new ArrayList<>();
        List<TransitionMatrix> matrices = filteringIndicesByAlpha(25);
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

    private static List<TransitionMatrix> filteringIndicesByAlpha(int alpha) {
        List<TransitionMatrix> matrices = createTransitionMatrices();
        double alphaProcent = CalculateInformative.informativeY * alpha / 100;

        return matrices
                .stream()
                .filter(matrix -> matrix.getInformative() > alphaProcent)
                .collect(Collectors.toList());
    }

    private static TransitionMatrix createTransitionMatrix(String indices1, String indices2,
                                                           String concatenationNumberOfIndices) {
        int[] transitions = new int[4];
        for (int ind2 = 0; ind2 < indices2.length(); ind2++) {
            if (indices1.charAt(ind2) == '1' && indices2.charAt(ind2) == '1') {
                transitions[3] = transitions[3] + 1;
            }
            if (indices1.charAt(ind2) == '1' && indices2.charAt(ind2) == '0') {
                transitions[2] = transitions[2] + 1;
            }
            if (indices1.charAt(ind2) == '0' && indices2.charAt(ind2) == '1') {
                transitions[1] = transitions[1] + 1;
            }
            if (indices1.charAt(ind2) == '0' && indices2.charAt(ind2) == '0') {
                transitions[0] = transitions[0] + 1;
            }
        }

        return new TransitionMatrix(transitions, concatenationNumberOfIndices, indices1 + "," + indices2);
    }

    private static List<TransitionMatrix> createTransitionMatrices() {
        List<TransitionMatrix> resultList = new ArrayList<>();
        Map<String, String> indices = Recognition.createIndices();
        String indicesY = indices.get("Y");

        for (int k = 1; k < indices.size(); k++) {
            int[] transitions = new int[4];
            for (int i = 0; i < indices.get("X" + k).length(); i++) {
                if (indices.get("X" + k).charAt(i) == '1' && indicesY.charAt(i) == '1') {
                    transitions[3] = transitions[3] + 1;
                }
                if (indices.get("X" + k).charAt(i) == '1' && indicesY.charAt(i) == '0') {
                    transitions[2] = transitions[2] + 1;
                }
                if (indices.get("X" + k).charAt(i) == '0' && indicesY.charAt(i) == '1') {
                    transitions[1] = transitions[1] + 1;
                }
                if (indices.get("X" + k).charAt(i) == '0' && indicesY.charAt(i) == '0') {
                    transitions[0] = transitions[0] + 1;
                }
            }
            TransitionMatrix transitionMatrix = new TransitionMatrix(transitions, "X" + k, indices.get("X" + k));
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
        indicesList = indices;

        return indices;
    }
}
