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
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * This represents the file reader task. In this task we read the game data from file.
 * Also this will avoid ANR dialog if something goes wrong.
 * See also the file saving task.
 *
 * @see eu.lucci.cirechclone.SaveScoreTask
 * @see android.os.AsyncTask
 */
public class ReadScoreTask extends AsyncTask<Void, Void, Integer> {

    /**
     * Logcat tag for debug.
     */
    final static String TAG = "ReadFileTask";

    WeakReference<MainActivity> weakReference;

    public ReadScoreTask(MainActivity activity) {
        this.weakReference = new WeakReference<>(activity);
    }

    /**
     * This method performs background operations.
     * The result of the computation must be returned by this step and will be passed back
     * to the last step.
     *
     * @param params
     * @return result of the background computation
     */
    @Override
    protected Integer doInBackground(Void... params) {
        Context applicationContext = weakReference.get().getApplicationContext();
        if (applicationContext == null) return null;
        int data = 0;
        try {
            FileInputStream inputStream = applicationContext.openFileInput("highscore");
            DataInputStream din = new DataInputStream(inputStream);
            data = din.readInt();
            din.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "file not found");
        } catch (IOException e) {
            Log.e(TAG, "error while closing the stream");
        }
        Log.d(TAG, "file read successfully");
        return data;
    }

    /**
     * The result of the background computation is passed to this step as a parameter.
     * Here we post the result of the async operation.
     *
     * @param data result of the background computation
     */
    @Override
    protected void onPostExecute(Integer data) {
        MainActivity activity = weakReference.get();
        if (activity != null) activity.updateGameHighScore(data);
    }
}
