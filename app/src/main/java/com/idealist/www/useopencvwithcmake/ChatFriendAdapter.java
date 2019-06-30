package com.idealist.www.useopencvwithcmake;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class ChatFriendAdapter extends BaseAdapter {

    String profile_image_url;
    String profile_text;
    private Activity mContext;
    private ArrayList<ChatFriendItem> listitem;
    String load_profile_image_url_and_text_from_DB = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_member_info/photo_statement_update.php";

    CheckBox friendlist_checkbox;
    ImageView friend_list_photo, friend_photo;
    TextView friend_list_id, friend_list_text, friend_id, friend_introduce;
    Button friend_chat_start,friend_call_start;

    public ChatFriendAdapter(Context context, ArrayList<ChatFriendItem> listItem) {

        mContext = (Activity) context;
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

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(R.layout.custom_friend_list, parent, false);

        final ChatFriendItem item = (ChatFriendItem) getItem(position); //전역으로 할수도 있지만 그냥 안에다가 여러번선언


        friendlist_checkbox = convertView.findViewById(R.id.friendlist_checkbox);
        friendlist_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setIschecked(isChecked);

            }
        });
        friendlist_checkbox.setChecked(item.ischecked());


        if (loginsuccess.friend_room_invitation_mode == true) {
            friendlist_checkbox.setVisibility(View.VISIBLE);
        }

        friend_list_photo = convertView.findViewById(R.id.friend_list_photo);
        friend_list_id = convertView.findViewById(R.id.friend_list_id);
        friend_list_text = convertView.findViewById(R.id.friend_list_text);

        load_profile_image_url_from_DB(item.getFriend_id(), friend_list_photo);
        load_profile_text_from_DB(item.getFriend_id(), friend_list_text);
        friend_list_id.setText(item.getFriend_id());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                final View mView = mContext.getLayoutInflater().inflate(R.layout.activity_friend_info, null);


                friend_photo = mView.findViewById(R.id.friend_photo);
                friend_introduce = mView.findViewById(R.id.friend_introduce);
                friend_id = mView.findViewById(R.id.friend_id);
                friend_chat_start = mView.findViewById(R.id.friend_apply);
                friend_call_start = mView.findViewById(R.id.friend_call);

                friend_chat_start.setText("1:1대화");
                friend_call_start.setVisibility(View.VISIBLE);

                load_profile_image_url_from_DB(item.getFriend_id(), friend_photo);
                load_profile_text_from_DB(item.getFriend_id(), friend_introduce);
                friend_id.setText(item.getFriend_id());

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                friend_chat_start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent myintent = new Intent(mContext, ChatRoom.class);
                        String mixStr = SeperationAndReOrder(loginsuccess.id + "]" + friend_id.getText().toString());
                        myintent.putExtra("subject", mixStr);
                        mContext.startActivity(myintent);

                        Toast.makeText(mContext, "1:1채팅을 시작했습니다.", Toast.LENGTH_SHORT).show();

                        loginsuccess.dream_friend_apply_layout.setVisibility(View.INVISIBLE);
                        loginsuccess.dream_friend_list_layout.setVisibility(View.INVISIBLE);
                        loginsuccess.dream_friend_chatlist_layout.setVisibility(View.VISIBLE);
                        dialog.dismiss();


                    }
                });

                friend_call_start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myintent = new Intent(mContext, ChatRoom.class);
                        String mixStr = SeperationAndReOrder(loginsuccess.id + "]" + friend_id.getText().toString());
                        myintent.putExtra("subject", mixStr);
                        myintent.putExtra("friend_call","friend_call");
                        mContext.startActivity(myintent);

                        Toast.makeText(mContext, "1:1채팅을 시작했습니다.", Toast.LENGTH_SHORT).show();
                        Toast.makeText(mContext, "영상통화를 겁니다", Toast.LENGTH_SHORT).show();


                        loginsuccess.dream_friend_apply_layout.setVisibility(View.INVISIBLE);
                        loginsuccess.dream_friend_list_layout.setVisibility(View.INVISIBLE);
                        loginsuccess.dream_friend_chatlist_layout.setVisibility(View.VISIBLE);
                        dialog.dismiss();




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

                            if (!profile_image_url.equals("default") && loginsuccess.id != null) { // 처음에 하얀화면 뜨는거 방지
                                Glide.with(mContext).load(profile_image_url).centerCrop().into(iv);
                            }
//                            profilepr.setText(profile_text);

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

    private static String SeperationAndReOrder(String string) {

        StringTokenizer str = new StringTokenizer(string, "]");
        int UserNum = str.countTokens();
        String[] test = new String[UserNum];
        for (int i = 0; i < UserNum; i++) {
            test[i] = str.nextToken();
            // System.out.println("test[" + i + "] = " + test[i]);
        }

        String sum;
        sum = SplitAndMigrateToString(test, UserNum);
        System.out.println("initial form : " + string);
        System.out.println("rearranged form : " + sum);
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
