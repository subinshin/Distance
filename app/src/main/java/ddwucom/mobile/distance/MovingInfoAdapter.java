package ddwucom.mobile.distance;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class MovingInfoAdapter extends CursorAdapter {
    Cursor cursor;
    Context context;
    int layout;
    LayoutInflater layoutInflater;
    DBHelper helper;

    public MovingInfoAdapter(Context context, int layout, Cursor c) {
        super(context, c);
        this.context = context;
        this.layout = layout;
        this.cursor = c;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        helper = new DBHelper(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv_date = view.findViewById(R.id.tv_date);
        TextView tv_time = view.findViewById(R.id.tv_time);
        TextView tv_gps = view.findViewById(R.id.tv_gps);
        TextView tv_location = view.findViewById(R.id.tv_location);

        tv_date.setText(cursor.getInt(cursor.getColumnIndex(helper.COL_YEAR)) + "/" +cursor.getInt(cursor.getColumnIndex(helper.COL_MONTH)) + "/" + cursor.getInt(cursor.getColumnIndex(helper.COL_DAY)));
        tv_time.setText(cursor.getString(cursor.getColumnIndex(helper.COL_START_TIME)) + " ~ " + cursor.getString(cursor.getColumnIndex(helper.COL_END_TIME)));
        tv_gps.setText("(" + cursor.getString(cursor.getColumnIndex(helper.COL_LATITUDE)) + ", " + cursor.getString(cursor.getColumnIndex(helper.COL_LONGITUDE)) + ")");
        tv_location.setText(cursor.getString(cursor.getColumnIndex(helper.COL_LOCATION)));
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
