package com.anupam.speechrecogdemo.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.anupam.speechrecogdemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextView speechText;
    ImageView speechIcon;
    ArrayList<String> localdata = new ArrayList<>();
    public static final String DATABASE_NAME = "speech.db";
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speechText = findViewById(R.id.speechText);
        speechIcon = findViewById(R.id.speechicon);
        Cursor c;
        db = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        c = db.rawQuery("SELECT * from speech_table", null);
        int count = c.getCount();
        if (count >= 1) {
            if (c.moveToFirst()) {
                do {
                    localdata.add(c.getString(c.getColumnIndex("speech")));
                }
                while (c.moveToNext());
            }
        }
        c.close();
        db.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    speechText.setText(result.get(0));
                    for (int i = 0; i < localdata.size(); i++) {
                        if (localdata.get(i).toUpperCase().equals(result.get(0).toUpperCase())) {
                            Intent intent = new Intent(getApplicationContext(), FirstActivity.class);
                            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("chooseValue", localdata.get(i));
                            SQLiteDatabase dbS = MainActivity.this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
                            Cursor c = dbS.rawQuery("select frequent from speech_table where " + "speech= '" + localdata.get(i) + "' ", null);
                            if (c.getCount() == 1) {
                                c.moveToFirst();
                                int frquentIncr = Integer.parseInt(c.getString(c.getColumnIndex("frequent"))) + 1;
                                dbS.execSQL("update speech_table set frequent='" + frquentIncr + "' where " + "speech= '" + localdata.get(i) + "' ");
                            }
                            c.close();
                            dbS.close();

                            startActivity(intent);
                            break;
                        } else {
                            Toast.makeText(this, "Not Matched with Dictionary.Try Again!!!0", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
    }

    public void speech(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "This device doesn't support", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), FirstActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
