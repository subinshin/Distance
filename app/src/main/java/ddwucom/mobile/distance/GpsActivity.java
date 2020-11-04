package ddwucom.mobile.distance;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class GpsActivity extends AppCompatActivity {
    private static final int REQUEST_SMS_RECEIVE = 1000;
    //MovingActivity에 넘길 ArrayList
    ArrayList<PathInfo> pathList = new ArrayList<PathInfo>();

    private DrawerLayout mDrawerLayout;
    private Context context = this;
    String user_email;

    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.

    private final static String TAG = "GpsActivity";        // 로그 TAG
    private final static int ZOOM_LEVEL = 17;                   // 지도 확대 배율
    private final static int PERMISSION_REQ_CODE = 100;         // permission 요청 코드
    private final static int LINE_COLOR = Color.RED;            // 선그리기 지정 색상
    private final static int LINE_WIDTH = 5;                    // 선그리기 두께

    private GoogleMap mGoogleMap;           // 구글맵 객체 저장 멥버 변수
    private LocationManager locManager;     // 위치 관리자
    private Location lastLocation; // 앱 실행 중 최종으로 수신한 위치 저장 멤버 변수
    private Geocoder geocoder;

    private Marker centerMarker;            // 현재 위치를 표현하는 마커 멤버 변수
    private MarkerOptions markerOptions;    // 마커 옵션
    private PolylineOptions lineOptions;    // 선 그리기 옵션

    DBManager dbManager;

    LatLng startLatLng;
    LatLng currentLatLng;
    float distance;
    int count = 0;

    // 현재시간을 msec 으로 구한다.
    long now = System.currentTimeMillis();
    // 현재시간을 date 변수에 저장한다.
    Date date = new Date(now);
    // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
    SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm");
    SimpleDateFormat sdfNowDate = new SimpleDateFormat("yyyy/MM/dd");
    // nowDate 변수에 값을 저장한다.
    String startDateTime = sdfNow.format(date);
    String startDate = sdfNowDate.format(date);
    String endDateTime;

    Marker selectedPositionMarker = null;

    ConstraintLayout gps_bottom_layout;
    TextView tv_gps_loc;
    Button btn_gps_loc_add = null;
    SearchView sv_location;

    String loc;
    LatLng selectedLatLng = null;

    TextView tv_myPage;
    TextView tv_moving;
    TextView tv_message;
    TextView tv_logOut;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_gps);

        int permissionCheck = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                // no permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS},REQUEST_SMS_RECEIVE);
            } else {
                // already have permission
            }
        } else {
            // OS version is lower than marshmallow
        }
        dbManager = new DBManager(this);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); // 위치관리자 준비
        geocoder = new Geocoder(GpsActivity.this);

        // 구글맵 준비
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallback);

        // 마커를 생성하기 위한 옵션 지정
        markerOptions = new MarkerOptions();
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        // 선을 그리기 위한 옵션 지정
        lineOptions = new PolylineOptions();
        lineOptions.color(LINE_COLOR);
        lineOptions.width(LINE_WIDTH);

        if (checkPermission()) {
            lastLocation = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        Intent intent = getIntent();
        user_email = intent.getStringExtra("email_id");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.menu_24px_5); //뒤로가기 버튼 이미지 지정

//        sv_location = findViewById(R.id.sv_location);
//
//        sv_location.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                String location = sv_location.getQuery().toString();
//                List<Address> addressList = null;
//
//                if (location != null || !location.equals("")) {
//                    Geocoder geocoder = new Geocoder(GpsActivity.this);
//                    try {
//                        addressList = geocoder.getFromLocationName(location, 1);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    Address address = addressList.get(0);
//                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//
//                    //해당 위치로 이동하게끔 하기.
//                    //autocomplete 추가하기
//                    //add마커가 아니라 한번 표시되고 다른장소 가면 마커 사라지도록
//                    //정보 추출해서 동선 저장하도록하기
//
//
//                }
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });

