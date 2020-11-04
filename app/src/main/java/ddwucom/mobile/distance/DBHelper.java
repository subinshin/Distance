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
    public final String COL_END_DATE = "endDate";
    public final String COL_START_TIME = "startTime";
    public final String COL_END_TIME = "endTime";
    public final String COL_LOCATION = "location";
    public final String COL_LATITUDE = "latitude";
    public final String COL_LONGITUDE = "longitude";
    public final String COL_MEMO = "memo";
    public final String COL_STORE = "store";

    public DBHelper(Context context){
        super(context, DB_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " integer primary key autoincrement, " +
                COL_YEAR + " integer, " + COL_MONTH + " integer, " + COL_DAY + " integer, " + COL_END_DATE + " TEXT, " + COL_START_TIME + " String, " + COL_END_TIME + " String, "
                + COL_LOCATION + " TEXT, " + COL_LATITUDE + " double, " + COL_LONGITUDE + " double, " + COL_MEMO + " TEXT, " + COL_STORE + " TEXT)";

        Log.d(TAG, sql);
        db.execSQL(sql);

        db.execSQL("insert into " + TABLE_NAME + " values(null, 2020, 10, 24, '2020/10/24', '"
                + "12:05', '12:07"
                + "', '광진구 구의동 43-1', 37.550711, 127.095457, 'sample', 'store1');" );
        db.execSQL("insert into " + TABLE_NAME + " values(null, 2020, 10, 28, '2020/10/28', '"
                + "14:02', '14:07"
                + "', '스타벅스 월곡역점, 성북구 하월곡동 46-73', 37.601944, 127.040408, 'sample', 'store2');" );
        db.execSQL("insert into " + TABLE_NAME + " values(null, 2020, 10, 29, '2020/10/29', '"
                + "12:15', '13:26"
                + "', '동덕여자대학교, 성북구 하월곡동 화랑로13길 60', 37.60632, 127.041808, 'sample', 'store4');" );
        db.execSQL("insert into " + TABLE_NAME + " values(null, 2020, 10, 31, '2020/10/31', '"
                + "18:21', '19:46"
                + "', '서울숲, 성동구 성수동1가 뚝섬로 273', 37.544566, 127.037496, 'sample', 'store5');" );
        db.execSQL("insert into " + TABLE_NAME + " values(null, 2020, 11, 02, '2020/11/02', '"
                + "10:05', '20:07"
                + "', '롯데월드, 송파구 잠실동 올림픽로 240', 37.511329, 127.098092, 'sample', 'store6');" );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
