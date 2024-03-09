package model;

import android.content.Context;

public class DataLocalManager {
    private static final String PREF_IS_LOGIN = "isLogin";
    private static final String PREF_IS_AVAIL = "isAvail";
    private static DataLocalManager instance;
    private MySharePreferences mSharePreferences;

    public static void init(Context context)
    {
        instance = new DataLocalManager();
        instance.mSharePreferences = new MySharePreferences(context);
    }

    public static DataLocalManager getInstance()
    {
        if(instance == null)
        {
            instance = new DataLocalManager();
        }
        return instance;
    }

    public static void setIsLogin(boolean isLogin)
    {
        DataLocalManager.getInstance().mSharePreferences.putBooleanValue(PREF_IS_LOGIN, isLogin);
    }
    public static boolean getIsLogin()
    {
        return DataLocalManager.getInstance().mSharePreferences.getBooleanValue(PREF_IS_LOGIN);
    }
    public static void setIsAvailable(boolean isAvailable)
    {
        DataLocalManager.getInstance().mSharePreferences.setIsAvailable2Try(PREF_IS_AVAIL, isAvailable);
    }
    public static boolean getIsAvail()
    {
        return DataLocalManager.getInstance().mSharePreferences.getIsAvailable2Try(PREF_IS_AVAIL);
    }
}
