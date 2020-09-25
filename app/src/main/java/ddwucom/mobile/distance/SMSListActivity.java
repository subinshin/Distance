package ddwucom.mobile.distance;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ListView;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SMSListActivity extends AppCompatActivity {
    SMSDBManager manager;
    ListView listView;
    Cursor cursor;
    LayoutInflater inflater;
    SMSDBHelper helper;
    ArrayList<SMSInfo> smsList;
    SMSInfoAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_list);

        listView = findViewById(R.id.sms_listview);
        smsList = new ArrayList<SMSInfo>();
        manager = new SMSDBManager(this);
        adapter = new SMSInfoAdapter(SMSListActivity.this, R.layout.layout_listview_sms, cursor);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        smsList.clear();
        smsList.addAll(manager.getAllSMSInfos());
        adapter.notifyDataSetChanged();
    }
}
