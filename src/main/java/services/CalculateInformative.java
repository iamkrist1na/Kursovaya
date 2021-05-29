package services;

import org.apache.commons.math3.util.CombinatoricsUtils;
import services.Utils.MathUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

public final class CalculateInformative {
    public static Double informativeY;

    private static double getInformativeYX(List<Integer> matrix) {
        return MathUtils.log2(((double) CombinatoricsUtils.factorial(
                matrix.get(0) + matrix.get(1)) /
                (
                        CombinatoricsUtils.factorial(matrix.get(0)) *
                                CombinatoricsUtils.factorial(matrix.get(1))
                )) *
                ((double) CombinatoricsUtils.factorial(
                        (matrix.get(2) + matrix.get(3))) /
                        (
                                CombinatoricsUtils.factorial(matrix.get(2)) *
                                        CombinatoricsUtils.factorial(matrix.get(3))
                        )));
    }

    public static double getInformativeY(List<Integer> matrix) {
        int sumAllElements = matrix.stream().mapToInt(row -> row).sum();
        double result = MathUtils.log2((double) CombinatoricsUtils.factorial(sumAllElements) /
                (CombinatoricsUtils.factorial(
                        (matrix.get(0) + matrix.get(2))) *
                        CombinatoricsUtils.factorial(
                                (matrix.get(1) + matrix.get(3)))));

        BigDecimal bd = new BigDecimal(Double.toString(result));
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        if (CalculateInformative.informativeY == null) {
            CalculateInformative.informativeY = bd.doubleValue();
        }

        return result;
    }

    public static double calculateInformative(List<Integer> matrix) {
        double informative0_YX = getInformativeYX(matrix);
        double informative0_Y;
        informative0_Y = Objects.requireNonNullElseGet(CalculateInformative.informativeY, () -> getInformativeY(matrix));


        // HINT: I0(X:Y) = I0(Y) – I0(Y|X) = I0(X) – I0(X|Y)
        double informative = informative0_Y - informative0_YX;

        BigDecimal bd = new BigDecimal(Double.toString(informative));
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }

    public static double calculateInformativeForIndices(List<Integer> matrixXiXj, List<Integer> matrixXjXi) {
        double informative0_XiXj = getInformativeYX(matrixXiXj);
        double informative0_XjXi = getInformativeYX(matrixXjXi);

        // HINT: I0(X:Y) = I0(Y) – I0(Y|X) = I0(X) – I0(X|Y)
        double informative = (double) 1 / 2 * (informative0_XiXj + informative0_XjXi);

        BigDecimal bd = new BigDecimal(Double.toString(informative));
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }
}
