package ddwucom.mobile.distance;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
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

import java.util.Date;

public class MovingActivity extends AppCompatActivity {

    Spinner spinner;
    CalendarView calendarView;
    DBManager manager;
    MovingInfoAdapter adapter;
    ListView listView;
    Cursor cursor;
    ConstraintLayout layout_moving;
    LayoutInflater inflater;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moving);

        layout_moving = findViewById(R.id.layout_moving);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        manager = new DBManager(this);
        listView = findViewById(R.id.listView);

        cursor = manager.getAllInfos();
        adapter = new MovingInfoAdapter(MovingActivity.this, R.layout.layout_listview, cursor);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        //calendarListView.setAdapter(adapter);

        spinner = findViewById(R.id.search_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        layout_moving.removeAllViews();
                        break;
                    case 1:
                        layout_moving.removeAllViews();
                        inflater.inflate(R.layout.layout_moving_calendar, layout_moving, true);
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

        calendarView = findViewById(R.id.calendarView);
//        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
////                Cursor cursor = manager.searchWithDate(year, month, dayOfMonth);
////
////                adapter.changeCursor(cursor);
//
//            }
//        });

    }
}
