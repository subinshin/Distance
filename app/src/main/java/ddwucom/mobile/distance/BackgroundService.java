package ddwucom.mobile.distance;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundService extends Service {
    private static final String TAG = "BackgroundService";
    private static final int TODO = 0;

    LocationManager locManager;
    DBManager dbManager;
    LatLng currentLatLng;
    Location lastLocation;

    long now = System.currentTimeMillis();
    // 현재시간을 date 변수에 저장한다.
    Date date = new Date(now);
    // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
    SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm");
    SimpleDateFormat sdfNowDate = new SimpleDateFormat("yyyy/MM/dd");

    String startTime = sdfNow.format(date);
    String startDate = sdfNowDate.format(date);
    String endDateTime;

    int count;

    int year;
    int month;
    int day;
    Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
        Log.d(TAG, "BackgroundService : onCreate 호출됨");
        dbManager = new DBManager(this);
        count = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스가 호출될 때마다 실행
        Log.d(TAG, "BackgroundService : onStartCommand 호출됨");
        Log.d(TAG, "count : " + count);

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return TODO;
        }
        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 0, locationListener);
        lastLocation = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "BackgroundService : onDestroy 호출됨");
        // 서비스가 종료될 때 실행
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location currentLocation) {
            Log.i(TAG, "Current Location : " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude());

//            현재 수신한 위치 정보 Location을 LatLng 형태로 변환
            currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());;
            if (count == 0) {
                // 현재시간을 msec 으로 구한다.
                long now = System.currentTimeMillis();
                // 현재시간을 date 변수에 저장한다.
                Date date = new Date(now);
                // nowDate 변수에 값을 저장한다.
                startTime = sdfNow.format(date);
                startDate = sdfNowDate.format(date);

                String a[] = startDate.split("/");

                year = Integer.parseInt(a[0]);
                month = Integer.parseInt(a[1]);
                day = Integer.parseInt(a[2]);
            }

            float distance = lastLocation.distanceTo(currentLocation);

            if (distance <= 15 && count == 0) { // lastLocation 과 currentLocation이 일치한다고 간주하는 부분
                timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                            count++;
                            Log.d(TAG, "count : " + count);
                       // }
                    }
                };
                timer.schedule(timerTask, 0, 60000); // 타이머 시작
            } else if(count >= 5 && distance > 15) { // lastLocation과 currentLocation이 다르다고 간주하는 부분
                Log.d(TAG, "위치 추가 코드로 진입");
                saveLocation();
                timer.cancel();
                count = 0;
                lastLocation.setLatitude(currentLocation.getLatitude());
                lastLocation.setLongitude(currentLocation.getLongitude());
            }

            Log.d(TAG, String.valueOf(count));

        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }
        @Override
        public void onProviderEnabled(String s) { }
        @Override
        public void onProviderDisabled(String s) { }
    };

    public void saveLocation(){
        now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        date = new Date(now);
        // nowDate 변수에 값을 저장한다.
        endDateTime = sdfNow.format(date);
        String endDate = sdfNowDate.format(date);

        double latitude = Double.parseDouble(String.format("%.6f", lastLocation.getLatitude()));
        double longitude = Double.parseDouble(String.format("%.6f", lastLocation.getLongitude()));
        Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> address = null;
        try {
            address = geocoder.getFromLocation(latitude, longitude, 3);
        } catch (IOException e) {
            Log.d(TAG, "geocoding error");
        }
        String addressString;

        if(address.size() != 0) {
            Log.d(TAG, Double.toString(latitude));
            Log.d(TAG, Double.toString(longitude));
            Log.d(TAG, address.get(0).getAddressLine(0));
//                    Toast.makeText(GpsActivity.this, address.get(0).toString(), Toast.LENGTH_SHORT).show();

            addressString = address.get(0).getAddressLine(0);

        }else {
            addressString = "주소정보 가져올 수 없음";
        }

        boolean result = dbManager.addNewGps(
                new MovingInfo(year, month, day, endDate, startTime, endDateTime, latitude, longitude, addressString , "auto", "store"));
        if (result) {    // 정상수행에 따른 처리
            Log.d(TAG, "새로운 위치 추가 성공");
        } else {        // 이상에 따른 처리
            Log.d(TAG, "새로운 위치 추가 실패");
        }
        Log.d(TAG, startTime);
        Log.d(TAG, endDateTime);
    }
}
