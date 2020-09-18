package ddwucom.mobile.distance;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MovingActivity extends AppCompatActivity {

    Spinner spinner;
    DBManager manager;
    MovingInfoAdapter adapter;
    ListView all_listView;
    Cursor cursor;
    FrameLayout layout_moving;
    LayoutInflater inflater;

    LinearLayout all_layout;
    ConstraintLayout map_layout;

    FragmentManager fragmentManager;
    MapFragment mapFragment;
    GoogleMap map;

    DBHelper helper;
    private static final String TAG = "MovingActivity";

    Button btn_map_all;
    Button btn_map_date;

    CameraPosition cameraPosition;
    ArrayList<MarkerOptions> markersOption;
    ArrayList<Marker> markers;

    final static int SPINNER_LIST = 0;
    final static int SPINNER_MAP = 1;
    int spinnerSelected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moving);

        btn_map_date = findViewById(R.id.btn_map_date);
        btn_map_all = findViewById(R.id.btn_map_all);

        markersOption = new ArrayList<MarkerOptions>();
        markers = new ArrayList<Marker>();

        helper = new DBHelper(this);

        layout_moving = findViewById(R.id.layout_moving);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        manager = new DBManager(this);
        cursor = manager.getAllInfos();
        adapter = new MovingInfoAdapter(MovingActivity.this, R.layout.layout_listview, cursor);

        all_layout = findViewById(R.id.all_layout);
        map_layout = findViewById(R.id.map_layout);

        all_listView = findViewById(R.id.all_listView);
        all_listView.setAdapter(adapter);

        fragmentManager = getFragmentManager();
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.moving_map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                cameraPosition = new CameraPosition.Builder().target(new LatLng(37.5759, 126.9769)).zoom(16).build();
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

                        spinnerSelected = SPINNER_LIST;
                        break;
                    case 1:
                        all_layout.setVisibility(View.INVISIBLE);
                        map_layout.setVisibility(View.VISIBLE);

                        spinnerSelected = SPINNER_MAP;
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void putMyMark(Cursor cursor){
        // 원래 찍었던 마크들 전부 삭제
        for(Marker m : markers){
            m.remove();
        }
        markers.clear();

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
            LatLng pos = new LatLng(info.getLatitude(), info.getLongitude());
            MarkerOptions marker = new MarkerOptions().position(pos)
                    .title(info.getLocation()).snippet(s);

            markersOption.add(marker);
        }

        for(MarkerOptions m : markersOption){
            markers.add(map.addMarker(m));
        }
        markersOption.clear();

        if(info != null) {
            cameraPosition = new CameraPosition.Builder().target(new LatLng(info.getLatitude(), info.getLongitude())).zoom(16).build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void putPatientMark(Cursor cursor){

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void btnMapOnClick(View v){

        switch(v.getId()){
            case R.id.btn_map_all:
                cursor = manager.getAllInfos();
                if(spinnerSelected == SPINNER_LIST){
                    adapter.changeCursor(cursor);
                }else if(spinnerSelected == SPINNER_MAP){
                    putMyMark(cursor);
                }
                break;

            case R.id.btn_map_date:
                DatePickerDialog pickerDialog = new DatePickerDialog(this, pickerCallBack, 2020, 9 - 1, 18);
                pickerDialog.show();
                break;
        }
    }

    DatePickerDialog.OnDateSetListener pickerCallBack = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Log.d(TAG, year + "/" + month + "/" + dayOfMonth);
            cursor = manager.searchWithDate(year, month, dayOfMonth);

            if(spinnerSelected == SPINNER_LIST){
                adapter.changeCursor(cursor);
            }else if(spinnerSelected == SPINNER_MAP){
                putMyMark(cursor);
            }
        }
    };
}
