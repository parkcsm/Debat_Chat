package com.idealist.www.useopencvwithcmake;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class Video_Call extends AppCompatActivity implements Session.SessionListener, PublisherKit.PublisherListener {

    private static String MODE = "";
    private static String ASK_IF_CAN_ACCEPT_OR_NOT = "";
    private static String API_KEY = "46216022";
    private static String SESSION_ID = "2_MX40NjIxNjAyMn5-MTU0MTgyNjE0NjAwN35IbGpjanJOekZUeDNPc2p2Y3RXTjFXamx-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00NjIxNjAyMiZzaWc9ODQ4OWI0MmUzNjg5NzY5ODI3ZWFlMTAxMDRmNDhlOWRlNjU4NGI3NDpzZXNzaW9uX2lkPTJfTVg0ME5qSXhOakF5TW41LU1UVTBNVGd5TmpFME5qQXdOMzVJYkdwamFuSk9la1pVZUROUGMycDJZM1JYVGpGWGFteC1mZyZjcmVhdGVfdGltZT0xNTQ3NDg0MzQ5JnJvbGU9bW9kZXJhdG9yJm5vbmNlPTE1NDc0ODQzNDkuODgxNjE3ODA2NzA1MTQ=";
    private static String LOG_TAG = Video_Call.class.getSimpleName();
    private static final int RC_SETTINGS = 123;

    private Session session;

    private FrameLayout PublisherContainer;
    private FrameLayout SubscriberContainer;
    private Button disconnect_call;

    private Publisher publisher;
    private Subscriber subscriber;

    //dialog
    TextView friend_name_trying_video_call;
    Button accept_friend_video_call, reject_friend_video_call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video__call);

        PublisherContainer = findViewById(R.id.publisher_container_in_video_call);
        SubscriberContainer = findViewById(R.id.subscriber_container_in_video_call);
        disconnect_call = findViewById(R.id.disconnect_call);

        Intent mintent = getIntent();
        MODE = mintent.getStringExtra("mode");
        ASK_IF_CAN_ACCEPT_OR_NOT = mintent.getStringExtra("ask_if_can_accept_or_not");

        if (ASK_IF_CAN_ACCEPT_OR_NOT != null) {
            if (ASK_IF_CAN_ACCEPT_OR_NOT.equals("ask_if_can_accept_or_not")) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
                final View mView = this.getLayoutInflater().inflate(R.layout.dialogue_call_accept_or_cancel, null);
                friend_name_trying_video_call = mView.findViewById(R.id.friend_name_trying_video_call);
                friend_name_trying_video_call.setText(ChatRoom.receiver);

                accept_friend_video_call = mView.findViewById(R.id.accept_friend_video_call);
                reject_friend_video_call = mView.findViewById(R.id.reject_friend_video_call);

                mBuilder.setView(mView);
                mBuilder.setCancelable(false);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                accept_friend_video_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChatRoom.msg_send_and_save_process(ChatRoom.id + "]님이 " + ChatRoom.receiver + "님과의 영상통화를 수락했습니다.");
                        ChatRoom.pw.println("accept_call" + "/chat/" + ChatRoom.receiver + "/" + ChatRoom.id + "/" + "accept_call");
                        requestpermissions();
                        dialog.dismiss();
                    }
                });
                reject_friend_video_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ChatRoom.msg_send_and_save_process(ChatRoom.id + "]님이 " + ChatRoom.receiver + "님과의 영상통화를 거절했습니다.");
                        ChatRoom.pw.println("finish_call" + "/chat/" + ChatRoom.receiver + "/" + ChatRoom.id + "/" + "finish_call");
                        Thread thread = new Thread() {
                            public void run() {
                                try {
                                    sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                        finish();
                    }
                });
            }
        } else {
            requestpermissions();
        }

        disconnect_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (registerReceiver(mMessageReceiver, new IntentFilter("finish_video_call_activity")) != null) {
                        unregisterReceiver(mMessageReceiver);
                        Toast.makeText(Video_Call.this, "unregisterd_receiver", Toast.LENGTH_SHORT).show();
                    }
                    ChatRoom.msg_send_and_save_process(ChatRoom.id + "]님이 " + ChatRoom.receiver + "님과의 영상통화를 종료했습니다.");
                    ChatRoom.pw.println("finish_call" + "/chat/" + ChatRoom.receiver + "/" + ChatRoom.id + "/" + "finish_call");
                    session.disconnect();
                    finish();

            }
        });

        registerReceiver(mMessageReceiver, new IntentFilter("finish_video_call_activity"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (session != null) {
                session.disconnect();
                finish();
            } else {
                finish();
            }
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_SETTINGS)
    private void requestpermissions() {
        String[] perm = {Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perm)) {
            session = new Session.Builder(this, API_KEY, SESSION_ID).build();
            session.setSessionListener(this);
            session.connect(TOKEN);
        } else {
            EasyPermissions.requestPermissions(this, "This app needs to access your camera and mic", RC_SETTINGS, perm);
        }
    }


    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }

    @Override
    public void onConnected(Session session) {
        if (MODE.equals("publisher_plus_subscriber")) {
            publisher = new Publisher.Builder(this).build();
            publisher.setPublisherListener(this);
            PublisherContainer.addView(publisher.getView());
            session.publish(publisher);
        }
    }

    @Override
    public void onDisconnected(Session session) {
        Toast.makeText(this, "disconnect!!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        if (MODE.equals("publisher_plus_subscriber")) {
            if (subscriber == null) {
                subscriber = new Subscriber.Builder(this, stream).build();
                session.subscribe(subscriber);
                SubscriberContainer.addView(subscriber.getView());
            }
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        if (MODE.equals("publisher_plus_subscriber")) {
            if (subscriber != null) {
                subscriber = null;
                SubscriberContainer.removeAllViews();
            }
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (registerReceiver(mMessageReceiver, new IntentFilter("finish_video_call_activity")) != null) {
            unregisterReceiver(mMessageReceiver);
            Toast.makeText(Video_Call.this, "unregisterd_receiver", Toast.LENGTH_SHORT).show();
        }
        ChatRoom.msg_send_and_save_process(ChatRoom.id + "]님이 " + ChatRoom.receiver + "님과의 영상통화를 종료했습니다.");
        ChatRoom.pw.println("finish_call" + "/chat/" + ChatRoom.receiver + "/" + ChatRoom.id + "/" + "finish_call");
        if (session != null) {
            session.disconnect();
        }
        finish();
    }
}

