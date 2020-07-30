package ddwucom.mobile.distance;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText login_et_id;
    EditText login_et_pw;

    // db로 전달해야 할 것들
    String id;
    String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       login_et_id = findViewById(R.id.login_et_id);
       login_et_pw = findViewById(R.id.login_et_pw);


    }

    public void onClick(View v){
        Intent intent;
        switch(v.getId()){
            case R.id.login_btn:
                //정보를 xml 혹은 json화 시켜서 db로 전달할 것
                id = login_et_id.getText().toString();
                pass = login_et_pw.getText().toString();

                if(id.equals("") || pass.equals("")){
                    Toast.makeText(this, "빈칸을 빠짐없이 입력하세요.", Toast.LENGTH_SHORT).show();
                }else {
                    intent = new Intent(this, GpsActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
                break;

            case R.id.join_btn:
                intent = new Intent(this, JoinActivity.class);
                startActivity(intent);
                break;
        }
    }

}