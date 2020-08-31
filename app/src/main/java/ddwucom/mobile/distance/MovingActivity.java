package ddwucom.mobile.distance;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MovingActivity extends AppCompatActivity {

    Spinner spinner;
    CalendarView calendarView;
    DBManager manager;
    MovingInfoAdapter adapter;
    ListView all_listView;
    ListView calendar_listView;
    Cursor cursor;
    ConstraintLayout layout_moving;
    LayoutInflater inflater;

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

        spinner = findViewById(R.id.search_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        adapter.changeCursor(manager.getAllInfos());

                        layout_moving.removeAllViews();
                        inflater.inflate(R.layout.layout_moving_all, layout_moving, true);

                        all_listView = findViewById(R.id.all_listView);
                        all_listView.setAdapter(adapter);

                        break;
                    case 1:
                        adapter.changeCursor(null);

                        layout_moving.removeAllViews();
                        inflater.inflate(R.layout.layout_moving_calendar, layout_moving, true);

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

                        break;
                    case 2:
                        layout_moving.removeAllViews();
                        inflater.inflate(R.layout.layout_moving_location, layout_moving, true);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





    }
}
