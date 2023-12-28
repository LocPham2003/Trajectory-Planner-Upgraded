package com.example.trajectoryplanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

public class MainApplication extends Application {
    // Control variables
    private boolean isSelected = false;
    private boolean isShifted = false;
    private boolean isBezier = false;
    private double[] verticalBoundaries = new double[2];

    // Main UI components
    Pane canvas;
    MenuBar sceneMenuBar;
    Scene mainScene;

    MainController mainController;
    ArrayList<Trajectory> generatedTrajectories = new ArrayList<>();
    ArrayList<Circle> controlPointsUI = new ArrayList<>();
    ArrayList<Circle> selectedControlPointsUI = new ArrayList<>();
    ArrayList<Circle> selectedBezierSegments = new ArrayList<>();
    Circle pointDragUI; // The UI element of the point that is being dragged.
    Point pointDrag; // The mathematical object of the point that is being dragged.
    public MenuBar createMenuBar() {
        // TO-DO: Generate this dynamically by reading from JSON.
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu solveMenu = new Menu("Solve");

        String[] fileMenuOptions = new String[]{"New", "Save"};
        String[] editMenuOptions = new String[]{"Select", "Delete", "Shift", "Bezier"};
        String[] solveMenuOptions = new String[]{"Cubic Splines", "Bezier - 1 control point", "Bezier - 2 control points"};

        for (String fileMenuOption : fileMenuOptions) {
            MenuItem fileMenuItem = new MenuItem(fileMenuOption);
            fileMenuItem.setId(fileMenuOption);
            fileMenu.getItems().add(fileMenuItem);
        }

        for (String editMenuOption : editMenuOptions) {
            MenuItem editMenuItem;
            if (editMenuOption.equals("Select") || editMenuOption.equals("Shift") || editMenuOption.equals("Bezier")) {
                editMenuItem = new CheckMenuItem(editMenuOption);
            } else {
                editMenuItem = new MenuItem(editMenuOption);
            }
            editMenuItem.setId(editMenuOption);
            editMenu.getItems().add(editMenuItem);
        }

        for (String solveMenuOption : solveMenuOptions) {
            MenuItem solveMenuItem = new MenuItem(solveMenuOption);
            solveMenuItem.setId(solveMenuOption);
            solveMenu.getItems().add(solveMenuItem);
        }

        menuBar.getMenus().addAll(fileMenu, editMenu, solveMenu);
        return menuBar;
    }

