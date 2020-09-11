package ddwucom.mobile.distance;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
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

        //모든 확진자 동선과 나의 동선을 ArrayList에 담는다.
        paths = getAllPath();
        myMoving = getAllMoving();

        matchedPath = new ArrayList<PathInfo>();
        matchedMoving = new ArrayList<MovingInfo>();

        //call by reference..
        comparePathAndMoving();
    }

    //위,경도를 비교하는 메소드
    protected void comparePathAndMoving() {
        //동선 비교 메뉴를 누를 때마다 onCreate가 실행된다면 clear필요..........
        matchedPath.clear();
        matchedMoving.clear();
        //나의 동선 vs 모든 확진자들의 동선 하나씩 비교
        for (MovingInfo moving : myMoving) {
            double movingLat = moving.getLatitude();
            double movinglon = moving.getLongitude();
            //내 동선의 위,경도 추출
            Location myLocation = new Location("");
            myLocation.setLatitude(movingLat);
            myLocation.setLongitude(movinglon);
            for (PathInfo path : paths) {
                double pathLat = path.getLat();
                double pathlon = path.getLng();
                //확진자 동선의 위,경도 추출
                Location patientLocation = new Location("");
                patientLocation.setLatitude(pathLat);
                patientLocation.setLongitude(pathlon);
                //두 좌표간의 간격 비교
                float distanceInMeters = myLocation.distanceTo(patientLocation);
                //두 좌표간 간격이 15미터 이내라면 같은 장소에 있던 것으로 간주
                if(distanceInMeters <= 15) {
                    Log.d(TAG, "나의 동선 좌표: ( " + moving.getLatitude() + ", " + moving.getLongitude() + ") \n확진자 동선 좌표: (" +
                            path.getLat() + ", " + path.getLng() + ") / 확진자 동선 주소: " + path.getPlace());
                    matchedMoving.add(moving);
                    matchedPath.add(path);
                }

            }
        }

    }

    protected ArrayList<MovingInfo> getAllMoving() {
        cursor = dbManager.getAllInfos();
        ArrayList<MovingInfo> myMoving = new ArrayList<MovingInfo>();

        myMoving.clear();

        if(cursor.moveToNext()) {
            while(!cursor.isAfterLast()) {
//                location 변수 저장하는 코드, 시간 가져오는 코드 수정 필요
                int year = cursor.getInt(cursor.getColumnIndex(helper.COL_YEAR));
                int month = cursor.getInt(cursor.getColumnIndex(helper.COL_MONTH));
                int dayOfMonth = cursor.getInt(cursor.getColumnIndex(helper.COL_DAY));
                String startTime = cursor.getString(cursor.getColumnIndex(helper.COL_START_TIME));
                String endTime = cursor.getString(cursor.getColumnIndex(helper.COL_END_TIME));
                double latitude = cursor.getDouble(cursor.getColumnIndex(helper.COL_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndex(helper.COL_LATITUDE));
//                MovingInfo 객체 생성 (일단 location 없이)
                MovingInfo movingInfo = new MovingInfo(year, month, dayOfMonth, startTime, endTime, latitude, longitude);
//                list에 add
                myMoving.add(movingInfo);
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

//                            Log.d(TAG, path.getPatient_no() + " / " + path.getPlace());
                        }
                    }
                });

        return paths;

    }
}
