package com.volla.launcher.util;

import androidnative.SystemDispatcher;
import android.app.Activity;
import android.app.PendingIntent;
import android.util.Log;
import android.content.Intent;
import android.telephony.gsm.SmsManager;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.qtproject.qt5.android.QtNative;
import com.volla.launcher.activity.ReceiveTextActivity;

public class MessageUtil {

    private static final String TAG = "MessageUtil";

    public static final String SEND_MESSAGE = "volla.launcher.messageAction";
    public static final String DID_SENT_MESSAGE = "volla.launcher.messageResponse";

    static {
        SystemDispatcher.addListener(new SystemDispatcher.Listener() {

            public void onDispatched(String type, Map message) {
                final Activity activity = QtNative.activity();

                if (type.equals(SEND_MESSAGE)) {

                    final String number = (String) message.get("number");
                    final String text = (String) message.get("text");

                    Runnable runnable = new Runnable () {

                        public void run() {
                            //Getting intent and PendingIntent instance
                            Intent intent = new Intent(activity.getApplicationContext(), ReceiveTextActivity.class);
                            PendingIntent pi = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent,0);

                            //Get the SmsManager instance and call the sendTextMessage method to send message
                            SmsManager sms = SmsManager.getDefault();
                            sms.sendTextMessage(number, null, text, pi,null);

                            Map responseMessage = new HashMap();
                            responseMessage.put("sent", true);
                            SystemDispatcher.dispatch(DID_SENT_MESSAGE, responseMessage);
                        }
                    };

                    Thread thread = new Thread(runnable);
                    thread.start();
                }
            }
        });
    }
}
