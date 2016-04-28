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

/**
 * Created by Gabriele Lucci on 07/08/14.
 * Project: Cirech Clone
 */
public class GameThread extends Thread {

    /**
     * The preferred update frequency of the loop.
     */
    public final static int PREFERRED_FPS = 60;  // updates per second (Hz)

    /**
     * Preferred sleep time of this thread, equal to (1 second / PREFERRED_FPS).
     */
    private final static long FRAME_PERIOD = 1000 / PREFERRED_FPS;

    /**
     * Max renders to skip while the game is behind.
     */
    private final static int MAX_SKIPPED_FRAMES = 5;

    private GameEngine game;

    private GameRenderer renderer;

    private boolean running;    //thread running flag

    private GameThread.Callback callback;

    /**
     * @param renderer the renderer
     * @param game     the game to update
     */
    public GameThread(GameRenderer renderer, GameEngine game) {
        this.setName("game loop");
        this.renderer = renderer;
        this.game = game;
    }

    /**
     * Set the game loop running.
     *
     * @param state if true the game loop runs
     */
    public void setRunning(boolean state) {
        this.running = state;
    }

    /**
     * Game loop here.
     */
    public void run() {
        startup();
        //todo: separate tps from fps
        long delta;
        long beginTime;
        long sleepPeriod;
        setRunning(true);
        while (running) {
            beginTime = System.currentTimeMillis();
            game.updateGame();          //update game logic
            renderer.renderGame(game);  //update screen
            delta = (System.currentTimeMillis() - beginTime);  // time elapsed
            sleepPeriod = FRAME_PERIOD - delta;
            if (sleepPeriod > 0) {
                try {
                    Thread.sleep(sleepPeriod);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        shutdown();
    }

    /**
     * Eseguito automaticamente all'inizio di run(), prima del loop di gioco.
     *
     * @see this.onStartup()
     */
    private void startup() {
        if (callback != null) callback.onStartup();
    }

    /**
     * Eseguito automaticamente alla fine di run(), dopo il loop di gioco.
     *
     * @see this.onStartup()
     */
    private void shutdown() {
        if (callback != null) callback.onShutdown();
    }

    /**
     * Sets the implementing callback object.
     *
     * @param callback the object which holds the callbacks
     */
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     *
     */
    public interface Callback {
        void onStartup();

        void onShutdown();
    }
}
