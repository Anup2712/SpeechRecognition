package com.anupam.speechrecogdemo.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.anupam.speechrecogdemo.R;
import com.anupam.speechrecogdemo.model.SpeechTextmodel;
import com.anupam.speechrecogdemo.view.adapter.DictionaryAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FirstActivity extends AppCompatActivity {
    Button speak;
    ProgressDialog progress;
    ArrayList<String> dictionaryText;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    public DictionaryAdapter adapter;
    public static final String DATABASE_NAME = "speech.db";
    public static final String First_time_SHARED = "first_time";
    SQLiteDatabase db;
    List<SpeechTextmodel> data = new ArrayList<>();
    public static final String SpeechText = "speech_table";
    private static final String CREATE_SPEECH_TABLE = "CREATE TABLE IF NOT EXISTS " + SpeechText + "( " +
            "id INTEGER PRIMARY KEY, " +
            "speech TEXT, " +
            "frequent INTEGER)";
    String choosenValue;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        speak = findViewById(R.id.speak);
        recyclerView = findViewById(R.id.recyclerView);
        dictionaryText = new ArrayList<>();
        Intent intent = getIntent();
        choosenValue = intent.getStringExtra("chooseValue");
        db = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        db.execSQL(CREATE_SPEECH_TABLE);
        db.close();
        SharedPreferences sharedPreferences = getSharedPreferences(First_time_SHARED, 0);
        if (!sharedPreferences.getString("firstTime", "0").equals("1")) {
            dictionaryWords();
        } else {
            localData();
        }
    }

    public boolean dictionaryWords() {
        try {
            progress = new ProgressDialog(FirstActivity.this, R.style.MyTheme);
            progress.setCancelable(false);
            progress.setProgressStyle(android.R.style.Widget_Holo_ProgressBar);
            progress.show();
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String URL = "http://a.galactio.com/interview/dictionary-v2.json";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONArray jsonArray = new JSONArray(object.getString("dictionary"));

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject innerobject = jsonArray.getJSONObject(i);
                            dictionaryText.add(innerobject.getString("word"));

                            SQLiteDatabase dbS = FirstActivity.this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
                            dbS.execSQL("INSERT INTO speech_table ( speech, frequent ) " +
                                            "VALUES ( ?, ? )",
                                    new Object[]{
                                            innerobject.getString("word"),
                                            "0",
                                    }
                            );
                            dbS.close();
                        }
                        progress.hide();
                        SharedPreferences sharedPreferences = getSharedPreferences(First_time_SHARED, 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.putString("firstTime", "1");
                        editor.apply();
                        localData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progress.hide();
                        Toast.makeText(FirstActivity.this, "Server error try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progress.hide();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void localData() {
        Cursor c;
        db = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        c = db.rawQuery("SELECT * from speech_table order by frequent desc ", null);
        int count = c.getCount();
        if (count >= 1) {
            if (c.moveToFirst()) {
                do {
                    SpeechTextmodel speechTextmodel = new SpeechTextmodel();
                    speechTextmodel.setSpeechText(c.getString(c.getColumnIndex("speech")));
                    speechTextmodel.setFrequent(c.getString(c.getColumnIndex("frequent")));
                    data.add(speechTextmodel);
                }
                while (c.moveToNext());
            }
        }
        c.close();
        db.close();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new DictionaryAdapter(FirstActivity.this, data,choosenValue);
        recyclerView.setAdapter(adapter);
    }

    public void speakBtn(View view) {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
                Intent intent = new Intent();
                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
        }, 2000);
    }

}
