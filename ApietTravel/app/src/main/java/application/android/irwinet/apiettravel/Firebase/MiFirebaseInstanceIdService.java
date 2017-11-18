package application.android.irwinet.apiettravel.Firebase;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Irwinet on 16/11/2017.
 */

public class MiFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "NOTICIAS";
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String token=FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"Token: "+token);
    }
}
