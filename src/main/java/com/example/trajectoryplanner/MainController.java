package com.example.trajectoryplanner;

import java.util.ArrayList;
import java.util.Comparator;
public class MainController {
    private double[] verticalBoundaries;
    private ArrayList<Point> listOfPoints = new ArrayList<>();
    private ArrayList<Trajectory> trajectories;

    public MainController(double[] verticalBoundaries) {
        this.verticalBoundaries = verticalBoundaries;
    }

    public void addPoint(Point point) {
        listOfPoints.add(point);
        this.listOfPoints.sort(Comparator.comparingDouble(Point::getX));
    }

    public void removePoint(Point point) {
        this.listOfPoints.remove(point);
    }

    public ArrayList<Point> getListOfPoints() {
        return this.listOfPoints;
    }

    public ArrayList<Trajectory> interpolateCubic() {
        TrajectoryGenerator trajectoryGenerator = new TrajectoryGenerator(listOfPoints, verticalBoundaries);
        trajectories = trajectoryGenerator.generateCubicSplineTrajectories();
        return this.trajectories;
    }

    public ArrayList<Trajectory> interpolateBezier(ArrayList<Point> bezierControlPoints, int numControlPoints) {
        TrajectoryGenerator trajectoryGenerator = new TrajectoryGenerator(listOfPoints, verticalBoundaries);
        return trajectoryGenerator.generateBezierTrajectories(bezierControlPoints, numControlPoints);
    }
}