    // Adding or selecting control points for the splines
    public void pointManipulation() {
        mainScene.setOnMouseClicked(event -> {
            if (!this.isSelected && !this.isShifted && !this.isBezier) {
                if (event.getSceneY() > verticalBoundaries[0] && event.getSceneY() - Constants.CURSOR_SHIFT < verticalBoundaries[1]) {
                    drawPoint(event.getSceneX(), event.getSceneY() - Constants.CURSOR_SHIFT);
                    mainController.addPoint(new Point(event.getSceneX(), event.getSceneY() - Constants.CURSOR_SHIFT));
                }
            } else if ((this.isSelected || this.isBezier) && !this.isShifted) {
                for (Circle point : controlPointsUI) {
                    // Check which point is intersecting with the mouse cursor
                    if (Math.pow(event.getSceneX() - point.getCenterX(), 2) +
                            Math.pow(event.getSceneY() - Constants.CURSOR_SHIFT - point.getCenterY(), 2) <= Math.pow(Constants.CONTROL_POINTS_RADIUS, 2)) {
                        if (point.getFill() == Constants.CONTROL_POINTS_COLOR) {
                            if (this.isBezier) {
                                point.setFill(Constants.CONTROL_POINTS_BEZIER_COLOR);
                                selectedBezierSegments.add(point);
                            } else {
                                point.setFill(Constants.CONTROL_POINTS_SELECTED_COLOR);
                                selectedControlPointsUI.add(point);
                            }
                        } else {
                            point.setFill(Constants.CONTROL_POINTS_COLOR);
                            if (this.isBezier) {
                                selectedBezierSegments.add(point);
                            } else {
                                selectedControlPointsUI.remove(point);
                            }
                        }
                        break;
                    }
                }
            }
        });

        mainScene.setOnMousePressed(event -> {
            if (!this.isSelected && this.isShifted) {
                for (Circle point : controlPointsUI) {
                    // Check if there is a point intersecting with the mouse cursor
                    if (Math.pow(event.getSceneX() - point.getCenterX(), 2) +
                            Math.pow(event.getSceneY() - Constants.CURSOR_SHIFT - point.getCenterY(), 2) <= Math.pow(Constants.CONTROL_POINTS_RADIUS, 2)) {
                        this.pointDragUI = new Circle(point.getCenterX(), point.getCenterY(),
                                Constants.CONTROL_POINTS_RADIUS, Constants.POINT_DRAG_COLOR);
                        this.canvas.getChildren().add(this.pointDragUI);
                        break;
                    }
                }
                // Search the point in the list of control points that is selected
                for (Point point : mainController.getListOfPoints()) {
                    System.out.println(pointDragUI.getCenterX() + " " + point.getX() + " " + pointDragUI.getCenterY() + " " + point.getY());
                    if (Math.abs(pointDragUI.getCenterX() - point.getX()) <= Constants.COORDINATE_FLOAT_DIFF &&
                            Math.abs(pointDragUI.getCenterY() - point.getY()) <= Constants.COORDINATE_FLOAT_DIFF) {
                        this.pointDrag = point;
                        break;
                    }
                }
            }
        });

        mainScene.setOnMouseDragged(event -> {
            if (!this.isSelected && this.isShifted) {
                if (this.pointDragUI != null) {
                    this.pointDragUI.setCenterX(event.getSceneX());
                    this.pointDragUI.setCenterY(event.getSceneY() - Constants.CURSOR_SHIFT);
                }
            }
        });

        mainScene.setOnMouseReleased(event -> {
            if (!this.isSelected && this.isShifted) {
                if (this.pointDragUI != null) {
                    this.canvas.getChildren().remove(this.pointDragUI);
                    this.pointDrag.setX(this.pointDragUI.getCenterX());
                    this.pointDrag.setY(this.pointDragUI.getCenterY());
                    this.pointDragUI = null;
                    this.pointDrag = null;
                    // Update the trajectory
                    this.generatedTrajectories = mainController.interpolate(mainController.getCurrSplineType());
                    visualizeTrajectory();
                }
            }
        });
    }

    private void solveBezierSegment(int numControlPoints) {
        System.out.println(numControlPoints);
        for (Circle circle : selectedBezierSegments) {
            System.out.println(circle.getCenterX() + " " + circle.getCenterY());
        }
    }

    private void deselectPoints() {
        for (Circle point : controlPointsUI) {
            point.setFill(Constants.CONTROL_POINTS_COLOR);
        }
    }

    private void deletePoints() {
        if (!selectedControlPointsUI.isEmpty()) {
            for (Circle pointUI : selectedControlPointsUI) {
                for (Point point : mainController.getListOfPoints()) {
                    if (Math.abs(pointUI.getCenterX() - point.getX()) <= Constants.COORDINATE_FLOAT_DIFF &&
                            Math.abs(pointUI.getCenterY() - point.getY()) <= Constants.COORDINATE_FLOAT_DIFF) {
                        mainController.removePoint(point);
                        break;
                    }
                }
            }

            // Clear list of control points
            selectedControlPointsUI.clear();
            // Update the trajectories
            this.generatedTrajectories = mainController.interpolate(mainController.getCurrSplineType());
            visualizeTrajectory();
        }
    }

    private Pane createCanvas(Scene scene) {
        Rectangle box = new Rectangle(scene.getWidth(), scene.getHeight(), Color.WHITE);
        return new Pane(box);
    }

