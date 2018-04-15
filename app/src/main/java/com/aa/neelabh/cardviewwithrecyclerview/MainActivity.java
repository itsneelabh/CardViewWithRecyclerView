package com.aa.neelabh.cardviewwithrecyclerview;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static String LOG_TAG = "CardViewActivity";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        mAdapter = new MyRecyclerViewAdapter(getDataSet());
        rv.setAdapter(mAdapter);

        StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);

        runThread();
    }


    @Override
    protected void onResume() {
        super.onResume();

        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
            .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(LOG_TAG, " Clicked on Item " + position);

                ((MyRecyclerViewAdapter) mAdapter).addItem(new DataObject("You have now reached" +
                    " your gate. Bon Voyage!", R.drawable.gateb17), 0);
            }
        });
    }

    private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();

        results.add(new DataObject("Please proceed towards the Gate 15. Your Gate 17 will be " +
            "next gate on your left.", R.drawable.gate15));

        return results;
    }
/*
    private void runThread() throws IOException {
        new Thread() {
            public void run() {
                int count = 0;
                int max_count = 30;
                int id = 1;

                MessageListenerService messageListenerService = new MessageListenerService();

                try {
                    while (count <= max_count) {
                        runOnUiThread(new Runnable() {

                        Thread.sleep(5000);

                        String url = "http://10.0.2.2:8080/message/" + Integer.toString(id);
                        Log.i(LOG_TAG, "URL to hit : " + url);

                        String responseMessage = messageListenerService.run(url);

                        JsonObject json = new JsonParser().parse(responseMessage).getAsJsonObject();
                        JsonElement messageAsJson = json.get("message");
                        JsonElement statusAsJson = json.get("status");
                        Log.i(LOG_TAG, "messageAsJson received : " + messageAsJson.toString());

                        if (messageAsJson != null && !messageAsJson.toString().isEmpty()
                            && statusAsJson == null) {
                            Log.i(LOG_TAG, "Got message : " + responseMessage);

                            ((MyRecyclerViewAdapter) mAdapter).addItem(new DataObject(messageAsJson.toString(), R.drawable.gateagent), 0);

                            id++;
                        } else {
                            Log.i(LOG_TAG, "No message available yet");
                        }

                        count = count + 1;

                      }.sta
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }*/

    private void runThread() {
        new Thread() {
            public void run() {

                int count = 0;
                int max_count = 100;
                int id = 1;

                MessageListenerService messageListenerService = new MessageListenerService();

                try {
                    while (count <= max_count) {
                        Thread.sleep(5000);

                        String url = "http://10.0.2.2:8080/message/" + Integer.toString(id);

                        Log.i(LOG_TAG, "URL to hit : " + url);

                        String responseMessage = messageListenerService.run(url);

                        JsonObject json = new JsonParser().parse(responseMessage).getAsJsonObject();
                        JsonElement messageAsJson = json.get("message");
                        JsonElement statusAsJson = json.get("status");
                        Log.i(LOG_TAG, "messageAsJson received : " + messageAsJson.toString());

                        if (messageAsJson != null && !messageAsJson.toString().isEmpty()
                            && statusAsJson == null) {
                            Log.i(LOG_TAG, "Got message : " + responseMessage);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((MyRecyclerViewAdapter) mAdapter).addItem(new DataObject(messageAsJson.toString(), R.drawable.gateagent),
                                        0);
                                }
                            });

                            id++; //increment the counter to next message
                        } else {
                            Log.i(LOG_TAG, "No message available yet");
                        }

                        count = count + 1;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
