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
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Main activity of the app.
 */
public class MainActivity extends Activity implements SurfaceHolder.Callback, CirechGame.Callback, GameThread.Callback {

    /**
     * Logcat tag for debugging.
     */
    private final static String TAG = "MainActivity";

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
     * The game loop. (CONTROLLER).
     */
    private GameThread mLoop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init game
        mGame = new CirechGame();
        mGame.setCallback(this);
        // read high score from file
        new ReadScoreTask(this).execute();
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
        new SaveScoreTask(getApplicationContext()).execute(mGame.highScore);
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
                Log.e(TAG, "interrupted");
            }
        }
        Log.d(TAG, "game thread stopped");
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
        Log.d(TAG, "surface destroyed");
    }

    /**
     * Called when the game status changes.
     */
    @Override
    public void stateChanged(int newStatus) {
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

    /**
     * Things to do when the game loop starts. Executed by the game loop.
     */
    @Override
    public void onStartup() {
        Log.d(TAG, "game loop started");
    }

    /**
     * Things to do when the game loop terminates. Executed by the game loop.
     */
    @Override
    public void onShutdown() {
        Log.d(TAG, "game loop stopped");
    }

    public void updateGameScore(int newScore) {
        mGame.score = newScore;
    }



}
