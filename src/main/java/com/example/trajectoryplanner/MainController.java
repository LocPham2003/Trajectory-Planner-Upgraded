package com.example.trajectoryplanner;

import java.util.ArrayList;

public class MainController {
    ArrayList<Point> listOfPoints = new ArrayList<>();
    public void addPoint(Point point) {
        System.out.println("Point added");
        listOfPoints.add(point);
    }

    public void interpolate(int option) {
        TrajectoryGenerator trajectoryGenerator = new TrajectoryGenerator(listOfPoints);
        if (option == 0) {
            System.out.println("Solving cubic spline");
            ArrayList<Trajectory> trajectories = trajectoryGenerator.generateCubicSplineTrajectories();
        } else {
            trajectoryGenerator.solveBezier(listOfPoints);
        }
    }
}