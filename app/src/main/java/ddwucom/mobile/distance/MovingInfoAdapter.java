package ddwucom.mobile.distance;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MovingInfoAdapter extends CursorAdapter {
    Cursor cursor;
    Context context;
    int layout;
    LayoutInflater layoutInflater;
    DBHelper helper;
    View custom_dialog;
    final String TAG = "MovingInfoAdapter";
    AlertDialog.Builder builder;
    AlertDialog ad;


    public MovingInfoAdapter(Context context, int layout, Cursor c) {
        super(context, c);
        this.context = context;
        this.layout = layout;
        this.cursor = c;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        helper = new DBHelper(context);
        builder = new AlertDialog.Builder(context);

    }

    @Override
    public void bindView(View view, Context context2, Cursor cursor) {

        TextView tv_date = view.findViewById(R.id.tv_date);
        TextView tv_time = view.findViewById(R.id.tv_time);
        TextView tv_gps = view.findViewById(R.id.tv_gps);
        TextView tv_location = view.findViewById(R.id.tv_location);
        ImageView ic_search = view.findViewById(R.id.ic_search);

        final String location = cursor.getString(cursor.getColumnIndex(helper.COL_LOCATION));
        final String latlng = "(" + cursor.getDouble(cursor.getColumnIndex(helper.COL_LATITUDE)) + ", " + cursor.getDouble(cursor.getColumnIndex(helper.COL_LONGITUDE)) + ")";
        final String time = cursor.getString(cursor.getColumnIndex(helper.COL_START_TIME)) + " ~ " + cursor.getString(cursor.getColumnIndex(helper.COL_END_TIME));
        final String date = cursor.getInt(cursor.getColumnIndex(helper.COL_YEAR)) + "/" +cursor.getInt(cursor.getColumnIndex(helper.COL_MONTH)) + "/" + cursor.getInt(cursor.getColumnIndex(helper.COL_DAY));
        final String memo = cursor.getString(cursor.getColumnIndex(helper.COL_MEMO));
        tv_date.setText(date);
        tv_time.setText(time);
        tv_gps.setText(latlng);
        tv_location.setText(location);

        ic_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                custom_dialog = View.inflate(context, R.layout.my_alert_dialog, null);
                TextView tv_alert_location = custom_dialog.findViewById(R.id.tv_alert_location);
                TextView tv_alert_dateTime = custom_dialog.findViewById(R.id.tv_alert_dateTime);
                TextView tv_alert_latlng = custom_dialog.findViewById(R.id.tv_alert_latlng);
                TextView tv_alert_memo = custom_dialog.findViewById(R.id.tv_alert_memo);
                Button btn_alert_close = custom_dialog.findViewById(R.id.btn_myalert_close);

                btn_alert_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) { ad.dismiss();
                        }
                    });
                tv_alert_location.setText(location);
                tv_alert_latlng.setText(latlng);
                tv_alert_dateTime.setText(date + ", " + time);
                tv_alert_memo.setText("메모 : " + memo);

                builder.setView(custom_dialog);
                ad = builder.create();
                ad.show();
            }
        });
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = layoutInflater.inflate(layout, parent, false);

        return v;
    }

    public Cursor getCursor(){
        return cursor;
    }



}

