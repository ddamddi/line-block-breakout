package com.example.mobileapp;
import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

public class GameActivity extends AppCompatActivity {
    GameView view;
    GameDataModel model;
    static GameTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Display display = getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        Log.d("ddamddi", " [SIZE]   WIDTH:" + screenSize.x + " HEIGHT:" + screenSize.y );

        model = new GameDataModel(screenSize);
        view = new GameView(this, model);
        timer = new GameTimer(view);
        timer.start();

        setContentView(view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.stop= true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.stop = false;
    }

    @Override
    public void onBackPressed() {
        timer.stop = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("게임종료")
                .setMessage("진행중인 게임을 종료하고 메뉴화면으로 돌아가시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GameActivity.super.onBackPressed();
                        timer.stop = false;
                        model.isBack = true;
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        timer.stop = false;
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
