package com.idealist.www.useopencvwithcmake;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Chat_Post_Change extends AppCompatActivity {

    String uri_String;
    Uri imageuri;
    private Bitmap bitmap;
    String Image_Name = "";
    String Image_Url = "";

    String Post_Image_Url;
    String Post_Text;
    String Writer;
    String Regdate;

    private String UploadUrl = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/ImageUploadApp/updateinfo.php";
    String board_change_one_post_info_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_board_info/change_board_info.php";

    ImageView iv_in_change_post;
    EditText edt_in_change_post;
    Button btn_of_changeinfo_in_change_post;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat__post__change);

        Intent mintent = getIntent();
        Post_Image_Url = mintent.getStringExtra("Post_Image_Url");
        Post_Text = mintent.getStringExtra("Post_Text");
        Writer = mintent.getStringExtra("Writer");
        Regdate = mintent.getStringExtra("Regdate");


        iv_in_change_post = findViewById(R.id.change_img);
        registerForContextMenu(iv_in_change_post);
        edt_in_change_post = findViewById(R.id.change_text);
        btn_of_changeinfo_in_change_post = findViewById(R.id.change_btn);

        btn_of_changeinfo_in_change_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change();
            }

        });


        Glide.with(Chat_Post_Change.this).load(Post_Image_Url).centerCrop().into(iv_in_change_post);
        edt_in_change_post.setText(Post_Text);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            imageuri = data.getData();
            Glide.with(this).load(imageuri).centerCrop().into(iv_in_change_post);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {

            /** Uri로 하는 방법도 있고, bitmap으로 하는 방법도 있다.*/
            imageuri = data.getData();
            Glide.with(this).load(imageuri).centerCrop().into(iv_in_change_post);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // 컨텍스트 메뉴가 최초로 한번만 호출되는 콜백 메서드
        Log.d("test", "onCreateContextMenu");
//        getMenuInflater().inflate(R.menu.main, menu);

        menu.setHeaderTitle("포스트사진 등록방법을 선택하세요!");
        menu.add(0, 1, 100, "사진 촬영");
        menu.add(0, 2, 100, "갤러리에서 불러오기");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // 롱클릭했을 때 나오는 context Menu 의 항목을 선택(클릭) 했을 때 호출
        switch (item.getItemId()) {
            case 1:// 사진 촬영 선택시
                Intent CameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (CameraIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(CameraIntent, 1000);
                }
                return true;
            case 2:// 사진 불러오기 선택시
                Intent myintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(myintent, 1);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void uploadImage() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UploadUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Response = jsonObject.getString("response");
//                            Toast.makeText(profileinfo.this, Response, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finish();
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
                params.put("name", Image_Name);
                params.put("image", imageToString(bitmap));

                return params;
            }
        };

        MySingleton.getmInstance(Chat_Post_Change.this).addToRequestQue(stringRequest);
    }

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return android.util.Base64.encodeToString(imgBytes, android.util.Base64.DEFAULT);
    }

    public String gettime() {
        long mNow;
        Date mDate;
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd aa hh:mm:ss");

        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    private void change() {


        if (edt_in_change_post.getText().toString().length() <= 4 || edt_in_change_post.getText().toString().length() > 20) {
            Toast.makeText(this, "5자이상 20자 미만의 글자를 입력해주세요!", Toast.LENGTH_SHORT).show();
        } else {
            if (imageuri == null) {
                Change_Board_One_Post_Info_In_DB();
                Toast.makeText(Chat_Post_Change.this, "포스트 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                uri_String = imageuri.toString();
                Image_Name = "board*" + loginsuccess.id + "*" + gettime();
                Post_Image_Url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/ImageUploadApp/uploads/" + Image_Name + ".jpg";


                Change_Board_One_Post_Info_In_DB();
                uploadImage();
                Toast.makeText(Chat_Post_Change.this, "포스트 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();

            }
        }

    }

    private void Change_Board_One_Post_Info_In_DB() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, board_change_one_post_info_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Chat_Post_Change.this, "Error", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Writer", Writer);
                params.put("Regdate", Regdate);
                params.put("Post_Image_Url", Post_Image_Url);
                params.put("Post_Text", edt_in_change_post.getText().toString());
                return params;
            }
        };
        MySingleton.getmInstance(Chat_Post_Change.this).addToRequestQue(stringRequest);
    }

}
