package entities;

import java.util.Objects;

public class Metric {

    private final String indicesNumbers;
    private final double metric;

    public Metric(String indicesNumbers, double metric) {
        this.indicesNumbers = indicesNumbers;
        this.metric = metric;
    }

    public double getMetric() {
        return metric;
    }

    public String getIndicesNumbers() {
        return indicesNumbers;
    }

    @Override
    public String toString() {
        return "Metric for " + indicesNumbers + " : " + metric;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Metric metric = (Metric) o;
        String[] split = metric.getIndicesNumbers().split("\\|");
        String[] split1 = indicesNumbers.split("\\|");
        return (split1[0].equals(split[0]) && split1[1].equals(split[1]))
                || (split1[0].equals(split[1]) && split1[1].equals(split[0]));
    }

    @Override
    public int hashCode() {
        return Objects.hash(indicesNumbers);
    }
}
