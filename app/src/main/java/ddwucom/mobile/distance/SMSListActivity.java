package ddwucom.mobile.distance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SMSListActivity extends AppCompatActivity {

    private static final String TAG = "SMSListActivity";

    SMSDBManager manager;
    SwipeMenuListView listView;
    Cursor cursor;
    LayoutInflater inflater;
    SMSDBHelper smsdbHelper;
    ArrayList<SMSInfo> smsList;
    SMSInfoAdapter adapter;

    ConstraintLayout allList;
    ConstraintLayout noData;

    View custom_dialog;
    AlertDialog.Builder builder;
    AlertDialog ad;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_list);

        allList = findViewById(R.id.all_layout);
        noData = findViewById(R.id.no_data_layout);

        listView = findViewById(R.id.sms_listview);
        smsList = new ArrayList<SMSInfo>();
        manager = new SMSDBManager(this);
        smsdbHelper = new SMSDBHelper(this);
        adapter = new SMSInfoAdapter(SMSListActivity.this, R.layout.layout_listview_sms, cursor);



        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                final int pos = position;
                Log.d(TAG, Integer.toString(pos));
                cursor.moveToPosition(pos);
                final int id = cursor.getInt(cursor.getColumnIndex(smsdbHelper.COL_ID));
                switch(index){
                    case 0:
                        custom_dialog = View.inflate(SMSListActivity.this, R.layout.delete_alert_dialog, null);
                        builder = new AlertDialog.Builder(SMSListActivity.this);

                        TextView alert_title = custom_dialog.findViewById(R.id.alert_title);
                        alert_title.setText("결제 문자 삭제");
                        Button btn_alert_delete = custom_dialog.findViewById(R.id.btn_alert_delete);
                        Button btn_alert_close = custom_dialog.findViewById(R.id.btn_alert_close);

                        btn_alert_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String s = "";
                                if(manager.deleteSMS(id)){
                                    s = "삭제성공";
                                    cursor = manager.getAllSMSInfos();
                                    cursorCheck(cursor);
                                    adapter.changeCursor(cursor);
                                }else {
                                    s = "삭제실패";
                                }
                                ad.dismiss();
                                Toast.makeText(SMSListActivity.this, s, Toast.LENGTH_SHORT).show();
                            }
                        });
                        btn_alert_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ad.dismiss();
                            }
                        });

                        builder.setView(custom_dialog);
                        ad = builder.create();
                        ad.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                        ad.show();
                        break;
                }

                return false;
            }
        });

        // 스와이프 메뉴 설정
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        listView.setMenuCreator(creator);
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        smsList.clear();
        cursor = manager.getAllSMSInfos();
        if (cursorCheck(cursor)) {
            listView.setAdapter(adapter);
        }
        adapter.changeCursor(cursor);
    }

    public boolean cursorCheck(Cursor cursor){
        if(!cursor.moveToNext()){
            noData.setVisibility(View.VISIBLE);
            allList.setVisibility(View.INVISIBLE);

            return false;
        }else{
            noData.setVisibility(View.INVISIBLE);
            cursor.moveToPrevious();
        }
        return true;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
