package be.playing.with.projections.model.projections;

import java.util.Arrays;

/**
 * Code from http://stackoverflow.com/questions/7988486/how-do-you-calculate-the-variance-median-and-standard-deviation-in-c-or-java
 */
public class Statistics {
  double[] data;
  int size;

  public Statistics(double[] data) {
    this.data = data;
    size = data.length;
  }

  double getMean() {
    double sum = 0.0;
    for (double a : data) {
      sum += a;
    }
    return sum / size;
  }

  double getVariance() {
    double mean = getMean();
    double temp = 0;
    for (double a : data) {
      temp += (a - mean) * (a - mean);
    }
    return temp / size;
  }

  double getStdDev() {
    return Math.sqrt(getVariance());
  }

  public double median() {
    Arrays.sort(data);

    if (data.length % 2 == 0) {
      return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
    }
    return data[data.length / 2];
  }
}
