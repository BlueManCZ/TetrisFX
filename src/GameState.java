package tetris;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class GameState {

	public static final int ROWS = 16;
	public static final int COLUMNS = 10;

	public static final int DEFAULT_X = COLUMNS / 2 - 1;
	public static final int DEFAULT_Y = 0;

    private Cube cube = new Cube(DEFAULT_Y, DEFAULT_X);
    private Cube predictedCube = cube.makeClone();

    private int gameGrid[][] = new int[ROWS][COLUMNS];

	private BooleanProperty active = new SimpleBooleanProperty(false);

	private boolean updateNeeded = false;

	public GameState() {
        reinitialize();
	}

	public void reinitialize() {
		setDefaultValues();
        updatePredictedCube();
	}

	private void setDefaultValues() {
        resetCube();

        for (int w = 0; w < COLUMNS; w++) {
            for (int h = 0; h < ROWS; h++) {
                gameGrid[h][w] = 0;
            }
        }
	}

	synchronized public boolean update() {
	    boolean result = checkCollisions(cube, 0, 1, false);

	    if (cube.getRow() + cube.getOffset(1) + cube.getHeight() > ROWS - 1) result = false;

		if (result) cube.moveDown();
		else {
		    if (cube.getRow() == 0) {
		        setActive(false);
		        return true;
            }
		    saveCube(cube);
            removeFullRows();
		    resetCube();
        }

        return false;
	}

	public boolean skipFalling() {
	    if (cube.getRow() > 0) {
            boolean result = saveCube(predictedCube);
            removeFullRows();
            resetCube();
            return result;
        } else return false;
    }

	private boolean saveCube(Cube cube) {
        for (int w = 0; w < cube.getWidth(); w++) {
            for (int h = 0; h < cube.getHeight(); h++) {
                if (cube.getMatrix()[h][w] != 0) {
                    gameGrid[cube.getRow() + cube.getOffset(1) + h]
                            [cube.getColumn() + cube.getOffset(0) + w] = cube.getMatrix()[h][w];
                    updateNeeded = true;
                }
            }
        }
        return updateNeeded;
    }

    private void recalculatePrediction() {
        predictedCube.setRow(cube.getRow());

        while (checkCollisions(predictedCube, 0, 1, false)) {
	        predictedCube.setRow(predictedCube.getRow() + 1);
        }
    }

    private void removeFullRows() {
	    for (int h = 0; h < gameGrid.length; h++) {
	        int counter = 0;
	        for (int w = 0; w < gameGrid[0].length; w++) {
	            if (gameGrid[h][w] != 0) counter++;
            }

            if (counter == COLUMNS) {
                for (int h2 = h; h2 > 0; h2--) {
                    for (int i = 0; i < gameGrid[0].length; i++) {
                        gameGrid[h2][i] = gameGrid[h2 - 1][i];
                    }
                }

                for (int i = 0; i < gameGrid[0].length; i++) {
                    gameGrid[0][i] = 0;
                }
            }
        }
    }

    public void resetCube() {
	    cube.randomize();
	    cube.setColumn(DEFAULT_X);
	    cube.setRow(DEFAULT_Y);
        updatePredictedCube();
    }

    public void updatePredictedCube() {
        predictedCube = cube.makeClone();
        predictedCube.setColumnProperty(cube.columnProperty());
        recalculatePrediction();
    }

	private boolean checkRotation(Cube cube) {
        if (cube.getColumn() + cube.getFutureOffset(0) < 0) return false;
        if (cube.getColumn() + cube.getFutureWidth() + cube.getFutureOffset(0) > COLUMNS) return false;
        if (cube.getRow() + cube.getFutureHeight() + cube.getFutureOffset(1) > ROWS) return false;

        return checkCollisions(cube, 0, 0, true);
    }

    private boolean checkCollisions(Cube cube, int factorX, int factorY, boolean factorR) {
	    int[][] matrix = factorR ? cube.getNextMatrix() : cube.getMatrix();
	    int offsetX = factorR ? cube.getFutureOffset(0) : cube.getOffset(0);
	    int offsetY = factorR ? cube.getFutureOffset(1) : cube.getOffset(1);

        for (int w = 0; w < matrix[0].length; w++) {
            for (int h = 0; h < matrix.length; h++) {
                int row = cube.getRow() + h + offsetY + factorY;
                int column = cube.getColumn() + w + offsetX + factorX;
                if (row < ROWS && row >= 0 && column < COLUMNS && column >= 0) {
                    if (gameGrid[row][column] != 0 && matrix[h][w] != 0) {
                        return false;
                    }
                } else return false;
            }
        }

        return true;
    }

    public boolean checkUpdate() {
	    boolean result = updateNeeded;
	    if (result) updateNeeded = false;

	    return result;
    }

	public void moveLeft() {
        moveCube(-1);
	}

	public void moveRight() {
		moveCube(1);
	}

	private void moveCube(int x) {
        if (!isActive() || !checkCollisions(cube, x, 0, false)) return;
        int cubePos = cube.getColumn();
        if ((x > 0) ? cubePos + cube.getWidth() + cube.getOffset(0) < COLUMNS : cubePos + cube.getOffset(0) > 0) {
            cube.setColumn(cubePos + x);
            updatePredictedCube();
        }
    }

	public void rotateCube() {
	    if (checkRotation(cube)) {
	        cube.rotate();
            updatePredictedCube();
        }
    }

    public Cube getCube() {
        return cube;
    }

    public Cube getPredictedCube() { return predictedCube; }

    public int[][] getGameGrid() {
        return gameGrid;
    }

	public final BooleanProperty activeProperty() { return this.active; }
	
	public final boolean isActive() { return this.activeProperty().get(); }
	
	public final void setActive(final boolean active) { this.activeProperty().set(active); }
}
