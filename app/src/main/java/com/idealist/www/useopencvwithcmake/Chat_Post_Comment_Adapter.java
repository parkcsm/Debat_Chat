package com.idealist.www.useopencvwithcmake;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class Chat_Post_Comment_Adapter extends BaseAdapter{

    String profile_image_url;
    String profile_text;
    String load_profile_image_url_and_text_from_DB = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_member_info/photo_statement_update.php";
    String save_friend_apply_info_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_friend_info/save_friend_apply_info.php";
    String check_if_we_are_friend_or_not_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_friend_info/check_if_we_are_friend_or_not.php";

    ImageView friend_photo;
    TextView friend_id, friend_introduce;
    Button friend_apply;

    String id;

    private Activity mContext;

    private ArrayList<Chat_Post_Comment_Item> listitem;
    TextView comment_id;
    TextView comment_text;
    TextView comment_time;
    ImageView comment_change;
    ImageView comment_delete;
    String change_board_comment_info_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_board_info/change_board_comment_info.php";
    String delete_board_comment_info_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_board_info/delete_board_comment_info.php";
    String minus_board_comment_num_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_board_info/minus_board_comment_number.php";

    public Chat_Post_Comment_Adapter(android.content.Context context, ArrayList<Chat_Post_Comment_Item> listitem) {

        mContext = (Activity) context;
        this.listitem = listitem;
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


            LayoutInflater inflater = mContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.custom_comment, null);

        comment_id = convertView.findViewById(R.id.comment_id);
        comment_text = convertView.findViewById(R.id.comment_text);
        comment_time = convertView.findViewById(R.id.comment_time);
        comment_change = convertView.findViewById(R.id.comment_change);
        comment_delete = convertView.findViewById(R.id.comment_delete);


        final Chat_Post_Comment_Item item = (Chat_Post_Comment_Item) getItem(position);

        comment_id.setText(item.getId());

        comment_id.setOnClickListener(new View.OnClickListener() {
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

                load_profile_image_url_from_DB(item.getId(), friend_photo);
                load_profile_text_from_DB(item.getId(), friend_introduce);
                friend_id.setText(item.getId());

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                check_if_we_are_friend_or_not(item.getId(), new ChatRoomAdapter.CallBack() {
                    @Override
                    public void get_if_we_are_friend_or_not(String reaction_from_server) {
                        if (reaction_from_server.equals("이미 친구입니다..)")){
                            friend_apply.setText("1:1대화");
                        } /*else {
                            friend_apply.setText("친구 추가");
                            Toast.makeText(mContext, "친구 추가", Toast.LENGTH_SHORT).show();
                        }*/  //CallBack에서 else문이 작동하지 않는 것 같다. -> Direct접근만 가능한 코드인가?
                    }
                });


                if (item.getId().equals(loginsuccess.id)) {
                    friend_apply.setText("나 자신");
                }


                friend_apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (friend_apply.getText().toString().equals("친구 신청") && !item.getId().equals(loginsuccess.id)) {
                            save_friend_apply_info(item.getId());
                        } else if (friend_apply.getText().toString().equals("1:1대화")){
                            Intent myintent = new Intent(mContext, ChatRoom.class);
                            String mixStr = SeperationAndReOrder(loginsuccess.id + "]" + friend_id.getText().toString());
                            myintent.putExtra("subject", mixStr);
                            mContext.startActivity(myintent);

                            Toast.makeText(mContext, "1:1채팅을 시작했습니다.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        comment_text.setText(item.getText());
        comment_time.setText(item.getTime());

        comment_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                final View mView = mContext.getLayoutInflater().inflate(R.layout.custom_post_comment_change,null);
                final EditText edt_comment_change = mView.findViewById(R.id.edt_comment_change);
                edt_comment_change.setText(item.getText());
                Button btn_comment_change = mView.findViewById(R.id.btn_comment_change);


                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                btn_comment_change.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        change_board_comment_info(item.getId(),item.getTime(),edt_comment_change.getText().toString());

                        Chat_Post_Comment_Item newitem = new Chat_Post_Comment_Item(item.getId(), edt_comment_change.getText().toString(),item.getTime());
                        listitem.remove(position);
                        listitem.add(position, newitem);
                        notifyDataSetChanged();

                        dialog.dismiss();
                        Toast.makeText(mContext, "댓글이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        comment_delete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        show(position);
                    }
                });



        if (comment_id.getText().toString().equals(loginsuccess.id)) {
        } else{
            comment_change.setVisibility(View.INVISIBLE);
            comment_delete.setVisibility(View.INVISIBLE);
        }

        // 다른 아이디로 로그인시 댓글 수정삭제가 불가능하게 설정했음

        return convertView;
    }


    private void delete(int position) {

        Minus_Board_Comment_Num(Chat_Post_Comment.Post_Writer,Chat_Post_Comment.Post_Regdate);
        final Chat_Post_Comment_Item item = (Chat_Post_Comment_Item) getItem(position);
        delete_board_comment_info(item.getId(),item.getTime());
        listitem.remove(position);
        notifyDataSetChanged();
        Toast.makeText(mContext, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
    }

    void show(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("해당 댓글을 삭제하시겠습니까?");
        builder.setMessage("삭제된 정보는 복구불가능합니다.");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        delete(position);
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();


    }

    private void delete_board_comment_info(final String id,final String time) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, delete_board_comment_info_url,
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
                params.put("Writer", id);
                params.put("Regdate", time);
                return params;
            }
        };

        MySingleton.getmInstance(mContext).addToRequestQue(stringRequest);
    }

    private void change_board_comment_info(final String id,final String time, final String changed_comment) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, change_board_comment_info_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
                params.put("Writer", id);
                params.put("Regdate", time);
                params.put("Comment_Text", changed_comment);
                return params;
            }
        };
        MySingleton.getmInstance(mContext).addToRequestQue(stringRequest);
    }

    private void Minus_Board_Comment_Num(final String Writer, final String Regdate) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, minus_board_comment_num_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
                params.put("Writer",Writer);
                params.put("Regdate",Regdate);
                return params;
            }
        };
        MySingleton.getmInstance(mContext).addToRequestQue(stringRequest);
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
                params.put("Regdate", loginsuccess.gettime());

                return params;
            }
        };

        MySingleton.getmInstance(mContext).addToRequestQue(stringRequest);
    }

    private void check_if_we_are_friend_or_not(final String Frequest_Receiver, final ChatRoomAdapter.CallBack onCallBack) {
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
