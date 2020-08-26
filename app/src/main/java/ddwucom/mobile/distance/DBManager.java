package ddwucom.mobile.distance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Time;
import static android.content.ContentValues.TAG;

public class DBManager {
    DBHelper helper;
    Context context;
    Cursor cursor;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        this.context = context;
    }

    public Cursor getAllInfos(){
        SQLiteDatabase db = helper.getReadableDatabase();

        cursor = db.rawQuery("SELECT * FROM " + helper.TABLE_NAME, null);

        return cursor;
    }

    public void saveMovingInfo(int year, int month, int dayOfMonth, Time startTime, Time finshTime, String latitude, String longitude){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues row = new ContentValues();

        row.put(helper.COL_YEAR, year);
        row.put(helper.COL_MONTH, month);
        row.put(helper.COL_DAY, dayOfMonth);
        row.put(helper.COL_START_TIME, startTime.toString());
        row.put(helper.COL_FINISH_TIME, finshTime.toString());
        row.put(helper.COL_LATITUDE, latitude);
        row.put(helper.COL_LONGITUDE, longitude);

        if(db.insert(helper.TABLE_NAME, null, row) < 0){
            Log.e(TAG, "[saveGps] INSERT ERROR");
        }
    }

    public Cursor searchWithDate(int year, int month, int dayOfMonth){
        SQLiteDatabase db = helper.getReadableDatabase();

        String selection = helper.COL_YEAR + "=? and " + helper.COL_MONTH + "=? and " + helper.COL_DAY + "=?";
        String [] selectArgs = new String[]{ String.valueOf(year), String.valueOf(month + 1), String.valueOf(dayOfMonth)};

        cursor = db.query(helper.TABLE_NAME, null, selection, selectArgs, null, null, null, null);

        return cursor;
    }

    public Cursor searchWithGps(String latitude, String longitude){
        SQLiteDatabase db = helper.getReadableDatabase();

        String selection = null;
        String [] selectArgs = new String [] {latitude, longitude};

        cursor = db.query(helper.TABLE_NAME, null, selection, selectArgs, null, null, null, null);

        return cursor;
    }

}
