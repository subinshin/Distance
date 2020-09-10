package ddwucom.mobile.distance;

import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MovingActivity extends AppCompatActivity {

    Spinner spinner;
    CalendarView calendarView;
    DBManager manager;
    MovingInfoAdapter adapter;
    ListView all_listView;
    ListView calendar_listView;
    Cursor cursor;
    FrameLayout layout_moving;
    LayoutInflater inflater;

    LinearLayout all_layout;
    LinearLayout calendar_layout;
    LinearLayout map_layout;

    FragmentManager fragmentManager;
    MapFragment mapFragment;
    GoogleMap map;

    DBHelper helper;
    private static final String TAG = "MovingActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moving);

        helper = new DBHelper(this);

        layout_moving = findViewById(R.id.layout_moving);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        manager = new DBManager(this);
        cursor = manager.getAllInfos();
        adapter = new MovingInfoAdapter(MovingActivity.this, R.layout.layout_listview, cursor);

        all_layout = findViewById(R.id.all_layout);
        calendar_layout = findViewById(R.id.calendar_layout);
        map_layout = findViewById(R.id.map_layout);

        all_listView = findViewById(R.id.all_listView);
        all_listView.setAdapter(adapter);

        calendar_listView = findViewById(R.id.calendar_listView);
        calendar_listView.setAdapter(adapter);

        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                Log.d(TAG, year + ", " + month + ", " + dayOfMonth);
                Cursor cursor = manager.searchWithDate(year, month, dayOfMonth);
                adapter.changeCursor(cursor);

            }
        });

        fragmentManager = getFragmentManager();
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.moving_map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(37.5759, 126.9769)).zoom(16).build();
                map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                Cursor cursor = manager.getAllInfos();

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
                    map.addMarker(marker);
                }


                cameraPosition = new CameraPosition.Builder().target(new LatLng(info.getLatitude(), info.getLongitude())).zoom(16).build();
                map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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
                        calendar_layout.setVisibility(View.INVISIBLE);
                        map_layout.setVisibility(View.INVISIBLE);

                        break;
                    case 1:
                        adapter.changeCursor(null);

                        all_layout.setVisibility(View.INVISIBLE);
                        calendar_layout.setVisibility(View.VISIBLE);
                        map_layout.setVisibility(View.INVISIBLE);

                        break;
                    case 2:

                        all_layout.setVisibility(View.INVISIBLE);
                        calendar_layout.setVisibility(View.INVISIBLE);
                        map_layout.setVisibility(View.VISIBLE);

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





    }
}
