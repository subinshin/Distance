package ddwucom.mobile.distance;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GpsActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Context context = this;
    String id;

    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.

    private final static String TAG = "MyGooglemapTest";        // 로그 TAG
    private final static int ZOOM_LEVEL = 17;                   // 지도 확대 배율
    private final static int PERMISSION_REQ_CODE = 100;         // permission 요청 코드
    private final static int LINE_COLOR = Color.RED;            // 선그리기 지정 색상
    private final static int LINE_WIDTH = 5;                    // 선그리기 두께

    private GoogleMap mGoogleMap;           // 구글맵 객체 저장 멥버 변수
    private LocationManager locManager;     // 위치 관리자
    private Location lastLocation;          // 앱 실행 중 최종으로 수신한 위치 저장 멤버 변수

    private Marker centerMarker;            // 현재 위치를 표현하는 마커 멤버 변수
    private MarkerOptions markerOptions;    // 마커 옵션
    private PolylineOptions lineOptions;    // 선 그리기 옵션


    LatLng lastLatLng;
    LatLng currentLatLng;
    float distance;
    int count = 0;

    // 현재시간을 msec 으로 구한다.
    long now = System.currentTimeMillis();
    // 현재시간을 date 변수에 저장한다.
    Date date = new Date(now);
    // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    // nowDate 변수에 값을 저장한다.
    String formatDate = sdfNow.format(date);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_gps);

        // 위치관리자 준비
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 구글맵 준비
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallback);

        // 마커를 생성하기 위한 옵션 지정
        markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        // 선을 그리기 위한 옵션 지정
        lineOptions = new PolylineOptions();
        lineOptions.color(LINE_COLOR);
        lineOptions.width(LINE_WIDTH);

        if (checkPermission()) {
            lastLocation = locManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

///////
        Intent intent = getIntent();
        id = intent.getStringExtra("email_id");
        Toast.makeText(this,  "사용자 이메일 : " + id, Toast.LENGTH_SHORT).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.menu_icon); //뒤로가기 버튼 이미지 지정

        mDrawerLayout = (DrawerLayout) findViewById(R.id.gps_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                String title = menuItem.getTitle().toString();

                if(id == R.id.item_myPage){
                    Intent intent = new Intent(GpsActivity.this, MyPageActivity.class);
                    startActivity(intent);
                }else if(id == R.id.item_moving){
                    Intent intent = new Intent(GpsActivity.this, MovingActivity.class);
                    startActivity(intent);
                }else if(id == R.id.item_condition){
                    Intent intent = new Intent(GpsActivity.this, ConfirmedCaseActivity.class);
                    startActivity((intent));
                    Toast.makeText(context, "확진자 현황 확인", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.item_setting){
                    Toast.makeText(context, "설정", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.item_logout){
                    Toast.makeText(context, "로그아웃", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    startLoginActivity();
                }

                return true;
            }
        });
///////

    }

    public void onResume() {
        super.onResume();
        if (checkPermission()) {
            // 위치 정보 수신 시작 - 10초 간격, 0m 이상 이동 시 수신
            locManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 10000, 0, locationListener);
        }
    }


    protected void onPause() {
        super.onPause();
        // 위치 정보 수신 종료 - 위치 정보 수신 종료를 누르지 않았을 경우를 대비
        locManager.removeUpdates(locationListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, loginActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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
                lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            } else {
                lastLatLng = new LatLng(Double.valueOf(getString(R.string.init_lat)),
                        Double.valueOf(getString(R.string.init_lng)));     // 최종 위치가 없을 경우 지정한 곳으로 위치 지정
            }


            Log.i(TAG, "Start location: " + lastLatLng.latitude + ", " + lastLatLng.longitude);

            // 이동 시
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, ZOOM_LEVEL));

            // 애니메이션 효과로 이동 시
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, ZOOM_LEVEL));

            // 지정한 위치로 마커 위치 설정
            markerOptions.position(lastLatLng);
            centerMarker = mGoogleMap.addMarker(markerOptions);

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

    /*위치 정보 수신 LocationListener*/
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location currentLocation) {
            Log.i(TAG, "Current Location : " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude());

//            현재 수신한 위치 정보 Location을 LatLng 형태로 변환
            currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            distance = lastLocation.distanceTo(currentLocation);

            if (distance <= 15) {
                count++;
            } else {
                if (count >= 5) {
                    Log.d(TAG, "저장");
                }
                count = 0;
                lastLocation.setLatitude(currentLocation.getLatitude());
                lastLocation.setLongitude(currentLocation.getLongitude());
            }

            Log.d(TAG, String.valueOf(count));

//            새로운 위치로 지도 이동
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOM_LEVEL));


            String snippet = String.format("위도:%f, 경도:%f", currentLocation.getLatitude(), currentLocation.getLongitude());

            // 새로운 위치로 마커의 위치 지정 및 정보 표시- 윈도우 표시를 안 할 경우 마커를 터치할 때 표시됨
            centerMarker.setPosition(currentLatLng);
            centerMarker.setTitle("현재 위치");
            centerMarker.setSnippet(snippet);
            centerMarker.showInfoWindow();

//            현재 위치를 라인 정보로 추가
            lineOptions.add(currentLatLng);
            mGoogleMap.addPolyline(lineOptions);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }
        @Override
        public void onProviderEnabled(String s) { }
        @Override
        public void onProviderDisabled(String s) { }
    };


    /* 필요 permission 요청 */
    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSION_REQ_CODE);
            return false;
        }
        return true;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case PERMISSION_REQ_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission is granted!\nTry again!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission is denied!\n", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
