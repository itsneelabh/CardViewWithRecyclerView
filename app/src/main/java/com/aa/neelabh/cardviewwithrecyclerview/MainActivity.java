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

        try {
            updateBroadcastMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(MainActivity::updateBroadcastMessage, 0, 1, TimeUnit.SECONDS);*/
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

    public void updateBroadcastMessage() throws IOException {
        int count = 0;
        int max_count = 3;
        int id = 1;

        MessageListenerService messageListenerService = new MessageListenerService();

        try {
            while (count <= max_count) {
                String url = "http://10.0.2.2:8080/message/" + Integer.toString(id);
                Log.i(LOG_TAG, "URL to hit : " + url);

                String responseMessage = messageListenerService.run(url);

                JsonObject json = new JsonParser().parse(responseMessage).getAsJsonObject();
                JsonElement messageAsJson = json.get("message");
                JsonElement statusAsJson = json.get("status");
                Log.i(LOG_TAG, "messageAsJson received : " + messageAsJson.toString());

                if (messageAsJson != null && !messageAsJson.toString().isEmpty()
                    && statusAsJson == null ) {
                    Log.i(LOG_TAG, "Got message : " + responseMessage);

                    ((MyRecyclerViewAdapter) mAdapter).addItem(new DataObject(messageAsJson.toString(), R.drawable.gateagent), 0);
                    id++;
                } else {
                    Log.i(LOG_TAG, "No message available yet");
                }

                count = count + 1;

                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
