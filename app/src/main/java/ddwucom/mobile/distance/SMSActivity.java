package ddwucom.mobile.distance;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;


public class SMSActivity extends AppCompatActivity {

    private static final String TAG = "c";
    EditText editText;
    DBManager dbManager;
    SMSDBManager smsdbManager;
    SMSDBHelper smsdbHelper;
    String time = "";
    String store = "";
    private Context context = this;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        dbManager = new DBManager(this);
        smsdbHelper = new SMSDBHelper(this);
        smsdbManager = new SMSDBManager(this);

        editText = (EditText)findViewById(R.id.editText);

        // (1) 리시버에 의해 해당 액티비티가 새롭게 실행된 경우
        Intent passedIntent = getIntent();
        processIntent(passedIntent);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                saveNewGps();
                cursor = smsdbManager.searchWithDateLocation(time, store);
                cursor.moveToFirst();
                final int id = cursor.getInt(cursor.getColumnIndex(smsdbHelper.COL_ID));
                Log.d(TAG, Integer.toString(id));
                boolean deleteResult = smsdbManager.deleteSMS(id);
                if (deleteResult) {
                    Log.d(TAG, "삭제 성공");
                } else {
                    Log.d(TAG, "삭제 실패");
                }
                break;
            case R.id.btnCancel:
                finish();
                break;
        }
    }

    public void saveNewGps() {
        String[] str = time.split(" ");
        String[] date = str[0].split("/");
        int year = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]);
        int day = Integer.parseInt(date[2]);

        MovingInfo m = getAddress();

        boolean result = dbManager.addNewGps(
                new MovingInfo(year, month, day, str[0], str[1], str[1], m.getLatitude(), m.getLongitude(), m.getLocation(), "", store));
        if (result) {
            Toast.makeText(context, "동선 저장 성공", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(context, "동선 저장 실패", Toast.LENGTH_SHORT).show();
        }
    }

    public MovingInfo getAddress() {
        Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> addressList = null;
        double latitude;
        double longitude;
        String address = "";

        try {
            addressList = geocoder.getFromLocationName(store, 1);
        } catch (IOException e) {
            Log.d(TAG, "geocoding error");
        }

        if(addressList.size() != 0) {
            address = addressList.get(0).getAddressLine(0);
            latitude = Double.parseDouble(String.format("%.6f", addressList.get(0).getLatitude()));
            longitude = Double.parseDouble(String.format("%.6f", addressList.get(0).getLongitude()));
        } else {
            latitude = 0;
            longitude = 0;
            address = "주소정보 없음";
        }

        return new MovingInfo(latitude, longitude, store + ", " + address);
    }

    private void processIntent(Intent intent){
        if(intent != null){
            // 인텐트에서 전달된 데이터를 추출하여, 활용한다.(여기서는 edittext를 통하여 내용을 화면에 뿌려주었다.)
            time = intent.getStringExtra("time");
            store = intent.getStringExtra("store");
            editText.setText(time + " " + store);
        }
    }

    // (2) 이미 실행된 상태였는데 리시버에 의해 다시 켜진 경우
    // (이러한 경우 onCreate()를 거치지 않기 때문에 이렇게 오버라이드 해주어야 모든 경우에 SMS문자가 처리된다!
    @Override
    protected void onNewIntent(Intent intent) {
        processIntent(intent);

        super.onNewIntent(intent);
    }

}
