package com.aa.neelabh.cardviewwithrecyclerview;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

public class MessageListenerIntentService extends IntentService {

    private static final String TAG = "MessageListener";
    private static final String CHANNEL_ID = "com.aa.neelabh.demo";
    private static final int NOTIFICATION_ID = 100001;

    public MessageListenerIntentService() {
        super("MessageListenerService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        int count = 0;
        int max_count = 100;
        int id = 1;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        MessageListenerService messageListenerService = new MessageListenerService();

        try {
            while (count <= max_count) {
                Thread.sleep(5000);

                String url = "http://10.0.2.2:8080/message/" + Integer.toString(id);

                Log.i(TAG, "URL to hit : " + url);

                String responseMessage = messageListenerService.run(url);

                JsonObject json = new JsonParser().parse(responseMessage).getAsJsonObject();
                JsonElement messageAsJson = json.get("message");
                JsonElement statusAsJson = json.get("status");
                Log.i(TAG, "messageAsJson received : " + messageAsJson.toString());

                if (messageAsJson != null && !messageAsJson.toString().isEmpty()
                        && statusAsJson == null) {
                    Log.i(TAG, "Got message : " + responseMessage);

//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ((MyRecyclerViewAdapter) mAdapter).addItem(new DataObject(messageAsJson.toString(), R.drawable.gateagent),
//                                    0);
//                        }
//                    });

                    // Create notification

                    Intent actionIntent = new Intent(this, MainActivity.class);
                    actionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, actionIntent, 0);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                            .setSmallIcon(R.drawable.gateagent)
                            .setContentTitle("Message")
                            .setContentText(messageAsJson.toString())
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true);

                    notificationManager.notify(NOTIFICATION_ID, builder.build());

                    id++; //increment the counter to next message
                } else {
                    Log.i(TAG, "No message available yet");
                }

                count = count + 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
