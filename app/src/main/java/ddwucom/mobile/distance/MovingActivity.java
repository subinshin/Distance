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

    private static final String TAG = "MovingActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moving);

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
                LatLng standard = new LatLng(37.519576, 126.940245);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(standard).zoom(16).build();
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
