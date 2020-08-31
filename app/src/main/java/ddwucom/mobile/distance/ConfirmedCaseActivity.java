package ddwucom.mobile.distance;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

public class ConfirmedCaseActivity extends AppCompatActivity {
    final static String TAG = "ConfirmedCaseActivity";

    ArrayList<PathInfo> paths;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        paths = getPath();
    }

    protected ArrayList<PathInfo> getPath() {
        final ArrayList<PathInfo> paths = new ArrayList<PathInfo>();
        paths.clear();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collectionGroup("paths").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                            DocumentSnapshot documentSnapshot = snap;
                            PathInfo path = documentSnapshot.toObject(PathInfo.class);

                            paths.add(path);

                            Log.d(TAG, path.getPatient_no() + " / " + path.getPlace());
//                            Log.d(TAG, snap.getId() + " => " + snap.getData());
                        }
                    }
                });

        return paths;

    }
}
