package com.idealist.www.useopencvwithcmake;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * no meaning
 */
public class friend_info extends AppCompatActivity {

    private String Friendid;

    ImageView friend_photo;
    TextView friend_introduce, friend_id;
    Button friend_apply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);

        Intent myintent = getIntent();
        Friendid = myintent.getStringExtra("FRIENDID");

        friend_photo = findViewById(R.id.friend_photo);
        friend_introduce = findViewById(R.id.friend_introduce);
        friend_id = findViewById(R.id.friend_id);
        friend_apply = findViewById(R.id.friend_apply);


    }
}
