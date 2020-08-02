package ddwucom.mobile.distance;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyPageActivity extends AppCompatActivity {
    private static final String TAG ="MypageActivity";
    private FirebaseAuth mAuth;

    EditText myPage_et_email;
    EditText myPage_et_pw;
    EditText myPage_et_name;
    EditText myPage_et_birth;
    EditText myPage_et_phone;
    EditText myPage_et_pw2;

    String email;
    String pw;
    String name;
    String birth;
    String phone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        myPage_et_email = findViewById(R.id.mypage_et_email);
        myPage_et_pw = findViewById(R.id.mypage_et_pw);
        myPage_et_name = findViewById(R.id.mypage_et_name);
        myPage_et_birth = findViewById(R.id.mypage_et_birth);
        myPage_et_phone = findViewById(R.id.mypage_et_phone);
        myPage_et_pw2 = findViewById(R.id.mypage_et_pw_2);

        getUserInfo();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }

    private void getUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            if(document.getData() != null) {
                                UserInfo user = document.toObject(UserInfo.class);
                                //이메일, 폰번호 수정불가해야함..textView로 수정요망!!
                                
                                myPage_et_email.setText(user.getEmail());
                                myPage_et_pw.setText(user.getPass());
                                myPage_et_pw2.setText(user.getPass());
                                myPage_et_name.setText(user.getName());
                                myPage_et_birth.setText(user.getBirth());
                                myPage_et_phone.setText(user.getPhone());


                            }

                        } else {//false
                            Log.d(TAG, "No such document");
                        }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.mypage_btn_cancel:
                finish();
                break;
            case R.id.mypage_btn_modify:
                //add..
                //비밀번호 체크
                break;

        }
    }
}
