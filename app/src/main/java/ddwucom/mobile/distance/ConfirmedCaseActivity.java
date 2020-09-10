package ddwucom.mobile.distance;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

public class ConfirmedCaseActivity extends AppCompatActivity {
    final static String TAG = "ConfirmedCaseActivitys";

    private GoogleMap mGoogleMap;           // 구글맵 객체 저장 멥버 변수
    private LocationManager locManager;     // 위치 관리자
    private Location lastLocation;          // 앱 실행 중 최종으로 수신한 위치 저장 멤버 변수
    LatLng latlng;
    private final static int ZOOM_LEVEL = 17;                   // 지도 확대 배율
    private final static int PERMISSION_REQ_CODE = 100;
    private Marker centerMarker;            // 현재 위치를 표현하는 마커 멤버 변수
    private MarkerOptions markerOptions;    // 마커 옵션

    ArrayList<PathInfo> paths;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmedcase);

        // 위치관리자 준비
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 구글맵 준비
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallback);

        // 마커를 생성하기 위한 옵션 지정
        markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));


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

    /*Google Map 준비 시 호출할 CallBack 인터페이스*/
    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            // 로딩한 구글맵을 보관
            mGoogleMap = googleMap;

            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // 기록한 최종 위치가 있을 경우와 없을 경우를 구분하여 구현
            if (lastLocation != null) {
                latlng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            } else {
                latlng = new LatLng(Double.valueOf(getString(R.string.init_lat)),
                        Double.valueOf(getString(R.string.init_lng)));     // 최종 위치가 없을 경우 지정한 곳으로 위치 지정
            }

            paths = getPath();

            for (PathInfo path : paths) {
                latlng = new LatLng(path.getLat(), path.getLng());
                Toast.makeText(ConfirmedCaseActivity.this, Double.toString(path.getLat()), Toast.LENGTH_SHORT).show();
                markerOptions.position(latlng);
                centerMarker = mGoogleMap.addMarker(markerOptions);


                Log.i(TAG, "Start location: " + latlng.latitude + ", " + latlng.longitude);
            }


//            // 이동 시
//            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, ZOOM_LEVEL));
//
//            // 애니메이션 효과로 이동 시
//            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, ZOOM_LEVEL));

//            // 지정한 위치로 마커 위치 설정
//            markerOptions.position(latlng);
//            centerMarker = mGoogleMap.addMarker(markerOptions);

            /*이하의 내용은 실습 1, 2 내용에 포함되어 있지 않은 지도 관련 이벤트 처리에 대한 예이므로 참고*/

//            마커 윈도우 클릭 시 이벤트 처리
//            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//                @Override
//                public void onInfoWindowClick(Marker marker) {
//                    LatLng markerPosition = marker.getPosition();
//                    String loc = String.format("윈도우 클릭 - 위도:%f, 경도:%f",  markerPosition.latitude, markerPosition.longitude);
//                    Toast.makeText(MainActivity.this, loc, Toast.LENGTH_SHORT).show();
//                }
//            });
//
////            map 클릭 시 이벤트 처리
//            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                @Override
//                public void onMapClick(LatLng latLng) {
//                    String loc = String.format("클릭 - 위도:%f, 경도:%f", latLng.latitude, latLng.longitude);
//                    Toast.makeText(MainActivity.this, loc, Toast.LENGTH_SHORT).show();
//                }
//            });
//
////            map 롱클릭 시 이벤트 처리
////            롱클릭 시 NewActivity 를 호출, 호출 시 intent에 현재 위치의 위도 경도를 저장하여 전달
//            mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
//                @Override
//                public void onMapLongClick(LatLng latLng) {
//                    Intent intent = new Intent(MainActivity.this, NewActivity.class);
//                    intent.putExtra("latitude", latLng.latitude);
//                    intent.putExtra("longitude", latLng.longitude);
//                    startActivity(intent);
//                }
//            });
        }
    };
}
