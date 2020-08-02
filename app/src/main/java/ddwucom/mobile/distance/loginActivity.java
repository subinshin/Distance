package ddwucom.mobile.distance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginActivity extends AppCompatActivity {
    private static final String TAG ="loginActivity";
    private FirebaseAuth mAuth;

    EditText login_et_id;
    EditText login_et_pw;

    Intent intent;

    // db로 전달해야 할 것들
    String email_id;
    String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

       login_et_id = findViewById(R.id.login_et_id);
       login_et_pw = findViewById(R.id.login_et_pw);


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }

    public void onClick(View v){

        switch(v.getId()){
            case R.id.login_btn:
                //정보를 xml 혹은 json화 시켜서 db로 전달할 것
                email_id = login_et_id.getText().toString();
                pass = login_et_pw.getText().toString();

                if(email_id.equals("") || pass.equals("")){
                    Toast.makeText(this, "빈칸을 빠짐없이 입력하세요.", Toast.LENGTH_SHORT).show();
                }else {
                    login();

                }
                break;

            case R.id.join_btn:
                intent = new Intent(this, JoinActivity.class);
                startActivity(intent);
                break;

            case R.id.goto_btn_password_reset:
                intent = new Intent(this, PasswordResetActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void login() {
        mAuth.signInWithEmailAndPassword(email_id, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            startGpsActivity();

                            
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(loginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            // ...
                        }

                        // ...
                    }
                });
    }

    private void startGpsActivity() {
        intent = new Intent(loginActivity.this, GpsActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("email_id", email_id);
        startActivity(intent);
    }

    //로그인 창에서 뒤로가기하면 앱이 종료

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}