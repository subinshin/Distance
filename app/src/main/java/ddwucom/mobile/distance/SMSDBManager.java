package ddwucom.mobile.distance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Time;

import static android.content.ContentValues.TAG;

public class SMSDBManager {
    SMSDBHelper helper;
    Context context;
    Cursor cursor;

    public SMSDBManager(Context context) {
        helper = new SMSDBHelper(context);
        this.context = context;
    }
        public Cursor getAllSMSInfos(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + helper.TABLE_NAME, null);

        return cursor;
    }

    public void saveSMSInfo(String datetime, String location){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues row = new ContentValues();

        row.put(helper.COL_DATETIME, datetime);
        row.put(helper.COL_LOCATION, location);

        if(db.insert(helper.TABLE_NAME, null, row) < 0){
            Log.e(TAG, "[saveSMS] INSERT ERROR");
        }
    }

    //    DB 에 새로운 Gps 추가
    public boolean addNewSMS(SMSInfo newSMS) {

        Log.d("SMSReceiver", "addNewSMS");
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(helper.COL_DATETIME, newSMS.getDatetime());
        value.put(helper.COL_LOCATION, newSMS.getLocation());

//      insert 메소드를 사용할 경우 데이터 삽입이 정상적으로 이루어질 경우 1 이상, 이상이 있을 경우 0 반환 확인 가능
        long count = db.insert(helper.TABLE_NAME, null, value);
        helper.close();
        if (count > 0) return true;
        return false;
    }
}
