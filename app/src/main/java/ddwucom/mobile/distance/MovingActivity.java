package ddwucom.mobile.distance;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MovingActivity extends AppCompatActivity{
    //gpsAcitivity로 부터 가져온 확진자 동선
    ArrayList<PathInfo> pathList;

    DBManager manager;
    MovingInfoAdapter adapter;
    SwipeMenuListView all_listView;
    Cursor cursor;
    FrameLayout layout_moving;

    ConstraintLayout all_layout;
    ConstraintLayout map_layout;
    ConstraintLayout no_data_layout;

    FragmentManager fragmentManager;
    MapFragment mapFragment;
    GoogleMap map;

    DBHelper helper;
    private static final String TAG = "MovingActivity";

    ImageButton btn_all;
    ImageButton btn_date;
    ImageButton btn_map_patient;

    CameraPosition cameraPosition;
    ArrayList<MarkerOptions> markersOption;
    ArrayList<Marker> myMarkers;

    ArrayList<PathInfo> patientPathList;
    ArrayList<PathInfo> patientSelectedList;
    ArrayList<Marker> patientMarkers;

    final static int LIST_TAB = 0;
    final static int MAP_TAB = 1;
    int tabSelected;

    //확진자 동선보기 버튼 클릭시 필요
    boolean patientOnOff;

    Bitmap smallMarker;

    Date currentTime;
    int currentYear;
    int currentMonth;
    int currentDay;
    int selectedYear;
    int selectedMonth;
    int selectedDay;

    int clickedBtn;
    final int BTN_ALL = 10;
    final int BTN_DATE = 20;

    TabLayout tabLayout;
    TextView tv_searchCondition;

    AlertDialog.Builder builder;
    AlertDialog dialog;
    View custom_dialog;
    ArrayList<MovingInfo> infoArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moving);

        Log.d(TAG, "Start movingActivity");
        tabSelected = LIST_TAB;
        clickedBtn = BTN_ALL;

        Intent intent = getIntent();
        //intent로 부터 전달받은 확진자 동선
        patientPathList = (ArrayList<PathInfo>) intent.getSerializableExtra("pathList");
        patientSelectedList = new ArrayList<PathInfo>();

        Log.d(TAG, "확진자리스트 갯수: " + String.valueOf(patientPathList.size()));
        for (PathInfo pathInfo : patientPathList) {
            Log.d(TAG, "확진자: " + pathInfo.toString());
        }

        //확진자동선 전체내용복사
        getAllPatientList();
        patientMarkers = new ArrayList<Marker>();
        patientOnOff = false;

        // 아이콘 설정
        btn_date = findViewById(R.id.btn_date);
        btn_all = findViewById(R.id.btn_all);
        btn_map_patient = findViewById(R.id.btn_map_patient);

        // 내 동선 저장할 마커 배열
        markersOption = new ArrayList<MarkerOptions>();
        myMarkers = new ArrayList<Marker>();

        // 레이아웃 가져오기
        layout_moving = findViewById(R.id.layout_moving);
        all_layout = findViewById(R.id.all_layout);
        map_layout = findViewById(R.id.map_layout);
        no_data_layout = findViewById(R.id.no_data_layout);
        tabLayout = findViewById(R.id.tabLayout);
        // tab 선택시 작동하는 리스너
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                cursor = manager.getAllInfos();
                tv_searchCondition.setText("전체");
                clickedBtn = BTN_ALL;

                int pos = tab.getPosition();
                    switch (pos){
                        case LIST_TAB:
                            adapter.changeCursor(cursor);
                            all_layout.setVisibility(View.VISIBLE);
                            map_layout.setVisibility(View.INVISIBLE);
                            btn_map_patient.setVisibility(View.INVISIBLE);
                            tabSelected = LIST_TAB;
                            break;
                        case MAP_TAB:
                            all_layout.setVisibility(View.INVISIBLE);
                            map_layout.setVisibility(View.VISIBLE);
                            btn_map_patient.setVisibility(View.VISIBLE);
                            putMyMark();
                            tabSelected = MAP_TAB;
                            break;
                    }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        // db데이터 가져올 때 사용 할 변수들
        helper = new DBHelper(this);
        manager = new DBManager(this);
        adapter = new MovingInfoAdapter(MovingActivity.this, R.layout.layout_listview_2, cursor);

        all_listView = findViewById(R.id.all_listView);
        all_listView.setAdapter(adapter);
        all_listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                final int pos = position;
                cursor.moveToPosition(pos);
                final int id = cursor.getInt(cursor.getColumnIndex(helper.COL_ID));
                switch(index){
                    case 0:
                        Intent intent = new Intent(MovingActivity.this, UpdateLocationActivity.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                        break;
                    case 1:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MovingActivity.this);
                        builder.setTitle("동선 삭제")
                                .setMessage("해당 항목을 삭제하시겠습니까?")
                                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String s = null;
                                        if(manager.removeInfo(id)){
                                            s = "삭제성공";
                                            if(clickedBtn == BTN_ALL){
                                                cursor = manager.getAllInfos();
                                            }else if(clickedBtn == BTN_DATE) {
                                                cursor = manager.searchWithDate(selectedYear, selectedMonth, selectedDay);
                                            }
                                            cursorCheck(cursor);
                                            adapter.changeCursor(cursor);
                                        }else {
                                            s = "삭제실패";
                                        }
                                        Toast.makeText(MovingActivity.this, s, Toast.LENGTH_SHORT).show();
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
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("수정");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

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
        all_listView.setMenuCreator(creator);
        all_listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        tv_searchCondition = findViewById(R.id.tv_searchCondition);

        // 현재시간 저장
        long now = System.currentTimeMillis();
        currentTime = new Date(now);
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        currentYear = Integer.parseInt(yearFormat.format(currentTime));
        currentMonth = Integer.parseInt(monthFormat.format(currentTime));
        currentDay = Integer.parseInt(dayFormat.format(currentTime));

        infoArray = new ArrayList<MovingInfo>();

        // 마커 이미지 불러오기
        final BitmapDrawable bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.som_mark_big);
        Bitmap b = bitmapDrawable.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, 150, 200, false);

        fragmentManager = getFragmentManager();
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.moving_map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        builder = new AlertDialog.Builder(MovingActivity.this);
                        custom_dialog = View.inflate(MovingActivity.this, R.layout.my_alert_dialog, null);
                        Object object = marker.getTag();

                        ImageView img_alert = custom_dialog.findViewById(R.id.img_myalert);
                        TextView tv_alert_location = custom_dialog.findViewById(R.id.tv_alert_location);
                        TextView tv_alert_dateTime = custom_dialog.findViewById(R.id.tv_alert_dateTime);
                        TextView tv_alert_latlng = custom_dialog.findViewById(R.id.tv_alert_latlng);
                        TextView tv_alert_memo = custom_dialog.findViewById(R.id.tv_alert_memo);
                        Button btn_alert_close = custom_dialog.findViewById(R.id.btn_myalert_close);

                        btn_alert_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        if(object instanceof MovingInfo) {
                            MovingInfo info = (MovingInfo) object;

                            tv_alert_location.setText(info.getLocation());
                            tv_alert_latlng.setText("(" + info.getLatitude() + ", " + info.getLongitude() + ")");
                            tv_alert_dateTime.setText(info.getYear() + "/" + info.getMonth() + "/" + info.getDayOfMonth() + ", " + info.getStartTime() + "~" + info.getEndTime());
                            tv_alert_memo.setText("메모 : " + info.getMemo());
                        }
                        else if(object instanceof PathInfo){
                            PathInfo info = (PathInfo) object;

                            img_alert.setImageResource(R.drawable.patient_alert_img);
                            btn_alert_close.setBackground(ContextCompat.getDrawable(MovingActivity.this, R.drawable.patient_alert_btn));
                            tv_alert_location.setText(info.getPlace());
                            tv_alert_latlng.setText("(" + info.getLat() + ", " + info.getLng() + ")");
                            tv_alert_dateTime.setText(info.getVisitDate());
                            tv_alert_memo.setText("확진자 번호 : " + info.getPatient_no());
                        }


                        builder.setView(custom_dialog);
                        dialog = builder.create();
                        dialog.show();
                        return true;
                    }
                });

                cameraPosition = new CameraPosition.Builder().target(new LatLng(37.5759, 126.9769)).zoom(30).build();
                map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "tabSelected : " + tabSelected);

        cursor = manager.getAllInfos();
        adapter.changeCursor(cursor);
    }

    public void getAllPatientList(){
        patientSelectedList.clear();
        for(PathInfo p : patientPathList){
            patientSelectedList.add(p);
        }
    }

    public void putMyMark(){
        // 원래 찍었던 마크들 전부 삭제
        removeMarkers(myMarkers);

        // cursor 값으로 새로운 마크 생성
        MovingInfo info = null;
        int dataFlag = 0;

        while(cursor.moveToNext()){
            dataFlag = 1;
            info = new MovingInfo();
            info.setId(cursor.getInt(cursor.getColumnIndex(helper.COL_ID)));
            info.setYear(cursor.getInt(cursor.getColumnIndex(helper.COL_YEAR)));
            info.setMonth(cursor.getInt(cursor.getColumnIndex(helper.COL_MONTH)));
            info.setDayOfMonth(cursor.getInt(cursor.getColumnIndex(helper.COL_DAY)));
            info.setLatitude(cursor.getDouble(cursor.getColumnIndex(helper.COL_LATITUDE)));
            info.setLongitude(cursor.getDouble(cursor.getColumnIndex(helper.COL_LONGITUDE)));
            info.setLocation(cursor.getString(cursor.getColumnIndex(helper.COL_LOCATION)));
            info.setStartTime(cursor.getString(cursor.getColumnIndex(helper.COL_START_TIME)));
            info.setEndTime(cursor.getString(cursor.getColumnIndex(helper.COL_END_TIME)));
            info.setMemo(cursor.getString(cursor.getColumnIndex(helper.COL_MEMO)));

            Log.d(TAG,  "latitude : "+ info.getLatitude() + ", longitude : " + info.getLongitude());

            String s = info.getYear() + "/" + info.getMonth() + "/" + info.getDayOfMonth() + ", " + info.getStartTime() + " - " + info.getEndTime();

            // 이미 추가된 markerOption중에 좌표가 동일한 것이 있는지 확인
            MarkerOptions mo = null;
            for(MarkerOptions m : markersOption){
                if(m.getPosition().longitude == info.getLongitude() && m.getPosition().latitude == info.getLatitude()){
                    m.snippet(m.getSnippet() + "\n" + s);
                    mo = m;
                    break;
                }
            }

            //동일한 좌표가 없다면 markerOption 생성 후 추가
            if(mo == null) {
                LatLng pos = new LatLng(info.getLatitude(), info.getLongitude());
                MarkerOptions marker = new MarkerOptions().position(pos);

                marker.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                markersOption.add(marker);
                //마커 찍기
                Marker m = map.addMarker(marker);
                m.setTag(info);
                myMarkers.add(m);
            }

            infoArray.add(info);
        }

        // data 없으면 화면처리
        if(dataFlag == 0){
            no_data_layout.setVisibility(View.VISIBLE);
            map_layout.setVisibility(View.INVISIBLE);

            return;
        }else{
            no_data_layout.setVisibility(View.INVISIBLE);
            map_layout.setVisibility(View.VISIBLE);
        }
//
//        //마커찍기
//        for(int i = 0; i < markersOption.size(); i++){
//            Marker marker = map.addMarker(markersOption.get(i));
//            marker.setTag(infoArray.get(i));
//            myMarkers.add(marker);
//        }

        markersOption.clear();
        infoArray.clear();

        if(info != null) {
            cameraPosition = new CameraPosition.Builder().target(new LatLng(info.getLatitude(), info.getLongitude())).zoom(16).build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void putPatientMark(){
        removeMarkers(patientMarkers);

        BitmapDrawable bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.som_patient_marker_blue);
        Bitmap b = bitmapDrawable.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 180, 200, false);

        for(PathInfo p : patientSelectedList){
            MarkerOptions markerOptions
                    = new MarkerOptions()
                    .position(new LatLng(p.getLat(), p.getLng()))
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

            Marker m = map.addMarker(markerOptions);
            m.setTag(p);
            patientMarkers.add(m);
        }
    }

    public void removeMarkers(ArrayList<Marker> markers){
        for(Marker m : markers){
            m.remove();
        }
        markers.clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void btnOnClick(View v){
        switch(v.getId()){
            case R.id.btn_all:
                clickedBtn = BTN_ALL;
                tv_searchCondition.setText("전체");
                cursor = manager.getAllInfos();
                if(cursorCheck(cursor)){
                    if (tabSelected == LIST_TAB) {
                        adapter.changeCursor(cursor);
                        adapter.notifyDataSetChanged();
                        all_layout.setVisibility(View.VISIBLE);
                    } else if (tabSelected == MAP_TAB) {
                        map_layout.setVisibility(View.VISIBLE);
                        if (patientOnOff) {
                            getAllPatientList();
                            putPatientMark();
                        }
                        putMyMark();
                        //확진자 전체 검색
                    }
                }
                break;

            case R.id.btn_date:

                DatePickerDialog pickerDialog = new DatePickerDialog(this, pickerCallBack, currentYear, currentMonth - 1, currentDay);
                pickerDialog.show();
                break;

            case R.id.btn_map_patient:
                if(patientOnOff == false){
                    putPatientMark();
                    patientOnOff = true;
                    Toast.makeText(this, "On", Toast.LENGTH_SHORT).show();
                }else {
                    removeMarkers(patientMarkers);
                    patientOnOff = false;
                    Toast.makeText(this, "Off", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    //날짜별로 확진자 동선 찾기
    public ArrayList<PathInfo> searchWithDatePatient(int year, int month, int dayOfMonth){
        patientSelectedList.clear();

        for(PathInfo info : patientPathList){
            if(year == info.getYear() && month == info.getMonth() && dayOfMonth == info.getDayOfMonth()){
                patientSelectedList.add(info);
            }
        }
        return patientSelectedList;
    }

    DatePickerDialog.OnDateSetListener pickerCallBack = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Log.d(TAG, year + "/" + month + "/" + dayOfMonth);
            tv_searchCondition.setText(year +"/" + (month + 1)+ "/" + dayOfMonth);

            clickedBtn = BTN_DATE;
            selectedYear = year;
            selectedMonth = month;
            selectedDay = dayOfMonth;
            cursor = manager.searchWithDate(selectedYear, selectedMonth, selectedDay);

            // 검색결과가 없을 때 화면처리

            if(tabSelected == LIST_TAB){
                if(cursorCheck(cursor)){
                    adapter.changeCursor(cursor);
                    all_layout.setVisibility(View.VISIBLE);
                }
            }else if(tabSelected == MAP_TAB){
                map_layout.setVisibility(View.VISIBLE);
                putMyMark();

                if (patientOnOff) {
                    searchWithDatePatient(year, month + 1, dayOfMonth);
                    putPatientMark();
                }
            }

        }
    };

    public boolean cursorCheck(Cursor cursor){
        if(!cursor.moveToNext()){
            no_data_layout.setVisibility(View.VISIBLE);
            all_layout.setVisibility(View.INVISIBLE);
            map_layout.setVisibility(View.INVISIBLE);

            return false;
        }else{
            no_data_layout.setVisibility(View.INVISIBLE);
            cursor.moveToPrevious();
        }
        return true;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

}

