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

    // Main UI components
    Pane canvas;
    MenuBar sceneMenuBar;
    Scene mainScene;

    MainController mainController = new MainController();
    ArrayList<Trajectory> generatedTrajectories = new ArrayList<>();
    ArrayList<Circle> controlPointsUI = new ArrayList<>();
    ArrayList<Circle> selectedControlPointsUI = new ArrayList<>();
    public MenuBar createMenuBar() {
        // TO-DO: Generate this dynamically by reading from JSON.
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu solveMenu = new Menu("Solve");

        String[] fileMenuOptions = new String[]{"New", "Save"};
        String[] editMenuOptions = new String[]{"Select", "Delete", "Shift"};
        String[] solveMenuOptions = new String[]{"Cubic Splines", "Bezier"};

        for (String fileMenuOption : fileMenuOptions) {
            MenuItem fileMenuItem = new MenuItem(fileMenuOption);
            fileMenuItem.setId(fileMenuOption);
            fileMenu.getItems().add(fileMenuItem);
        }

        for (String editMenuOption : editMenuOptions) {
            MenuItem editMenuItem;
            if (editMenuOption.equals("Select")) {
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

    public void pointManipulation() {
        mainScene.setOnMouseClicked(event -> {
            if (!this.isSelected) {
                drawPoint(event.getSceneX(), event.getSceneY() - Constants.CURSOR_SHIFT);
                mainController.addPoint(new Point(event.getSceneX(), event.getSceneY() - Constants.CURSOR_SHIFT));
            } else {
                for (Circle point : controlPointsUI) {
                    // Check which point is intersecting with the mouse cursor
                    if (Math.pow(event.getSceneX() - point.getCenterX(), 2) +
                            Math.pow(event.getSceneY() - Constants.CURSOR_SHIFT - point.getCenterY(), 2) <= Math.pow(Constants.CONTROL_POINTS_RADIUS, 2)) {
                        point.setFill(Color.RED);
                        selectedControlPointsUI.add(point);
                        break;
                    }
                }
            }
        });
    }

    private void deselectPoints() {
        for (Circle point : controlPointsUI) {
            point.setFill(Constants.CONTROL_POINTS_COLOR);
        }
    }

    private void deletePoints() {
        for (Circle pointUI : selectedControlPointsUI) {
            for (Point point : mainController.getListOfPoints()) {
                if (Math.abs(pointUI.getCenterX() - point.getX()) <= Constants.COORDINATE_DIFF &&
                        Math.abs(pointUI.getCenterY() - point.getY()) <= Constants.COORDINATE_DIFF) {
                    mainController.removePoint(point);
                    break;
                }
            }
        }
        // Update the trajectories
        this.generatedTrajectories = mainController.interpolate(mainController.getCurrSplineType());
        visualizeTrajectory();
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
                            CheckMenuItem selectMode = (CheckMenuItem) menuItem;
                            this.isSelected = selectMode.isSelected();
                            if (!this.isSelected) {
                                deselectPoints();
                            }
                            break;
                        case "Delete":
                            deletePoints();
                            break;
                        case "Cubic Splines":
                            generatedTrajectories = mainController.interpolate(0);
                            visualizeTrajectory();
                            break;
                        case "Bezier":
                            generatedTrajectories = mainController.interpolate(1);
                            visualizeTrajectory();
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
    }

    public static void main(String[] args) {
        launch();
    }
}