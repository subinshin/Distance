package ddwucom.mobile.distance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SMSListActivity extends AppCompatActivity {

    private static final String TAG = "SMSListActivity";

    SMSDBManager manager;
    SwipeMenuListView listView;
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

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                final int pos = position;
                Log.d(TAG, Integer.toString(pos));
                cursor.moveToPosition(pos);
                final int id = cursor.getInt(0);
                switch(index){
                    case 0:
                        AlertDialog.Builder builder = new AlertDialog.Builder(SMSListActivity.this);
                        builder.setTitle("결제 문자 삭제")
                                .setMessage("해당 항목을 삭제하시겠습니까?")
                                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String s = "";
                                        if(manager.deleteSMS(id)){
                                            s = "삭제성공";
                                            cursor = manager.getAllSMSInfos();
                                            adapter.changeCursor(cursor);
                                        }else {
                                            s = "삭제실패";
                                        }
                                        Toast.makeText(SMSListActivity.this, s, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("취소", null)
                                .show();
                        break;
                }

                return false;
            }
        });

        // 스와이프 메뉴 설정
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
//                // create "open" item
//                SwipeMenuItem openItem = new SwipeMenuItem(
//                        getApplicationContext());
//                // set item background
//                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
//                        0xCE)));
//                // set item width
//                openItem.setWidth(dp2px(90));
//                // set item title
//                openItem.setTitle("수정");
//                // set item title fontsize
//                openItem.setTitleSize(18);
//                // set item title font color
//                openItem.setTitleColor(Color.WHITE);
//                // add to menu
//                menu.addMenuItem(openItem);

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
        adapter.changeCursor(cursor);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
