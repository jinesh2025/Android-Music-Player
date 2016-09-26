/*
 * This is the source code of Music Player Lite for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @CodeCrunch Team, 2016-2017.
 */
package co.codecrunch.musicplayerlite.utility;

import android.util.Log;


public class LogWriter {

    public static void debug(String TAG, String writeText) {
        Log.d(TAG, writeText);
    }

    public static void info(String TAG, String writeText) {
        Log.i(TAG, writeText);
    }
}
