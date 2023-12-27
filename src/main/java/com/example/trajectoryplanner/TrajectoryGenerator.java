package com.example.trajectoryplanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TrajectoryGenerator {
    private final ArrayList<Point> listOfPoints;
    private final double[] verticalBoundaries;
    private ArrayList<Trajectory> trajectories;
    private double[][] coefMat;
    private int lastUpdatedRow = 0;
    private int lastUpdatedCol = 0;
    private int rightHandSideIndex = 0;
    public TrajectoryGenerator(ArrayList<Point> listOfPoints, double[] verticalBoundaries) {
        this.verticalBoundaries = verticalBoundaries;
        this.listOfPoints = listOfPoints;
        this.trajectories = new ArrayList<>();
    }

    // Coefficients that always exist in the matrix and has a fixed way of defining it
    // This includes: coefficient of start slope, coefficient of end slope, coefficient of first slope at first point's X, coefficient
    // of last slope at last point's X.
    private void boundaryCoefficients(double alpha, double beta) {
        Point firstPoint = this.listOfPoints.get(0);
        Point lastPoint = this.listOfPoints.get(this.listOfPoints.size() - 1);

        // First starting point coefficient
        coefMat[lastUpdatedRow][0] = 1;
        coefMat[lastUpdatedRow][1] = firstPoint.getX();
        coefMat[lastUpdatedRow][2] = Math.pow(firstPoint.getX(), 2);
        coefMat[lastUpdatedRow][3] = Math.pow(firstPoint.getX(), 3);
        coefMat[lastUpdatedRow][this.rightHandSideIndex] = firstPoint.getY();
        lastUpdatedRow++;
        // Last point coefficient
        coefMat[lastUpdatedRow][this.rightHandSideIndex - 4] = 1;
        coefMat[lastUpdatedRow][this.rightHandSideIndex - 3] = lastPoint.getX();
        coefMat[lastUpdatedRow][this.rightHandSideIndex - 2] = Math.pow(lastPoint.getX(), 2);
        coefMat[lastUpdatedRow][this.rightHandSideIndex - 1] = Math.pow(lastPoint.getX(), 3);
        coefMat[lastUpdatedRow][this.rightHandSideIndex] = lastPoint.getY();
        lastUpdatedRow++;

        // Start slope coefficient
        coefMat[lastUpdatedRow][0] = 0;
        coefMat[lastUpdatedRow][1] = 1;
        coefMat[lastUpdatedRow][2] = 2 * firstPoint.getX();
        coefMat[lastUpdatedRow][3] = 3 * Math.pow(firstPoint.getX(), 2);
        coefMat[lastUpdatedRow][this.rightHandSideIndex] = alpha;
        lastUpdatedRow++;

        // End slope coefficient
        coefMat[lastUpdatedRow][this.rightHandSideIndex - 4] = 0;
        coefMat[lastUpdatedRow][this.rightHandSideIndex - 3] = 1;
        coefMat[lastUpdatedRow][this.rightHandSideIndex - 2] = 2 * lastPoint.getX();
        coefMat[lastUpdatedRow][this.rightHandSideIndex - 1] = 3 * Math.pow(lastPoint.getX(), 2);
        coefMat[lastUpdatedRow][this.rightHandSideIndex] = beta;
        lastUpdatedRow++;
    }

    // The inner coefficients where the current point is equal to the next point at the same y-coordinate
    private void innerCoefficients(double[][] coefMatrix, Point currPoint) {
        // Coefficient of p(t)
        // Connection point (p(t) = currPoint.y)
        coefMatrix[lastUpdatedRow][lastUpdatedCol] = 1;
        coefMatrix[lastUpdatedRow][lastUpdatedCol + 1] = currPoint.getX();
        coefMatrix[lastUpdatedRow][lastUpdatedCol + 2] = Math.pow(currPoint.getX(), 2);
        coefMatrix[lastUpdatedRow][lastUpdatedCol + 3] = Math.pow(currPoint.getX(), 3);
        coefMatrix[lastUpdatedRow][this.rightHandSideIndex] = currPoint.getY();
        lastUpdatedRow++;

        // first derivative (p'(t) = q'(t))
        coefMatrix[lastUpdatedRow][lastUpdatedCol] = 0;
        coefMatrix[lastUpdatedRow][lastUpdatedCol + 1] = 1;
        coefMatrix[lastUpdatedRow][lastUpdatedCol + 2] = 2 * currPoint.getX();
        coefMatrix[lastUpdatedRow][lastUpdatedCol + 3] = 3 * Math.pow(currPoint.getX(), 2);

        coefMatrix[lastUpdatedRow][lastUpdatedCol + 4] = 0;
        coefMatrix[lastUpdatedRow][lastUpdatedCol + 5] = -1;
        coefMatrix[lastUpdatedRow][lastUpdatedCol + 6] = -2 * currPoint.getX();
        coefMatrix[lastUpdatedRow][lastUpdatedCol + 7] = -3 * Math.pow(currPoint.getX(), 2);
        coefMatrix[lastUpdatedRow][this.rightHandSideIndex] = 0;
        lastUpdatedRow++;

        // Connection point q(t) = currPoint.y
        coefMatrix[lastUpdatedRow][lastUpdatedCol + 4] = 1;
        coefMatrix[lastUpdatedRow][lastUpdatedCol + 5] = currPoint.getX();
        coefMatrix[lastUpdatedRow][lastUpdatedCol + 6] = Math.pow(currPoint.getX(), 2);
        coefMatrix[lastUpdatedRow][lastUpdatedCol + 7] = Math.pow(currPoint.getX(), 3);
        coefMatrix[lastUpdatedRow][this.rightHandSideIndex] = currPoint.getY();
        lastUpdatedRow++;

        // second derivative (p''(t) = q''(t))
        coefMatrix[lastUpdatedRow][lastUpdatedCol] = 0;
        coefMatrix[lastUpdatedRow][lastUpdatedCol + 1] = 0;
        coefMatrix[lastUpdatedRow][lastUpdatedCol + 2] = 2;
        coefMatrix[lastUpdatedRow][lastUpdatedCol + 3] = 6 * currPoint.getX();

        coefMatrix[lastUpdatedRow][lastUpdatedCol + 4] = 0;
        coefMatrix[lastUpdatedRow][lastUpdatedCol + 5] = 0;
        coefMatrix[lastUpdatedRow][lastUpdatedCol + 6] = -2;
        coefMatrix[lastUpdatedRow][lastUpdatedCol + 7] = -6 * currPoint.getX();
        coefMatrix[lastUpdatedRow][this.rightHandSideIndex] = 0;
        lastUpdatedRow++;
        lastUpdatedCol += 4;
    }

    private void rowSwap(double[][] coefMat, int currRow) {
        for (int i = 0; i < coefMat.length - 1; i++) {
            if (i < currRow) {
                if (coefMat[i][currRow] != 0 && coefMat[currRow][i] != 0) {
                    // Perform row swap
                    double[] prevRow = coefMat[currRow];
                    coefMat[currRow] = coefMat[i];
                    coefMat[i] = prevRow;
                    break;
                }
            } else {
                if (coefMat[i][currRow] != 0) {
                    // Perform row swap
                    double[] prevRow = coefMat[currRow];
                    coefMat[currRow] = coefMat[i];
                    coefMat[i] = prevRow;
                    break;
                }
            }
        }

    }

    // Implement a local search algorithm to create a non-zero diagonal for the matrix
    private void formatMatrix() {
        for (int i = 0; i < coefMat.length - 1; i++) {
            if (coefMat[i][i] == 0) {
                // Search for row to swap, the row to be swapped must satisfy 2 conditions
                rowSwap(coefMat, i);
            }
        }
    }


    // Gaussian elimination to get the reduced row echelon form
    private void gaussianElimination() {
        // Simplified logic -> arrange the matrix to be non-zero diagonally using local search.
        formatMatrix();

        // Solve for rear echelon-form
        // Make everything below the diagonal to be 0
        for (int i = 0; i < coefMat.length - 1; i++) {
            for (int j = i + 1; j < coefMat.length; j++) {
                if (coefMat[j][i] != 0) {
                    double coefDiff = coefMat[i][i] / coefMat[j][i];
                    for (int k = i; k < coefMat[j].length; k++) {
                        coefMat[j][k] = coefMat[j][k] - coefMat[i][k] / coefDiff;
                    }
                }
            }
        }

        // Make everything above the diagonal to be 0
        for (int i = coefMat.length - 1;  i >= 0; i--) {
            for (int j = i - 1; j >= 0; j--) {
                if (coefMat[j][i] != 0) {
                    double coefDiff = coefMat[j][i] / coefMat[i][i];
                    // System.out.println(coefMat[j][i - 1] + " " + coefDiff + " " + coefMat[i][i]);
                    for (int k = coefMat[i].length - 1; k >= i; k--) {
                        coefMat[j][k] = coefMat[j][k] - coefMat[i][k] * coefDiff;
                    }
                }
            }
        }
    }

    private void solveForCoefficient() {
        for (int i = 0; i < coefMat.length; i++) {
            if (coefMat[i][i] != 1) {
                coefMat[i][this.rightHandSideIndex] = coefMat[i][this.rightHandSideIndex] / coefMat[i][i];
                coefMat[i][i] = 1.0;
            }
        }
    }

    private void generateSplineFunctions() {
        for (int i = 0; i < this.coefMat.length / 4; i++) {
            this.trajectories.add(new Trajectory(
                    this.coefMat[i * 4][this.rightHandSideIndex],
                    this.coefMat[i * 4 + 1][this.rightHandSideIndex],
                    this.coefMat[i * 4 + 2][this.rightHandSideIndex],
                    this.coefMat[i * 4 + 3][this.rightHandSideIndex]));
        }

        // Check if the spline is going out of bounds, then generate a straight line
        int pointIndex = 0;
        for (Trajectory trajectory : this.trajectories) {
            double[] coef = trajectory.getCoefficients();
            double[] coefDerivative = new double[]{coef[1], 2 * coef[2], 3 * coef[3]};

            double delta = Math.pow(coefDerivative[1], 2) - (4 * coefDerivative[0] * coefDerivative[2]);

            if (Math.sqrt(delta) > 0) {
                double firstSol = (-coefDerivative[1] - Math.sqrt(delta)) / (2 * coefDerivative[2]);
                double secondSol = (-coefDerivative[1] + Math.sqrt(delta)) / (2 * coefDerivative[2]);
                if (trajectory.getFuncOutput(firstSol) < verticalBoundaries[0] ||
                        trajectory.getFuncOutput(secondSol) < verticalBoundaries[0] ||
                        trajectory.getFuncOutput(firstSol) > verticalBoundaries[1] ||
                        trajectory.getFuncOutput(secondSol) > verticalBoundaries[1]) {

                    Point currPoint = this.listOfPoints.get(pointIndex);
                    Point nextPoint = this.listOfPoints.get(pointIndex + 1);

                    double slope = (nextPoint.getY() - currPoint.getY()) /
                            (nextPoint.getX() - currPoint.getX());
                    double intercept = nextPoint.getY() - slope * nextPoint.getX();

                    trajectory.setCoefficient(intercept, slope, 0, 0);
                }
            }
            pointIndex++;
        }
    }

    // Allow control of the trajectory curvature with a bezier curve control points  (reference: https://javascript.info/bezier-curve)
    public ArrayList<Trajectory> generateBezierTrajectories() {
        return this.trajectories;
    }

    // Fit a list of points with a cubic spline
    public ArrayList<Trajectory> generateCubicSplineTrajectories() {
        if (!listOfPoints.isEmpty()) {
            int dof = 1;
            if (listOfPoints.size() == 2) {
                dof = 4;
            } else if (listOfPoints.size() > 2) {
                dof = 4 + 4 * (listOfPoints.size() - 2);
            }

            coefMat = new double[dof][dof + 1];
            this.rightHandSideIndex = dof;
            // slope freedom constants
            double alpha = 1.0;
            double beta = 1.0;
            boundaryCoefficients(alpha, beta);

            for (int i = 1; i < this.listOfPoints.size() - 1; i++) {
                innerCoefficients(coefMat, this.listOfPoints.get(i));
            }

            // obtain row echelon form through gaussian elimination
            gaussianElimination();
            // obtain the coefficient for the spline functions
            solveForCoefficient();
            // Get the trajectories as functions
            generateSplineFunctions();
        }
        return this.trajectories;
    }

}
