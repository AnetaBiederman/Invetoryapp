package com.example.android.invetoryapp.data;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();
    private BookDbHelper _dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate my app");
        _dbHelper = new BookDbHelper(this);
    }

    public BookDbHelper getDbHelper() {
        return _dbHelper;
    }
}
