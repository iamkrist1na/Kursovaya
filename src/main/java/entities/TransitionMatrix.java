package entities;

import services.CalculateInformative;

import java.util.ArrayList;
import java.util.List;

public class TransitionMatrix {
    private final String indicesX;
    private final double informative;
    private final List<Integer> matrix = new ArrayList<>();
    private final String indicesValue;

    public TransitionMatrix(int[] transitions, String indicesX, String indicesValue) {
        if (transitions.length != 4) {
            throw new RuntimeException("Transition array must be length 4");
        } else {
            for (int i = 0; i < 4; i++) {
                matrix.add(transitions[i]);
            }
            this.indicesX = indicesX;
            this.indicesValue = indicesValue;
            this.informative = CalculateInformative.calculateInformative(matrix);
        }
    }
    public TransitionMatrix(int[] transitionsXiXj, int[] transitionsXjXi, String indicesXiXj, String indicesValue) {
        this.indicesX = indicesXiXj;
        this.indicesValue = indicesValue;
        List<Integer> secondMatrix = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            matrix.add(transitionsXiXj[i]);
        }
        for (int i = 0; i < 4; i++) {
            secondMatrix.add(transitionsXiXj[i]);
        }

        this.informative = CalculateInformative.calculateInformativeForIndices(matrix, secondMatrix);
    }

    public List<Integer> getMatrix() {
        return matrix;
    }

    public String getIndicesValue() {
        return indicesValue;
    }

    public String getIndicesX() {
        return indicesX;
    }

    public double getInformative() {
        return informative;
    }

    @Override
    public String toString() {
        return "\nTransition Matrix for " + indicesX
                + ":\n(" + matrix.get(0) + " " + matrix.get(1) + ")"
                + "\n(" + matrix.get(2) + " " + matrix.get(3) + ")"
                + "\nInformative: " + informative
                + "\nIndices value: " + indicesValue;

    }
}
