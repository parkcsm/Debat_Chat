package com.idealist.www.useopencvwithcmake;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BroadCast_RoomList extends AppCompatActivity {

    static String Msg_After_Finishing_Streaming = "";

    ImageView make_broad_cast_room;

    EditText broad_cast_subject;
    EditText broad_cast_qualification;
    Button broad_cast_create_ok;
    Button broad_cast_create_cancel;
    Button btn_ok_dialog;
    TextView dialog_title;
    TextView dialog_subject;
    TextView dialog_plus_info;

    public static ArrayList<BroadCast_RoomList_Item> BroadCastListPool;
    public static BroadCast_RoomList_Adapter adapter;
    public static ListView broad_cast_room_listview;

    String broad_cast_room_save_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_broadcast_room_info/broad_cast_room_save.php";
    String broad_cast_room_load_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_broadcast_room_info/broad_cast_room_load.php";
    String broad_cast_room_dup_check_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_broadcast_room_info/check_if_can_make_new_room_or_not.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broad_cast__room_list);

        make_broad_cast_room = findViewById(R.id.make_broad_cast_room);
        broad_cast_room_listview = findViewById(R.id.broad_cast_room_listview);

        make_broad_cast_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(BroadCast_RoomList.this);
                LayoutInflater inflater = LayoutInflater.from(BroadCast_RoomList.this);
                final View mView = inflater.inflate(R.layout.openchat_newroom_dialog, null);

                dialog_title = mView.findViewById(R.id.dialog_title); // open_chat의 레이아웃을 빌려쓰기때문에 TextView를 setText해서 바꿔줘야한다.
                dialog_title.setText("나만의 토론방송국 생성");
                dialog_subject = mView.findViewById(R.id.dialog_subject); // open_chat의 레이아웃을 빌려쓰기때문에 TextView를 setText해서 바꿔줘야한다.
                dialog_subject.setText("방송 주제");
                dialog_plus_info = mView.findViewById(R.id.dialog_plus_info); // open_chat의 레이아웃을 빌려쓰기때문에 TextView를 setText해서 바꿔줘야한다.
                dialog_plus_info.setText("참고사항");

                broad_cast_subject = mView.findViewById(R.id.open_chat_subject); // open_chat의 레이아웃을 빌려쓰기때문에 R.id.open은 유지시켜두자.
                broad_cast_qualification = mView.findViewById(R.id.open_chat_qualification);
                broad_cast_create_ok = mView.findViewById(R.id.open_chat_room_create_ok);
                broad_cast_create_cancel = mView.findViewById(R.id.open_chat_room_create_cancel);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                broad_cast_create_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (broad_cast_subject.getText().toString().equals("") || broad_cast_qualification.getText().toString().equals("")) {
                            Toast.makeText(BroadCast_RoomList.this, "방송주제와 참고사항을 빈칸없이 입력해주세요!.", Toast.LENGTH_SHORT).show();
                        } else {
                            Check_Room_Dup_Or_Not(broad_cast_subject.getText().toString(), broad_cast_room_dup_check_url, new CallBack() {
                                @Override
                                public void getCodeValue(String code) {
                                    if (code.equals("broadcast_roomname_duplicate")) {
                                        Toast.makeText(BroadCast_RoomList.this, "중복된 이름의 방이 있으므로 새로운 방을 생성할수 없습니다. 방제목을 다시 설정해주세요.", Toast.LENGTH_SHORT).show();
                                    } else if (code.equals("broadcast_roomname_empty")) {
                                        Toast.makeText(BroadCast_RoomList.this, "중복된 이름의 방이 없으므로 방을 생성합니다.", Toast.LENGTH_SHORT).show();
                                        Save_Broad_Cast_Room_Info(broad_cast_subject.getText().toString(), broad_cast_qualification.getText().toString());
                                        dialog.dismiss();
                                        Intent myintent = new Intent(BroadCast_RoomList.this, Streaming.class);
                                        myintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        myintent.putExtra("mode", "only_publisher");
                                        myintent.putExtra("subject_of_broadcast", broad_cast_subject.getText().toString());
                                        startActivity(myintent);
                                    } else {
                                        Toast.makeText(BroadCast_RoomList.this, "else 예외 상황(if,else if 이외에 조건을 정해두지 않은 상황 발생, 보통 새로운 조건이 추가되었을때 이런일이 발생한다.) 코드를 확인해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                });

                broad_cast_create_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });
    }

    private void Check_Room_Dup_Or_Not(final String RoomName, final String broad_cast_room_dup_check_url, final CallBack onCallBack) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, broad_cast_room_dup_check_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            onCallBack.getCodeValue(code);
//                            Toast.makeText(BroadCast_RoomList.this, code, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(BroadCast_RoomList.this, "Error", Toast.LENGTH_SHORT).show();
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

        MySingleton.getmInstance(BroadCast_RoomList.this).addToRequestQue(stringRequest);
    }

    private void Save_Broad_Cast_Room_Info(final String RoomName, final String Detail) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, broad_cast_room_save_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            Toast.makeText(BroadCast_RoomList.this, code, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BroadCast_RoomList.this, "Error", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("RoomName", RoomName);
                params.put("Detail", Detail);
                params.put("Regdate", gettime());
                return params;
            }
        };
        MySingleton.getmInstance(BroadCast_RoomList.this).addToRequestQue(stringRequest);
    }

    private void Load_Broad_Cast_Room_Info(final OpenChatRoom.CallBack onCallBack) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, broad_cast_room_load_url,
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
                Toast.makeText(BroadCast_RoomList.this, "Error", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        MySingleton.getmInstance(BroadCast_RoomList.this).addToRequestQue(stringRequest);
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
    protected void onResume() {
        super.onResume();
//        Toast.makeText(this, "OnResume", Toast.LENGTH_SHORT).show();
        BroadCastListPool = new ArrayList<>();
        Load_Broad_Cast_Room_Info(new OpenChatRoom.CallBack() { // 인터페이스는 딱히 의미가 없다. 단지 코드를 눈에띄게 정리하고 정렬하는 용도로 사용한다. 앞으로 적극 사용하자.
            @Override
            public void getJsonArray(JSONArray jsonArray) throws JSONException {
                /**
                 * 발생 문제 : For{Msg.add} =(미연동)> Adapter(Msg) [ Msg.size()가 계속 0으로 나옴 ]
                 * For문안에다가 Adapter넣어서 문제 일시적으로 해결 (왜 For과 Array.add가 궁합이 안맞는지는 근본적인은 못참음)
                 */
                for (int i = 0; jsonArray.getJSONObject(i) != null; i++) {
//                    Toast.makeText(BroadCast_RoomList.this, i + "통과", Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String code = jsonObject.getString("code");
                    String RoomName = jsonObject.getString("RoomName");
                    String Detail = jsonObject.getString("Detail");

                    BroadCast_RoomList_Item broad_cast_roomList_item = new BroadCast_RoomList_Item(RoomName, Detail);
                    BroadCastListPool.add(broad_cast_roomList_item);

                    adapter = new BroadCast_RoomList_Adapter(BroadCast_RoomList.this, BroadCastListPool); //--1
                    broad_cast_room_listview.setAdapter(adapter);  //--2
                }
            }
        });


        if (Msg_After_Finishing_Streaming.equals("only_publisher")) {
            Toast.makeText(this, "내 방송을 종료했습니다.(only_publisher)", Toast.LENGTH_SHORT).show();
            Msg_After_Finishing_Streaming = "";
        } else if (Msg_After_Finishing_Streaming.equals("subscriber")) {
            Toast.makeText(this, "publisher가 방송을 종료했습니다.(subscriber)", Toast.LENGTH_SHORT).show();
            Msg_After_Finishing_Streaming = "";

            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(BroadCast_RoomList.this);
            LayoutInflater inflater = LayoutInflater.from(BroadCast_RoomList.this);
            final View mView = inflater.inflate(R.layout.ok_dialog, null);

            btn_ok_dialog = mView.findViewById(R.id.btn_ok_dialog); // open_chat의 레이아웃을 빌려쓰기때문에 TextView를 setText해서 바꿔줘야한다.

            mBuilder.setView(mView);
            mBuilder.setCancelable(false);

            final AlertDialog dialog = mBuilder.create();
            dialog.show();

            btn_ok_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        } else if (Msg_After_Finishing_Streaming.equals("subscriber_itself")) {
            Toast.makeText(this, "방송보기를 종료했습니다.(subscriber)", Toast.LENGTH_SHORT).show();
            Msg_After_Finishing_Streaming = "";
        } else if (Msg_After_Finishing_Streaming.equals("")) {
        } else {
            Toast.makeText(this, "BroadCast_RoomList Activity Else에러:" + Msg_After_Finishing_Streaming, Toast.LENGTH_SHORT).show();
        }

    }

    public interface CallBack {
        void getCodeValue(String string);
    }
}
