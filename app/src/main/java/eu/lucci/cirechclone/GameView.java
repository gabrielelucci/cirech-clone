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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * TODO: document your custom view class.
 */
public class GameView extends SurfaceView implements GameRenderer {
    /**
     * Colore di sfondo.
     */
    private final int BACKGROUND = Color.BLACK;
    /**
     * Colore del testo.
     */
    private final int TEXT_COLOR = Color.WHITE;

    private BallDrawable ballDrawable;
    private BarrierDrawable barrierDrawable;
    private Paint mPaint;
    private boolean isReady;
    // measures and bounds
    private float textSize;

    /**
     * @param context
     */
    public GameView(Context context) {
        super(context);
        init(null, 0);
    }

    /**
     *
     * @param context
     * @param attrs
     */
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    /**
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     *
     * @param attrs
     * @param defStyle
     */
    private void init(AttributeSet attrs, int defStyle) {
        ballDrawable = new BallDrawable();
        barrierDrawable = new BarrierDrawable();
        mPaint = new Paint();
    }

    /**
     * Dynamically draws the specified game on this surface.
     *
     * @param game the GameEngine to render.
     */
    public void renderGame(GameEngine game) {
        if (isReady) {
            Canvas c = null;
            try {
                c = getHolder().lockCanvas();
                synchronized (getHolder()) {
                    render(c, (CirechGame) game);
                }
            } finally {
                if (c != null) getHolder().unlockCanvasAndPost(c);
            }
        }
    }

    /**
     * @param canvas
     * @param game
     */
    private synchronized void render(Canvas canvas, CirechGame game) {
        if (canvas == null) return;
        //draw background
        canvas.drawColor(BACKGROUND);
        switch (game.getCurrentState()) {
            case CirechGame.MENU_STATE:
                //ball
                mPaint.setColor(game.currentColor);
                ballDrawable.draw(canvas, mPaint);
                //text
                mPaint.setColor(TEXT_COLOR);
                canvas.drawText("Tap to match the colors.", 0, textSize, mPaint);
                canvas.drawText("High Score: " + game.highScore, 0, textSize * 2, mPaint);
                canvas.drawText("Swipe down to start.", 0, textSize * 3, mPaint);
                break;
            case CirechGame.PLAY_STATE:
                //draw models
                drawModels(canvas, game);
                //draw score
                mPaint.setColor(TEXT_COLOR);
                canvas.drawText("" + game.score, 0, getHeight() - 2, mPaint);
                break;
            case CirechGame.PAUSE_STATE:
                //draw models
                drawModels(canvas, game);
                //with transparency layer
                mPaint.setColor(BACKGROUND);
                mPaint.setAlpha(223);
                canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
                //draw text
                mPaint.setColor(TEXT_COLOR);
                canvas.drawText("Paused game. Tap to resume.", 0, textSize, mPaint);
                break;
            case CirechGame.GAME_OVER_STATE:
                mPaint.setColor(game.currentColor);
                ballDrawable.draw(canvas, mPaint);
                mPaint.setColor(TEXT_COLOR);
                canvas.drawText("Game over. Score: " + game.score, 0, textSize, mPaint);
                canvas.drawText("High Score: " + game.highScore, 0, textSize * 2, mPaint);
                canvas.drawText("Swipe down to restart.", 0, textSize * 3, mPaint);
                break;
            default:
        }

    }

    /**
     * @param canvas
     * @param game
     */
    private void drawModels(Canvas canvas, CirechGame game) {
        //ball
        mPaint.setColor(game.currentColor);
        ballDrawable.draw(canvas, mPaint);
        //barriers
        for (Barrier b : game.barriers) {
            barrierDrawable.x = b.position * barrierDrawable.k;
            mPaint.setColor(b.color);
            barrierDrawable.draw(canvas, mPaint);
        }
    }

    /**
     *
     */
    public void measure() {
        //measure
        ballDrawable.radius = getHeight() / 14;
        ballDrawable.centerX = getWidth() / 2;
        ballDrawable.centerY = getHeight() - ballDrawable.radius;
        barrierDrawable.h = getHeight() / 14;
        barrierDrawable.k = (getHeight() - ballDrawable.radius * 2) / CirechGame.LIMIT;
        textSize = getWidth() / 15;
        mPaint.setTextSize(textSize);
    }

    /**
     * Check if the surface is ready to draw
     *
     * @return
     */
    public boolean isReady() {
        return isReady;
    }

    /**
     * @param isReady
     */
    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    /**
     * Represents a ball that is drawable on this surface.
     */
    private class BallDrawable {
        float centerX;
        float centerY;
        float radius;

        /**
          * @param canvas
         * @param paint
         */
        void draw(Canvas canvas, Paint paint) {
            canvas.drawCircle(centerX, centerY, radius, paint);
        }

    }

    /**
     *
     */
    private class BarrierDrawable {
        float h;
        float x;
        float k;
        void draw(Canvas canvas, Paint paint) {
            canvas.drawRect(
                    0,          // left
                    x - h,
                    getWidth(), // to right
                    x,
                    mPaint
            );
        }
    }
}
