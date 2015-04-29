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

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * CONTROLLER.
 * Controller che gestisce l'input dell'utente.
 * Created by Gabriele Lucci on 10/08/14.
 * Project: Cirech Clone
 */
public class GameController implements View.OnTouchListener {
    private GestureDetector swipeDetector;
    private CirechGame game;

    public GameController(View view, CirechGame game) {
        this.game = game;
        swipeDetector = new GestureDetector(view.getContext(), new SwipeDetector());
        view.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return swipeDetector.onTouchEvent(event);
    }

    private class SwipeDetector extends GestureDetector.SimpleOnGestureListener {
        private static final int LEFT_SWIPE = 0;
        private static final int RIGHT_SWIPE = 1;
        private static final int UP_SWIPE = 3;
        private static final int DOWN_SWIPE = 4;
        private static final int SWIPE_MIN_DISTANCE = 100;
        private static final int SWIPE_THRESHOLD_VELOCITY = 50;

        @Override
        public boolean onDown(MotionEvent e) {
            switch (game.getCurrentState()) {
                case CirechGame.PLAY_STATE:
                    game.switchColor();
                    return false;
            }
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            switch (game.getCurrentState()) {
                case CirechGame.MENU_STATE:
                    /*if (listeningView.ball.contains(event.getX(), event.getY()))
                        game.setCurrentState(CirechGame.PLAY_STATE);*/
                    return true;
                case CirechGame.PLAY_STATE:
                    return false;
                case CirechGame.PAUSE_STATE:
                    game.setCurrentState(CirechGame.PLAY_STATE);
                    //todo
                    return false;
                case CirechGame.GAME_OVER_STATE:
                    //dispatch to another event handler
                    return true;
                default:
                    return true;
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            int swipeDirection = getSwipeDirection(e1, e2, velocityX, velocityY);
            if (swipeDirection == DOWN_SWIPE) {
                switch (game.getCurrentState()) {
                    case CirechGame.MENU_STATE:
                        game.setCurrentState(CirechGame.PLAY_STATE);
                        return false;
                    case CirechGame.GAME_OVER_STATE:
                        game.reset();
                        game.setCurrentState(CirechGame.PLAY_STATE);
                        return false;
                }
            }
            return true;
        }

        private int getSwipeDirection(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                return LEFT_SWIPE;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                return RIGHT_SWIPE;
            } else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return UP_SWIPE;
            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return DOWN_SWIPE;
            } else return -1;
        }
    }
}