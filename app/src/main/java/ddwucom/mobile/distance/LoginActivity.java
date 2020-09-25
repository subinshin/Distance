package ddwucom.mobile.distance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG ="loginActivity";
    private FirebaseAuth mAuth;

    EditText login_et_id;
    EditText login_et_pw;

    Intent intent;

    // db로 전달해야 할 것들
    String email_id;
    String pass;

    Button btn_login;
    TextView tv_join;
    TextView tv_pw_find;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

       login_et_id = findViewById(R.id.login_et_id);
       login_et_pw = findViewById(R.id.login_et_pw);

       btn_login = findViewById(R.id.btn_login);
       tv_join = findViewById(R.id.tv_join);
       tv_pw_find = findViewById(R.id.tv_pw_find);


       btn_login.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               email_id = login_et_id.getText().toString();
               pass = login_et_pw.getText().toString();

               if(email_id.equals("") || pass.equals("")){
                   Toast.makeText(LoginActivity.this, "빈칸을 빠짐없이 입력하세요.", Toast.LENGTH_SHORT).show();
               }else {
                   login();
               }
           }
       });

       tv_join.setOnClickListener(new TextView.OnClickListener(){
           @Override
           public void onClick(View v) {
               intent = new Intent(LoginActivity.this, JoinActivity.class);
               startActivity(intent);
           }
       });

       tv_pw_find.setOnClickListener(new TextView.OnClickListener(){
           @Override
           public void onClick(View v) {
               intent = new Intent(LoginActivity.this, PasswordResetActivity.class);
               startActivity(intent);
           }
       });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
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
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            // ...
                        }

                        // ...
                    }
                });
    }

    private void startGpsActivity() {
        intent = new Intent(LoginActivity.this, GpsActivity.class);
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