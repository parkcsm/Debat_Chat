package com.idealist.www.useopencvwithcmake;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static java.lang.Thread.sleep;

public class Streaming extends AppCompatActivity implements Session.SessionListener, PublisherKit.PublisherListener {

    /**
     * Chat Part
     */
    private TextView name_of_broadcast;
    EditText broad_cast_chat_msg;
    Button broad_cast_chat_msgBtn;
    public static ListView broad_cast_chat_Msglist;
    public static String AreYouWatchingThisActivity = "";

    String subject;
    String broad_cast_room_info_delete_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_broadcast_room_info/broad_cast_room_info_delete.php";
    String broad_cast_chat_msg_load_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_broadcast_room_info/broad_cast_chat_load.php";
    String broad_cast_chat_msg_save_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_broadcast_room_info/broad_cast_chat_save.php";
    public static ArrayList<ChatRoomItem> msgPool;
    public static ChatRoomAdapter adapter;
    private PrintWriter pw;
    private Handler NetworkdHandler;

    /**
     * Streaming Part
     */
    Handler handler;
    private static String MODE = "";
    private static String API_KEY = "46216022";
    private static String SESSION_ID = "2_MX40NjIxNjAyMn5-MTU0MTgyNjE0NjAwN35IbGpjanJOekZUeDNPc2p2Y3RXTjFXamx-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00NjIxNjAyMiZzaWc9ODQ4OWI0MmUzNjg5NzY5ODI3ZWFlMTAxMDRmNDhlOWRlNjU4NGI3NDpzZXNzaW9uX2lkPTJfTVg0ME5qSXhOakF5TW41LU1UVTBNVGd5TmpFME5qQXdOMzVJYkdwamFuSk9la1pVZUROUGMycDJZM1JYVGpGWGFteC1mZyZjcmVhdGVfdGltZT0xNTQ3NDg0MzQ5JnJvbGU9bW9kZXJhdG9yJm5vbmNlPTE1NDc0ODQzNDkuODgxNjE3ODA2NzA1MTQ=";
    private static String LOG_TAG = Streaming.class.getSimpleName();
    private static final int RC_SETTINGS = 123;

    private Session session;

    private FrameLayout SubscriberContainer;
    private Button disconnect_broad_cast;

