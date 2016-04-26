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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

import java.util.Random;

/**
 * This class represents the game rendering engine. It uses the drawing surface of SurfaceView.
 * This is a temporary solution, I want to replace it with a GLSurfaceView.
 *
 * @author gabriele lucci
 * @version 1/5/2015
 * @see android.view.SurfaceView
 */
public class GameView extends SurfaceView implements GameRenderer {

    /**
     * This is the text color.
     */
    public static final int TEXT_COLOR = Color.WHITE;

    private static final String TAG = "GameView";

    /**
     * This is the background color.
     */
    private int background;

    /**
     * This is the drawable model for the ball.
     */
    private BallDrawable ballDrawable;

    /**
     * This is the drawable model for the barriers.
     */
    private BarrierDrawable barrierDrawable;

    private Paint mPaint;

    private boolean isReady;

    // measures and bounds
    /**
     * Size of the text. To be initialized by the method measure()
     */
    private float textSize;

    /**
     * 1st game color.
     */
    private int color0;

    /**
     * 2nd game color.
     */
    private int color1;

    private Random rand;

    /**
     * @param context
     */
    public GameView(Context context) {
        super(context);
        init();
    }

    /**
     * @param context
     * @param attrs
     */
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * This method initializes the models.
     */
    private void init() {
        Resources mResources = getResources();
        background = mResources.getColor(R.color.game_background);

        ballDrawable = new BallDrawable();
        barrierDrawable = new BarrierDrawable();
        rand = new Random();
        mPaint = new Paint();
        resetColors();
    }

    public void resetColors() {
        color0 = color1 = Color.HSVToColor(generateHSVColor()); //generate game colors
        do {
            color1 = Color.HSVToColor(generateHSVColor());
        } while (color0 == color1); //make sure that we have two different colors
        Log.d(TAG, "color0=" + color0);
        Log.d(TAG, "color1=" + color1);
    }

    /**
     * Generates a random (possibly bright) HSV color
     *
     * @return a random generated HSV color.
     */
    private float[] generateHSVColor() {
        return new float[]{
                rand.nextFloat() * 360f,        //hue
                rand.nextFloat() / 0.5f + 0.5f, //sat
                rand.nextFloat() / 0.5f + 0.5f, //val
        };
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
     * @param game   the game to be rendered.
     */
    private synchronized void render(Canvas canvas, CirechGame game) {
        if (canvas == null) return;
        //draw background
        canvas.drawColor(background);
        switch (game.getCurrentState()) {
            case CirechGame.MENU_STATE:     //draw menu state
                //draw ball
                drawModels(canvas, game);
                //text
                mPaint.setColor(TEXT_COLOR);
                canvas.drawText("Tap to match the colors.", 0, textSize, mPaint);
                canvas.drawText("High Score: " + game.highScore, 0, textSize * 2, mPaint);
                canvas.drawText("Swipe down to start.", 0, textSize * 3, mPaint);
                break;
            case CirechGame.PLAY_STATE:     //draw the game in play state
                //draw models
                drawModels(canvas, game);
                //draw score
                mPaint.setColor(TEXT_COLOR);
                canvas.drawText("" + game.score, 0, getHeight() - 2, mPaint);
                break;
            case CirechGame.PAUSE_STATE:    //draw paused game
                drawModels(canvas, game);   //draw models as they are
                //with transparency layer
                mPaint.setColor(background);
                mPaint.setAlpha(191);
                canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
                //draw text
                mPaint.setColor(TEXT_COLOR);
                canvas.drawText("Paused game. Tap to resume.", 0, textSize, mPaint);
                break;
            case CirechGame.GAME_OVER_STATE:    //draw game over screen
                drawModels(canvas, game);
                //with transparency layer
                mPaint.setColor(background);
                mPaint.setAlpha(191);
                canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
                mPaint.setColor(TEXT_COLOR);
                canvas.drawText("Game over. Score: " + game.score, 0, textSize, mPaint);
                canvas.drawText("High Score: " + game.highScore, 0, textSize * 2, mPaint);
                canvas.drawText("Swipe down to restart.", 0, textSize * 3, mPaint);
                break;
            default:
        }
    }


    /**
     * This method draws the game objects.
     *
     * @param canvas the target canvas
     * @param game   the game to draw
     */
    private void drawModels(Canvas canvas, CirechGame game) {
        //draw ball
        if (game.currentColor) mPaint.setColor(color1);
        else mPaint.setColor(color0);   //pick the color from the game value
        ballDrawable.draw(canvas, mPaint);      //draw on canvas
        //draw barriers one by one
        for (Barrier b : game.barriers) {
            barrierDrawable.x = b.position * barrierDrawable.k;
            if (b.color) mPaint.setColor(color1);
            else mPaint.setColor(color0);
            barrierDrawable.draw(canvas, mPaint);
        }
    }

    /**
     * Dynamically sets the size of the models.
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
     * @return true if the SurfaceView is ready to draw
     */
    public boolean isReady() {
        return isReady;
    }

    /**
     * @param isReady pass true if the surface is ready to draw
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
            canvas.drawCircle(
                    centerX,
                    centerY,
                    radius,
                    paint);
        }

    }

    /**
     * Represents the drawable model of a barrier.
     */
    private class BarrierDrawable {
        float h;    // height
        float x;    // vertical position
        float k;    // vertical bound, max drawable v. position

        void draw(Canvas canvas, Paint paint) {
            canvas.drawRect(
                    0,          // left
                    x - h,      // top
                    getWidth(), // right
                    x,          // bottom
                    mPaint      // paint
            );
        }
    }
}
