package ddwucom.mobile.distance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Time;

public class SMSDBHelper extends SQLiteOpenHelper {

    final static String TAG = "SMSDBHelper";
    final static String DB_NAME = "sms.db";
    public static String TABLE_NAME = "sms_table";
    public final String COL_ID = "_id";
    public final String COL_DATETIME = "datetime";
    public final String COL_LOCATION = "location";

    public SMSDBHelper(Context context){
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " integer primary key autoincrement, " +
                COL_DATETIME + " TEXT, " + COL_LOCATION + " TEXT)";

        Log.d(TAG, sql);
        db.execSQL(sql);

        db.execSQL("insert into " + TABLE_NAME + " values(null, '2020/11/02 16:15', '홈플러스 월곡점');" );
        db.execSQL("insert into " + TABLE_NAME + " values(null, '2020/11/03 10:32', '스타벅스 월곡역점');" );
        db.execSQL("insert into " + TABLE_NAME + " values(null, '2020/11/04 12:03', '누들아한타이 월곡점');" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
