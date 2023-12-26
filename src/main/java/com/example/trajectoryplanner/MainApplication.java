package com.example.trajectoryplanner;

import javafx.application.Application;
import javafx.scene.Scene;
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
import java.util.Collections;

public class MainApplication extends Application {
    // Main UI components
    Pane canvas;
    MenuBar sceneMenuBar;
    Scene mainScene;

    MainController mainController = new MainController();
    ArrayList<Trajectory> generatedTrajectories = new ArrayList<>();
    public MenuBar createMenuBar() {
        // TO-DO: Generate this dynamically by reading from JSON.
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu solveMenu = new Menu("Solve");

        String[] fileMenuOptions = new String[]{"New", "Save"};
        String[] editMenuOptions = new String[]{"Cut", "Copy", "Paste"};
        String[] solveMenuOptions = new String[]{"Cubic Splines", "Bezier"};

        for (String fileMenuOption : fileMenuOptions) {
            MenuItem fileMenuItem = new MenuItem(fileMenuOption);
            fileMenuItem.setId(fileMenuOption);
            fileMenu.getItems().add(fileMenuItem);
        }

        for (String editMenuOption : editMenuOptions) {
            MenuItem editMenuItem = new MenuItem(editMenuOption);
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

    public void getControlPoints() {
        mainScene.setOnMouseClicked(event -> {
            drawPoint(event.getSceneX(), event.getSceneY());
            System.out.println(event.getScreenX() + " " + event.getSceneY());
            mainController.addPoint(new Point(event.getSceneX(), event.getSceneY()));
        });
    }

    private Pane createCanvas(Scene scene) {
        Rectangle box = new Rectangle(scene.getWidth(), scene.getHeight(), Color.WHITE);
        return new Pane(box);
    }

    private void visualizeTrajectory() {
        // Visualize trajectory on the canvas
        // Clear the previous trajectory
        canvas.getChildren().clear();

        ArrayList<Point> listOfPoints = mainController.getListOfPoints();
        if (listOfPoints.size() >= 2) {
            Point[] boundaryPoints = new Point[]{listOfPoints.get(0), listOfPoints.get(listOfPoints.size() - 1)};
            int currIndex = 0;

            for (double i = boundaryPoints[0].getX(); i <= boundaryPoints[1].getX() - 0.1; i += 0.01) {
                if (Math.abs(i - listOfPoints.get(currIndex + 1).getX()) <= 0.00000001) {
                    currIndex++;
                }
                canvas.getChildren().add(new Line(i, generatedTrajectories.get(currIndex).getFuncOutput(i) - 25,
                        i + 0.1, generatedTrajectories.get(currIndex).getFuncOutput(i + 0.1) - 25));
            }
        }
    }

    private void drawPoint(double x, double y) {
        canvas.getChildren().add(new Circle(x, y - 25, 2, Color.BLACK));
    }

    public void menuBarEventHandler(MenuBar menuBar) {
        for (Menu menu : menuBar.getMenus()) {
            for (MenuItem menuItem : menu.getItems()) {
                menuItem.setOnAction(event -> {
                    System.out.println(menuItem.getId());
                    switch(menuItem.getId()) {
                        case "Cubic Splines":
                            generatedTrajectories = mainController.interpolate(0);
                            break;
                        case "Bezier":
                            generatedTrajectories = mainController.interpolate(1);
                            break;
                        default:
                            break;
                    }
                    visualizeTrajectory();
                });

            }
        }
    }

    @Override
    public void start(Stage stage) {
        System.out.println("Generating the scene");
        // Generate the scene
        BorderPane root = new BorderPane();

        mainScene = new Scene(root, 800, 800);
        // Generate scene's assets
        sceneMenuBar = createMenuBar();
        root.setTop(sceneMenuBar);
        stage.setTitle("Trajectory planner - by Loc Pham");

        // Generate a canvas for visualization
        canvas = createCanvas(mainScene);
        root.setCenter(canvas);

        // Assign event handlers to relevant graphic nodes
        getControlPoints();
        menuBarEventHandler(sceneMenuBar);

        // Show scene
        stage.setScene(mainScene);
        stage.setResizable(false);
        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }
}