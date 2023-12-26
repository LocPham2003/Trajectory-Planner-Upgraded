package com.example.trajectoryplanner;

import java.util.ArrayList;

public class MainController {
    ArrayList<Point> listOfPoints = new ArrayList<>();
    public void addPoint(Point point) {
        System.out.println("Point added");
        listOfPoints.add(point);
    }

    public ArrayList<Point> getListOfPoints() {
        return this.listOfPoints;
    }

    public ArrayList<Trajectory> interpolate(int option) {
        TrajectoryGenerator trajectoryGenerator = new TrajectoryGenerator(listOfPoints);
        if (option == 0) {
            System.out.println("Solving cubic spline");
            return trajectoryGenerator.generateCubicSplineTrajectories();
        } else {
            return trajectoryGenerator.generateBezierTrajectories(listOfPoints);
        }
    }
}