package com.idealist.www.useopencvwithcmake;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Chat_Post extends AppCompatActivity {

    ImageView fab_post;
    public static ArrayList<Chat_Post_Item> PostPool;
    public static Chat_Post_Adapter adapter;
    public static ListView PostList;
    String board_load_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_board_info/load_board_info.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat__post);
        fab_post = findViewById(R.id.fab_post);
        fab_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Chat_Post.this,Chat_Post_Write.class);
                startActivity(mIntent);
            }
        });
        PostList = findViewById(R.id.post_listview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PostPool = new ArrayList<>();
        adapter = new Chat_Post_Adapter(Chat_Post.this, PostPool);
        PostList.setAdapter(adapter);

        Load_Board_Info_from_DB(new OpenChatRoom.CallBack() { // 인터페이스는 딱히 의미가 없다. 단지 코드를 눈에띄게 정리하고 정렬하는 용도로 사용한다. 앞으로 적극 사용하자.
            @Override
            public void getJsonArray(JSONArray jsonArray) throws JSONException {
                /**
                 * 발생 문제 : For{Msg.add} =(미연동)> Adapter(Msg) [ Msg.size()가 계속 0으로 나옴 ]
                 * For문안에다가 Adapter넣어서 문제 일시적으로 해결 (왜 For과 Array.add가 궁합이 안맞는지는 근본적인은 못참음)
                 */

                for (int i = jsonArray.length()-1; jsonArray.getJSONObject(i) != null; i--) {
//                                Toast.makeText(OpenChatRoom.this, i + "통과", Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);


                    String Post_Image_Url = jsonObject.getString("Post_Image_Url");
                    String Writer = jsonObject.getString("Writer");
                    String Post_Text = jsonObject.getString("Post_Text");
                    String Like_Num = jsonObject.getString("Like_Num");
                    String Comment_Num = jsonObject.getString("Comment_Num");
                    String Regdate = jsonObject.getString("Regdate");
//                           Toast.makeText(Chat_Post.this, Writer + "//" + Post_Text + "//" + Like_Num + "//" + Comment_Num + "//" + Regdate + "//" +Post_Image_Url , Toast.LENGTH_SHORT).show();

                    Chat_Post_Item chat_post_item = new Chat_Post_Item(Post_Image_Url,Writer,Post_Text,false,Like_Num,Comment_Num, Regdate);
                    PostPool.add(chat_post_item);

                    adapter = new Chat_Post_Adapter(Chat_Post.this, PostPool); //--1
                    PostList.setAdapter(adapter);  //--2
                    adapter.notifyDataSetChanged();
//                  Toast.makeText(OpenChatRoom.this, msgPool.size()+"", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void Load_Board_Info_from_DB(final OpenChatRoom.CallBack onCallBack) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, board_load_url,
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
                Toast.makeText(Chat_Post.this, "Error", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };

        MySingleton.getmInstance(Chat_Post.this).addToRequestQue(stringRequest);
    }
}
