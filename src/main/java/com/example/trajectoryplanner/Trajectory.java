package com.example.trajectoryplanner;

public class Trajectory {
    private final double x_0;
    private final double x_1;
    private final double x_2;
    private final double x_3;
    Trajectory(double x_0, double x_1, double x_2, double x_3) {
        this.x_0 = x_0;
        this.x_1 = x_1;
        this.x_2 = x_2;
        this.x_3 = x_3;
    }

    @Override
    public String toString() {
        return x_0 + " + " + x_1 + "x + " + x_2 + "x^2 + " + x_3 + "x^3";
    }

    public double getFuncOutput(double x) {
        return x_0 + x_1 * x + x_2 * Math.pow(x, 2) + x_3 * Math.pow(x, 3);
    }
}
