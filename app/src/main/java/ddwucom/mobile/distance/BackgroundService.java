package ddwucom.mobile.distance;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundService extends Service {
    private static final String TAG="BackgroundService";

    static final int MSG_REGISTER_CLIENT = 44;

    ArrayList<Messenger> mClients = new ArrayList<Messenger>();

    TimerTask timerTask;
    Timer timer;
    static int counter = 0;

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
        // Service 객체와 (화면단 Activity 사이에서)
        // 통신(데이터를 주고받을) 때 사용하는 메서드
        // 데이터를 전달할 필요가 없으면 return null;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)

    }

    public void createTimeTask(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if(counter % 5 == 0){
                    Log.d(TAG, "Counter is " + String.valueOf(counter));
                }
                counter++;
            }
        };

        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent == null){
            return Service.START_STICKY;
        }else{
            createTimeTask();
        }

        // 서비스가 호출될 때마다 실행

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스가 종료될 때 실행
    }
}
