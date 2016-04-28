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

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * This represents the file writer task, it is an asynchronous task, so we can save data
 * without hogging the main UI thread. Also this will avoid ANR dialog if something goes wrong.
 * See also the file reader task.
 *
 * @see eu.lucci.cirechclone.ReadScoreTask
 * @see android.os.AsyncTask
 */
public class SaveScoreTask extends AsyncTask<Integer, Void, Void> {

    /**
     * Logcat tag for debug.
     */
    final static String TAG = "SaveScoreTask";

    WeakReference<Context> weakContext;

    public SaveScoreTask(Context applicationContext) {
        this.weakContext = new WeakReference<Context>(applicationContext);
    }

    /**
     * This method performs background operations.
     *
     * @param params
     * @return result of the background computation
     */
    @Override
    protected Void doInBackground(Integer... params) {
        Context applicationContext = weakContext.get();
        if (applicationContext == null) return null;
        int aNumber = params[0];
        try {
            FileOutputStream outputStream = applicationContext.openFileOutput("highscore", Context.MODE_PRIVATE);
            DataOutputStream dout = new DataOutputStream(outputStream);
            dout.writeInt(aNumber);
            dout.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "file not found");
        } catch (IOException e) {
            Log.e(TAG, "error while closing the stream");
        }
        Log.d(TAG, "file saved successfully");
        return null;
    }
}
