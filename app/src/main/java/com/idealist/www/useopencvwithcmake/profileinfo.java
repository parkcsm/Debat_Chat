package com.idealist.www.useopencvwithcmake;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class profileinfo extends AppCompatActivity {

    public static boolean Goto_loginsuccess = false;

    String profile_image_url;
    String profile_text;

    private String id;

    Button btn_save;
    ImageView profilephoto;
    EditText profilepr;

    private String UploadUrl = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/ImageUploadApp/updateinfo.php";
    private String update_profile_image_url_and_text_in_DB = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_member_info/update_url_info.php";
    private String load_profile_image_url_and_text_from_DB = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_member_info/photo_statement_update.php";

    Bitmap bitmap; //고른사진이 Bitmap으로 임시로 저장되는 공간
    String uri_String; // Bitmap사진이 String으로 바뀌어서 임시로 저장되는 공간
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileinfo);

        SharedPreferences Operation = getSharedPreferences("Operation", MODE_PRIVATE);
        id = Operation.getString("Operation", null);
        Toast.makeText(this, id+"로그인했음", Toast.LENGTH_SHORT).show();

        profilephoto = findViewById(R.id.iv_profilephoto);
        profilepr = findViewById(R.id.tv_profiletext);
        btn_save = findViewById(R.id.btn_profilesave);

        registerForContextMenu(profilephoto);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        Toast.makeText(this, "동그라미를 길게 누르면 프로필사진을 등록할 수 있습니다.", Toast.LENGTH_SHORT).show();

        load();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1) {
            uri = data.getData();

            Glide.with(this).load(uri).centerCrop().into(profilephoto);
//            Toast.makeText(this, "request code 1", Toast.LENGTH_SHORT).show();
            //서버로 파일 업로드함 -> "image]"+namev+gettime()


        }

        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {

            /** Uri로 하는 방법도 있고, bitmap으로 하는 방법도 있다.*/
            uri = data.getData();

            Glide.with(this).load(uri).centerCrop().into(profilephoto);
//            Toast.makeText(this, "request code 1000", Toast.LENGTH_SHORT).show();
            //서버로 파일 업로드함 -> "image]"+namev+gettime()


        }
    }

    private void save() {

        if (uri == null) {
            Toast.makeText(this, "프로필 사진을 등록해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            if (profilepr.getText().toString().length() > 18) {
                Toast.makeText(this, "상태메세지는 18자 이내로 작성해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                uploadImage();
                update_profile_image_url_and_text_in_DB();

                SharedPreferences sharedPreferences = getSharedPreferences(id, MODE_PRIVATE); // 로그인한 id 저장소를 불러오기, 이 때 불려와지는 friend list는 각각 다름
                SharedPreferences.Editor editor = sharedPreferences.edit();
                uri_String = uri.toString();
                editor.putString("profilephoto", uri_String);
                editor.putString("profilepr", profilepr.getText().toString());
                editor.commit();

                Goto_loginsuccess = true;
                onBackPressed();
                finish();
                Toast.makeText(profileinfo.this, "프로필 정보를 저장했습니다.", Toast.LENGTH_SHORT).show();

            }
            //bytebitmap에 저장
        }
    }

    private void load() {
//        SharedPreferences sharedPreferences = getSharedPreferences(id, MODE_PRIVATE); // 로그인한 id 저장소를 불러오기, 이 때 불려와지는 friend list는 각각 다름
//        if (sharedPreferences.getString("profilephoto", null) != null) {
//            String savefile = sharedPreferences.getString("profilephoto", null);
//            uri_String = savefile;
//            uri = Uri.parse(uri_String);
//            Glide.with(this).load(uri).centerCrop().into(profilephoto);
//        }

        load_profile_image_url_and_text_from_DB();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // 컨텍스트 메뉴가 최초로 한번만 호출되는 콜백 메서드
        Log.d("test", "onCreateContextMenu");
//        getMenuInflater().inflate(R.menu.main, menu);

        menu.setHeaderTitle("프로필사진 등록방법을 선택하세요!");
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
                params.put("name", "profile_photo" + "*" + id + "*" + gettime());
                params.put("image", imageToString(bitmap));
                return params;
            }
        };

        MySingleton.getmInstance(profileinfo.this).addToRequestQue(stringRequest);
    }

    private void update_profile_image_url_and_text_in_DB() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, update_profile_image_url_and_text_in_DB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");

//                            Toast.makeText(profileinfo.this, "code :" + code + "///" + "message :" + message, Toast.LENGTH_SHORT).show();
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
                params.put("profile_image_url", "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/ImageUploadApp/uploads/" + "profile_photo" + "*" + id + "*" + gettime() + ".jpg");
                params.put("profile_text", profilepr.getText().toString());
                return params;
            }
        };
        MySingleton.getmInstance(profileinfo.this).addToRequestQue(stringRequest);
    }

    private void load_profile_image_url_and_text_from_DB() {
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
                                Glide.with(profileinfo.this).load(profile_image_url).centerCrop().into(profilephoto);
                            }
                            profilepr.setText(profile_text);

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
        MySingleton.getmInstance(profileinfo.this).addToRequestQue(stringRequest);
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

}