    private Publisher publisher;
    private Subscriber subscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);

        handler = new Handler();
        Intent mintent = getIntent();
        subject = mintent.getStringExtra("subject_of_broadcast");
        MODE = mintent.getStringExtra("mode");

        SubscriberContainer = findViewById(R.id.subscriber_container_in_broad_cast);
        disconnect_broad_cast = findViewById(R.id.disconnect_broad_cast);

        disconnect_broad_cast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(Streaming.this, "disconnect_broad_cast", Toast.LENGTH_SHORT).show();

                if (MODE.equals("only_publisher")) {
                    broad_cast_room_info_delete_plus_response(subject);
                    BroadCast_RoomList.Msg_After_Finishing_Streaming = MODE;
                    pw.println("finish_streaming" + "/broad_cast/" + subject + "/" + loginsuccess.id + "/" + "finish_streaming");
                    session.disconnect();
                    finish();
                } else if (MODE.equals("subscriber")) {
                    BroadCast_RoomList.Msg_After_Finishing_Streaming = "subscriber_itself";
                    if (registerReceiver(mMessageReceiver, new IntentFilter("finish_streaming_activity")) != null) {
                        unregisterReceiver(mMessageReceiver);
//                        Toast.makeText(Streaming.this, "unregisterd_receiver", Toast.LENGTH_SHORT).show();
                    }
                    session.disconnect();
                    finish();
                } else {
                    Toast.makeText(Streaming.this, "Streaming Activity - disconnect_broad_cast Button의 Mode값 else 예외 생황이니 토스트메세지 띄워랑", Toast.LENGTH_SHORT).show();
                }
            }
        });

        NetworkdHandler = new Handler();
        try {
            pw = new PrintWriter(new BufferedOutputStream(MyService.s.getOutputStream()), true);
        } catch (IOException e) {

        }
        name_of_broadcast = findViewById(R.id.name_of_broad_cast);
        broad_cast_chat_Msglist = (ListView) findViewById(R.id.broad_cast_chat_Msglist);
        broad_cast_chat_msg = (EditText) findViewById(R.id.broad_cast_chat_msg);
        broad_cast_chat_msgBtn = (Button) findViewById(R.id.broad_cast_chat_msgBtn);

        broad_cast_chat_msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Msg = broad_cast_chat_msg.getText().toString();
                if (Msg.equals("")) {
                    Toast.makeText(Streaming.this, "메세지를 입력해주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    Save_Broad_Cast_Msg_Info_Into_DB("text", Msg);
                    ChatRoomItem chatroomitem = new ChatRoomItem("text", Msg, loginsuccess.id, Msg, gettime());
                    msgPool.add(chatroomitem);
                    adapter.notifyDataSetChanged();
                    pw.println("text" + "/broad_cast/" + subject + "/" + loginsuccess.id + "/" + Msg);
                    broad_cast_chat_msg.setText("");
                    broad_cast_chat_msg.requestFocus();
                }
            }
        });

        if (MODE.equals("subscriber")) {
            Log.d("Msg","Receiver registered!!");
            registerReceiver(mMessageReceiver, new IntentFilter("finish_streaming_activity"));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

//        Toast.makeText(this, MODE, Toast.LENGTH_SHORT).show();

        requestpermissions(); // broad_cast_start

        AreYouWatchingThisActivity = subject; // MyService에서 화면 보고있는 사람의 경우, 자동으로 메세지 장착되게!
//        Toast.makeText(this, AreYouWatchingThisActivity, Toast.LENGTH_SHORT).show();
        name_of_broadcast.setText(subject);

        msgPool = new ArrayList<>();
        adapter = new ChatRoomAdapter(Streaming.this, msgPool);
        broad_cast_chat_Msglist.setAdapter(adapter);

        Load_Broad_Cast_Msg_From_DB(new OpenChatRoom.CallBack() { // 인터페이스는 딱히 의미가 없다. 단지 코드를 눈에띄게 정리하고 정렬하는 용도로 사용한다. 앞으로 적극 사용하자.
            @Override
            public void getJsonArray(JSONArray jsonArray) throws JSONException {
                /**
                 * 발생 문제 : For{Msg.add} =(미연동)> Adapter(Msg) [ Msg.size()가 계속 0으로 나옴 ]
                 * For문안에다가 Adapter넣어서 문제 일시적으로 해결 (왜 For과 Array.add가 궁합이 안맞는지는 근본적인 원인은 못참음)
                 */
                for (int i = 0; jsonArray.getJSONObject(i) != null; i++) {
//                           Toast.makeText(OpenChatRoom.this, i + "통과", Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String code = jsonObject.getString("code");
                    String RoomName = jsonObject.getString("RoomName");
                    String Sender = jsonObject.getString("Sender");
                    String Msg = jsonObject.getString("Msg");
                    String Regdate = jsonObject.getString("Regdate");
                    String Type = jsonObject.getString("Type");
//                           Toast.makeText(OpenChatRoom.this, code + "//" + RoomName + "//" + Sender + "//" + Msg + "//" + Regdate + "//" + Type, Toast.LENGTH_SHORT).show();
                    ChatRoomItem chatRoomItem = new ChatRoomItem(Type, Msg, Sender, Msg, Regdate);
                    msgPool.add(chatRoomItem);
                    adapter = new ChatRoomAdapter(Streaming.this, msgPool); //--1
                    broad_cast_chat_Msglist.setAdapter(adapter);  //--2
//                  Toast.makeText(OpenChatRoom.this, msgPool.size()+"", Toast.LENGTH_SHORT).show();
                    broad_cast_chat_Msglist.setSelection(adapter.getCount() - 1); // --3 이게 For밖에서 계속 작동을 안했었다.
                }
            }
        });
    }

    private void Save_Broad_Cast_Msg_Info_Into_DB(final String type, final String Msg) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, broad_cast_chat_msg_save_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            Toast.makeText(Streaming.this, code, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Streaming.this, "Error", Toast.LENGTH_SHORT).show();
                error.printStackTrace();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("RoomName", subject);
                params.put("Sender", loginsuccess.id);
                params.put("Msg", Msg);
                params.put("Regdate", gettime());
                params.put("Type", type);
                return params;
            }
        };
        MySingleton.getmInstance(Streaming.this).addToRequestQue(stringRequest);
    }


    private void Load_Broad_Cast_Msg_From_DB(final OpenChatRoom.CallBack onCallBack) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, broad_cast_chat_msg_load_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            onCallBack.getJsonArray(jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Streaming.this, "Error", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("RoomName", subject);
                return params;
            }
        };
        MySingleton.getmInstance(Streaming.this).addToRequestQue(stringRequest);
    }


    public String gettime() {
        long mNow;
        Date mDate;
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd aa hh:mm:ss");
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AreYouWatchingThisActivity = "";
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("Msg","broad_cast_receiver_operation_basic");

                BroadCast_RoomList.Msg_After_Finishing_Streaming = MODE;

                if (session != null) {
                    Log.d("Msg","broad_cast_receiver_operation_basic_in_if_sentence");
                    session.disconnect();
                    finish();
//                    Toast.makeText(context, "session_not_null", Toast.LENGTH_SHORT).show();
                }
                session.disconnect();
                finish();
        }
    };


    private void broad_cast_room_info_delete_plus_response (final String RoomName) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,  broad_cast_room_info_delete_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");
