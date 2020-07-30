package ddwucom.mobile.distance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText login_et_id;
    EditText login_et_pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       login_et_id = findViewById(R.id.login_et_id);
       login_et_pw = findViewById(R.id.login_et_pw);


    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.login_btn:
                Intent intent = new Intent(this, GpsActivity.class);
                startActivity(intent);
                break;
        }
    }
}