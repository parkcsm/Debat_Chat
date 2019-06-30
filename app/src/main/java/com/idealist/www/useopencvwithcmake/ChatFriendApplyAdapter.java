package com.idealist.www.useopencvwithcmake;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatFriendApplyAdapter extends BaseAdapter{

    public TextView friend_apply_name, friend_accept, friend_reject;
    private Activity mContext;
    private ArrayList<ChatFriendApplyItem> listitem;
    String accept_friend_apply_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_friend_info/accept_friend_apply.php";
    String reject_friend_apply_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_friend_info/reject_friend_apply.php";
    private String load_friend_info_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_friend_info/load_friend_info.php";

    public ChatFriendApplyAdapter(Context context, ArrayList<ChatFriendApplyItem> listItem) {
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
    public View getView(final int position, View convertView, ViewGroup viewGroup) {


        final ChatFriendApplyItem items = (ChatFriendApplyItem) getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.custom_friend_apply_list, null);
        }

        friend_apply_name = convertView.findViewById(R.id.friend_apply_name);
        friend_apply_name.setText(items.getFriend_apply_name());

        friend_accept = convertView.findViewById(R.id.friend_accept);
        friend_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    friend_accept();
            }

            private void friend_accept() {
                accept_friend_apply(items.getFriend_apply_name());
                listitem.remove(position);
                notifyDataSetChanged();
                // 서버코드 수정하는 부분 넣으면 된다.

                load_Friendlist();
            }
        });

        friend_reject = convertView.findViewById(R.id.friend_reject);
        friend_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    friend_reject();
            }

            private void friend_reject() {
                reject_friend_apply(items.getFriend_apply_name());
                listitem.remove(position);
                notifyDataSetChanged();
                // 서버코드 수정하는 부분 넣으면 된다.
            }
        });


        return convertView;
    }

    private void accept_friend_apply(final String Frequest_Sender) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, accept_friend_apply_url,
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
                params.put("Frequest_Sender", Frequest_Sender);
                params.put("Frequest_Receiver", loginsuccess.id);
                return params;
            }
        };

        MySingleton.getmInstance(mContext).addToRequestQue(stringRequest);
    }

    private void reject_friend_apply(final String Frequest_Sender) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, reject_friend_apply_url,
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
                params.put("Frequest_Sender", Frequest_Sender);
                params.put("Frequest_Receiver", loginsuccess.id);
                return params;
            }
        };

        MySingleton.getmInstance(mContext).addToRequestQue(stringRequest);
    }



    public void load_Friendlist() {
        loginsuccess.FriendPool = new ArrayList();
        viewAllFriend();
    }

    private void viewAllFriend() {
        load_friend_info(loginsuccess.id, new loginsuccess.CallBack() { // 인터페이스는 딱히 의미가 없다. 단지 코드를 눈에띄게 정리하고 정렬하는 용도로 사용한다. 앞으로 적극 사용하자.
            @Override
            public void getJsonArray(JSONArray jsonArray) throws JSONException {
                /**
                 * 발생 문제 : For{Msg.add} =(미연동)> Adapter(Msg) [ Msg.size()가 계속 0으로 나옴 ]
                 * For문안에다가 Adapter넣어서 문제 일시적으로 해결 (왜 For과 Array.add가 궁합이 안맞는지는 근본적인은 못참음)
                 */
                for (int i = 0; jsonArray.getJSONObject(i) != null; i++) {
//                    Toast.makeText(loginsuccess.this, i + "통과", Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String code = jsonObject.getString("code");
                    String result = jsonObject.getString("result");

                    ChatFriendItem chatfrienditem = new ChatFriendItem(result);
                    loginsuccess.FriendPool.add(0, chatfrienditem);

                    loginsuccess.chatFriendAdapter = new ChatFriendAdapter(mContext, loginsuccess.FriendPool); //--1
                    loginsuccess.Friendlist.setAdapter(loginsuccess.chatFriendAdapter);  //--2
                }
            }
        });
    }


    private void load_friend_info(final String id, final loginsuccess.CallBack onCallBack) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, load_friend_info_url,
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

                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                error.printStackTrace();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                return params;
            }
        };

        MySingleton.getmInstance(mContext).addToRequestQue(stringRequest);
    }


    public interface CallBack {
        void getJsonArray(JSONArray jsonArray) throws JSONException;
    }
}