//                            Toast.makeText(Streaming.this, message, Toast.LENGTH_SHORT).show();

//                            handler.post(new Runnable(){
//                                @Override
//                                public void run() {
//                                    try {
//                                        sleep(3000);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                }
//                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("RoomName", RoomName);
                return params;
            }
        };

        MySingleton.getmInstance(Streaming.this).addToRequestQue(stringRequest);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////

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
        if (MODE.equals("only_publisher")) {
            publisher = new Publisher.Builder(this).build();
            publisher.setPublisherListener(this);
            SubscriberContainer.addView(publisher.getView());
            session.publish(publisher);
        }
    }

    @Override
    public void onDisconnected(Session session) {
        finish();
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        if (MODE.equals("subscriber")) {
            if (subscriber == null) {
                subscriber = new Subscriber.Builder(this, stream).build();
                session.subscribe(subscriber);
                SubscriberContainer.addView(subscriber.getView());
            }
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        if (MODE.equals("subscriber")) {
            if (subscriber != null) {
                subscriber = null;
                SubscriberContainer.removeAllViews();
            }
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

    }

    public interface CallBack {
        void getJsonArray(JSONArray jsonArray) throws JSONException;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (MODE.equals("only_publisher")) {
            pw.println("finish_streaming" + "/broad_cast/" + subject + "/" + loginsuccess.id + "/" + "finish_streaming");
            if (session != null) {
                session.disconnect();
            }
            finish();
        } else if (MODE.equals("subscriber")) {
            if (registerReceiver(mMessageReceiver, new IntentFilter("finish_streaming_activity")) != null) {
                unregisterReceiver(mMessageReceiver);
                Toast.makeText(Streaming.this, "unregisterd_receiver", Toast.LENGTH_SHORT).show();
            }
            if (session != null) {
                session.disconnect();
            }
            finish();
        } else {
            Toast.makeText(Streaming.this, "Streaming Activity - disconnect_broad_cast Button의 Mode값 else 예외 생황이니 토스트메세지 띄워랑", Toast.LENGTH_SHORT).show();
        }
    }



}
