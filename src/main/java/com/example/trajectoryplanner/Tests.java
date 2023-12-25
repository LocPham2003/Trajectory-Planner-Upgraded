package com.example.trajectoryplanner;

import java.util.ArrayList;

public class Tests {

    public static boolean checkMatrix(double[][] mat) {
        for (int i = 0; i < mat.length - 1; i++) {
            if (mat[i][i] == 0) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        ArrayList<Point> listOfPoints = new ArrayList<>();
        listOfPoints.add(new Point(0, 0));
        listOfPoints.add(new Point(1, 2));
        listOfPoints.add(new Point(3, 1));
        TrajectoryGenerator trajectoryGenerator = new TrajectoryGenerator(listOfPoints);
        ArrayList<Trajectory> trajectories = trajectoryGenerator.generateCubicSplineTrajectories();
        System.out.println(trajectories.get(0).toString());
        System.out.println(trajectories.get(1).toString());
        System.out.println(trajectories.get(0).getFuncOutput(0));
        System.out.println(trajectories.get(0).getFuncOutput(1));
        System.out.println(trajectories.get(1).getFuncOutput(1));
        System.out.println(trajectories.get(1).getFuncOutput(3));
    }


}
