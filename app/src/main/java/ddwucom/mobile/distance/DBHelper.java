package ddwucom.mobile.distance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Time;

public class DBHelper extends SQLiteOpenHelper {

    final static String TAG = "DBHelper";
    final static String DB_NAME = "personal.db";
    public static String TABLE_NAME = "location_table";
    public final String COL_ID = "_id";
    public final String COL_YEAR = "year";
    public final String COL_MONTH = "month";
    public final String COL_DAY = "dayOfMonth";
    public final String COL_START_TIME = "startTime";
    public final String COL_END_TIME = "endTime";
    public final String COL_LOCATION = "location";
    public final String COL_LATITUDE = "latitude";
    public final String COL_LONGITUDE = "longitude";

    public DBHelper(Context context){
        super(context, DB_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " integer primary key autoincrement, " +
                COL_YEAR + " integer, " + COL_MONTH + " integer, " + COL_DAY + " integer, " + COL_START_TIME + " String, " + COL_END_TIME + " String, "
                + COL_LOCATION + " TEXT, " + COL_LATITUDE + " double, " + COL_LONGITUDE + " double)";

        Log.d(TAG, sql);
        db.execSQL(sql);

        db.execSQL("insert into " + TABLE_NAME + " values(null, 2020, 9, 10, '"
                + (new Time(12, 5, 0)).toString() +"', '" + (new Time(12, 11, 0)).toString()
                + "', '광진구 구의동 43-1', 37.550711, 127.095457);" );
        db.execSQL("insert into " + TABLE_NAME + " values(null, 2020, 9, 10, '"
                + (new Time(13, 2, 0)).toString() +"', '" + (new Time(13, 52, 0)).toString()
                + "', '스타벅스 월곡역점 (성북구 하월곡동 46-73)', 37.601944, 127.040408);" );

        db.execSQL("insert into " + TABLE_NAME + " values(null, 2020, 9, 11, '"
                + (new Time(10, 34, 0)).toString() +"', '" + (new Time(10, 59, 0)).toString()
                + "', '광진구 구의동 43-1', 37.550711, 127.095457);" );
        db.execSQL("insert into " + TABLE_NAME + " values(null, 2020, 9, 11, '"
                + (new Time(12, 5, 0)).toString() +"', '" + (new Time(14, 11, 0)).toString()
                + "', '동덕여자대학교 (성북구 하월곡동 화랑로13길 60)', 37.60632, 127.041808);" );

        db.execSQL("insert into " + TABLE_NAME + " values(null, 2020, 9, 18, '"
                + (new Time(12, 5, 0)).toString() +"', '" + (new Time(14, 11, 0)).toString()
                + "', '서울숲(성동구 성수동1가 뚝섬로 273)', 37.544566, 127.037496);" );
        db.execSQL("insert into " + TABLE_NAME + " values(null, 2020, 9, 16, '"
                + (new Time(12, 5, 0)).toString() +"', '" + (new Time(14, 11, 0)).toString()
                + "', '롯데월드 (송파구 잠실동 올림픽로 240)', 37.511329, 127.098092);" );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
