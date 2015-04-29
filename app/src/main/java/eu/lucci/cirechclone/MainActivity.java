/*
 * Copyright 2015 Gabriele Lucci
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.lucci.cirechclone;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 */
public class MainActivity extends Activity implements SurfaceHolder.Callback, CirechGame.Callback, GameThread.Callback {
    /**
     * MODEL
     */
    private CirechGame mGame;
    /**
     * VIEW
     */
    private GameView mGameView;
    /**
     * CONTROLLER
     */
    private GameController mController;
    /**
     * The game loop. (CONTROLLER)
     */
    private GameThread mLoop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init game
        mGame = new CirechGame();
        mGame.setCallback(this);
        // read high score from file
        new ReadFileTask().execute();
        //init view and listeners
        mGameView = new GameView(this);
        mGameView.getHolder().addCallback(this);
        setContentView(mGameView);
        mController = new GameController(mGameView, mGame);
        //init game loop
        mLoop = new GameThread(mGameView, mGame);
        mLoop.setCallback(this);
        mLoop.start();
        //all ready, reset game state and do the callbacks
        mGame.setCurrentState(CirechGame.MENU_STATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // save high score to file, execute in worker thread
        new SaveFileTask().execute();
        // pause game
        switch (mGame.getCurrentState()) {
            case CirechGame.MENU_STATE:
                break;
            case CirechGame.PLAY_STATE:
                mGame.setCurrentState(CirechGame.PAUSE_STATE);
                break;
            case CirechGame.PAUSE_STATE:
                break;
            case CirechGame.GAME_OVER_STATE:
                break;
        }
        // stop game loop and kill thread
        mLoop.setRunning(false);
        boolean retry = true;
        while (retry) {
            try {
                mLoop.join();
                retry = false;
            } catch (InterruptedException e) {
                Log.e("loop", "interrupted");
            }
        }
        Log.d("loop", "game thread stopped");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mLoop = new GameThread(mGameView, mGame);
        mLoop.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // pause game
        switch (mGame.getCurrentState()) {
            case CirechGame.MENU_STATE:
                break;
            case CirechGame.PLAY_STATE:
                mGame.setCurrentState(CirechGame.PAUSE_STATE);
                break;
            case CirechGame.PAUSE_STATE:
                break;
            case CirechGame.GAME_OVER_STATE:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //resume game
        switch (mGame.getCurrentState()) {
            case CirechGame.MENU_STATE:
                break;
            case CirechGame.PLAY_STATE:
                break;
            case CirechGame.PAUSE_STATE:
                break;
            case CirechGame.GAME_OVER_STATE:
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mGameView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
            );
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mGameView.measure();
        mGameView.setReady(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mGameView.measure();
        mGameView.setReady(true);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mGameView.setReady(false);
        Log.d("surface", "destroyed");
    }

    /**
     * Called when the game status changes.
     */
    @Override
    public void stateChanged(int newStatus) {
        switch (mGame.getCurrentState()){
            case CirechGame.MENU_STATE:
                break;
            case CirechGame.PLAY_STATE:
                break;
            case CirechGame.PAUSE_STATE:
                break;
            case CirechGame.GAME_OVER_STATE:
                break;
        }
    }

    /**
     * Things to do when the game loop starts. Executed by the game loop.
     */
    @Override
    public void onStartup() {
    }

    /**
     * Things to do when the game loop terminates. Executed by the game loop.
     */
    @Override
    public void onShutdown() {
    }

    private class SaveFileTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                FileOutputStream outputStream = getApplicationContext().openFileOutput("highscore", Context.MODE_PRIVATE);
                DataOutputStream dout = new DataOutputStream(outputStream);
                dout.writeInt(mGame.highScore);
                dout.close();
            } catch (FileNotFoundException e) {
                Log.e("save file task", "file not found");
            } catch (IOException e) {
                Log.e("save file task", "error while closing the stream");
            }
            Log.d("file task", "file saved successfully");
            return null;
        }
    }

    private class ReadFileTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            int data = 0;
            try {
                FileInputStream inputStream = getApplicationContext().openFileInput("highscore");
                DataInputStream din = new DataInputStream(inputStream);
                data = din.readInt();
                din.close();
            } catch (FileNotFoundException e) {
                Log.e("read file task", "file not found");
            } catch (IOException e) {
                Log.e("read file task", "error while closing the stream");
            }
            Log.d("file task", "file read successfully");
            return data;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mGame.highScore = integer;
        }
    }


}