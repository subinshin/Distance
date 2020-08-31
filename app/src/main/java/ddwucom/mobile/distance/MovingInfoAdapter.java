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

    public MovingInfoAdapter(Context context, int layout, Cursor c) {
        super(context, c);
        this.context = context;
        this.layout = layout;
        this.cursor = c;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv_date = view.findViewById(R.id.tv_date);
        TextView tv_time = view.findViewById(R.id.tv_time);
        TextView tv_gps = view.findViewById(R.id.tv_gps);
        TextView tv_location = view.findViewById(R.id.tv_location);

        tv_date.setText(cursor.getInt(cursor.getColumnIndex("year")) + "/" +cursor.getInt(cursor.getColumnIndex("month")) + "/" + cursor.getInt(cursor.getColumnIndex("dayOfMonth")));
        tv_time.setText(cursor.getString(cursor.getColumnIndex("startTime")) + " ~ " + cursor.getString(cursor.getColumnIndex("finishTime")));
        tv_gps.setText("(" + cursor.getString(cursor.getColumnIndex("latitude")) + ", " + cursor.getString(cursor.getColumnIndex("longitude")) + ")");
        tv_location.setText(cursor.getString(cursor.getColumnIndex("location")));
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
