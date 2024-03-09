package tunermusic.metronome.chords;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.ConfigUpdate;
import com.google.firebase.remoteconfig.ConfigUpdateListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.Arrays;
import java.util.concurrent.Executor;

import model.DataLocalManager;
import model.LogEventManager;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DataLocalManager.init(getApplicationContext());
        LogEventManager.init(getApplicationContext());

    }


}
