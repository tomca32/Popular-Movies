package io.tomislav.movies.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

class Connectivity {

    static boolean isOffline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo == null || !netInfo.isConnectedOrConnecting();
    }

    static void displayOfflineWarning(Context context) {
        Toast toast = Toast.makeText(context, R.string.offline_warning, Toast.LENGTH_SHORT);
        toast.show();
    }
}
