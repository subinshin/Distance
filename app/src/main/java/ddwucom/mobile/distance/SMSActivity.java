package ddwucom.mobile.distance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SMSActivity extends AppCompatActivity {

    EditText editText;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        editText = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // (1) 리시버에 의해 해당 액티비티가 새롭게 실행된 경우
        Intent passedIntent = getIntent();
        processIntent(passedIntent);
    }

    private void processIntent(Intent intent){
        if(intent != null){
            // 인텐트에서 전달된 데이터를 추출하여, 활용한다.(여기서는 edittext를 통하여 내용을 화면에 뿌려주었다.)
            String string = intent.getStringExtra("string");
            editText.setText(string);
        }
    }

    // (2) 이미 실행된 상태였는데 리시버에 의해 다시 켜진 경우
    // (이러한 경우 onCreate()를 거치지 않기 때문에 이렇게 오버라이드 해주어야 모든 경우에 SMS문자가 처리된다!
    @Override
    protected void onNewIntent(Intent intent) {
        processIntent(intent);

        super.onNewIntent(intent);
    }

}
