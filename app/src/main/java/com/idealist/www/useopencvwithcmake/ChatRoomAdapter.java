package com.idealist.www.useopencvwithcmake;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class ChatRoomAdapter extends BaseAdapter {
    String profile_image_url;
    String profile_text;
    String load_profile_image_url_and_text_from_DB = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_member_info/photo_statement_update.php";
    String save_friend_apply_info_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_friend_info/save_friend_apply_info.php";
    String check_if_we_are_friend_or_not_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_friend_info/check_if_we_are_friend_or_not.php";
    private Context mContext;
    private ArrayList<ChatRoomItem> listitem;
    public LinearLayout me, others;
    public ImageView Msg_iv_me, Msg_iv_others;
    public TextView Msg_tv_me, Msg_tv_others;

    public TextView my_message_time, my_message;
    public ImageView my_iv;

    public TextView friend_chat_id, friend_message, friend_message_time;
    public ImageView friend_iv, friend_image;

    ImageView friend_photo;
    TextView friend_id, friend_introduce;
    Button friend_apply, friend_call_start;


    public ChatRoomAdapter(Context context, ArrayList<ChatRoomItem> listItem) {
        mContext = context;
        listitem = listItem;
    }

    @Override
    public int getCount() {
        return listitem.size();
    }

    @Override
    public Object getItem(int position) {
        return listitem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ChatRoomItem item = (ChatRoomItem) getItem(position); //전역으로 할수도 있지만 그냥 안에다가 여러번선언

        if (item.getType().equals("text")) {
            StringTokenizer stz2 = new StringTokenizer(item.getChatMsg(), ":");
//            final String sender = stz2.nextToken();
            String sender = item.getMessege_send_id();

            if (loginsuccess.id.equals(sender.trim())) {
//                Toast.makeText(mContext,"MainChat.namev : "+MainChat.namev+"/equal/ sender :" + sender , Toast.LENGTH_SHORT).show();
                LayoutInflater inflater = LayoutInflater.from(mContext);

                convertView = inflater.inflate(R.layout.custom_new_chatmsg_me, parent, false);
                my_message_time = convertView.findViewById(R.id.my_message_time);
                my_message = convertView.findViewById(R.id.my_message);
                my_iv = convertView.findViewById(R.id.my_iv);
                my_iv.setVisibility(View.GONE);
                my_message_time.setText(item.getMessege_time());
                my_message.setText(item.getMessege_text());

            } else {
//                Toast.makeText(mContext, "MainChat.namev : "+MainChat.namev+"/not equal/ sender :" + sender, Toast.LENGTH_SHORT).show();
                LayoutInflater inflater = LayoutInflater.from(mContext);


                convertView = inflater.inflate(R.layout.custom_new_chatmsg_others, parent, false);
                friend_image = convertView.findViewById(R.id.chat_room_friend_image);
                friend_chat_id = convertView.findViewById(R.id.friend_chat_id);
                friend_message = convertView.findViewById(R.id.friend_message);
                friend_message_time = convertView.findViewById(R.id.friend_message_time);
                friend_iv = convertView.findViewById(R.id.friend_iv);
                friend_iv.setVisibility(View.GONE);

                // RequestQue Glide써서 구현예정 friend_image

                load_profile_image_url_from_DB(item.getMessege_send_id(), friend_image);
                friend_chat_id.setText(item.getMessege_send_id());
                friend_message.setText(item.getMessege_text());
                friend_message_time.setText(item.getMessege_time());

            }

        } else if (item.getType().equals("image")) {
            StringTokenizer stz3 = new StringTokenizer(item.getChatMsg(), "*");
            final String image = stz3.nextToken();
            String roomname = stz3.nextToken();
//            String sender = stz3.nextToken();

            String sender = item.getMessege_send_id();

            if (loginsuccess.id.equals(sender)) {
                LayoutInflater inflater = LayoutInflater.from(mContext);

                convertView = inflater.inflate(R.layout.custom_new_chatmsg_me, parent, false);
                my_message_time = convertView.findViewById(R.id.my_message_time);
                my_message = convertView.findViewById(R.id.my_message);
                my_iv = convertView.findViewById(R.id.my_iv);
                my_message_time.setText(item.getMessege_time());
                my_message.setText("Image");

                String server_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/ImageUploadApp/uploads/" + item.getChatMsg() + ".jpg";
                //                String server_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/ImageUploadApp/uploads/profile_photo*parkcsm*2018-09-15%20%EC%98%A4%ED%9B%84%2004:19:09.jpg";

                Glide.with(mContext)
                        .load(server_url)
                        .centerCrop()
                        .into(my_iv);

            } else {
                LayoutInflater inflater = LayoutInflater.from(mContext);

                convertView = inflater.inflate(R.layout.custom_new_chatmsg_others, parent, false);
                friend_image = convertView.findViewById(R.id.chat_room_friend_image);
                friend_chat_id = convertView.findViewById(R.id.friend_chat_id);
                friend_message = convertView.findViewById(R.id.friend_message);
                friend_message_time = convertView.findViewById(R.id.friend_message_time);
                friend_iv = convertView.findViewById(R.id.friend_iv);

                load_profile_image_url_from_DB(item.getMessege_send_id(), friend_image);
                // RequestQue Glide써서 구현예정 friend_image
                friend_chat_id.setText(item.getMessege_send_id());
                friend_message.setText("Image");
                friend_message_time.setText(item.getMessege_time());

                String server_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/ImageUploadApp/uploads/" + item.getChatMsg() + ".jpg";
                //                String server_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/ImageUploadApp/uploads/profile_photo*parkcsm*2018-09-15%20%EC%98%A4%ED%9B%84%2004:19:09.jpg";

                Glide.with(mContext)
                        .load(server_url)
                        .centerCrop()
                        .into(friend_iv);
            }
        }


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext, "item.getType() =" + item.getType() + "///" + "item.getChatMsg()" + item.getChatMsg(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(mContext, item.getChatMsg(), Toast.LENGTH_SHORT).show();

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = LayoutInflater.from(mContext);
                final View mView = inflater.inflate(R.layout.activity_friend_info, null);

                friend_photo = mView.findViewById(R.id.friend_photo);
                friend_introduce = mView.findViewById(R.id.friend_introduce);
                friend_id = mView.findViewById(R.id.friend_id);
                friend_apply = mView.findViewById(R.id.friend_apply);
                friend_call_start = mView.findViewById(R.id.friend_call);

                load_profile_image_url_from_DB(item.getMessege_send_id(), friend_photo);
                load_profile_text_from_DB(item.getMessege_send_id(), friend_introduce);
                friend_id.setText(item.getMessege_send_id());

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                check_if_we_are_friend_or_not(item.getMessege_send_id(), new CallBack() {
                    @Override
                    public void get_if_we_are_friend_or_not(String reaction_from_server) {
                        if (reaction_from_server.equals("이미 친구입니다..)")) {
                            friend_apply.setText("1:1대화");
                            if (OpenChatRoom.AreYouWatchingThisActivity.equals("") && Streaming.AreYouWatchingThisActivity.equals("")) { // OpenChat에서는 영상통화가 안되게 막아놨으니, ""(openchatActivity를 현재보고있지 않으면)이면 영상통화 버튼을 숨긴다.
                                friend_call_start.setVisibility(View.VISIBLE);
                            }

                        } /*else {
                            friend_apply.setText("친구 추가");
                            Toast.makeText(mContext, "친구 추가", Toast.LENGTH_SHORT).show();
                        }*/  //CallBack에서 else문이 작동하지 않는 것 같다. -> Direct접근만 가능한 코드인가?
                    }
                });


                if (item.getMessege_send_id().equals(loginsuccess.id)) {
                    friend_apply.setText("나 자신");
                }


                friend_apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (friend_apply.getText().toString().equals("친구 신청") && !item.getMessege_send_id().equals(loginsuccess.id)) {
                            save_friend_apply_info(item.getMessege_send_id());
                        } else if (friend_apply.getText().toString().equals("1:1대화")) {
                            Intent myintent = new Intent(mContext, ChatRoom.class);
                            String mixStr = SeperationAndReOrder(loginsuccess.id + "]" + friend_id.getText().toString());
                            myintent.putExtra("subject", mixStr);
                            mContext.startActivity(myintent);

                            Toast.makeText(mContext, "1:1채팅을 시작했습니다.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });

                friend_call_start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, "영상통화를 시작했습니다.", Toast.LENGTH_SHORT).show();
                        ChatRoom.msg_send_and_save_process(ChatRoom.id + "]님이 " + ChatRoom.receiver + "님에게 영상통화를 걸었습니다.");
                        ChatRoom.pw.println("start_call" + "/chat/" + ChatRoom.receiver + "/" + ChatRoom.id + "/" + "start_call");
                        dialog.dismiss();
                        Intent mintent = new Intent(mContext, Video_Call.class);
                        mintent.putExtra("mode", "publisher_plus_subscriber");
                        mContext.startActivity(mintent);
                    }
                });
            }
        });

        return convertView;
    }


    private void load_profile_image_url_from_DB(final String id, final ImageView iv) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, load_profile_image_url_and_text_from_DB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            String code = jsonObject.getString("code");
