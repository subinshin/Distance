package ddwucom.mobile.distance;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class ComparePathActivity extends AppCompatActivity {
    final static String TAG = "ComparePathActivity";

    //DB에서 가져온 확진자 동선
    ArrayList<PathInfo> paths;
    //SQLite에서 가져온 나의 동선
    ArrayList<MovingInfo> myMoving;

    //paths와 myMoving에서 일치하는 객체들을 저장
    //저장한 순서대로 출력 필요 -> hashMap대신 List 사용
    ArrayList<PathInfo> matchedPath;
    ArrayList<MovingInfo> matchedMoving;

    DBHelper helper;
    DBManager dbManager;
    Cursor cursor;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helper = new DBHelper(this);
        dbManager = new DBManager(this);

        paths = getAllPath();
    }

    protected ArrayList<MovingInfo> getAllMyMoving() {
        cursor = dbManager.getAllInfos();
        ArrayList<MovingInfo> myMoving = new ArrayList<MovingInfo>();

        myMoving.clear();

        if(cursor.moveToNext()) {
            while(!cursor.isAfterLast()) {
//                location 변수 저장하는 코드 필요
                String startTime = cursor.getString(cursor.getColumnIndex(helper.COL_START_TIME));
                String endTime = cursor.getString(cursor.getColumnIndex(helper.COL_END_TIME));
                double latitude = cursor.getDouble(cursor.getColumnIndex(helper.COL_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndex(helper.COL_LATITUDE));
//                MovingInfo 객체 생성

//                list에 add

            }
        }
        helper.close();
        cursor.close();

        return  myMoving;
    }

    protected ArrayList<PathInfo> getAllPath() {
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
                        }
                    }
                });

        return paths;

    }
}
