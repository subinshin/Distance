package ddwucom.mobile.distance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class GpsActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Context context = this;
    String id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_gps);

        Intent intent = getIntent();
        id = intent.getStringExtra("email_id");
        Toast.makeText(this,  "사용자 이메일 : " + id, Toast.LENGTH_SHORT).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.menu_icon); //뒤로가기 버튼 이미지 지정

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                String title = menuItem.getTitle().toString();

                if(id == R.id.item_myPage){
                    Intent intent = new Intent(GpsActivity.this, MyPageActivity.class);
                    startActivity(intent);
                }else if(id == R.id.item_moving){
                    Toast.makeText(context, "나의 동선 확인", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.item_condition){
                    Toast.makeText(context, "확진자 현황 확인", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.item_setting){
                    Toast.makeText(context, "설정", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.item_logout){
                    Toast.makeText(context, "로그아웃", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    startLoginActivity();
                }

                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, loginActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