//                            Toast.makeText(profileinfo.this, "code :" + code, Toast.LENGTH_SHORT).show();

                            profile_image_url = jsonObject.getString("profile_image_url").toString();
                            profile_text = jsonObject.getString("profile_text");

                            if (!profile_image_url.equals("default")) { // 처음에 하얀화면 뜨는거 방지
                                Glide.with(mContext).load(profile_image_url).centerCrop().into(iv);
                            }

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
                params.put("id", id);
                return params;
            }
        };
        MySingleton.getmInstance(mContext).addToRequestQue(stringRequest);
    }

    private void load_profile_text_from_DB(final String id, final TextView tv) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, load_profile_image_url_and_text_from_DB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            String code = jsonObject.getString("code");
//                            Toast.makeText(profileinfo.this, "code :" + code, Toast.LENGTH_SHORT).show();

                            profile_image_url = jsonObject.getString("profile_image_url").toString();
                            profile_text = jsonObject.getString("profile_text");


                            tv.setText(profile_text);

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
                params.put("id", id);
                return params;
            }
        };
        MySingleton.getmInstance(mContext).addToRequestQue(stringRequest);
    }

    private void save_friend_apply_info(final String Frequest_Receiver) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, save_friend_apply_info_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
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
                params.put("Frequest_Sender", loginsuccess.id);
                params.put("Frequest_Receiver", Frequest_Receiver);
                params.put("Regdate", gettime());

                return params;
            }
        };

        MySingleton.getmInstance(mContext).addToRequestQue(stringRequest);
    }

    private void check_if_we_are_friend_or_not(final String Frequest_Receiver, final CallBack onCallBack) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, check_if_we_are_friend_or_not_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");
                            onCallBack.get_if_we_are_friend_or_not(message);
//                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
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
                params.put("Frequest_Sender", loginsuccess.id);
                params.put("Frequest_Receiver", Frequest_Receiver);
                return params;
            }
        };

        MySingleton.getmInstance(mContext).addToRequestQue(stringRequest);
    }

    public String gettime() {
        long mNow;
        Date mDate;
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd aa hh:mm:ss");
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    public interface CallBack {
        void get_if_we_are_friend_or_not(String reaction_from_server);
    }

    private String SeperationAndReOrder(String string) {

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

    private String SplitAndMigrateToString(String[] test, int ArraySize) {
        Arrays.sort(test);

        String sum = "";
        for (int i = 0; i < ArraySize; i++) {
            sum += test[i] + "]";
        }
        return sum;
    }

}

