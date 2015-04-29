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
    private final static long FRAME_PERIOD = 1000 /  PREFERRED_FPS;
    /**
     * Max renders to skip while the game is behind.
     */
    private final static int MAX_SKIPPED_FRAMES = 0;

    private GameEngine game;
    private GameRenderer renderer;

    private boolean running;    //thread running flag
    private Callback callback;

    /**
     * @param renderer the renderer
     * @param game     the game to update
     */
    public GameThread(GameRenderer renderer, GameEngine game) {
        this.setName("game loop");
        setPriority(MAX_PRIORITY);
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
        int skippedFrames;
        setRunning(true);
        while (running) {
            long beginTime = System.currentTimeMillis();
            skippedFrames = 0;
            game.updateGame();
            renderer.renderGame(game);
            long delta = (System.currentTimeMillis() - beginTime);  // time elapsed
            long sleepTime = FRAME_PERIOD - delta;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //todo modificare questa parte del loop, non funziona come dovrebbe
            while (sleepTime < 0 && skippedFrames < MAX_SKIPPED_FRAMES) {
                //running behind
                game.updateGame();
                sleepTime += FRAME_PERIOD;
                skippedFrames++;
            }
        }
        shutdown();
    }

    /**
     * Eseguito automaticamente all'inizio di run(), prima del loop di gioco.
     * @see this.onStartup()
     */
    private void startup() {
        if (callback != null) callback.onStartup();
    }
    /**
     * Eseguito automaticamente alla fine di run(), dopo il loop di gioco.
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
        public void onStartup();
        public void onShutdown();
    }
}
