package com.example.mobileapp;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class GameDataModel {
    private GridPoint[][] gridPoints;
    private static ArrayList <Line> lines;
    private static Area[][] areas;
    private static Player[] players;
    private static int xMin, yMin, deadArea, LENGTH, RANDOM_CREATE;
    public static final int GRID = 5;
    public static int turn, LEFT_TIME;
    public static boolean isGameOver, isDrawGame, isBack;

    public GameDataModel(Point screenSize) {
        setGridSize(screenSize);
        init();
    }

    private void setGridSize(Point screenSize){
        xMin = screenSize.x / 12;
        yMin = screenSize.y / 10 * 3;
        LENGTH = screenSize.x / 6;
    }

    public void init(){
        players = new Player[2];
        gridPoints = new GridPoint[GRID+1][GRID+1];
        areas = new Area[GRID][GRID];
        lines = new ArrayList<>();
        turn = deadArea = 0;
        isGameOver = isDrawGame = isBack = false;

        // Player Initialize
        for(int i = 0; i < players.length; i++)
            players[i] = new Player();
        Log.d("ddamddi", " [INIT]   Players Initalize");

        // gridPoints Initialize
        for(int x = 0; x < GRID+1; x++) {
            for(int y = 0; y < GRID+1; y++) {
                gridPoints[x][y] = new GridPoint(xMin + x * LENGTH, yMin + y * LENGTH);
            }
        }
        Log.d("ddamddi", " [INIT]   GirdPoints Initialize");

        // areas Initialize
        for(int x = 0; x < GRID; x++) {
            for(int y = 0; y < GRID; y++) {
                areas[x][y] = new Area();
            }
        }
        Log.d("ddamddi", " [INIT]   Areas initialize");

        // Line Initialize
        Line tmp_line;
        for(int x = 0; x < GRID; x++) {
            for(int y = 0; y < GRID+1; y++) {
                tmp_line = new Line(gridPoints[x][y], gridPoints[x+1][y], LINE_TYPE.HORIZONTAL);
                lines.add(tmp_line);

                if(y > 0)
                    areas[x][y-1].addNeighborLine(tmp_line);
                if(y < GRID)
                    areas[x][y].addNeighborLine(tmp_line);

            }
        }
        for(int y = 0; y < GRID; y++) {
            for(int x = 0; x < GRID+1; x++) {
                tmp_line = new Line(gridPoints[x][y], gridPoints[x][y+1], LINE_TYPE.VERTICAL);
                lines.add(tmp_line);

                if(x > 0)
                    areas[x-1][y].addNeighborLine(tmp_line);
                if(x < GRID) {
                    areas[x][y].addNeighborLine(tmp_line);
                }
            }
        }
        Log.d("ddamddi", " [INIT]   Lines initialize    # of lines: " + lines.size());

//        for(int i = 0; i < GRID; i++) {
//            for(int j = 0; j < GRID; j++) {
//                Log.d("ddamddi",  "[" + i + "]["+j + "] : " + areas[i][j].getNeighborLines().size());
//                for(Line line : areas[i][j].getNeighborLines())
//                   Log.d("ddamddi",     line.getLINE_TYPE() + ":" + "("+(line.getP1().getX()-xMin)/LENGTH + "," +(line.getP1().getY()-yMin)/LENGTH + ") - (" + (line.getP2().getX()-xMin)/LENGTH + "," + (line.getP2().getY()-yMin)/LENGTH + ")");
//            }
//        }

        // Random Line Create
        Random random = new Random();
        RANDOM_CREATE = random.nextInt(lines.size() / 3);
        for(int i = 0; i < RANDOM_CREATE; i++) {
            int tmp = random.nextInt(lines.size());
            if(lines.get(tmp).getDraw() == false){
                lines.get(tmp).setDraw(true);
                lines.get(tmp).getPaint().setColor(Color.BLACK);
            }
            else
                i--;
                continue;
        }

        // Check and Count Dead Area
        for(int x = 0; x < GRID; x++) {
            for(int y = 0; y < GRID; y++) {
                if(areas[x][y].isLocked()){
                    areas[x][y].setOwner(-1);
                    deadArea++;
                }
            }
        }
        playerLog();
        LEFT_TIME = 20;
    }

    public static Player getNowPlayer(){
        Player nowPlayer = players[turn % 2];
        return nowPlayer;
    }

    public Player[] getPlayers(){
        return players;
    }

    public static void turnover() {
        Log.d("ddamddi","============================================================");
        playerLog();
        turn++; LEFT_TIME = 20;
    }

    public boolean checkGameOver() {
        int p1 = 0, p2 = 0, unOccupied = GRID * GRID - deadArea;

        // 플레이어가 획득한 영역(점수) 확인
        for(Area[] areas : areas) {
            for(Area area: areas) {
                if(area.getOccupied() && area.getOwner() == 1)
                    p1++;
                else if (area.getOccupied() && area.getOwner() == 2)
                    p2++;
            }
        }

        unOccupied -= (p1 + p2);
        if(unOccupied == 0) {
            isGameOver = true;
            if(p1 == p2)
                isDrawGame = true;
        }
        else{
            if(unOccupied < Math.abs(p1 - p2)){
                isGameOver = true;
            }
            else
                isGameOver = false;
        }
        return isGameOver;
    }

    public static boolean checkArea(Line touchline) {
        GridPoint std = touchline.getCenter();
        GridPoint index = new GridPoint((std.getX()-xMin)/LENGTH, (std.getY()-yMin)/LENGTH);
        boolean isOccupied = false;
        Area tmp;

        if(touchline == null)
            return false;

        if(touchline.getLINE_TYPE() == LINE_TYPE.HORIZONTAL) {
            // 아래 Area
            if(index.getY() < GRID) {
                tmp = areas[index.getX()][index.getY()];
                if(tmp.isLocked() && !tmp.getOccupied()){
                    tmp.setOccupied(true);
                    tmp.setOwner(turn % 2 + 1);

                    Log.d("ddamddi", " [AREA]   Areas[" + index.getX() + "][" + index.getY() +"] is Occupied");
                    getNowPlayer().addScore();
                    isOccupied = true;
                }
            }
            // 위 Area
            if(index.getY() > 0) {
                tmp = areas[index.getX()][index.getY()-1];
                if(tmp.isLocked() && !tmp.getOccupied()){
                    tmp.setOccupied(true);
                    tmp.setOwner(turn % 2 + 1);

                    Log.d("ddamddi", " [AREA]   Areas[" + index.getX() + "][" + (index.getY()-1) +"] is Occupied");
                    getNowPlayer().addScore();
                    isOccupied = true;
                }
            }
        }
        else {
            // 오른쪽 Area
            if(index.getX() < GRID) {
                tmp = areas[index.getX()][index.getY()];
                if(tmp.isLocked() && !tmp.getOccupied()){
                    tmp.setOccupied(true);
                    tmp.setOwner(turn % 2 + 1);

                    Log.d("ddamddi", " [AREA]   Areas[" + index.getX() + "][" + index.getY() +"] is Occupied");
                    getNowPlayer().addScore();
                    isOccupied = true;
                }
            }
            // 왼쪽 Area
            if(index.getX() > 0) {
                tmp = areas[index.getX()-1][index.getY()];
                if(tmp.isLocked() && !tmp.getOccupied()){
                    tmp.setOccupied(true);
                    tmp.setOwner(turn % 2 + 1);

                    Log.d("ddamddi", " [AREA]   Areas[" + (index.getX()-1) + "][" + index.getY() +"] is Occupied");
                    getNowPlayer().addScore();
                    isOccupied = true;
                }
            }
        }

        if(isOccupied) {
            turn--;
            turnover();
            LEFT_TIME = 10;
        }
        return isOccupied;
    }

    public static ArrayList<Line> getLines() { return lines; }

    public GridPoint[][] getGridPoints() { return gridPoints; }

    public Area[][] getAreas() { return areas; }

    public static int getLENGTH() { return LENGTH; }

    public static int getxMin() { return xMin; }

    public static int getyMin() { return yMin; }

    public int getTurn() {
        return turn;
    }

    public static void playerLog(){
        Log.d("ddamddi", "[SCORE]   PLAYER1 : " + players[0].getScore() + "   " + "PLAYER2 : " + players[1].getScore() + "   " + "Dead Area: " + deadArea);
        Log.d("ddamddi", " [TURN]   Player " + (turn % 2 + 1) + "'s Turn");
    }

}
