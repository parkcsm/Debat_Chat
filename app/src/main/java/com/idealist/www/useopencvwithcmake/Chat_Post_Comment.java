package com.idealist.www.useopencvwithcmake;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Chat_Post_Comment extends AppCompatActivity {

    ImageView img_to_comment_for;
    TextView id_to_comment_for;
    TextView text_to_comment_for;
    EditText edt_comment_msg;
    ImageView iv_comment_msg_send;

    String Post_Image_Url;
    String Post_Text;
    public static String Post_Writer;
    public static String Post_Regdate;
    String board_post_comment_save_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_board_info/save_board_comment_info.php";
    String board_comment_load_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_board_info/load_board_comment_info.php";
    String plus_board_comment_num_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_board_info/plus_board_comment_number.php";
    public static ArrayList<Chat_Post_Comment_Item> CommentPool;
    public static Chat_Post_Comment_Adapter adapter;
    ListView comment_listview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat__post__comment);

        img_to_comment_for = findViewById(R.id.img_to_comment_for);
        id_to_comment_for = findViewById(R.id.id_to_comment_for);
        text_to_comment_for = findViewById(R.id.text_to_comment_for);
        edt_comment_msg = findViewById(R.id.edt_comment_msg);
        iv_comment_msg_send = findViewById(R.id.iv_comment_msg_send);
        comment_listview = findViewById(R.id.comment_listview);

        Intent mintent = getIntent();
        Post_Image_Url = mintent.getStringExtra("Post_Image_Url");
        Post_Text = mintent.getStringExtra("Post_Text");
        Post_Writer = mintent.getStringExtra("Writer");
        Post_Regdate = mintent.getStringExtra("Regdate");

        Glide.with(Chat_Post_Comment.this).load(Post_Image_Url).centerCrop().into(img_to_comment_for);
        id_to_comment_for.setText(Post_Writer);
        text_to_comment_for.setText(Post_Text);

        CommentPool = new ArrayList<>();
        adapter = new Chat_Post_Comment_Adapter(Chat_Post_Comment.this, CommentPool);
        comment_listview.setAdapter(adapter);

        iv_comment_msg_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_comment_msg.getText().toString().length() < 3) {
                    Toast.makeText(Chat_Post_Comment.this, "댓글을 3자이상 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
//                    CommentPool = new ArrayList<>();
//                    Load_Board_Info_from_DB(new OpenChatRoom.CallBack() {
//                        @Override
//                        public void getJsonArray(JSONArray jsonArray) throws JSONException {
//                            for (int i = 0; jsonArray.getJSONObject(i) != null; i++) {
////                                Toast.makeText(OpenChatRoom.this, i + "통과", Toast.LENGTH_SHORT).show();
//                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                String Writer = jsonObject.getString("Writer");
//                                String Comment_Text = jsonObject.getString("Comment_Text");
//                                String Regdate = jsonObject.getString("Regdate");
////                           Toast.makeText(Chat_Post.this, Writer + "//" + Post_Text + "//" + Like_Num + "//" + Comment_Num + "//" + Regdate + "//" +Post_Image_Url , Toast.LENGTH_SHORT).show();
//
//                                Chat_Post_Comment_Item chat_post_comment_item = new Chat_Post_Comment_Item(Writer, Comment_Text, Regdate);
//                                CommentPool.add(chat_post_comment_item);
//
//                                adapter = new Chat_Post_Comment_Adapter(Chat_Post_Comment.this, CommentPool); //--1
//                                comment_listview.setAdapter(adapter);  //--2
//                                adapter.notifyDataSetChanged();
////                  Toast.makeText(OpenChatRoom.this, msgPool.size()+"", Toast.LENGTH_SHORT).show();
//                            }
//
//
//
//                        }
//                    }, Post_Writer, Post_Regdate);
                    Chat_Post_Comment_Item chat_post_comment_item = new Chat_Post_Comment_Item(loginsuccess.id, edt_comment_msg.getText().toString(), loginsuccess.gettime());
                    CommentPool.add(CommentPool.size(), chat_post_comment_item);
                    adapter.notifyDataSetChanged();
                    Save_Comment_Msg_To_The_Post(Post_Writer, Post_Regdate);
                    Plus_Board_Comment_Num(Post_Writer,Post_Regdate);
                    comment_listview.setSelection(adapter.getCount() - 1);
                    edt_comment_msg.setText("");

                }
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Load_Board_Info_from_DB(new OpenChatRoom.CallBack() {
            @Override
            public void getJsonArray(JSONArray jsonArray) throws JSONException {

                for (int i = 0; jsonArray.getJSONObject(i) != null; i++) {
//                                Toast.makeText(OpenChatRoom.this, i + "통과", Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);


                    String Writer = jsonObject.getString("Writer");
                    String Comment_Text = jsonObject.getString("Comment_Text");
                    String Regdate = jsonObject.getString("Regdate");
//                           Toast.makeText(Chat_Post.this, Writer + "//" + Post_Text + "//" + Like_Num + "//" + Comment_Num + "//" + Regdate + "//" +Post_Image_Url , Toast.LENGTH_SHORT).show();

                    Chat_Post_Comment_Item chat_post_comment_item = new Chat_Post_Comment_Item(Writer, Comment_Text, Regdate);
                    CommentPool.add(chat_post_comment_item);

                    adapter = new Chat_Post_Comment_Adapter(Chat_Post_Comment.this, CommentPool); //--1
                    comment_listview.setAdapter(adapter);  //--2
                    adapter.notifyDataSetChanged();
//                  Toast.makeText(OpenChatRoom.this, msgPool.size()+"", Toast.LENGTH_SHORT).show();
                }
            }
        }, Post_Writer, Post_Regdate);
    }

    private void Save_Comment_Msg_To_The_Post(final String Post_Writer, final String Post_Regdate) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, board_post_comment_save_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            if (code != null) {
                                Toast.makeText(Chat_Post_Comment.this, "댓글이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Chat_Post_Comment.this, "Error", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Writer", loginsuccess.id);
                params.put("Comment_Text", edt_comment_msg.getText().toString());
                params.put("Regdate", loginsuccess.gettime());
                params.put("Post_Writer", Post_Writer);
                params.put("Post_Regdate", Post_Regdate);
                return params;
            }
        };
        MySingleton.getmInstance(Chat_Post_Comment.this).addToRequestQue(stringRequest);
    }


    private void Load_Board_Info_from_DB(final OpenChatRoom.CallBack onCallBack, final String Post_Writer, final String Post_Regdate) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, board_comment_load_url,
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
                Toast.makeText(Chat_Post_Comment.this, "Error", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Post_Writer", Post_Writer);
                params.put("Post_Regdate", Post_Regdate);
                return params;
            }
        };
        MySingleton.getmInstance(Chat_Post_Comment.this).addToRequestQue(stringRequest);
    }

    private void Plus_Board_Comment_Num(final String Writer, final String Regdate) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, plus_board_comment_num_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Chat_Post_Comment.this, "Error", Toast.LENGTH_SHORT).show();
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
        MySingleton.getmInstance(Chat_Post_Comment.this).addToRequestQue(stringRequest);
    }
}
