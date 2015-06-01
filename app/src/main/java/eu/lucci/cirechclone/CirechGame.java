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

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * MODEL.
 * Created by Gabriele Lucci on 31/07/14.
 * Project: Cirech Clone
 */
public class CirechGame implements GameEngine {
    // Games states.
    public static final int MENU_STATE = 0;
    public static final int PLAY_STATE = 1;
    public static final int PAUSE_STATE = 2;
    public static final int GAME_OVER_STATE = 3;
    // Game parameters.
    public static final float LIMIT = 1f;   // should be 1.
    private static final float K = 0.5f;    // base speed multipliers (default: 0.5)
    private static final float BASE_SPEED = K / (float) GameThread.PREFERRED_FPS;
    private static final int NUMBER_OF_BARRIERS = 3;    //(default: 3)
    private static final float DISTANCE_DELTA = LIMIT / (float) NUMBER_OF_BARRIERS;

    /**
     * Current game color.
     */
    volatile boolean currentColor;    // current game color
    /**
     *
     */
    volatile Barrier[] barriers;
    /**
     * Current game score.
     */
    volatile int score;
    /**
     * High score.
     */
    int highScore;
    /**
     * Current game state.
     */
    private int currentState;
    /**
     * Holds the current barrier speed.
     */
    private float barrierSpeed;
    /**
     * "Pointer" to last barrier in the barrier array. Should not be allocated.
     */
    private Barrier lastBarrier;    // reference to last barrier
    /**
     * Random generator for this game.
     */
    private Random rand;
    /**
     * List of implemented callbacks for this game.
     */
    private List<Callback> callbacks;

    // INIT METHODS

    /**
     * Empty constructor.
     */
    public CirechGame() {
        init();
        currentState = MENU_STATE;
        reset();
    }

    /**
     *
     * @param callback
     */
    public CirechGame(Callback callback) {
        init();
        setCallback(callback);
        setCurrentState(MENU_STATE);
        reset();
    }

    /**
     * Utility method for initializing important things.
     * Should only be called once and in constructors methods.
     */
    private void init() {
        callbacks = new LinkedList<>();
        rand = new Random(System.nanoTime());
        barriers = new Barrier[NUMBER_OF_BARRIERS];
    }

    // GAME RELATED METHODS

    /**
     * Resets the game. Called every time you start a new game.
     */
    public void reset() {
        score = 0;      //reset the score
        updateSpeed();  //update the speed according to the initial score
        currentColor = false;
        generateBarriers();
    }

    /**
     * Switches the current color, should be called by game controllers.
     */
    public void switchColor() {
        currentColor = !currentColor;
    }

    /**
     * Updates the barrier speed based on the current score.
     */
    private void updateSpeed() {
        barrierSpeed = BASE_SPEED + score * (BASE_SPEED / 100);
    }

    /**
     * Randomly generates the first barriers. Should be called once, to generate new barriers use
     * reGenerateBarrier method.
     * @see this.reGenerateBarrier
     */
    private void generateBarriers() {
        float startPosition = 0, distance;
        for (int i = 0; i < barriers.length; i++) {
            if (rand.nextBoolean()) {
                barriers[i] = new Barrier(false, startPosition);
            } else {
                barriers[i] = new Barrier(true, startPosition);
            }
            distance = rand.nextFloat() * DISTANCE_DELTA + DISTANCE_DELTA;
            startPosition -= distance;
        }
        lastBarrier = barriers[barriers.length - 1];  //last barrier is last barrier
    }

    /**
     * Generates a new random coloured barrier at start position
     */
    private void reGenerateBarrier(Barrier b) {
        b.position = lastBarrier.position - (rand.nextFloat() * DISTANCE_DELTA + DISTANCE_DELTA);
        b.color = rand.nextBoolean();
        lastBarrier = b;
    }

    /**
     * Updates the game state. Called by game loop.
     */
    @Override
    public synchronized void updateGame() {
        if (currentState == PLAY_STATE && barriers != null) {
            // move barriers
            for (Barrier b : barriers) {
                b.move(barrierSpeed);
                // check collision
                if (b.position > LIMIT) {
                    if (currentColor != b.color) {
                        //game over
                        setCurrentState(GAME_OVER_STATE);
                        //check high score
                        if (score > highScore) {
                            highScore = score;
                        }
                    } else {
                        // continue game, generate new barriers
                        reGenerateBarrier(b);
                        score++;
                        updateSpeed();
                    }
                }
            }
        }
    }


    /**
     *
     * @return the current game state.
     */
    public int getCurrentState() {
        return currentState;
    }

    /**
     * Changes the state of the game.
     *
     * @param state the new state
     */
    public synchronized void setCurrentState(int state) {
        currentState = state;
        notifyStateChange(currentState);
    }

    /**
     * Notify the state change to all callback-implementing Objects.
     *
     * @param newState the new state of the game.
     */
    private void notifyStateChange(int newState) {
        if (callbacks != null) {
            for (int i = 0; i < callbacks.size(); i++) {
                callbacks.get(i).stateChanged(newState);
            }
        }
    }

    /**
     * Adds a Callback interface for this game.
     *
     * @param callback Callback fucntion to add.
     */
    public void addCallback(Callback callback) {
        callbacks.add(callback);
    }

    /**
     * Sets a Callback interface for this game, clearing all others previously added callbacks.
     *
     * @param callback Callback function to set.
     */
    public void setCallback(Callback callback) {
        callbacks.clear();
        callbacks.add(callback);
    }

    /**
     *
     */
    public interface Callback {
        void stateChanged(int newState);
    }
}
