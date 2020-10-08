package ddwucom.mobile.distance;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MovingActivity extends AppCompatActivity {
    //gpsAcitivity로 부터 가져온 확진자 동선
    ArrayList<PathInfo> pathList;


    Spinner spinner;
    DBManager manager;
    MovingInfoAdapter adapter;
    ListView all_listView;
    Cursor cursor;
    FrameLayout layout_moving;
    LayoutInflater inflater;

    ConstraintLayout all_layout;
    ConstraintLayout map_layout;
    ConstraintLayout no_data_layout;

    FragmentManager fragmentManager;
    MapFragment mapFragment;
    GoogleMap map;

    DBHelper helper;
    private static final String TAG = "MovingActivity";

    Button btn_all;
    Button btn_date;
    Button btn_map_patient;

    CameraPosition cameraPosition;
    ArrayList<MarkerOptions> markersOption;
    ArrayList<Marker> myMarkers;

    ArrayList<PathInfo> patientPathList;
    ArrayList<PathInfo> patientSelectedList;
    ArrayList<Marker> patientMarkers;

    final static int SPINNER_LIST = 0;
    final static int SPINNER_MAP = 1;
    int spinnerSelected;

    //확진자 동선보기 버튼 클릭시 필요
    boolean patientOnOff;

    Bitmap smallMarker;

    Date currentTime;
    int currentYear;
    int currentMonth;
    int currentDay;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moving);

        Log.d(TAG, "Start movingActivity");
        spinnerSelected = SPINNER_LIST;

        Intent intent = getIntent();
        //intent로 부터 전달받은 확진자 동선
        patientPathList = (ArrayList<PathInfo>) intent.getSerializableExtra("pathList");
        patientSelectedList = new ArrayList<PathInfo>();

        Log.d(TAG, "확진자리스트 갯수: " + String.valueOf(patientPathList.size()));
        for (PathInfo pathInfo : patientPathList) {
            Log.d(TAG, "확진자: " + pathInfo.toString());
        }

        //전체내용복사
        getAllPatientList();

        patientMarkers = new ArrayList<Marker>();
        patientOnOff = false;

        btn_date = findViewById(R.id.btn_date);
        btn_all = findViewById(R.id.btn_all);
        btn_map_patient = findViewById(R.id.btn_map_patient);

        markersOption = new ArrayList<MarkerOptions>();
        myMarkers = new ArrayList<Marker>();

        helper = new DBHelper(this);

        layout_moving = findViewById(R.id.layout_moving);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        manager = new DBManager(this);
        adapter = new MovingInfoAdapter(MovingActivity.this, R.layout.layout_listview_2, cursor);

        all_layout = findViewById(R.id.all_layout);
        map_layout = findViewById(R.id.map_layout);
        no_data_layout = findViewById(R.id.no_data_layout);

        all_listView = findViewById(R.id.all_listView);
        all_listView.setAdapter(adapter);

        // 현재시간 저장
        long now = System.currentTimeMillis();
        currentTime = new Date(now);
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        currentYear = Integer.parseInt(yearFormat.format(currentTime));
        currentMonth = Integer.parseInt(monthFormat.format(currentTime));
        currentDay = Integer.parseInt(dayFormat.format(currentTime));

        // 마커 이미지 불러오기
        BitmapDrawable bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.som_mark_big);
        Bitmap b = bitmapDrawable.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, 150, 200, false);

        fragmentManager = getFragmentManager();
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.moving_map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MovingActivity.this);// 대화상자 띄우기

                        LatLng n = marker.getPosition();

                        builder.setTitle(marker.getTitle())
                                .setMessage("위치좌표 : (" + n.latitude + ", " + n.longitude + ")\n날짜 : " + marker.getSnippet())
                                .setPositiveButton("확인", null)
                                .show();

                    }
                });

                cameraPosition = new CameraPosition.Builder().target(new LatLng(37.5759, 126.9769)).zoom(30).build();
                map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                Cursor cursor = manager.getAllInfos();

                putMyMark(cursor);
            }

        });


        spinner = findViewById(R.id.search_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        adapter.changeCursor(manager.getAllInfos());

                        all_layout.setVisibility(View.VISIBLE);
                        map_layout.setVisibility(View.INVISIBLE);
                        btn_map_patient.setVisibility(View.INVISIBLE);
                        no_data_layout.setVisibility(View.INVISIBLE);

                        spinnerSelected = SPINNER_LIST;
                        break;
                    case 1:
                        all_layout.setVisibility(View.INVISIBLE);
                        map_layout.setVisibility(View.VISIBLE);
                        btn_map_patient.setVisibility(View.VISIBLE);
                        no_data_layout.setVisibility(View.INVISIBLE);

                        spinnerSelected = SPINNER_MAP;
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "spinnerSelected : " + spinnerSelected);
        if(spinnerSelected == SPINNER_LIST){
            cursor = manager.getAllInfos();
            adapter.changeCursor(cursor);
        }
    }

    public void getAllPatientList(){
        patientSelectedList.clear();
        for(PathInfo p : patientPathList){
            patientSelectedList.add(p);
        }
    }

    public void putMyMark(Cursor cursor){
        // 원래 찍었던 마크들 전부 삭제
        removeMarkers(myMarkers);

        // cursor 값으로 새로운 마크 생성
        MovingInfo info = null;
        while(cursor.moveToNext()){
            info = new MovingInfo();
            info.setId(cursor.getInt(cursor.getColumnIndex(helper.COL_ID)));
            info.setYear(cursor.getInt(cursor.getColumnIndex(helper.COL_YEAR)));
            info.setMonth(cursor.getInt(cursor.getColumnIndex(helper.COL_MONTH)));
            info.setDayOfMonth(cursor.getInt(cursor.getColumnIndex(helper.COL_DAY)));
            info.setLatitude(cursor.getDouble(cursor.getColumnIndex(helper.COL_LATITUDE)));
            info.setLongitude(cursor.getDouble(cursor.getColumnIndex(helper.COL_LONGITUDE)));
            info.setLocation(cursor.getString(cursor.getColumnIndex(helper.COL_LOCATION)));
            info.setStartTime(cursor.getString(cursor.getColumnIndex(helper.COL_START_TIME)));
            info.setEndTime(cursor.getString(cursor.getColumnIndex(helper.COL_END_TIME)));

            Log.d(TAG,  "latitude : "+ info.getLatitude() + ", longitude : " + info.getLongitude());

            String s = info.getYear() + "/" + info.getMonth() + "/" + info.getDayOfMonth() + ", " + info.getStartTime() + " - " + info.getEndTime();

            // 이미 추가된 markerOption중에 좌표가 동일한 것이 있는지 확인
            MarkerOptions mo = null;
            for(MarkerOptions m : markersOption){
                if(m.getPosition().longitude == info.getLongitude() && m.getPosition().latitude == info.getLatitude()){
                    m.snippet(m.getSnippet() + "\n" + s);
                    mo = m;
                    break;
                }
            }

            //동일한 좌표가 없다면 markerOption 생성 후 추가
            if(mo == null) {
                LatLng pos = new LatLng(info.getLatitude(), info.getLongitude());
                MarkerOptions marker = new MarkerOptions().position(pos)
                        .title(info.getLocation()).snippet(s);

                marker.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                markersOption.add(marker);
            }
        }



        //마커 찍기
        for(MarkerOptions m : markersOption){
            myMarkers.add(map.addMarker(m));
        }

        markersOption.clear();

        if(info != null) {
            cameraPosition = new CameraPosition.Builder().target(new LatLng(info.getLatitude(), info.getLongitude())).zoom(16).build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void putPatientMark(){
        removeMarkers(patientMarkers);

        BitmapDrawable bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.som_patient_marker_blue);
        Bitmap b = bitmapDrawable.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 180, 200, false);

        for(PathInfo p : patientSelectedList){
            MarkerOptions markerOptions
                    = new MarkerOptions()
                    .position(new LatLng(p.getLat(), p.getLng()))
                    .title(p.getPlace())
                    .snippet(p.getVisitDate()+"\n" + p.getDisinfect())
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

            patientMarkers.add(map.addMarker(markerOptions));
        }
    }

    public void removeMarkers(ArrayList<Marker> markers){
        for(Marker m : markers){
            m.remove();
        }
        markers.clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void btnOnClick(View v){

        switch(v.getId()){
            case R.id.btn_all:
                cursor = manager.getAllInfos();
                if(spinnerSelected == SPINNER_LIST){
                    adapter.changeCursor(cursor);
                    adapter.notifyDataSetChanged();
                }else if(spinnerSelected == SPINNER_MAP){
                    if(patientOnOff){
                        getAllPatientList();
                    }
                    putPatientMark();
                    putMyMark(cursor);
                    //확진자 전체 검색
                }
                break;

            case R.id.btn_date:

                DatePickerDialog pickerDialog = new DatePickerDialog(this, pickerCallBack, currentYear, currentMonth - 1, currentDay);
                pickerDialog.show();
                break;

            case R.id.btn_map_patient:
                if(patientOnOff == false){
                    putPatientMark();
                    patientOnOff = true;
                    Toast.makeText(this, "On", Toast.LENGTH_SHORT).show();
                }else {
                    removeMarkers(patientMarkers);
                    patientOnOff = false;
                    Toast.makeText(this, "Off", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    //날짜별로 확진자 동선 찾기
    public ArrayList<PathInfo> searchWithDatePatient(int year, int month, int dayOfMonth){
        patientSelectedList.clear();

        for(PathInfo info : patientPathList){
            if(year == info.getYear() && month == info.getMonth() && dayOfMonth == info.getDayOfMonth()){
                patientSelectedList.add(info);
            }
        }
        return patientSelectedList;
    }

    DatePickerDialog.OnDateSetListener pickerCallBack = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Log.d(TAG, year + "/" + month + "/" + dayOfMonth);
            cursor = manager.searchWithDate(year, month, dayOfMonth);
            // 검색결과가 없을 때 화면처리
            if(cursor.moveToNext() == false){
                no_data_layout.setVisibility(View.VISIBLE);
                all_layout.setVisibility(View.INVISIBLE);
                map_layout.setVisibility(View.INVISIBLE);
            }else {
                no_data_layout.setVisibility(View.INVISIBLE);
                if (spinnerSelected == SPINNER_LIST) {
                    all_layout.setVisibility(View.VISIBLE);
                    adapter.changeCursor(cursor);
                } else if (spinnerSelected == SPINNER_MAP) {
                    map_layout.setVisibility(View.VISIBLE);
                    putMyMark(cursor);
                }

                if (patientOnOff == true) {
                    searchWithDatePatient(year, month + 1, dayOfMonth);
                    putPatientMark();
                }
            }
        }
    };
}
