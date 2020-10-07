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

    public boolean saveMovingInfo(MovingInfo info){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues row = new ContentValues();

        row.put(helper.COL_YEAR, info.getYear());
        row.put(helper.COL_MONTH, info.getMonth());
        row.put(helper.COL_DAY, info.getDayOfMonth());
        row.put(helper.COL_START_TIME, info.getStartTime());
        row.put(helper.COL_END_TIME, info.getEndTime());
        row.put(helper.COL_LATITUDE, info.getLatitude());
        row.put(helper.COL_LONGITUDE, info.getLongitude());
        row.put(helper.COL_LOCATION, info.getLocation());
        row.put(helper.COL_MEMO, info.getMemo());

        if(db.insert(helper.TABLE_NAME, null, row) < 0){
            Log.e(TAG, "[saveGps] INSERT ERROR");
            return false;
        }
        return true;
    }

    public Cursor searchWithDate(int year, int month, int dayOfMonth){
        SQLiteDatabase db = helper.getReadableDatabase();

        String selection = helper.COL_YEAR + "=? and " + helper.COL_MONTH + "=? and " + helper.COL_DAY + "=?";
        String [] selectArgs = new String[]{ String.valueOf(year), String.valueOf(month + 1), String.valueOf(dayOfMonth)};

        cursor = db.query(helper.TABLE_NAME, null, selection, selectArgs, null, null, null, null);

        return cursor;
    }

    public Cursor searchWithGps(Double latitude, Double longitude){
        SQLiteDatabase db = helper.getReadableDatabase();

        String selection = null;
        String [] selectArgs = new String [] {String.valueOf(latitude), String.valueOf(longitude)};

        cursor = db.query(helper.TABLE_NAME, null, selection, selectArgs, null, null, null, null);

        return cursor;
    }

    //    DB 에 새로운 Gps 추가
    public boolean addNewGps(MovingInfo newGps) {

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(helper.COL_YEAR, newGps.getYear());
        value.put(helper.COL_MONTH, newGps.getMonth());
        value.put(helper.COL_DAY, newGps.getDayOfMonth());
        value.put(helper.COL_START_TIME, newGps.getStartTime());
        value.put(helper.COL_END_TIME, newGps.getEndTime());
        value.put(helper.COL_LOCATION, newGps.getLocation());
        value.put(helper.COL_LATITUDE, newGps.getLatitude());
        value.put(helper.COL_LONGITUDE, newGps.getLongitude());
        value.put(helper.COL_STORE, newGps.getStore());

//      insert 메소드를 사용할 경우 데이터 삽입이 정상적으로 이루어질 경우 1 이상, 이상이 있을 경우 0 반환 확인 가능
        long count = db.insert(helper.TABLE_NAME, null, value);
        helper.close();
        if (count > 0) return true;
        return false;
    }


}
