package com.example.trajectoryplanner;

public class Trajectory {
    private double x_0;
    private double x_1;
    private double x_2;
    private double x_3;
    private double[] x_cubic;
    private double[] y_cubic;
    private boolean isCubic;
    private int numControlPoints;
    Trajectory(double x_0, double x_1, double x_2, double x_3, boolean isCubic) {
        this.x_0 = x_0;
        this.x_1 = x_1;
        this.x_2 = x_2;
        this.x_3 = x_3;
        this.isCubic = isCubic;
    }

    Trajectory(double[] x_cubic, double[] y_cubic, boolean isCubic, int numControlPoints) {
        this.x_cubic = x_cubic;
        this.y_cubic = y_cubic;
        this.isCubic = isCubic;
        this.numControlPoints = numControlPoints;
    }

    public void setCoefficient(double x_0, double x_1, double x_2, double x_3) {
        this.x_0 = x_0;
        this.x_1 = x_1;
        this.x_2 = x_2;
        this.x_3 = x_3;
    }

    @Override
    public String toString() {
        return x_0 + " + " + x_1 + "x + " + x_2 + "x^2 + " + x_3 + "x^3";
    }

    public double getFuncOutputCubic(double x) {
        return x_0 + x_1 * x + x_2 * Math.pow(x, 2) + x_3 * Math.pow(x, 3);
    }

    public double[] getFuncOutputBezier(double t) {
        if (this.numControlPoints == 2) {
            double x_coord = Math.pow(1 - t, 3) * x_cubic[0] +
                    3 * Math.pow(1 - t, 2) * t + x_cubic[1] +
                    3 * (1 - t) * Math.pow(t, 2) * x_cubic[2] +
                    Math.pow(t, 3) * x_cubic[3];
            double y_coord = Math.pow(1 - t, 3) * y_cubic[0] +
                    3 * Math.pow(1 - t, 2) * t + y_cubic[1] +
                    3 * (1 - t) * Math.pow(t, 2) * y_cubic[2] +
                    Math.pow(t, 3) * y_cubic[3];
            return new double[]{x_coord, y_coord};
        } else {
            double x_coord = Math.pow(1 - t, 2) * x_cubic[0] + 2 * (1 - t) * t * x_cubic[1] + Math.pow(t, 2)  * x_cubic[2];
            double y_coord = Math.pow(1 - t, 2) * y_cubic[0] + 2 * (1 - t) * t * y_cubic[1] + Math.pow(t, 2)  * y_cubic[2];
            return new double[]{x_coord, y_coord};
        }
    }


    // Return a list of coefficient representing the function, where f(x) = x_0 + x_1x + x_2x^2 + x_3x^3
    public double[] getCoefficients() {
        return new double[]{x_0, x_1, x_2, x_3};
    }
}
