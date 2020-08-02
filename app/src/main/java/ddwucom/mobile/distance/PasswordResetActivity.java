package ddwucom.mobile.distance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {
    private static final String TAG ="PasswordResetActivity";
    private FirebaseAuth mAuth;

    EditText password_et_id;
    Button password_btn_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        password_et_id = findViewById(R.id.password_et_id);
        password_btn_send = findViewById(R.id.password_btn_send);
        password_btn_send.setOnClickListener(onClickListener);


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.password_btn_send:
                    send();
                    break;
            }
        }
    };


    private void send() {
        String emailAddress = password_et_id.getText().toString();

        if(emailAddress.length() > 0) {

            mAuth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                                Toast.makeText(PasswordResetActivity.this, "이메일을 보냈습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
        else {
            Toast.makeText(PasswordResetActivity.this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
        }

    }


}