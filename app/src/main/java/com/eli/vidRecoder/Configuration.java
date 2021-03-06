package com.eli.vidRecoder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;

/**
 * Created by eli on 18-4-22.
 */

public class Configuration {
    private static Configuration mInstance;

    private Context mGlobalContext;

    //TODO add features
    private String mStoreFolder;
    private int mScreenDirection;
    private boolean mIsForceMode;

    private boolean mUserQuickMode;

    private int DIRECTION_PORT = 0;
    private int DIRECTION_LANDSCAPE = 1;

    private final String SP_NAME = "settingSP";
    private final String SP_KEY_SCREEN_DIRECTION = "sp_key_screen_direction";
    private final String SP_KEY_STORE_FOLDER = "storage_path";
    private final String SP_KEY_FORCE_MODE = "sp_key_force_mode";
    private final String SP_KEY_QUICK_MODE = "sp_key_quick_mode";


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
                Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                        mGlobalContext.getString(R.string.app_eng_name));

        mScreenDirection = sharedPreferences.getInt(SP_KEY_SCREEN_DIRECTION, DIRECTION_PORT);

        mIsForceMode = sharedPreferences.getBoolean(SP_KEY_FORCE_MODE, true);

        mUserQuickMode = sharedPreferences.getBoolean(SP_KEY_QUICK_MODE, false);
    }

    public String getStoreFolder() {
        return mStoreFolder;
    }

    public void setStoreFolder(String mStoreFolder) {
        this.mStoreFolder = mStoreFolder;
        SharedPreferences sharedPreferences = mGlobalContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(SP_KEY_STORE_FOLDER, mStoreFolder)
                .apply();
    }

    public boolean isIsForceMode() {
        return mIsForceMode;
    }

    public void setIsForceMode(boolean mIsForceMode) {
        this.mIsForceMode = mIsForceMode;
        SharedPreferences sharedPreferences = mGlobalContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(SP_KEY_FORCE_MODE, mIsForceMode)
                .apply();
    }

    public boolean isUserQuickMode() {
        return mUserQuickMode;
    }

    public void setUserQuickMode(boolean mUserQuickMode) {
        this.mUserQuickMode = mUserQuickMode;
        SharedPreferences sharedPreferences = mGlobalContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(SP_KEY_QUICK_MODE, mUserQuickMode)
                .apply();
    }
}
