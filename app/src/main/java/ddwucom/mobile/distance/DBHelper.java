package ddwucom.mobile.distance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Time;
import java.util.Date;


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
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " integer primary key autoincrement, " +
                COL_YEAR + " integer, " + COL_MONTH + " integer, " + COL_DAY + " integer, " + COL_START_TIME + " String, " + COL_END_TIME + " String, "
                + COL_LOCATION + " TEXT, " + COL_LATITUDE + " TEXT, " + COL_LONGITUDE + " TEXT)";

        Log.d(TAG, sql);
        db.execSQL(sql);

        db.execSQL("insert into " + TABLE_NAME + " values(null, 2020, 8, 24, '"
                + (new Time(12, 5, 0)).toString() +"', '" + (new Time(12, 11, 0)).toString()
                + "', '구의동 75-89', '32.7895', '45.7895');" );
        db.execSQL("insert into " + TABLE_NAME + " values(null, 2020, 8, 26, '"
                + (new Time(18, 52, 0)).toString() +"', '" + (new Time(19, 11, 0)).toString()
                + "', '성북구 23-1', '15.3485', '45.7895');" );
        db.execSQL("insert into " + TABLE_NAME + " values(null, 2020, 8, 27, '"
                + (new Time(22, 43, 0)).toString() +"', '" + (new Time(22, 50, 0)).toString()
                + "', '동대문구 89-21', '32.7895', '45.7895');" );
        db.execSQL("insert into " + TABLE_NAME + " values(null, 2020, 8, 24, '"
                + (new Time(12, 5, 0)).toString() +"', '" + (new Time(12, 11, 0)).toString()
                + "', '구의동 75-89', '32.7895', '45.7895');" );
        db.execSQL("insert into " + TABLE_NAME + " values(null, 2020, 8, 26, '"
                + (new Time(18, 52, 0)).toString() +"', '" + (new Time(19, 11, 0)).toString()
                + "', '성북구 23-1', '15.3485', '45.7895');" );
        db.execSQL("insert into " + TABLE_NAME + " values(null, 2020, 8, 27, '"
                + (new Time(22, 43, 0)).toString() +"', '" + (new Time(22, 50, 0)).toString()
                + "', '동대문구 89-21', '32.7895', '45.7895');" );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
