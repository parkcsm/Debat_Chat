package com.idealist.www.useopencvwithcmake;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {

    public MyService mService;
    private boolean mBound;

    TextView textView;

    Button login_button;
    EditText Id, Password;
    String id, password;
    public static String login_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/regis/login.php";
    AlertDialog.Builder builder;

    public static String hostv = "13.125.191.223";
    public static String namev;
    public static String receiverv;
    private int portv = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 전에 로그인한 아이디가 있을때 자동로그인되게 하는것.
//        SharedPreferences sharedPref = getSharedPreferences("Operation", MODE_PRIVATE);
//        id = sharedPref.getString("Operation", null);
//        if (id != null){
//            Intent intent = new Intent(login.this, loginsuccess.class);
//            startActivity(intent);
//
//            /**
//             * 로그인 하자마자 소켓 연결하는 부분.
//             */
//            Intent mintent = new Intent(login.this, MyService.class);
//            mintent.putExtra("hostv",hostv);
//            mintent.putExtra("namev",id);
//            mintent.putExtra("portv",portv);
//            startService(mintent);
//        }

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        textView = findViewById(R.id.reg_txt);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, register.class));
            }
        });

        builder = new AlertDialog.Builder(login.this);
        login_button = findViewById(R.id.bn_login);
        Id = findViewById(R.id.login_id);
        Password = findViewById(R.id.login_password);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = Id.getText().toString();
                password = Password.getText().toString();

                if (id.equals("") || password.equals("")) {
                    builder.setTitle("로그인 관련 메세지");
                    displayAlert("아이디와 비밀번호를 모두 입력해주세요!");
                } else {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, login_url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                        String code = jsonObject.getString("code");
                                        if (code.equals("login_failed")) {
                                            builder.setTitle("로그인 실패");
                                            displayAlert(jsonObject.getString("message"));
                                        } else {

                                            SharedPreferences Operation = getSharedPreferences("Operation", MODE_PRIVATE);
                                            SharedPreferences.Editor OperationEdit = Operation.edit();
                                            OperationEdit.putString("Operation", id);
                                            OperationEdit.commit();

                                            Intent intent = new Intent(login.this, loginsuccess.class);
                                            startActivity(intent);

                                            /**
                                             * 로그인 하자마자 소켓 연결하는 부분.
                                             */
                                            Intent mintent = new Intent(login.this, MyService.class);
                                            mintent.putExtra("hostv", hostv);
                                            mintent.putExtra("namev", id);
                                            mintent.putExtra("portv", portv);
                                            startService(mintent);

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(login.this, "Error", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            Map<String, String> params = new HashMap<String, String>();
                            params.put("id", id);
                            params.put("password", password);

                            return params;
                        }
                    };

                    MySingleton.getmInstance(login.this).addToRequestQue(stringRequest);

                }
            }
        });

    }


    public void displayAlert(String message) {
        builder.setMessage(message);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Id.setText("");
                Password.setText("");
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinder binder = (MyService.MyBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // 예기치 않은 종료
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, mConnection, BIND_ABOVE_CLIENT);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent mintent = new Intent();
        mintent.setAction(Intent.ACTION_MAIN);
        mintent.addCategory(Intent.CATEGORY_HOME);
        startActivity(mintent);
    }
}