//        you’d need to initialize PlacesClient like this
        String apiKey = getString(R.string.api_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                if(selectedPositionMarker != null){
                    selectedPositionMarker.remove();
                    selectedPositionMarker = null;
                    gps_bottom_layout.setVisibility(View.INVISIBLE);
                }
                else {
                    loc = null;
                    selectedLatLng = null;

                    // TODO: Get info about the selected place.
//                  Log.i(TAG, "정보를 확인합니다! Place: " + place.getName() + ", " + place.getLatLng() + ", " + place.getAddress());
                    String businessName = place.getName();
                    String address = place.getAddress();
                    LatLng latLng = place.getLatLng();

                    loc = businessName + ", " + address;
                    selectedLatLng = latLng;

                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, ZOOM_LEVEL));
                    MarkerOptions mo = new MarkerOptions().position(latLng);
                    if(selectedPositionMarker != null) {
                        selectedPositionMarker.remove();
                    }
                    selectedPositionMarker = mGoogleMap.addMarker(mo);

                    tv_gps_loc.setText(loc);
                    gps_bottom_layout.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        mDrawerLayout = (DrawerLayout) findViewById(R.id.gps_layout);
        gps_bottom_layout = (ConstraintLayout) findViewById(R.id.gps_buttom_layout);
        tv_gps_loc = findViewById(R.id.tv_gps_loc);
        btn_gps_loc_add = findViewById(R.id.btn_gps_loc_add);
        btn_gps_loc_add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GpsActivity.this, AddLocationActivity.class);
                intent.putExtra("location", loc);
                intent.putExtra("latlng", selectedLatLng);
                startActivity(intent);
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        tv_myPage = navigationView.findViewById(R.id.tv_mypage);
        tv_moving = navigationView.findViewById(R.id.tv_moving);
        tv_message = navigationView.findViewById(R.id.tv_message);
        tv_logOut = navigationView.findViewById(R.id.tv_logout);
        TextView tv_email = navigationView.findViewById(R.id.tv_email);
        tv_email.setText(user_email);

///////

    }

    public void tvOnClick(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.tv_mypage:
                tv_myPage.setTextColor(Color.GRAY);
                intent = new Intent(GpsActivity.this, MyPageActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_moving:
                tv_moving.setTextColor(Color.GRAY);
                intent = new Intent(GpsActivity.this, MovingActivity.class);
                intent.putExtra("pathList", pathList);
                startActivity(intent);
                break;
            case R.id.tv_message:
                tv_message.setTextColor(Color.GRAY);
                intent = new Intent(GpsActivity.this, SMSListActivity.class);
                startActivity((intent));
                break;
            case R.id.tv_logout:
                tv_logOut.setTextColor(Color.GRAY);
                FirebaseAuth.getInstance().signOut();
                startLoginActivity();
                break;
        }
        mDrawerLayout.closeDrawers();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onResume() {
        super.onResume();
        if (checkPermission()) {
            // 위치 정보 수신 시작 - 10초 간격, 0m 이상 이동 시 수신
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 0, locationListener);
        }

        new GetPathAsyncTask().execute();

        Log.d("BackgroundService", "onResume");
        Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
        startService(intent);

        tv_myPage.setTextColor(Color.parseColor("#AE53AC"));
        tv_moving.setTextColor(Color.parseColor("#AE53AC"));
        tv_message.setTextColor(Color.parseColor("#AE53AC"));
        tv_logOut.setTextColor(Color.parseColor("#AE53AC"));
    }
    
    protected void onPause() {
        super.onPause();
        // 위치 정보 수신 종료 - 위치 정보 수신 종료를 누르지 않았을 경우를 대비
        locManager.removeUpdates(locationListener);


        Log.d("BackgroundService", "onPause");
//        Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
//        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
        Log.d("BackgroundService", "onDestroy");
        startService(intent);
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
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /*Google Map 준비 시 호출할 CallBack 인터페이스*/
    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(final GoogleMap googleMap) {
            // 로딩한 구글맵을 보관
            mGoogleMap = googleMap;

            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // 기록한 최종 위치가 있을 경우와 없을 경우를 구분하여 구현
            if (lastLocation != null) {
                startLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            } else {
                startLatLng = new LatLng(Double.valueOf(getString(R.string.init_lat)),
                        Double.valueOf(getString(R.string.init_lng)));     // 최종 위치가 없을 경우 지정한 곳으로 위치 지정
            }


            Log.i(TAG, "Start location: " + startLatLng.latitude + ", " + startLatLng.longitude);

            // 이동 시
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, ZOOM_LEVEL));

            // 애니메이션 효과로 이동 시
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, ZOOM_LEVEL));

            BitmapDrawable bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.som_mark_big);
            Bitmap b = bitmapDrawable.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 200, 250, false);


            // 지정한 위치로 마커 위치 설정
            markerOptions.position(startLatLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            centerMarker = mGoogleMap.addMarker(markerOptions);


////            map 클릭 시 이벤트 처리
            mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    if (selectedPositionMarker != null) {
                        selectedPositionMarker.remove();
                        selectedPositionMarker = null;
                        gps_bottom_layout.setVisibility(View.INVISIBLE);
                    } else {
                        loc = null;
                        selectedLatLng = latLng;
                        List<Address> address = null;
                        try {
                            address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (address.size() != 0) {
                            loc = address.get(0).getAddressLine(0);
                            MarkerOptions mo = new MarkerOptions().position(latLng);
                            selectedPositionMarker = googleMap.addMarker(mo);

                            tv_gps_loc.setText(loc);
                            gps_bottom_layout.setVisibility(View.VISIBLE);
                        }else {
                            Toast.makeText(context, "위치정보 불러올 수 없음", Toast.LENGTH_SHORT).show();
                            selectedPositionMarker = null;
                        }

                    }
                }
            });
            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (selectedPositionMarker != null) {
                        selectedPositionMarker.remove();
                        selectedPositionMarker = null;
                        gps_bottom_layout.setVisibility(View.INVISIBLE);
                    }
                }
            });

        }
    };


