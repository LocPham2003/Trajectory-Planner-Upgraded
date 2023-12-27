package com.example.trajectoryplanner;

import javafx.geometry.Bounds;

import java.util.ArrayList;
import java.util.Comparator;

public class MainController {
    private int splineType = 0;
    private double[] verticalBoundaries = new double[2];
    ArrayList<Point> listOfPoints = new ArrayList<>();

    public MainController(Bounds bounds) {
        verticalBoundaries[0] = bounds.getMinY() + Constants.MENU_BAR_SHIFT;
        verticalBoundaries[1] = bounds.getMaxY();
    }

    public void addPoint(Point point) {
        listOfPoints.add(point);
        this.listOfPoints.sort(Comparator.comparingDouble(Point::getX));
    }

    public void removePoint(Point point) {
        this.listOfPoints.remove(point);
    }

    public int getCurrSplineType() {
        return this.splineType;
    }

    public ArrayList<Point> getListOfPoints() {
        return this.listOfPoints;
    }

    public ArrayList<Trajectory> interpolate(int splineType) {
        this.splineType = splineType;
        TrajectoryGenerator trajectoryGenerator = new TrajectoryGenerator(listOfPoints, verticalBoundaries);
        if (splineType == 0) {
            System.out.println("Solving cubic spline");
            return trajectoryGenerator.generateCubicSplineTrajectories();
        } else {
            return trajectoryGenerator.generateBezierTrajectories();
        }
    }
}