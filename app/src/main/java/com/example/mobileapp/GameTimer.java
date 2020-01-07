package com.example.mobileapp;
import android.graphics.Color;
import android.util.Log;

import java.util.Random;

public class GameTimer extends Thread {
    GameView view;
    Random random;
    boolean stop = false;

    GameTimer(GameView v){
        view = v;
        random = new Random();
        Log.d("ddamddi","[TIMER]   Timer Start");
    }

    @Override
    public void run() {
        Log.d("ddamddi", "[THREAD]   Timer THREAD 시작");
        while(true) {
            try{
                if( GameDataModel.isBack )
                    break;

                while(GameDataModel.isGameOver || stop) { }// for stop thread
//                Log.d("ddamddi", "[TIMER]   " + GameDataModel.LEFT_TIME);
                sleep(1000);
                GameDataModel.LEFT_TIME--;
                while(GameDataModel.isGameOver || stop) { } // for stop thread

                // TIME OUT : 랜덤 LINE 하나 생성 후 턴 넘기기
                if(GameDataModel.LEFT_TIME <= 0) {
                    while (true) {
                        int idx = random.nextInt(GameDataModel.getLines().size());
                        Line line = GameDataModel.getLines().get(idx);
                        if (line.getDraw() == false) {
                            line.getPaint().setColor(Color.BLACK);
                            line.setDraw(true);
                            GameDataModel.checkArea(line);
                            break;
                        }
                    }
                    // 턴을 넘기고 타이머 다시 30으로 초기화
//                    Log.d("ddamddi", "[TIMER]   Time Over");
                    GameDataModel.turnover();
                    GameDataModel.LEFT_TIME = 20;
                }
                view.postInvalidate();
            }
            catch (InterruptedException e ){
                e.printStackTrace();
            }
        }
        Log.d("ddamddi", "[THREAD]   Timer THREAD 종료");
    }
}
