/*
 * This file is part of cirech-clone.
 *
 * cirech-clone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cirech-clone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cirech-clone.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2016.
 */

package eu.lucci.cirechclone;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * CONTROLLER.
 * This represents the game controller, manages the way the user interacts with the app.
 * Basically it is a touch event handler.
 * Implements the callback to be invoked when a touch event is dispatched to this view.
 * The callback will be invoked before the touch event is given to the view.
 *
 * @see android.view.View.OnTouchListener
 */
public class GameController implements View.OnTouchListener {

    /**
     * The gesture detector. In this case needed to detect swipes.
     */
    private GestureDetector swipeDetector;

    /**
     * The game to control.
     */
    private CirechGame game;

    /**
     * @param view
     * @param game
     */
    public GameController(View view, CirechGame game) {
        this.game = game;
        swipeDetector = new GestureDetector(view.getContext(), new SwipeDetector());
        view.setOnTouchListener(this);
    }

    /**
     * Called when a touch event is dispatched to a view.
     *
     * @param view  The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        return swipeDetector.onTouchEvent(event);
    }

    /**
     * Swipe event detector.
     */
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
