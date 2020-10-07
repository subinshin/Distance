package ddwucom.mobile.distance;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;


public class AddLocationActivity extends AppCompatActivity {

    DBManager manager;

    Date currentTime;
    TextView tv_add_loc;
    TextView tv_add_date;
    TextView tv_add_starttime;
    TextView tv_add_endtime;
    EditText et_add_memo;

    MovingInfo info;

    int currentYear;
    int currentMonth;
    int currentDay;
    int currentHour;
    int currentMinute;

    static final int START_TIME = 0;
    static final int END_TIME = 1;
    int timeFlag;

    TimePickerDialog timePickerDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_my_location);

        //dbManager 객체 생성
        manager = new DBManager(this);

        //인텐트 가져오기
        Intent intent = getIntent();
        String loc = intent.getStringExtra("location");
        LatLng latLng = (LatLng) intent.getParcelableExtra("latlng");

        // MovingInfo 객체 생성 후 알고 있는 값 모두 저장
        info = new MovingInfo();
        info.setLocation(loc);
        info.setLatitude(Double.parseDouble(String.format("%.6f", latLng.latitude)));
        info.setLongitude(Double.parseDouble(String.format("%.6f", latLng.longitude)));

        tv_add_loc = findViewById(R.id.tv_add_loc);
        tv_add_loc.setText(loc);
        tv_add_date = findViewById(R.id.tv_add_date);
        tv_add_starttime = findViewById(R.id.tv_add_starttime);
        tv_add_endtime = findViewById(R.id.tv_add_endtime);
        et_add_memo = findViewById(R.id.et_add_memo);

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

        timePickerDialog = new TimePickerDialog(AddLocationActivity.this, timePickerListener, currentHour, currentMinute, true);
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        tv_add_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddLocationActivity.this, datePickerListener, currentYear, currentMonth - 1, currentDay);
                datePickerDialog.getDatePicker();
                datePickerDialog.show();
            }
        });

        tv_add_starttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeFlag = START_TIME;
                timePickerDialog.show();
            }
        });

        tv_add_endtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeFlag = END_TIME;
                timePickerDialog.show();
            }
        });
    }

    DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            tv_add_date.setText(year + "년" + (month + 1) + "월" + day +"일");

            info.setYear(year);
            info.setMonth(month + 1);
            info.setDayOfMonth(day);
        }
    };

    TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            if(timeFlag == START_TIME){
                tv_add_starttime.setText(hour + "시" + minute + "분");
                info.setStartTime(hour + ":" + minute);
            }else if(timeFlag == END_TIME){
                tv_add_endtime.setText(hour + "시" + minute + "분");
                info.setEndTime(hour + ":" + minute);
            }
        }
    };

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_add_add:
                info.setMemo(et_add_memo.getText().toString());
                if(manager.saveMovingInfo(info)) {
                    Toast.makeText(this, "동선 추가 성공", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "추가 실패", Toast.LENGTH_SHORT).show();
                }
                finish();
                break;

            case R.id.btn_add_cancel:
                finish();
                break;
        }
    }
}
