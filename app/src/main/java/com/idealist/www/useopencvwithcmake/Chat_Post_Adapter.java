package com.idealist.www.useopencvwithcmake;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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

import static android.content.Context.MODE_PRIVATE;

public class Chat_Post_Adapter extends BaseAdapter {

    String profile_image_url;
    String profile_text;
    String load_profile_image_url_and_text_from_DB = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_member_info/photo_statement_update.php";
    String save_friend_apply_info_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_friend_info/save_friend_apply_info.php";
    String check_if_we_are_friend_or_not_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_friend_info/check_if_we_are_friend_or_not.php";

    public ImageView ivImg, iv_change, iv_delete, iv_comment;
    public TextView time;
    public TextView tvLikeCount, tvWriterName, tvWriterText;
    public CheckBox cbLike;
    public TextView comment_number;

    ImageView friend_photo;
    TextView friend_id, friend_introduce;
    Button friend_apply;

    private Activity mContext;
    private ArrayList<Chat_Post_Item> listitems;
    String delete_board_info_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_board_info/delete_board_info.php";
    String board_change_one_post_like_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_board_info/change_board_like.php";
//    static ArrayList<dream_post_comment_item> listItem = new ArrayList<>();

    public Chat_Post_Adapter(Context context, ArrayList<Chat_Post_Item> listItem) {
        mContext = (Activity) context;
        listitems = listItem;
    }

    @Override
    public int getCount() {
        return listitems.size();
    }

    @Override
    public Object getItem(int position) {

        return listitems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        final Chat_Post_Item item = (Chat_Post_Item) getItem(position);

            LayoutInflater inflater = mContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.custom_debate_post, null);

        ivImg = convertView.findViewById(R.id.iv_img);
        time = convertView.findViewById(R.id.post_time);
        iv_change = convertView.findViewById(R.id.iv_edit);
        iv_delete = convertView.findViewById(R.id.iv_delete);

        if (loginsuccess.id.equals(item.getId())) {
            iv_change.setVisibility(View.VISIBLE);
            iv_delete.setVisibility(View.VISIBLE);
        } else {
            iv_change.setVisibility(View.INVISIBLE);
            iv_delete.setVisibility(View.INVISIBLE);
        }


        tvWriterName = convertView.findViewById(R.id.tv_writer);
        tvWriterName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        tvWriterText = convertView.findViewById(R.id.tv_writercoment);
        cbLike = convertView.findViewById(R.id.cb_like);
        tvLikeCount = convertView.findViewById(R.id.tv_like_count);
        iv_comment = convertView.findViewById(R.id.iv_comment);
        comment_number = convertView.findViewById(R.id.comment_number);

        iv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WriteComment(position);
            }
        });


        cbLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences(loginsuccess.id+"heart", MODE_PRIVATE);
                    SharedPreferences.Editor ShareEdit = sharedPreferences.edit();
                    ShareEdit.putString(item.getId() + item.getTime(),"true");
                    ShareEdit.commit();
                    item.setPostLikeCount_plus_one();
                    notifyDataSetChanged();
                    Change_Board_One_Post_like_In_DB(item.getId(),item.getTime(),item.getPostLikeCount(),"Plus");
                } else {
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences(loginsuccess.id+"heart", MODE_PRIVATE);
                    SharedPreferences.Editor ShareEdit = sharedPreferences.edit();
                    ShareEdit.putString(item.getId() + item.getTime(),"false");
                    ShareEdit.commit();
                    item.setPostLikeCount_minus_one();
                    notifyDataSetChanged();
                    Change_Board_One_Post_like_In_DB(item.getId(),item.getTime(),item.getPostLikeCount(),"Minus");
                }

            }
        });


        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginsuccess.id.equals(item.getId())) {
                    show(position);
                } else {
                    Toast.makeText(mContext, "글을 삭제할 수 있는 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        iv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginsuccess.id.equals(item.getId())) {
                    change(position);
                } else {
                    Toast.makeText(mContext, "글을 수정할 수 있는 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Glide.with(mContext).
                load(item.getUrlString()).
                centerCrop().
                into(ivImg);
        time.setText(item.getTime());
        tvWriterName.setText(item.getId());
        tvWriterText.setText(item.getPostText());
        comment_number.setText(item.commentCount);


        SharedPreferences sharedPreferences = mContext.getSharedPreferences(loginsuccess.id+"heart", MODE_PRIVATE);
        // id에 따라서 이미 하트를 체크했으면 다음에 체크할수 없게 세팅
        if (sharedPreferences.getString(item.getId() + item.getTime(), "false").equals("true")) {
            item.isUserLike = true;
        } else {
            item.isUserLike = false;
        }
        cbLike.setChecked(item.getisUserLike());
        tvLikeCount.setText(item.getPostLikeCount());

        return convertView;
    }


    private void delete(int position) {
        final Chat_Post_Item item = (Chat_Post_Item) getItem(position);
        delete_board_info(item.getId(),item.getTime());
        listitems.remove(position);
        notifyDataSetChanged();


    }

    private void change(int position) {

        final Chat_Post_Item item = (Chat_Post_Item) getItem(position); //전역으로 할수도 있지만 그냥 안에다가 여러번선언

        Intent myintent = new Intent(mContext, Chat_Post_Change.class);
        myintent.putExtra("Post_Image_Url", item.getUrlString());
        myintent.putExtra("Post_Text",item.getPostText());
        myintent.putExtra("Writer", item.getId());
        myintent.putExtra("Regdate", item.getTime());
        this.mContext.startActivity(myintent);
    }

    private void WriteComment(int position) {

        final Chat_Post_Item item = (Chat_Post_Item) getItem(position); //전역으로 할수도 있지만 그냥 안에다가 여러번선언

        Intent myintent = new Intent(mContext, Chat_Post_Comment.class);
        myintent.putExtra("Post_Image_Url", item.getUrlString());
        myintent.putExtra("Post_Text",item.getPostText());
        myintent.putExtra("Writer", item.getId());
        myintent.putExtra("Regdate", item.getTime());
        this.mContext.startActivity(myintent);
    }

    void show(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("해당 글을 삭제하시겠습니까?");
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

    private void delete_board_info(final String id,final String time) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, delete_board_info_url,
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

    private void Change_Board_One_Post_like_In_DB(final String Writer, final String Regdate, final String Like_Num, final String Plus_or_Minus) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, board_change_one_post_like_url,
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
                params.put("Like_Num",Like_Num);
                params.put("Plus_or_Minus",Plus_or_Minus);
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