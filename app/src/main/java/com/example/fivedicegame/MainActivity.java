package com.example.fivedicegame;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final String PREFERENCE_FILE_KEY = "myAppPreference";

    EditText et1, et2;
    String name1 = "", name2 = "";
    TextView winner;
    StringBuilder s;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et1 = findViewById(R.id.name1);
        et2 = findViewById(R.id.name2);
        winner = findViewById(R.id.winner);
        spinner = findViewById(R.id.scoreList);
        s = new StringBuilder();
        spin(FileHelper.ReadFile(this));

    }

    public void start(View view) {
        name1 = et1.getText().toString();
        name2 = et2.getText().toString();
        Intent intent = new Intent(this, Game.class);
        intent.putExtra("name1", name1);
        intent.putExtra("name2", name2);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                winner.setVisibility(View.VISIBLE);
                String one = data.getStringExtra("winner");
                int two = data.getIntExtra("p1", 0);
                int three = data.getIntExtra("p2", 0);
                String res = one+" " + two + "x"+ three;
                winner.setText("Zwyciężył  "+ one+ "\n"+ two + "x"+ three);

                String result = name1 + " vs " + name2 + " wynik: " + two + "x"+ three;
                s.append("\n"+result);

                if (FileHelper.saveToFile(res)){
                    Toast.makeText(MainActivity.this,"Saved to file",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"Error save file!!!",Toast.LENGTH_SHORT).show();
                }

                spin(FileHelper.ReadFile(this));

            }
        }
    }

    private void spin(ArrayList list) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        spinner.setAdapter(arrayAdapter);
    }
}
