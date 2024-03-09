package model;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharePreferences {
    private static final String MY_SHARED_PREFERENCES = "MY_SHARED_PREFERENCES";
    private Context context;

    public MySharePreferences(Context context) {
        this.context = context;
    }
    public void putBooleanValue(String key, boolean val)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, val);
        editor.apply();
    }

    public boolean getBooleanValue(String key)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return  sharedPreferences.getBoolean(key, false);
    }

    public boolean getIsAvailable2Try(String key)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, true);
    }

    public void setIsAvailable2Try(String key, boolean val)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, val);
        editor.apply();
    }
}
