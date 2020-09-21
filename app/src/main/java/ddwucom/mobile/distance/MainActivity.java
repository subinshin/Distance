package ddwucom.mobile.distance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Intent intent;
    private FirebaseAuth mAuth;

    private static final String TAG ="MainActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        //미로그인 -> loginActivity
        if(mAuth.getCurrentUser() == null) {
            intent = new Intent(this, loginActivity.class);
            startActivity(intent);
        }
        else { //현재 로그인 된 상태 -> GpsActivity
            Log.d(TAG, "logined: "+mAuth.getCurrentUser().getUid());
            intent = new Intent(this, GpsActivity.class);
//            intent.putExtra("email_id", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            startActivity(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //미로그인 -> loginActivity
        if(mAuth.getCurrentUser() == null) {
            intent = new Intent(this, loginActivity.class);
            startActivity(intent);
        }
        else { //현재 로그인 된 상태 -> GpsActivity
            Log.d(TAG, "logined: "+mAuth.getCurrentUser().getUid());
            intent = new Intent(this, GpsActivity.class);
            intent.putExtra("email_id", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            startActivity(intent);
        }
    }
}

