package tetris;

import javafx.scene.paint.Color;

public class Templates {
    private static int[][][] cubes = {
        {
            {0, 1, 1},
            {1, 1, 0}},
        {
            {2, 2, 0},
            {0, 2, 2}},
        {
            {0, 3, 0},
            {3, 3, 3}},
        {
            {4, 4},
            {4, 4}},
        {
            {0, 0, 5},
            {5, 5, 5}},
        {
            {6, 0, 0},
            {6, 6, 6}},
        {
            {7, 7, 7, 7}},
    };
    private static int[][] offsets = {
        {0, 0},
        {0, 0},
        {0, 1},
        {1, 0}};

    private static int[][] offsetsForI = {
        {0, 0},
        {1, -1},
        {0, 1},
        {2, -1}};

    private static Color[] colors = {Color.rgb(230, 230, 230), Color.web("#845ec2"), Color.web("#ff6f91"), Color.web("#ffc75f"), Color.web("#0081cf"), Color.web("#ff8066"), Color.web("#00c2a8"), Color.web("#bf34b4")};

    public static int[][] getTemplate(int index) {
        return cubes[index];
    }

    public static Color getColor(int index) {
        return colors[index];
    }

    public static int[] getOffset(int rotation, int type) {
        int[] oBlock = {0, 0};
        switch (type) {
            case 3: return oBlock;
            case 6: return offsetsForI[rotation];
            default: return offsets[rotation];
        }
    }
}
