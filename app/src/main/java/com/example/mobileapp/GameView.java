package com.example.mobileapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class GameView extends View {
    Paint paint = new Paint();
    GameDataModel model;

    public GameView(Context context, GameDataModel model) {
        super(context);
        this.model = model;

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(8);
        this.setFocusableInTouchMode(true);
    }

    public void onDraw(Canvas canvas) {

        for(Area[] areas : model.getAreas()) {
            for(Area area : areas) {
                area.draw(canvas);
            }
        }

        for(Line line : model.getLines()) {
            line.draw(canvas);
        }

        for(GridPoint[] p_list: model.getGridPoints()) {
            for(GridPoint p : p_list) {
                paint.setColor(Color.BLACK);
                RectF bounds = new RectF();
                bounds.set(p.getX()-Line.WIDTH/2, p.getY()-Line.WIDTH/2, p.getX()+Line.WIDTH/2, p.getY()+Line.WIDTH/2);
                canvas.drawRect(bounds, paint);
            }
        }

        paint.setTextSize(getWidth()/10);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.GREEN);
//        canvas.drawText("Player1: " + model.getPlayers()[0].getScore(), 0,100,paint);
        canvas.drawText("Player1", 0,getWidth()/10,paint);
        paint.setTextSize((int)(getWidth()/10*1.2));
        canvas.drawText(model.getPlayers()[0].getScore()+"",getWidth()/10, getWidth()/10*2, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setColor(Color.BLUE);
        paint.setTextSize(getWidth()/10);
//        canvas.drawText("Player2: " + model.getPlayers()[1].getScore(), getWidth(),100,paint);
        canvas.drawText("Player2", getWidth(),100,paint);
        paint.setTextSize((int)(getWidth()/10*1.2));
        canvas.drawText(model.getPlayers()[1].getScore()+"",getWidth()-getWidth()/10, getWidth()/10*2, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize((int)(getWidth()/10*1.2));
        if(!model.checkGameOver()){
            paint.setColor(Color.BLACK);
            if(GameDataModel.LEFT_TIME <= 5)
                paint.setColor(Color.RED);
            canvas.drawText(""+ GameDataModel.LEFT_TIME, getWidth()/2, (int)(getWidth()/10*2.5), paint);

            if(model.getTurn() % 2 == 0)
                paint.setColor(Color.GREEN);
            else
                paint.setColor(Color.BLUE);
            canvas.drawText("Player " + (model.getTurn() % 2 == 0 ? "1" : "2") + "'s Turn", getWidth()/2,(int)(getWidth()/10*3.5), paint);
        }
        else {
            paint.setColor(Color.RED);
            paint.setTextSize((int)(getWidth()/10*1.2));
            canvas.drawText("GAME OVER", getWidth()/2,(int)(getWidth()/10*3), paint);
            if (model.isDrawGame)
                canvas.drawText("DRAW", getWidth()/2,(int)(getWidth()/10*4), paint);
            else
                canvas.drawText("Player " + (model.getPlayers()[0].getScore() > model.getPlayers()[1].getScore() ? "1" : "2") + " WIN!!!", getWidth()/2,(int)(getWidth()/10*4), paint);

            paint.setColor(Color.BLACK);
//            canvas.drawText("RETRY??", getWidth()/2,getHeight() - getHeight()/12, paint);
            paint.setTextSize((int)(getWidth()/10*0.6));
            canvas.drawText("재시작 하려면 화면을 터치", getWidth()/2, getHeight() - getHeight()/24, paint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float currentX = event.getX();
        float currentY = event.getY();
        Line touchLine = null;
        boolean isClickedLine = false;

        if(!model.checkGameOver()){
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    for (Line line : model.getLines()) {
                        touchLine = line;
                        if(line.getLINE_TYPE() == LINE_TYPE.HORIZONTAL) {
                            if (currentX > line.getP1().getX() && currentX < line.getP2().getX()
                                    && currentY > line.getP1().getY() - Line.WIDTH && currentY < line.getP2().getY() + Line.WIDTH) {

                                if(line.getDraw() == true)
                                    return false;

                                line.getPaint().setColor(Color.BLACK);
                                line.setDraw(true);
                                isClickedLine = true;
                                Log.d("ddamddi", " [LINE]   ("+(line.getP1().getX()-GameDataModel.getxMin())/GameDataModel.getLENGTH() + "," +(line.getP1().getY()-GameDataModel.getyMin())/GameDataModel.getLENGTH() + ")-(" + (line.getP2().getX()-GameDataModel.getxMin())/GameDataModel.getLENGTH() + "," + (line.getP2().getY()-GameDataModel.getyMin())/GameDataModel.getLENGTH() + ") is Drawn");
                                break;
                            }
                        }
                        else {
                            if (currentX > line.getP1().getX() - Line.WIDTH && currentX < line.getP2().getX() + Line.WIDTH
                                    && currentY > line.getP1().getY() && currentY < line.getP2().getY()) {

                                if(line.getDraw() == true)
                                    return false;

                                line.getPaint().setColor(Color.BLACK);
                                line.setDraw(true);
                                isClickedLine = true;
                                Log.d("ddamddi", " [LINE]   ("+(line.getP1().getX()-GameDataModel.getxMin())/GameDataModel.getLENGTH() + "," +(line.getP1().getY()-GameDataModel.getyMin())/GameDataModel.getLENGTH() + ")-(" + (line.getP2().getX()-GameDataModel.getxMin())/GameDataModel.getLENGTH() + "," + (line.getP2().getY()-GameDataModel.getyMin())/GameDataModel.getLENGTH() + ") is Drawn");
                                break;
                            }
                        }   // LINE_TYPE.VERTICAL
                    }
                    if(!isClickedLine)
                        return false;

                    if(!model.checkArea(touchLine))
                        model.turnover();

                    if(model.checkGameOver()){
                        for(int x = 0; x < 10; ++x)
                            Log.d("ddamddi", "[GAME OVER]");
//                            Log.d("ddamddi", "[GAME OVER] Player " + (model.getPlayers()[0].getScore() > model.getPlayers()[1].getScore() ? "1" : "2") + " WIN!!!");
                    }
                    invalidate();
                    break;
            }
        }
        else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    model.init();
                    invalidate();
                    break;
            }
        }
        return true;
    }
}