    private void cleanCanvas() {
        canvas.getChildren().clear();
        this.controlPointsUI.clear();
    }

    private void visualizeTrajectory() {
        // Visualize trajectory on the canvas
        // Clear the previous trajectory
        cleanCanvas();

        ArrayList<Point> listOfPoints = mainController.getListOfPoints();
        if (listOfPoints.size() >= 2) {
            Point[] boundaryPoints = new Point[]{listOfPoints.get(0), listOfPoints.get(listOfPoints.size() - 1)};
            int currIndex = 0;
            // Draw the boundary point
            drawPoint(boundaryPoints[0].getX(), generatedTrajectories.get(currIndex).getFuncOutput(boundaryPoints[0].getX()));
            drawPoint(boundaryPoints[1].getX(), generatedTrajectories.get(generatedTrajectories.size() - 1).getFuncOutput(boundaryPoints[1].getX()));

            for (double i = boundaryPoints[0].getX(); i <= boundaryPoints[1].getX() - 0.1; i += 0.01) {
                if (Math.abs(i - listOfPoints.get(currIndex + 1).getX()) <= 0.00000001) {
                    currIndex++;
                    drawPoint(i, generatedTrajectories.get(currIndex).getFuncOutput(i));
                }
                canvas.getChildren().add(new Line(i, generatedTrajectories.get(currIndex).getFuncOutput(i),
                        i + 0.1, generatedTrajectories.get(currIndex).getFuncOutput(i + 0.1)));
            }
        }
    }

    private void drawPoint(double x, double y) {
        Circle pointUI = new Circle(x, y, Constants.CONTROL_POINTS_RADIUS, Constants.CONTROL_POINTS_COLOR);
        canvas.getChildren().add(pointUI);
        this.controlPointsUI.add(pointUI);
    }

    public void menuBarEventHandler(MenuBar menuBar) {
        for (Menu menu : menuBar.getMenus()) {
            for (MenuItem menuItem : menu.getItems()) {
                menuItem.setOnAction(event -> {
                    System.out.println(menuItem.getId());
                    switch(menuItem.getId()) {
                        case "Select":
                            this.isSelected = !this.isSelected;
                            if (!this.isSelected) {
                                deselectPoints();
                            }
                            break;
                        case "Delete":
                            deletePoints();
                            break;
                        case "Shift":
                            this.isShifted = !this.isShifted;
                            break;
                        case "Cubic Splines":
                            generatedTrajectories = mainController.interpolate(0);
                            visualizeTrajectory();
                            break;
                        case "Bezier":
                            this.isBezier = !this.isBezier;
                            break;
                        case "Bezier - 1 control point":
                            solveBezierSegment(1);
                            break;
                        case "Bezier - 2 control points":
                            solveBezierSegment(2);
                            break;
                        default:
                            break;
                    }
                });

            }
        }
    }

    @Override
    public void start(Stage stage) {
        System.out.println("Generating the scene");
        // Generate the scene
        BorderPane root = new BorderPane();

        mainScene = new Scene(root);
        // Generate scene's assets
        sceneMenuBar = createMenuBar();
        root.setTop(sceneMenuBar);
        stage.setTitle("Trajectory planner - by Loc Pham");

        // Generate a canvas for visualization
        canvas = createCanvas(mainScene);
        root.setCenter(canvas);

        // Assign event handlers to relevant graphic nodes
        pointManipulation();
        menuBarEventHandler(sceneMenuBar);

        // Show scene
        stage.setScene(mainScene);
        stage.setMaximized(true);
        stage.show();

        // Update boundary
        this.verticalBoundaries[0] = canvas.getLayoutBounds().getMinY() + Constants.MENU_BAR_SHIFT;
        this.verticalBoundaries[1] = canvas.getLayoutBounds().getMaxY();
        mainController = new MainController(this.verticalBoundaries);

    }

    public static void main(String[] args) {
        launch();
    }
}