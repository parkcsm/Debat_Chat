package com.idealist.www.useopencvwithcmake;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class register extends AppCompatActivity {

    Button reg_bn;
    EditText Name, Email, Id, Password, ConPassword;
    String name, email, id, password, conpass;
    AlertDialog.Builder builder;
    String reg_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/regis/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        reg_bn = findViewById(R.id.bn_reg);
        Name = findViewById(R.id.reg_name);
        Email = findViewById(R.id.reg_email);
        Id = findViewById(R.id.reg_id);
        Password = findViewById(R.id.reg_password);
        ConPassword = findViewById(R.id.reg_password_check);
        builder = new AlertDialog.Builder(register.this);
        reg_bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = Name.getText().toString();
                email = Email.getText().toString();
                id = Id.getText().toString();
                password = Password.getText().toString();
                conpass = ConPassword.getText().toString();
                if (name.equals("") || email.equals("") || id.equals("") || password.equals("") || conpass.equals("")) {
                    builder.setTitle("회원가입 오류");
                    builder.setMessage("모든 빈칸을 채워주세요!");
                    displayAlert("input_error");
                } else {
                    if (!(password.equals(conpass))) {
                        builder.setTitle("회원가입 오류");
                        builder.setMessage("비밀번호-비밀번호확인은 같아야합니다.");
                        displayAlert("input_error");
                    } else {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, reg_url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {


//                                        builder.setTitle("회원가입 관련메세지");
//                                        builder.setMessage("Response :"+response);
                                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Name.setText("");
                                                Email.setText("");
                                            }
                                        });
//                                        AlertDialog alertDialog = builder.create();
//                                        alertDialog.show();

                                        try {
                                            JSONArray jsonArray = new JSONArray(response);
                                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                                            String code = jsonObject.getString("code");
                                            String message = jsonObject.getString("message");
                                            Toast.makeText(register.this, "code :"+code+"///" + "message :"+message, Toast.LENGTH_SHORT).show();
                                            builder.setTitle("회원가입 관련 메세지");
                                            builder.setMessage(message);
                                            displayAlert(code);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(register.this, "회원가입시 문제가 발생했습니다. 다시시도해주세요!", Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("name", name);
                                params.put("email", email);
                                params.put("id", id);
                                params.put("password", password);
                                return params;
                            }
                        };

                        MySingleton.getmInstance(register.this).addToRequestQue(stringRequest);
                    }
                }
            }
        });
    }

    public void displayAlert(final String code) {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (code.equals("input_error")) {
                    Password.setText("");
                    ConPassword.setText("");
                } else if (code.equals("reg_success")) {
                    finish();
                } else if (code.equals("reg_failed")) {
                    Name.setText("");
                    Email.setText("");
                    Id.setText("");
                    Password.setText("");
                    ConPassword.setText("");
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
