package ddwucom.mobile.distance;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateLocationActivity extends AppCompatActivity {

    DBManager manager;
    TextView tv_update_location;
    TextView tv_update_date;
    TextView tv_update_startTime;
    TextView tv_update_endTime;
    EditText et_update_memo;
    MovingInfo info;

    static final int START_TIME = 0;
    static final int END_TIME = 1;
    int timeFlag;

    Date currentTime;
    int currentYear;
    int currentMonth;
    int currentDay;
    int currentHour;
    int currentMinute;

    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_moving);

        tv_update_location = findViewById(R.id.tv_update_location);
        tv_update_date = findViewById(R.id.tv_update_date);
        tv_update_startTime = findViewById(R.id.tv_update_starttime);
        tv_update_endTime = findViewById(R.id.tv_update_endtime);
        et_update_memo = findViewById(R.id.et_update_memo);

        manager = new DBManager(this);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);

        info = getInfo(id);

        tv_update_location.setText(info.getLocation());
        tv_update_date.setText(info.getYear() + "/" + info.getMonth() + "/" + info.getDayOfMonth());
        tv_update_startTime.setText(info.getStartTime());
        tv_update_endTime.setText(info.getEndTime());
        et_update_memo.setText(info.getMemo());

        long now = System.currentTimeMillis();
        currentTime = new Date(now);
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat hourFormat = new SimpleDateFormat("hh");
        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");

        currentYear = Integer.parseInt(yearFormat.format(currentTime));
        currentMonth = Integer.parseInt(monthFormat.format(currentTime));
        currentDay = Integer.parseInt(dayFormat.format(currentTime));
        currentHour = Integer.parseInt(hourFormat.format(currentTime));
        currentMinute = Integer.parseInt(minuteFormat.format(currentTime));

        datePickerDialog = new DatePickerDialog(UpdateLocationActivity.this, datePickerListener, currentYear, currentMonth - 1, currentDay);
        timePickerDialog = new TimePickerDialog(UpdateLocationActivity.this, timePickerListener, currentHour, currentMinute, true);
    }

    public MovingInfo getInfo(int id){
        Cursor cursor = manager.searchWithId(id);
        MovingInfo info = null;

        while(cursor.moveToNext()){
            info = new MovingInfo();
            DBHelper helper= manager.getHelper();
            info.setId(cursor.getInt(cursor.getColumnIndex(helper.COL_ID)));
            info.setYear(cursor.getInt(cursor.getColumnIndex(helper.COL_YEAR)));
            info.setMonth(cursor.getInt(cursor.getColumnIndex(helper.COL_MONTH)));
            info.setDayOfMonth(cursor.getInt(cursor.getColumnIndex(helper.COL_DAY)));
            info.setLatitude(cursor.getDouble(cursor.getColumnIndex(helper.COL_LATITUDE)));
            info.setLongitude(cursor.getDouble(cursor.getColumnIndex(helper.COL_LONGITUDE)));
            info.setLocation(cursor.getString(cursor.getColumnIndex(helper.COL_LOCATION)));
            info.setStartTime(cursor.getString(cursor.getColumnIndex(helper.COL_START_TIME)));
            info.setEndTime(cursor.getString(cursor.getColumnIndex(helper.COL_END_TIME)));
            info.setMemo(cursor.getString(cursor.getColumnIndex(helper.COL_MEMO)));
        }

        return info;
    }

    public void textOnClick(View v){
        switch(v.getId()){
            case R.id.tv_update_location:
                Toast.makeText(this, "위치는 변경할 수 없습니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_update_date:
                datePickerDialog.getDatePicker();
                datePickerDialog.show();
                break;
            case R.id.tv_update_starttime:
                timeFlag = START_TIME;
                timePickerDialog.show();
                break;
            case R.id.tv_update_endtime:
                timeFlag = END_TIME;
                timePickerDialog.show();
                break;
        }
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_update_cancel:
                finish();
                break;
            case R.id.btn_update_update:
                info.setMemo(et_update_memo.getText().toString());
                String s = null;
                if(manager.updateInfo(info)){
                    s = "수정성공";
                }else {
                    s = "수정실패";
                }
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
                finish();
                break;

        }

    }

    DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            tv_update_date.setText(year + "/" + (month + 1) + "/" + day);

            info.setYear(year);
            info.setMonth(month + 1);
            info.setDayOfMonth(day);
        }
    };

    TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            if(timeFlag == START_TIME){
                tv_update_startTime.setText(hour + ":" + minute);
                info.setStartTime(hour + ":" + minute);
            }else if(timeFlag == END_TIME){
                tv_update_endTime.setText(hour + ":" + minute);
                info.setEndTime(hour + ":" + minute);
            }
        }
    };
}
