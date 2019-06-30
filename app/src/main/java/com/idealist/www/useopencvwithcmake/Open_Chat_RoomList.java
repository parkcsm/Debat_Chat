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

public class Open_Chat_RoomList extends AppCompatActivity {

    ImageView make_openchatroom;

    EditText open_chat_subject;
    EditText open_chat_qualification;
    Button open_chat_room_create_ok;
    Button open_chat_room_create_cancel;

    public static ArrayList<Open_Chat_RoomList_Item> RoomListPool;
    public static Open_Chat_RoomList_Adapter adapter;
    public static ListView open_chat_room_list;

    String open_chat_room_save_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_openchat_room_info/open_chat_room_save.php";
    String open_chat_room_load_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_openchat_room_info/open_chat_room_load.php";
    String open_chat_room_dup_check_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_openchat_room_info/check_if_can_make_new_room_or_not.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open__chat__room_list);

        make_openchatroom = findViewById(R.id.make_openchatroom);
        open_chat_room_list = findViewById(R.id.open_chat_room_listview);

        make_openchatroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(Open_Chat_RoomList.this);
                LayoutInflater inflater = LayoutInflater.from(Open_Chat_RoomList.this);
                final View mView = inflater.inflate(R.layout.openchat_newroom_dialog, null);

                open_chat_subject = mView.findViewById(R.id.open_chat_subject);
                open_chat_qualification = mView.findViewById(R.id.open_chat_qualification);
                open_chat_room_create_ok = mView.findViewById(R.id.open_chat_room_create_ok);
                open_chat_room_create_cancel = mView.findViewById(R.id.open_chat_room_create_cancel);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                open_chat_room_create_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(open_chat_subject.getText().toString().equals("") || open_chat_qualification.getText().toString().equals("")){
                            Toast.makeText(Open_Chat_RoomList.this, "토론주제와 유의사항을 빈칸없이 입력해주세요!.", Toast.LENGTH_SHORT).show();
                        } else {
                            Check_Room_Dup_Or_Not(open_chat_subject.getText().toString(),open_chat_room_dup_check_url , new CallBack() {
                                @Override
                                public void getCodeValue(String code) {
                                    if(code.equals("openchat_roomname_duplicate")){
                                        Toast.makeText(Open_Chat_RoomList.this, "중복된 이름의 방이 있으므로 새로운 방을 생성할수 없습니다. 방제목을 다시 설정해주세요.", Toast.LENGTH_SHORT).show();
                                    } else if(code.equals("openchat_roomname_empty")){
                                        Toast.makeText(Open_Chat_RoomList.this, "중복된 이름의 방이 없으므로 방을 생성합니다.", Toast.LENGTH_SHORT).show();
                                        Save_Open_Chat_Room_Info(open_chat_subject.getText().toString(), open_chat_qualification.getText().toString());
                                        dialog.dismiss();
                                        Intent myintent = new Intent(Open_Chat_RoomList.this, OpenChatRoom.class);
                                        myintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        myintent.putExtra("subject", open_chat_subject.getText().toString());
                                        startActivity(myintent);
                                    } else{
                                        Toast.makeText(Open_Chat_RoomList.this, "else 예외 상황(if,else if 이외에 조건을 정해두지 않은 상황 발생, 보통 새로운 조건이 추가되었을때 이런일이 발생한다.) 코드를 확인해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });

                open_chat_room_create_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });
    }

    private void Check_Room_Dup_Or_Not(final String RoomName,final String open_chat_room_dup_check_url,final CallBack onCallBack) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, open_chat_room_dup_check_url,
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

                Toast.makeText(Open_Chat_RoomList.this, "Error", Toast.LENGTH_SHORT).show();
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

        MySingleton.getmInstance(Open_Chat_RoomList.this).addToRequestQue(stringRequest);
    }

    private void Save_Open_Chat_Room_Info(final String RoomName, final String Detail) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, open_chat_room_save_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            Toast.makeText(Open_Chat_RoomList.this, code, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Open_Chat_RoomList.this, "Error", Toast.LENGTH_SHORT).show();
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

        MySingleton.getmInstance(Open_Chat_RoomList.this).addToRequestQue(stringRequest);
    }

    private void Load_open_chat_room_info(final OpenChatRoom.CallBack onCallBack) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, open_chat_room_load_url,
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

                Toast.makeText(Open_Chat_RoomList.this, "Error", Toast.LENGTH_SHORT).show();
                error.printStackTrace();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };

        MySingleton.getmInstance(Open_Chat_RoomList.this).addToRequestQue(stringRequest);
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
        RoomListPool = new ArrayList<>();
        Load_open_chat_room_info(new OpenChatRoom.CallBack() { //1-1 인터페이스는 딱히 의미가 없다. 단지 코드를 눈에띄게 정리하고 정렬하는 용도로 사용한다. 앞으로 적극 사용하자.
            @Override                                          //2-1 위의 말은 콜백을 완벽히 이해하지 못하고 한말이다. 콜백과 인터페이스는 동의어가 아니다. 함수의 중간 작동과정을 콜백(데이터콜)후 이용하기 위해
                                                               //2-2 콜백을 사용한다. 하지만 콜백을 하기 위해서는 미리 interface에 콜백을 선언해야 하기 때문에, interface와 콜백을 햇갈렸다.
                                                               //2-3 여기서 함수 자체의 return값을 Json이나 String으로 받아서 처리할 수 없었기 때문에, CallBack을 사용했다.
                                                               //2-4 현재까지의 결론 이와 같이 Volley값은 return값을 처리할 수 없기때문에 무조건 서버에서 처리한 값을 다시 클라로 불러오고 싶으면 CallBack을 사용해야 한다.
                                                               //2-5 완벽히 이해하지도 못했는데, 이해했다고 생각하고 넘어갔다. 아직 배울게 많구나~
            public void getJsonArray(JSONArray jsonArray) throws JSONException {
                /**
                 * 발생 문제 : For{Msg.add} =(미연동)> Adapter(Msg) [ Msg.size()가 계속 0으로 나옴 ]
                 * For문안에다가 Adapter넣어서 문제 일시적으로 해결 (왜 For과 Array.add가 궁합이 안맞는지는 근본적인은 못참음)
                 */
                for (int i = 0; jsonArray.getJSONObject(i) != null; i++) {
//                    Toast.makeText(Open_Chat_RoomList.this, i + "통과", Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String code = jsonObject.getString("code");
                    String RoomName = jsonObject.getString("RoomName");
                    String Detail = jsonObject.getString("Detail");


                    Open_Chat_RoomList_Item open_chat_roomList_item = new Open_Chat_RoomList_Item(RoomName, Detail);
                    RoomListPool.add(open_chat_roomList_item);

                    adapter = new Open_Chat_RoomList_Adapter(Open_Chat_RoomList.this, RoomListPool); //--1
                    open_chat_room_list.setAdapter(adapter);  //--2

                }
            }
        });
    }

    public interface CallBack {
        void getCodeValue(String string);
    }
}
