package com.idealist.www.useopencvwithcmake;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

public class OpenChatRoom extends AppCompatActivity {

    TextView Open_RoomName;
    Button open_Image_Get_Camera, open_Image_Get_Select;
    private EditText open_chatMsg;
    private Button open_chatBtn;
    public static ListView open_Msglist;
    public static String AreYouWatchingThisActivity = "";

    static Uri uri;
    static boolean OpenCV_Uri_isthere_or_not = false;
    private Bitmap bitmap;
    String subject;
    String open_chat_msg_save_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_openchat_msg_info/open_chat_msg_save.php";
    String open_chat_msg_load_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_openchat_msg_info/open_chat_msg_load.php";
    private String UploadUrl = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/ImageUploadApp/updateinfo.php";
    public static ArrayList<ChatRoomItem> msgPool;
    public static ChatRoomAdapter adapter;
    private PrintWriter pw;
    private Handler NetworkdHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_chat_room);
        NetworkdHandler = new Handler();
        try {
            pw = new PrintWriter(new BufferedOutputStream(MyService.s.getOutputStream()), true);
        } catch (IOException e) {

        }
        open_Image_Get_Camera = findViewById(R.id.open_image_get_camera);
        open_Image_Get_Select = findViewById(R.id.open_image_get_select);
        Open_RoomName = findViewById(R.id.Open_Room_Name);
        open_Msglist = (ListView) findViewById(R.id.open_chat_list);
        open_chatMsg = (EditText) findViewById(R.id.open_chat_msg);
        open_chatBtn = (Button) findViewById(R.id.open_chat_msgBtn);

        open_chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Msg = open_chatMsg.getText().toString();
                if (Msg.equals("")) {
                    Toast.makeText(OpenChatRoom.this, "메세지를 입력해주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    Save_Open_Chat_Msg_Info("text",Msg);
                    ChatRoomItem chatroomitem = new ChatRoomItem("text", Msg, loginsuccess.id, Msg, gettime());
                    msgPool.add(chatroomitem);
                    adapter.notifyDataSetChanged();
                    pw.println("text" + "/open_chat/" + subject + "/" + loginsuccess.id + "/" + Msg);
                    open_chatMsg.setText("");
                    open_chatMsg.requestFocus();
                }
            }
        });
        open_Image_Get_Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
        open_Image_Get_Select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent mintent = getIntent();
        subject = mintent.getStringExtra("subject");
        AreYouWatchingThisActivity = subject; // MyService에서 화면 보고있는 사람의 경우, 자동으로 메세지 장착되게!
        Open_RoomName.setText(subject);

        msgPool = new ArrayList<>();
        adapter = new ChatRoomAdapter(OpenChatRoom.this, msgPool);
        open_Msglist.setAdapter(adapter);

        Load_open_chat_msg_from_DB(new CallBack() { // 인터페이스는 딱히 의미가 없다. 단지 코드를 눈에띄게 정리하고 정렬하는 용도로 사용한다. 앞으로 적극 사용하자.
            @Override
            public void getJsonArray(JSONArray jsonArray) throws JSONException {
                /**
                 * 발생 문제 : For{Msg.add} =(미연동)> Adapter(Msg) [ Msg.size()가 계속 0으로 나옴 ]
                 * For문안에다가 Adapter넣어서 문제 일시적으로 해결 (왜 For과 Array.add가 궁합이 안맞는지는 근본적인 원인은 못참음)
                 */
                for (int i = 0; jsonArray.getJSONObject(i) != null; i++) {
//                                Toast.makeText(OpenChatRoom.this, i + "통과", Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String code = jsonObject.getString("code");
                    String RoomName = jsonObject.getString("RoomName");
                    String Sender = jsonObject.getString("Sender");
                    String Msg = jsonObject.getString("Msg");
                    String Regdate = jsonObject.getString("Regdate");
                    String Type = jsonObject.getString("Type");
//                           Toast.makeText(OpenChatRoom.this, code + "//" + RoomName + "//" + Sender + "//" + Msg + "//" + Regdate + "//" + Type, Toast.LENGTH_SHORT).show();
                    ChatRoomItem chatroomitem = new ChatRoomItem(Type, Msg, Sender, Msg, Regdate);
                    msgPool.add(chatroomitem);
                    adapter = new ChatRoomAdapter(OpenChatRoom.this, msgPool); //--1
                    open_Msglist.setAdapter(adapter);  //--2
//                  Toast.makeText(OpenChatRoom.this, msgPool.size()+"", Toast.LENGTH_SHORT).show();
                    open_Msglist.setSelection(adapter.getCount() - 1); // --3 이게 For밖에서 계속 작동을 안했었다.
                }
            }
        });
        if(OpenCV_Uri_isthere_or_not == true) {
            sendImage();
        }
        OpenCV_Uri_isthere_or_not = false;
    }

    private void Save_Open_Chat_Msg_Info(final String type, final String Msg) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, open_chat_msg_save_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            Toast.makeText(OpenChatRoom.this, code, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(OpenChatRoom.this, "Error", Toast.LENGTH_SHORT).show();
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
        MySingleton.getmInstance(OpenChatRoom.this).addToRequestQue(stringRequest);
    }


    private void Load_open_chat_msg_from_DB(final CallBack onCallBack) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, open_chat_msg_load_url,
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
                Toast.makeText(OpenChatRoom.this, "Error", Toast.LENGTH_SHORT).show();
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
        MySingleton.getmInstance(OpenChatRoom.this).addToRequestQue(stringRequest);
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

    public interface CallBack {
        void getJsonArray(JSONArray jsonArray) throws JSONException;
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
        Intent OpenCVIntent = new Intent(OpenChatRoom.this,opencv_setimage_activity.class);
        startActivity(OpenCVIntent);
    }

    private void uploadImage() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UploadUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Response = jsonObject.getString("response");
                            Toast.makeText(OpenChatRoom.this, Response, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("name", "image*" + subject + "*" + loginsuccess.id + "*" + gettime());
                params.put("image", imageToString(bitmap));
                return params;
            }
        };

        MySingleton.getmInstance(OpenChatRoom.this).addToRequestQue(stringRequest);
    }

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return android.util.Base64.encodeToString(imgBytes, android.util.Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1) {
            uri = data.getData();
            sendImage();
        }

        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            sendImage();
        }
    }



    private void sendImage() {
        //서버에 저장함
        Save_Open_Chat_Msg_Info("image","image*" + subject + "*" + loginsuccess.id + "*" + gettime());

        //서버로 파일 업로드함 -> "image]"+namev+gettime()
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        uploadImage();
        pw.println("image" + "/open_chat/" + subject + "/" + loginsuccess.id + "/" + "image*" + subject + "*" + loginsuccess.id + "*" + gettime());

        //adapter에 일단 뿌려줌
        final ChatRoomItem chatroomitem = new ChatRoomItem("image", "image*" + subject + "*" + loginsuccess.id + "*" + gettime(), loginsuccess.id, "image*" + subject + "*" + loginsuccess.id + "*" + gettime(), gettime());

        final int msgPool_int = msgPool.size();

        NetworkdHandler.post(new Runnable() { // setadapter를 통해 다른 사람이 나에게 전송한 메세지를 화면상에 표시해줌
            @Override
            public void run() {
                for (int i = 0; !AreYouWatchingThisActivity.equals("") && i < 6; i++) {
                    if(msgPool_int == msgPool.size()) {
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


}