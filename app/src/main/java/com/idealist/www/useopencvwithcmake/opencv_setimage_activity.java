package com.idealist.www.useopencvwithcmake;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class opencv_setimage_activity extends AppCompatActivity {

    public static Uri uri_opencv_image;
    Button btn_go_to_opencv;
    Button btn_send;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opencv_setimage_activity);

        btn_send = findViewById(R.id.btn_send);
        btn_go_to_opencv = findViewById(R.id.btn_go_to_opencv);
        iv = findViewById(R.id.iv);

        btn_go_to_opencv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(opencv_setimage_activity.this, opencv_face_detection_activity.class);
                startActivity(mIntent);
            }
        });
        btn_send.setVisibility(View.GONE);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ChatRoom.OpenCV_Uri_isthere_or_not == false) {
                    ChatRoom.uri = uri_opencv_image;
                    ChatRoom.OpenCV_Uri_isthere_or_not = true;
                }
                if (OpenChatRoom.OpenCV_Uri_isthere_or_not == false) {
                    OpenChatRoom.uri = uri_opencv_image;
                    OpenChatRoom.OpenCV_Uri_isthere_or_not = true;
                }
                finish();
                uri_opencv_image = null;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (uri_opencv_image != null) {
            Glide.with(this).load(uri_opencv_image).centerCrop().into(iv);
            Toast.makeText(this, "present_opencv_image_uri :" + uri_opencv_image, Toast.LENGTH_SHORT).show();
            btn_send.setVisibility(View.VISIBLE);
        }
    }
}
