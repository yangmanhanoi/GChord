package tunermusic.metronome.chords;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public class Common {
    public static boolean isConnectToInternet(Context context){
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null)
        {
            Network network = null;
            network = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }
        return false;
    }
}