//    TimerTask timerTask = new TimerTask() {
//        @Override
//        public void run() {
//            Log.d(TAG, "timerTask in counter : " + count);
//            count++;
//        }
//    };

    /*위치 정보 수신 LocationListener*/
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location currentLocation) {
            Log.i(TAG, "Current Location : " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude());

//            현재 수신한 위치 정보 Location을 LatLng 형태로 변환
            currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

//            if (count == 0) {
//                // 현재시간을 msec 으로 구한다.
//                now = System.currentTimeMillis();
//                // 현재시간을 date 변수에 저장한다.
//                date = new Date(now);
//                // nowDate 변수에 값을 저장한다.
//                startDateTime = sdfNow.format(date);
//                startDate = sdfNowDate.format(date);
//
//                String a[] = startDate.split("/");
//
//                year = Integer.parseInt(a[0]);
//                month = Integer.parseInt(a[1]);
//                day = Integer.parseInt(a[2]);
//            }

         //   distance = lastLocation.distanceTo(currentLocation);

//            if (distance <= 15 && count == 0) {
//                timer = new Timer();
//                timer.schedule(timerTask, 0, 1000);
//                Toast.makeText(GpsActivity.this, Integer.toString(count), Toast.LENGTH_SHORT).show();
//            } else if(count >= 5 && distance > 15){
//                    Log.d(TAG, "위치 추가 코드로 진입");
//
//                    // 현재시간을 msec 으로 구한다.
//                    now = System.currentTimeMillis();
//                    // 현재시간을 date 변수에 저장한다.
//                    date = new Date(now);
//                    // nowDate 변수에 값을 저장한다.
//                    endDateTime = sdfNow.format(date);
//
//                    double latitude = Double.parseDouble(String.format("%6f", lastLocation.getLatitude()));
//                    double longitude = Double.parseDouble(String.format("%6f", lastLocation.getLongitude()));

//                    List<Address> address = null;
//                    try {
//                        address = geocoder.getFromLocation(latitude, longitude, 3);
//                    } catch (IOException e) {
//                        Log.d(TAG, "geocoding error");
//                    }
//
//                    Log.d(TAG, Double.toString(latitude));
//                    Log.d(TAG, Double.toString(longitude));
//                    Log.d(TAG, address.get(0).getAddressLine(0));
//
//                    boolean result = dbManager.addNewGps(
//                            new MovingInfo(year, month, day, startDateTime, endDateTime, latitude, longitude, address.get(0).getAddressLine(0), "auto"));
//
//                    if (result) {    // 정상수행에 따른 처리
//                        Log.d(TAG, "위치 추가 성공!");
//                    } else {        // 이상에 따른 처리
//                        Log.d(TAG, "위치 추가 실패");
//                    }
//
//                    Log.d(TAG, startDateTime);
//                    Log.d(TAG, endDateTime);
////                    Toast.makeText(GpsActivity.this, Integer.toString(count), Toast.LENGTH_SHORT).show();
//
//                timer.cancel();
//                count = 0;
                lastLocation.setLatitude(currentLocation.getLatitude());
                lastLocation.setLongitude(currentLocation.getLongitude());
       //     }

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
                    Toast.makeText(this, "위치 권한이 허용되었습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "위치 권한이 거부되었습니다!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }



    public class GetPathAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "task Start");
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collectionGroup("paths").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                                DocumentSnapshot documentSnapshot = snap;
                                PathInfo path = documentSnapshot.toObject(PathInfo.class);
                                pathList.add(path);
                                Log.d(TAG, path.getPatient_no() + " / " + path.getPlace() + " / " + path.getVisitDate());
//                            Log.d(TAG, snap.getId() + " => " + snap.getData());
                            }
                        }
                    });
            Log.d(TAG, "task Finish");
           return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }


    }

}

