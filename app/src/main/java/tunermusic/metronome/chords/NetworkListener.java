package tunermusic.metronome.chords;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkListener extends BroadcastReceiver {
    private netWorkChangeListener listener;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(!Common.isConnectToInternet(context)) // Internet not connected
        {
            listener.onNetWorkChange(false);
        }
        else{
            listener.onNetWorkChange(true);
        }
    }
    public void setNetWorkChangeListener(netWorkChangeListener listener)
    {
        this.listener = listener;
    }
    public interface netWorkChangeListener{
        void onNetWorkChange(boolean isConnected);
    }
}
