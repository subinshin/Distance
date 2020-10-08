package ddwucom.mobile.distance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SMSReceiver extends BroadcastReceiver {

    private static final String TAG = "SMSReceiver";

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
    final SmsManager sms = SmsManager.getDefault();
    SMSDBManager dbManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");

        Log.d(TAG, "receiver 호출");

        dbManager = new SMSDBManager(context);
        // sms가 오면 onReceive() 가 호출된다. 여기에 처리하는 코드 작성하면 된다.

        Bundle bundle = intent.getExtras();
        // parseSmsMessage() 메서드의 코드들은 SMS문자의 내용을 뽑아내는 정형화된 코드이다.
        // 복잡해 보일 수 있으나 그냥 그대로 가져다 쓰면 된다.
        SmsMessage[] messages = parseSmsMessage(bundle);

        if (messages.length > 0) {
            // 문자메세지에서 송신자와 관련된 내용을 뽑아낸다.
            String sender = messages[0].getOriginatingAddress();
            Log.d(TAG, "sender: " + sender);

            // 문자메세지 내용 추출
            String contents = messages[0].getMessageBody();
            Log.d(TAG, "contents: " + contents);

            // 수신 날짜/시간 데이터 추출
            Date receivedDate = new Date(messages[0].getTimestampMillis());
            Log.d(TAG, "received date: " + receivedDate);

            String time = "";
            String store = "";
            if (contents != null) {
                if (contents.contains("[신한체크승인]")) {
                    String msg[] = contents.split(" ");
                    String year = simpleDateFormat.format(receivedDate);
                    time = year + "/" + msg[2] + " " + msg[3];
                    for (int i = 5; i < msg.length; i++) {
                        if (i == msg.length - 1) {
                            store += msg[i];
                        } else {
                            store += msg[i] + " ";
                        }
                    }
                    SMSInfo s = new SMSInfo(time, store);
                    Log.d(TAG, s.getDatetime() + s.getLocation());
                    boolean result = dbManager.addNewSMS(s);
                    if (result) {
                        Log.d(TAG, "success");
                    } else {
                        Log.d(TAG, "fail");
                    }

                    // 해당 내용을 모두 합쳐서 액티비티로 보낸다.
                    sendToActivity(context, time, store);

                }
            }
        }
    }

    private void sendToActivity(Context context, String time, String store){
        Intent intent = new Intent(context, SMSActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("time", time);
        intent.putExtra("store", store);
        context.startActivity(intent);
    }

    // 정형화된 코드. 그냥 가져다 쓰면 된다.
    private SmsMessage[] parseSmsMessage(Bundle bundle){
        Object[] objs = (Object[])bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[objs.length];

        for(int i=0;i<objs.length;i++){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                String format = bundle.getString("format");
                messages[i] = SmsMessage.createFromPdu((byte[])objs[i], format);
            }
            else{
                messages[i] = SmsMessage.createFromPdu((byte[])objs[i]);
            }

        }
        return messages;
    }
}
