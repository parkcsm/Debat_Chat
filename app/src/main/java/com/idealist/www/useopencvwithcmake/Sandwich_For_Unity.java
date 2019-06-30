package com.idealist.www.useopencvwithcmake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Sandwich_For_Unity extends AppCompatActivity {

    boolean bool = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sandwich__for__unity);
        Intent mintent = new Intent(Sandwich_For_Unity.this,unityplayer_holder.class);
        startActivity(mintent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(bool){
            finish();
        }
        bool = true; //When you put back_button in unity, This Activity will show up. but automatically will be finished.
    }

}
