package com.example.trajectoryplanner;

import javafx.scene.shape.Circle;

public class Utilities {
    public static boolean comparePoints(Point p1, Point p2) {
        return Math.abs(p1.getX() - p2.getX()) <= Constants.COORDINATE_FLOAT_DIFF
                && Math.abs(p1.getY() - p2.getY()) <= Constants.COORDINATE_FLOAT_DIFF;
    }

    public static boolean comparePointsWithCircle(Point point, Circle circle) {
        return Math.abs(point.getX() - circle.getCenterX()) <= Constants.COORDINATE_FLOAT_DIFF
                && Math.abs(point.getY() - circle.getCenterY()) <= Constants.COORDINATE_FLOAT_DIFF;
    }

    public static boolean compareCircleWithCircle(Circle c1, Circle c2) {
        return Math.abs(c1.getCenterX() - c2.getCenterX()) <= Constants.COORDINATE_FLOAT_DIFF
                && Math.abs(c1.getCenterY() - c2.getCenterY()) <= Constants.COORDINATE_FLOAT_DIFF;
    }

    public static boolean withinCircle(Point p1, Circle c1) {
        System.out.println("P1:" + p1 == null);
        System.out.println("C1:" + c1 == null);
        return Math.pow(p1.getX() - c1.getCenterX(), 2) +
                Math.pow(p1.getY() - c1.getCenterY(), 2) <= Math.pow(Constants.CONTROL_POINTS_RADIUS, 2);
    }
}
