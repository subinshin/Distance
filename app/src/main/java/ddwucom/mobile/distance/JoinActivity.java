package ddwucom.mobile.distance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class JoinActivity extends AppCompatActivity {
    EditText join_et_email;
    EditText join_et_pw;
    EditText join_et_pw_2;
    EditText join_et_name;
    EditText join_et_birth;
    EditText join_et_phone;
    EditText join_et_phoneCode;

    String email;
    String pw;
    String pw2;
    String name;
    String birth;
    String phone;
    String phoneCode;
    Intent resultIntent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        join_et_email = findViewById(R.id.join_et_email);
        join_et_pw = findViewById(R.id.join_et_pw);
        join_et_pw_2 = findViewById(R.id.join_et_pw_2);
        join_et_name = findViewById(R.id.join_et_name);
        join_et_birth = findViewById(R.id.join_et_birth);
        join_et_phone = findViewById(R.id.join_et_phone);
        join_et_phoneCode = findViewById(R.id.join_et_phoneCode);

        resultIntent = new Intent();
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.join_btn_join:
                email = join_et_email.getText().toString();
                pw = join_et_pw.getText().toString();
                pw2 = join_et_pw_2.getText().toString();
                name = join_et_name.getText().toString();
                birth = join_et_birth.getText().toString();
                phone = join_et_phone.getText().toString();
                phoneCode = join_et_phoneCode.getText().toString();

                if(!pw.equals(pw2)){
                    Toast.makeText(JoinActivity.this, "비밀번호가 일치하지 않습니다.\n다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                }else if(email.equals("") || pw.equals("") || pw2.equals("") || name.equals("") || birth.equals("") || phone.equals("") || phoneCode.equals("")){
                    Toast.makeText(JoinActivity.this, "항목을 빠짐 없이 입력하세요.", Toast.LENGTH_SHORT).show();
                }else{
                    //db전송
                    Toast.makeText(JoinActivity.this, "회원가입 되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            case R.id.join_btn_cancel:
                Toast.makeText(JoinActivity.this, "회원가입 취소", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }

    }
}
