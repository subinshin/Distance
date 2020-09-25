package ddwucom.mobile.distance;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class SMSInfoAdapter extends CursorAdapter {
    Cursor cursor;
    Context context;
    int layout;
    LayoutInflater layoutInflater;
    SMSDBHelper helper;

    public SMSInfoAdapter(Context context, int layout, Cursor c) {
        super(context, c);
        this.context = context;
        this.layout = layout;
        this.cursor = c;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        helper = new SMSDBHelper(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv_datetime = view.findViewById(R.id.tv_datetime);
        TextView tv_location = view.findViewById(R.id.tv_location);

        tv_datetime.setText(cursor.getString(cursor.getColumnIndex(helper.COL_DATETIME)));
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
