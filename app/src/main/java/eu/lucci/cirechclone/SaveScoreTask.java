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
