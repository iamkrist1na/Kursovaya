package services;


import database.DatabaseUtils;
import entities.Metric;
import entities.TransitionMatrix;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
        clustering(.7);

        return true;
    }

    private static List<List<String>> clustering(double minMetricProcent) {
        List<List<String>> clusters = new ArrayList<>();
        AtomicReference<Metric> maxMetric = new AtomicReference<>();
        List<Metric> metrics = calculateMetrics();
        Map<String, String> copy = new HashMap<>(Map.copyOf(indicesList));
        copy.remove("Y");

        metrics.stream()
                .max(Comparator.comparingDouble(Metric::getMetric))
                .ifPresentOrElse(maxMetric::set, () -> maxMetric.set(null));

        double maxMetricValue = maxMetric.get() != null ? maxMetric.get().getMetric() * minMetricProcent : 1.0;
        System.out.println(maxMetricValue);
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
        Map<String, String> map = new TreeMap<>();
        double alphaProcent = CalculateInformative.informativeY * alpha / 100;

        List<TransitionMatrix> filteredList = matrices
                .stream()
                .filter(matrix -> matrix.getInformative() > alphaProcent)
                .collect(Collectors.toList());

        filteredList.forEach(matrix -> {
            map.put(matrix.getIndicesX(), matrix.getIndicesValue());
        });

        indicesList = map;

        return filteredList;
    }

    private static TransitionMatrix createTransitionMatrix(String indices1, String indices2,
                                                           String concatenationNumberOfIndices) {
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

    private static List<TransitionMatrix> createTransitionMatrices() {
        List<TransitionMatrix> resultList = new ArrayList<>();
        Map<String, String> indices = Recognition.createIndices();
        String indicesY = indices.get("Y");

        for (int k = 1; k < indices.size(); k++) {
            int[] transitions = createTransitionsArray(indices.get("X" + k), indicesY);
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

        return indices;
    }
}
