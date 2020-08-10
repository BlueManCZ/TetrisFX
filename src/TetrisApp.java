package tetris;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class TetrisApp extends Application {

    private static final int DELAY = 500; // ms

    private static final int UNIT_SIZE = 30; // px

    private static final int RADIUS = 10; // px

    private static final int SPACE = 1; // px

    private GameState game = new GameState();

    private Pane cubesLayer;
    private Pane fallingLayer;

    private boolean enablePrediction = true;

    private Timeline timeline;

    @Override
    public void start(Stage primaryStage) {
        createGameLayers();

        timeline = new Timeline();
        KeyFrame updates = new KeyFrame(
                Duration.millis(DELAY),
                e -> {
                    if (game.update()) gameOver();
                    if (game.checkUpdate()) {
                        redrawGame();
                    }
                }
        );
        timeline.getKeyFrames().add(updates);
        timeline.setCycleCount(Animation.INDEFINITE);

        Button btnStart = new Button("Start");
        btnStart.visibleProperty().bind(game.activeProperty().not());
        btnStart.setOnAction(e -> {
            game.reinitialize();
            redrawGame();
            game.setActive(true);
            cubesLayer.setOpacity(0.9);
            fallingLayer.setOpacity(1);
            timeline.play();
        });

        btnStart.setScaleX(1.7);
        btnStart.setScaleY(1.7);

        StackPane gameStack = new StackPane();
        gameStack.getChildren().addAll(cubesLayer, fallingLayer, btnStart);

        BorderPane root = new BorderPane();
        root.setCenter(gameStack);

        Scene scene = new Scene(root, GameState.COLUMNS * UNIT_SIZE + 10, GameState.ROWS * UNIT_SIZE + 10);
        scene.setOnKeyPressed(this::dispatchKeyEvents);

        primaryStage.setTitle("TetrisFX");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createGameLayers() {
        cubesLayer = new Pane();
        cubesLayer.setMaxWidth(UNIT_SIZE * GameState.COLUMNS);
        cubesLayer.setMaxHeight(UNIT_SIZE * GameState.ROWS);

        redrawCubesLayer();

        fallingLayer = new Pane();
        fallingLayer.setMaxWidth(UNIT_SIZE * GameState.COLUMNS);
        fallingLayer.setMaxHeight(UNIT_SIZE * GameState.ROWS);
    }

    private List<Rectangle> createCube(Cube cube, Color fill) {
        List<Rectangle> polygons = new ArrayList<>();
        for (int w = 0; w < cube.getWidth(); w++) {
            for (int h = 0; h < cube.getHeight(); h++) {
                if (cube.getMatrix()[h][w] != 0) {
                    Rectangle rect = new Rectangle(UNIT_SIZE - SPACE, UNIT_SIZE - SPACE);
                    rect.setFill(fill);
                    rect.xProperty().bind(cube.columnProperty().add(w).add(cube.getOffset(0)).multiply(UNIT_SIZE));
                    rect.yProperty().bind(cube.rowProperty().add(h).add(cube.getOffset(1)).multiply(UNIT_SIZE));
                    rect.setArcWidth(RADIUS);
                    rect.setArcHeight(RADIUS);

                    InnerShadow is = new InnerShadow();
                    is.setOffsetY(1.0);
                    is.setOffsetX(1.0);
                    is.setColor(fill.darker());
                    rect.setEffect(is);

                    polygons.add(rect);
                }
            }
        }
        return polygons;
    }

    private void redrawGame() {
        redrawCubesLayer();
        redrawFallingLayer();
    }

    private void redrawCubesLayer() {
        cubesLayer.getChildren().removeAll(cubesLayer.getChildren());

        int[][] grid = game.getGameGrid();

        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                Rectangle rect = new Rectangle(UNIT_SIZE - 1, UNIT_SIZE - 1);
                rect.setX(c * UNIT_SIZE);
                rect.setY(r * UNIT_SIZE);
                rect.setArcWidth(RADIUS);
                rect.setArcHeight(RADIUS);
                if (grid[r][c] == 0) rect.setFill(Color.rgb(230, 230, 230));
                else rect.setFill(Templates.getColor(grid[r][c]));
                rect.setVisible(true);

                InnerShadow is = new InnerShadow();
                is.setOffsetY(1.0);
                is.setOffsetX(1.0);

                if (grid[r][c] == 0) is.setColor(Color.rgb(210, 210, 210));
                else is.setColor(Templates.getColor(grid[r][c]).darker());
                rect.setEffect(is);

                cubesLayer.getChildren().add(rect);
            }
        }
    }

    private void redrawFallingLayer() {
        fallingLayer.getChildren().removeAll(fallingLayer.getChildren());
        if (enablePrediction)
            fallingLayer.getChildren().addAll(createCube(game.getPredictedCube(), Color.rgb(220, 220, 220)));
        fallingLayer.getChildren().addAll(createCube(game.getCube(), game.getCube().getColor()));
    }

    /**
     * Provede zastaveni hry
     */
    private void gameOver() {
        timeline.stop();
        cubesLayer.setOpacity(0.2);
        fallingLayer.setOpacity(0.2);
    }

    /**
     * Sleduje stisknete klavesy a pradava udalosti do stavu hry
     */
    private void dispatchKeyEvents(KeyEvent e) {
        if (game.isActive()) {
            switch (e.getCode()) {
                case LEFT: game.moveLeft(); redrawFallingLayer(); break;
                case RIGHT: game.moveRight(); redrawFallingLayer(); break;
                case UP: game.rotateCube(); redrawFallingLayer(); break;
                case ENTER: case SPACE:
                    if (game.skipFalling()) {
                        timeline.playFromStart();
                        redrawGame();
                    } break;
                case DOWN:
                    if (game.update()) gameOver();
                    timeline.playFromStart();
                    if (game.checkUpdate()) {
                        redrawGame();
                    }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
