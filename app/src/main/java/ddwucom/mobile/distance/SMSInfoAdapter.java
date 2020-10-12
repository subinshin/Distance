package ddwucom.mobile.distance;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class SMSInfoAdapter extends CursorAdapter {

    private static final String TAG = "SMSInfoAdapter";

    Cursor cursor;
    Context context;
    int layout;
    LayoutInflater layoutInflater;
    String datetime;
    String location;
    int id;
    DBManager dbManager;
    SMSDBManager smsdbManager;
    SMSDBHelper smsdbHelper;

    public SMSInfoAdapter(Context context, int layout, Cursor c) {
        super(context, c);
        this.context = context;
        this.layout = layout;
        this.cursor = c;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dbManager = new DBManager(context);
        smsdbHelper = new SMSDBHelper(context);
        smsdbManager = new SMSDBManager(context);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView tv_datetime = view.findViewById(R.id.tv_datetime);
        TextView tv_location = view.findViewById(R.id.tv_location);
        Button btn = view.findViewById(R.id.saveBtn);

        id = cursor.getInt(cursor.getColumnIndex(smsdbHelper.COL_ID));
        datetime = cursor.getString(cursor.getColumnIndex(smsdbHelper.COL_DATETIME));
        location = cursor.getString(cursor.getColumnIndex(smsdbHelper.COL_LOCATION));
        tv_datetime.setText(datetime);
        tv_location.setText(location);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNewGps();
                Log.d(TAG, Integer.toString(id));
                boolean deleteResult = smsdbManager.deleteSMS(id);
                if (deleteResult) {
                    Log.d(TAG, "삭제 성공");
                } else {
                    Log.d(TAG, "삭제 실패");
                }
                Toast.makeText(context, Integer.toString(id), Toast.LENGTH_SHORT).show();
                changeCursor(cursor);
            }
        });
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = layoutInflater.inflate(layout, parent, false);

        return v;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    public void saveNewGps() {
        String[] str = datetime.split(" ");
        String[] date = str[0].split("/");
        int year = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]);
        int day = Integer.parseInt(date[2]);

        MovingInfo m = getAddress();

        boolean result = dbManager.addNewGps(
                new MovingInfo(year, month, day, str[0], str[1], str[1], m.getLatitude(), m.getLongitude(), m.getLocation(), "", location));
        if (result) {
            Toast.makeText(context, "저장 성공", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "저장 실패", Toast.LENGTH_SHORT).show();
        }
    }

    public MovingInfo getAddress() {
        Geocoder geocoder = new Geocoder(context);
        List<Address> addressList = null;
        double latitude;
        double longitude;
        String address = "";

        try {
            addressList = geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            Log.d(TAG, "geocoding error");
        }

        if(addressList.size() != 0) {
            address = addressList.get(0).getAddressLine(0);
            latitude = Double.parseDouble(String.format("%.6f", addressList.get(0).getLatitude()));
            longitude = Double.parseDouble(String.format("%.6f", addressList.get(0).getLongitude()));
//            Log.d(TAG, Double.toString(latitude));
//            Log.d(TAG, Double.toString(longitude));
//            Log.d(TAG, addressList.get(0).toString());
//            Log.d(TAG, addressList.get(0).getAddressLine(0));
        } else {
            latitude = 0;
            longitude = 0;
            address = "주소정보 없음";
        }

        return new MovingInfo(latitude, longitude, location + ", " + address);
    }
}
