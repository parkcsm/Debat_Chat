package com.idealist.www.useopencvwithcmake;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static java.lang.Thread.sleep;

public class ChatRoom extends AppCompatActivity {

    public static boolean OpenCV_Uri_isthere_or_not = false;

    TextView RoomName;
    Button Image_Get_Camera, Image_Get_Select;
    private static EditText chatMsg;
    private Button chatBtn;

    static String friend_call;
    static String id;
    static String receiver;
    public static String room_clist_string;
    String subject;
    static String mixStr;
    public static Uri uri;
    private Bitmap bitmap;
    private String UploadUrl = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/ImageUploadApp/updateinfo.php";
    boolean dupcheck_Between_NewIntent_and_OnResuem = false;
    String NewIntentString;
    private boolean OnBackPressed;


    public static ListView Msglist;
    public static ArrayList<ChatRoomItem> msgPool;
    public static ChatRoomAdapter adapter;

    static DatabaseHelper myDb;
    static DatabaseHelper2 myDb2;
    public static PrintWriter pw;
    private Handler NetworkdHandler;

    public ChatRoom() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        myDb = new DatabaseHelper(this);
        myDb2 = new DatabaseHelper2(this);
        NetworkdHandler = new Handler();
        try {
            pw = new PrintWriter(new BufferedOutputStream(MyService.s.getOutputStream()), true);
        } catch (IOException e) {

        }

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Image_Get_Camera = findViewById(R.id.image_get_camera);
        Image_Get_Select = findViewById(R.id.image_get_select);
        RoomName = findViewById(R.id.Room_Name);
        Msglist = (ListView) findViewById(R.id.chat_list);
        chatMsg = (EditText) findViewById(R.id.chat_msg);
        chatBtn = (Button) findViewById(R.id.chat_msgBtn);

        SharedPreferences sharedPref = getSharedPreferences("Operation", MODE_PRIVATE);
        id = sharedPref.getString("Operation", null);


        msgPool = new ArrayList<>();

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = chatMsg.getText().toString().trim();

