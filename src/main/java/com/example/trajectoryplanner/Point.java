package com.example.trajectoryplanner;

public class Point implements Comparable<Point> {
    private final double x;
    private final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public int compareTo(Point comparePoint) {
        double comparePointX = comparePoint.getX();
        return compare(this.x, comparePointX);
    }

    public static int compare(double x, double compX) {
        return Double.compare(x, compX);
    }
}
