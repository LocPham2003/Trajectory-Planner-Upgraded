package com.example.trajectoryplanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    MainController mainController = new MainController();
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

    public void getControlPoints(Scene scene) {
        scene.setOnMouseClicked(event -> mainController.addPoint(new Point(event.getSceneX(), event.getSceneY())));
    }

    public void menuBarEventHandler(MenuBar menuBar) {
        for (Menu menu : menuBar.getMenus()) {
            for (MenuItem menuItem : menu.getItems()) {
                menuItem.setOnAction(event -> {
                    System.out.println(menuItem.getId());
                    switch(menuItem.getId()) {
                        case "Cubic Splines":
                            System.out.println("Cubic Spline Selected");
                            mainController.interpolate(0);
                            break;
                        case "Bezier":
                            System.out.println("Bezier lmao");
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
        Scene scene = new Scene(root, 1000, 800);

        // Generate scene's assets
        MenuBar sceneMenuBar = createMenuBar();
        root.setTop(sceneMenuBar);
        stage.setTitle("Trajectory planner - by Loc Pham");

        // Assign event handlers to relevant graphic nodes
        getControlPoints(scene);
        menuBarEventHandler(sceneMenuBar);

        // Show scene
        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }
}