                if (msg.length() == 0) {
                    Toast.makeText(ChatRoom.this, "채팅메세지를 입력해주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    msg_send_and_save_process(msg);
                }
            }
        });

        Image_Get_Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
        Image_Get_Select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    public static void msg_send_and_save_process(String msg) {
        pw.println("text" + "/chat/" + receiver + "/" + id + "/" + msg);

        String gettime = gettime();
        String selfmsgv = id + ": " + msg + "\n" + gettime + "\n"; //내 클라위에 표시하는 부분이므로, 보낸 부분을 바로 화면상에 표시해줌

        ChatRoomItem chatroomitem = new ChatRoomItem("text", selfmsgv, id, msg, gettime);
        msgPool.add(chatroomitem);
        adapter.notifyDataSetChanged();

        boolean isInserted = myDb.insertData(room_clist_string, id, msg, gettime, "text");
        if (isInserted == true) {
//                        Toast.makeText(ChatRoom.this, "Data Inserted", Toast.LENGTH_SHORT).show();
        } else {
//                        Toast.makeText(ChatRoom.this, "Data not Inserted", Toast.LENGTH_SHORT).show();
        }

        mixStr = id + "]" + receiver;
//        Toast.makeText(ChatRoom.this, myDb2.getRoomDupCheck(id, SeperationAndReOrder(mixStr)).getCount() + "/ loginsuccess.class", Toast.LENGTH_SHORT).show();
        if (myDb2.getRoomDupCheck(id, SeperationAndReOrder(mixStr)).getCount() == 0) {
            boolean isInserted2 = myDb2.insertData(id, SeperationAndReOrder(mixStr));
            if (isInserted2 == true) {
//                            Toast.makeText(ChatRoom.this, "Data Inserted", Toast.LENGTH_SHORT).show();
            } else {
//                            Toast.makeText(ChatRoom.this, "Data not Inserted", Toast.LENGTH_SHORT).show();
            }
        } else {
//                            Toast.makeText(this, "추가안됨", Toast.LENGTH_SHORT).show();
        }

        chatMsg.setText("");
        chatMsg.requestFocus();
    }


    private String SeperationAndExtract(String string) {

        StringTokenizer str = new StringTokenizer(string, "]");
        int UserNum = str.countTokens();
        String[] test = new String[UserNum];

        for (int i = 0; i < UserNum; i++) {
            test[i] = str.nextToken();
        }

        String sum;
        sum = SplitAndMigrateToStringExtract(test, UserNum);
        return sum;
    }

    private String SplitAndMigrateToStringExtract(String[] test, int ArraySize) {
        Arrays.sort(test);

        String sum = "";
        for (int i = 0; i < ArraySize; i++) {
            if (test[i].equals(id)) {

            } else {
                sum += test[i] + "]";
            }
        }
        return sum;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        OnBackPressed = true;
        MyService.ChatRoomCheck = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra("subject2")) {
//            Toast.makeText(this, intent.getStringExtra("subject2") + "NewIntent", Toast.LENGTH_SHORT).show();
            NewIntentString = intent.getStringExtra("subject2");
            dupcheck_Between_NewIntent_and_OnResuem = true;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent_check = getIntent();
        if (intent_check.hasExtra("subject2") && dupcheck_Between_NewIntent_and_OnResuem == false) {
//            Toast.makeText(this, intent_check.getStringExtra("subject2") + "onResume", Toast.LENGTH_SHORT).show();
            NewIntentString = intent_check.getStringExtra("subject2");
        }
        dupcheck_Between_NewIntent_and_OnResuem = false;

        if (NewIntentString == "" || NewIntentString == null) { // Roomlist에서 intent값을 받아오는 경우 (고생많이함)
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            subject = bundle.getString("subject");
            room_clist_string = bundle.getString("subject"); // 채팅룸 리스트로부터 받은 RoomClists
            receiver = SeperationAndExtract(room_clist_string);
        } else { //MyService Notification 에서 intent값을 받아오는 경우 (고생많이함)
            subject = NewIntentString;
            room_clist_string = NewIntentString; // 채팅룸 리스트로부터 받은 RoomClists
            receiver = SeperationAndExtract(room_clist_string);
        }

        RoomName.setText(room_clist_string);
//        Toast.makeText(this, room_clist_string, Toast.LENGTH_SHORT).show();
        MyService.ChatRoomCheck = true;
//        MyService.ChatRoomName = room_clist_string;
        msgPool = new ArrayList<>();
        viewAll();
        adapter = new ChatRoomAdapter(ChatRoom.this, msgPool);
        Msglist.setAdapter(adapter);
        Msglist.setSelection(adapter.getCount() - 1);

        if (OpenCV_Uri_isthere_or_not == true) {
            sendImage();
        }
        OpenCV_Uri_isthere_or_not = false;


        if (intent_check.getStringExtra("friend_call") != null) {
            if (intent_check.getStringExtra("friend_call").equals("friend_call")) {
                msg_send_and_save_process(ChatRoom.id + "]님이 " + ChatRoom.receiver + "님에게 영상통화를 걸었습니다.");
                pw.println("start_call" + "/chat/" + receiver + "/" + id + "/" + "start_call");
                intent_check.removeExtra("friend_call");
                Intent mintent = new Intent(ChatRoom.this,Video_Call.class);
                mintent.putExtra("mode","publisher_plus_subscriber");
                startActivity(mintent);
            }
        }

        if(intent_check.getStringExtra("from_my_service") != null){
            if (intent_check.getStringExtra("from_my_service").equals("from_my_service")) {
                intent_check.removeExtra("from_my_service");
                Intent mIntent2 = new Intent(ChatRoom.this, Video_Call.class);
                mIntent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mIntent2.putExtra("mode", "publisher_plus_subscriber");
                mIntent2.putExtra("ask_if_can_accept_or_not", "ask_if_can_accept_or_not");
                startActivity(mIntent2);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        MyService.ChatRoomCheck = false;
//        MyService.ChatRoomName = "";
    }

    public static String gettime() {
        long mNow;
        Date mDate;
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd aa hh:mm:ss");
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    public void viewAll() {
        Cursor res = myDb.getAllData(subject);
        if (res.getCount() == 0) {
//     showMessage("Error", "Nothing found");
        } else {
            while (res.moveToNext()) {
                StringBuffer buffer = new StringBuffer();
                buffer.append(res.getString(2) + " : "); // sender
                buffer.append(res.getString(3) + "\n"); // msg
                buffer.append(res.getString(4) + "\n"); // regdate

                if (res.getString(5).equals("text")) {
                    ChatRoomItem chatroomitem = new ChatRoomItem(res.getString(5), buffer.toString(), res.getString(2), res.getString(3), res.getString(4));
                    msgPool.add(chatroomitem);
                } else if (res.getString(5).equals("image")) {
                    ChatRoomItem chatroomitem = new ChatRoomItem(res.getString(5), res.getString(3), res.getString(2), res.getString(3), res.getString(4));
                    msgPool.add(chatroomitem);
                }

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            uri = data.getData();
            sendImage();
        }
//        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
//            uri = data.getData();
//
//             sendImage();
//        }
    }

    private void selectImage() {
        Intent myintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(myintent, 1);
    }

    private void captureImage() {
//        Intent CameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (CameraIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(CameraIntent, 1000);
//        }
        Intent OpenCVIntent = new Intent(ChatRoom.this, opencv_setimage_activity.class);
        startActivity(OpenCVIntent);
    }

    private void sendImage() {
        //SQLite에 서버에 저장한 파일 이름을 저장함
        boolean isInserted = myDb.insertData(room_clist_string, id, "image*" + room_clist_string + "*" + id + "*" + gettime(), gettime(), "image");
        if (isInserted == true) {
//                Toast.makeText(ChatRoom.this, "Data Inserted", Toast.LENGTH_SHORT).show();
        } else {
//                Toast.makeText(ChatRoom.this, "Data not Inserted", Toast.LENGTH_SHORT).show();
        }
        //서버로 파일 업로드함 -> "image]"+namev+gettime()
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        uploadImage();
        pw.println("image" + "/chat/" + receiver + "/" + id + "/" + "image*" + room_clist_string + "*" + id + "*" + gettime());
        //adapter에 일단 뿌려줌
        final ChatRoomItem chatroomitem = new ChatRoomItem("image", "image*" + room_clist_string + "*" + id + "*" + gettime(), id, "image*" + room_clist_string + "*" + id + "*" + gettime(), gettime());

        final int msgPool_int = msgPool.size();

        NetworkdHandler.post(new Runnable() { // setadapter를 통해 다른 사람이 나에게 전송한 메세지를 화면상에 표시해줌
            @Override
            public void run() {
                for (int i = 0; MyService.ChatRoomCheck == true && i < 6; i++) {
                    if (msgPool_int == msgPool.size()) {
                        msgPool.add(chatroomitem);
                    }
                    adapter.notifyDataSetChanged();
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void uploadImage() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UploadUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Response = jsonObject.getString("response");
                            Toast.makeText(ChatRoom.this, Response, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", "image*" + room_clist_string + "*" + id + "*" + gettime());
                params.put("image", imageToString(bitmap));
                return params;
            }
        };

        MySingleton.getmInstance(ChatRoom.this).addToRequestQue(stringRequest);
    }

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return android.util.Base64.encodeToString(imgBytes, android.util.Base64.DEFAULT);
    }

    private static String SeperationAndReOrder(String string) {

        StringTokenizer str = new StringTokenizer(string, "]");
        int UserNum = str.countTokens();
        String[] test = new String[UserNum];
        for (int i = 0; i < UserNum; i++) {
            test[i] = str.nextToken();
        }

        String sum;
        sum = SplitAndMigrateToString(test, UserNum);
        return sum;
    }

    private static String SplitAndMigrateToString(String[] test, int ArraySize) {
        Arrays.sort(test);

        String sum = "";
        for (int i = 0; i < ArraySize; i++) {
            sum += test[i] + "]";
        }
        return sum;
    }


}
