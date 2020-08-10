package tetris;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;

import java.util.Random;

public class Cube {
    private Color color;
    private IntegerProperty row;
    private IntegerProperty column;
    private int rotation = 0;
    private int type = 0;

    private int[][] matrix = new int[4][4];

    public Cube() {
        super();
    }

    public Cube(int row, int column) {
        super();
        this.row = new SimpleIntegerProperty(row);
        this.column = new SimpleIntegerProperty(column);
    }

    public void randomize() {
        Random r = new Random();
        int number = r.nextInt(7);
        matrix = Templates.getTemplate(number);
        color = Templates.getColor(number + 1);
        type = number;
        rotation = 0;
    }

    public void rotate() {
        matrix = this.getNextMatrix();
        rotation = (rotation + 1) % 4;
    }

    public int[][] getNextMatrix() {
        int[][] newMatrix = new int[this.getWidth()][this.getHeight()];
        for (int w = 0; w < this.getWidth(); w++) {
            for (int h = 0; h < this.getHeight(); h++) {
                newMatrix[w][h] = matrix[h][w];
            }
        }

        int[][] tmpMatrix = new int[this.getWidth()][this.getHeight()];

        for (int h = 0; h < this.getWidth(); h++) {
            tmpMatrix[h] = newMatrix[newMatrix.length - h - 1];
        }

        return tmpMatrix;
    }


    public void moveDown() {
        setRow(getRow() + 1);
    }

    public final int getWidth() {
        return matrix[0].length;
    }

    public final int getFutureWidth() { return this.getHeight(); }

    public final int getHeight() {
        return matrix.length;
    }

    public final int getFutureHeight() { return this.getWidth(); }

    public final int[][] getMatrix() {
        return matrix;
    }

    public final Color getColor() { return color; }

    public final int getRotation() {
        return rotation;
    }

    public final int getFutureRotation() {
        return (rotation + 1) % 4;
    }

    public final int getType() {
        return type;
    }

    public final void setType(int type) {
        matrix = Templates.getTemplate(type);
        this.type = type;
    }

    public final int getOffset(int axis) {
        return Templates.getOffset(this.getRotation(), this.getType())[axis];
    }

    public final int getFutureOffset(int axis) {
        return Templates.getOffset(this.getFutureRotation(), this.getType())[axis];
    }

    public final IntegerProperty rowProperty() {
        return this.row;
    }

    public final void setRowProperty(IntegerProperty row) { this.row = row; }

    public final int getRow() {
        return this.rowProperty().get();
    }

    public final void setRow(final int row) {
        this.rowProperty().set(row);
    }

    public final IntegerProperty columnProperty() {
        return this.column;
    }

    public final void setColumnProperty(IntegerProperty column) { this.column = column; }

    public final int getColumn() {
        return this.columnProperty().get();
    }

    public final void setColumn(final int column) {
        this.columnProperty().set(column);
    }

    public Cube makeClone() {
        Cube clone = new Cube();
        clone.column = new SimpleIntegerProperty();
        clone.row = new SimpleIntegerProperty();
        clone.color = color;
        clone.rotation = rotation;
        clone.type = type;
        clone.matrix = matrix;
        return clone;
    }
}