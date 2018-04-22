package com.eli.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

/**
 * Created by eli on 18-4-22.
 */

public class Configuration {
    private static Configuration mInstance;

    private Context mGlobalContext;

    //TODO add features
    private String mStoreFolder;
    private int mScreenDirection;

    private int DIRECTION_PORT = 0;
    private int DIRECTION_LANDSCAPE = 1;

    private final String SP_NAME = "settingSP";
    private final String SP_KEY_SCREEN_DIRECTION = "sp_key_screen_direction";
    private final String SP_KEY_STORE_FOLDER = "storage_path";


    private Configuration(Context context) {
        this.mGlobalContext = context;
        init();
    }

    public synchronized static Configuration getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Configuration(context);
        }

        return mInstance;
    }

    public void init() {
        //store all properties
        SharedPreferences sharedPreferences = mGlobalContext.
                getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

        mStoreFolder = sharedPreferences.getString(SP_KEY_STORE_FOLDER,
                Environment.getExternalStorageDirectory().getAbsolutePath() +
                        mGlobalContext.getString(R.string.app_name));

        mScreenDirection = sharedPreferences.getInt(SP_KEY_SCREEN_DIRECTION, DIRECTION_PORT);
    }

    public String getStoreFolder() {
        return mStoreFolder;
    }

    public void setStoreFolder(String mStoreFolder) {
        this.mStoreFolder = mStoreFolder;
    }
}
