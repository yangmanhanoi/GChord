package model;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import tunermusic.metronome.chords.R;

public class LogEventManager {
    public static final int USER_OPEN_APP = 0;
    public static final int USER_IN_MENU = 1;
    public static final int USER_IN_FRAG = 2;
    public static final int USER_SEND_FEEDBACK = 3;

    private static final String EV_OPEN_APP = "ev2_g8_openapp";
    private static final String EV_IN_MENU = "ev2_g8_goto";
    private static final String EV_IN__FRAG_HOME = "ev2_g8_gotohome";
    private static final String EV_IN__FRAG_SEARCH = "ev2_g8_gotosearch";
    private static final String EV_IN__FRAG_CHORD = "ev2_g8_gotochord";
    private static final String EV_IN__FRAG_TUNING = "ev2_g8_gototuning";
    private static final String USER_FEEDBACKS = "user_feedback";


    private static LogEventManager instance;
    private FirebaseAnalytics mFirebaseAnalytics;



    public static void init(Context context)
    {
        instance = new LogEventManager();
        instance.mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }
    public static LogEventManager getInstance()
    {
        if(instance == null)
        {
            instance = new LogEventManager();
        }
        return instance;
    }

    public static void logEvent(String eventName, Bundle paramsValue)
    {
        LogEventManager.getInstance().mFirebaseAnalytics.logEvent(eventName, paramsValue);
    }
    public static void logUserBehavior(int type, Bundle params, int id)
    {
        if(type == USER_OPEN_APP)
        {
            logEvent(EV_OPEN_APP, params);
        }
        else if (type == USER_IN_MENU)
        {
            logEvent(EV_IN_MENU, params);
        } else if (type == USER_IN_FRAG) {
            logEvent(getInstance().getEvName(id), params);
        }
        else if(type == USER_SEND_FEEDBACK)
        {
            logEvent(USER_FEEDBACKS, params);
        }
    }
    private String getEvName(int id)
    {
        String evName = null;
        if(id == R.id.tunningBut)
        {
            evName = EV_IN__FRAG_TUNING;
        }
        else if(id == R.id.chordBut)
        {
            evName = EV_IN__FRAG_CHORD;
        }
        else if(id == R.id.searchBut)
        {
            evName = EV_IN__FRAG_SEARCH;
        }
        else evName = EV_IN__FRAG_HOME;
        return evName;
    }

}
