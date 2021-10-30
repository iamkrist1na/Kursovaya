package com.company.soldatowa.model;

import com.company.soldatowa.services.CalculateInformative;

import java.util.ArrayList;
import java.util.List;

public class TransitionMatrixForComplexInd {
    protected final String complexIndices;
    protected final double informative;
    protected final List<Integer> matrix = new ArrayList<>();
    protected final String indicesValue;

    public TransitionMatrixForComplexInd(int[] transitions, String complexIndices, String indicesValue) {
        if (transitions.length != 8 && transitions.length != 16) {
            throw new RuntimeException("Transition array must be length 8");
        } else {
            for (int transition : transitions) {
                matrix.add(transition);
            }
            this.complexIndices = complexIndices;
            this.indicesValue = indicesValue;
            this.informative = CalculateInformative.calculateInformative(matrix, true);
        }
    }

    public List<Integer> getMatrix() {
        return matrix;
    }


    public String getIndicesValue() {
        return indicesValue;
    }


    public String getComplexIndices() {
        return complexIndices;
    }


    public double getInformative() {
        return CalculateInformative.calculateInformative(matrix, true);
    }


    public String toString() {
        return "\nTransition Matrix for " + complexIndices
                + "\nMatrix: " + matrix
                + "\nInformative: " + informative
                + "\nIndices value: " + indicesValue;
    }
}
