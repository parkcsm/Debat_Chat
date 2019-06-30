package com.idealist.www.useopencvwithcmake;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.idealist.www.useopencvwithcmake.activities.MainActivity;
import com.idealist.www.useopencvwithcmake.activities.SendActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;


public class loginsuccess extends AppCompatActivity {

    String Uri_String;
    Uri uri;
    public static String id;
    public static boolean friend_room_invitation_mode = false;

    private String load_profile_image_url_and_text_from_DB = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_member_info/photo_statement_update.php";
    private String load_friend_apply_info_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_friend_info/load_friend_apply_info.php";
    private String load_friend_info_url = "http://ec2-13-125-191-223.ap-northeast-2.compute.amazonaws.com:8080/update_friend_info/load_friend_info.php";

    TextView Button_nickname;
    LinearLayout Li_nickname;
    public static ImageView Button_profilechange;
    LinearLayout Li_logout;

    // FrameLayout with SetVisibility
    public static LinearLayout dream_music;
    public static LinearLayout dream_friend_apply_layout;
    public static LinearLayout dream_friend_list_layout;
    public static LinearLayout dream_friend_chatlist_layout;
    TextView ether_wallet;
    AlertDialog dialog;
    EditText ethereum_wallet_password_input_edit;
    Button ethereum_wallet_password_input_ok;
    Button ethereum_wallet_password_input_cancel;

    TextView debate_article_reading, unity_game, music, debate_board, debate_open_chat, debate_broad_cast;
    TextView dream_friend_apply, dream_friend, dream_chat;

    public static ListView FriendApplylist;
    public static ArrayList<ChatFriendApplyItem> FriendApplyPool;
    public static ChatFriendApplyAdapter chatFriendApplyAdapter;

    public static ListView Friendlist;
    public static ArrayList<ChatFriendItem> FriendPool;
    public static ChatFriendAdapter chatFriendAdapter;

    public static TextView friend_room_invitation_ok;
    public static ImageView make_chatroom_with_friends;

    public static ListView Roomlist;
    public static ArrayList<ChatRoomListItem> RoomPool;
    public static ChatRoomListAdapter chatRoomListAdapter;

    static DatabaseHelper myDb;
    static DatabaseHelper2 myDb2;
    static Socket s;
    private PrintWriter pw;

    /**
     * 여기아래로 음악관련 부분
     */
    String streaming_time;
    static seekbar_time_thread task;
    boolean repeat_playing;
    boolean repeat_playing_random;
    boolean repeat_playing_all;
    boolean round;
    boolean plus, minus;
    static boolean breaker;
    LinearLayout LinearLayout_time;
    boolean resume;
    CheckBox button_convert_play_pause;
    ProgressDialog simpleWaitDialog;
    String streaming_time_now;
    int song_number = 1;
    String now_song;
    static MediaPlayer mp; // 음악 재생을 위한 객체
    int pos; // 재생 멈춘 시점
    private Button activity_change;
    private ImageView btn_previous_song;
    private ImageView btn_next_song;
    TextView name_of_song;
    TextView tv_streaming_time_now;
    TextView tv_streaming_time;
    CheckBox btn_repeat;
    CheckBox btn_repeat_all;
    CheckBox btn_repeat_random;
    SeekBar sb; // 음악 재생위치를 나타내는 시크바
    static boolean isPlaying = false; // 재생중인지 확인할 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginsuccess);

        SharedPreferences sharedPref = getSharedPreferences("Operation", MODE_PRIVATE);
        id = sharedPref.getString("Operation", null);

        Button_nickname = findViewById(R.id.Button_nickname);
        Li_nickname = findViewById(R.id.Li_nickname);
        Li_logout = findViewById(R.id.Li_logout);
        Li_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences Operation = getSharedPreferences("Operation", MODE_PRIVATE);
                SharedPreferences.Editor OperationEdit = Operation.edit();
                OperationEdit.putString("Operation", null);
                OperationEdit.commit();
                logout();
                finish();
            }

        });
        Button_profilechange = findViewById(R.id.Button_profilechange);

        debate_article_reading = findViewById(R.id.debate_article_reading);
        debate_article_reading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(loginsuccess.this, Chat_Article.class);
                startActivity(myintent);
            }
        });

        unity_game = findViewById(R.id.unity_game);
        unity_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(loginsuccess.this,Sandwich_For_Unity.class);
                startActivity(myintent);
            }
        });

        music = findViewById(R.id.music);
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dream_music.setVisibility(View.VISIBLE);
                dream_friend_apply_layout.setVisibility(View.INVISIBLE);
                dream_friend_list_layout.setVisibility(View.INVISIBLE);
                dream_friend_chatlist_layout.setVisibility(View.INVISIBLE);
                friend_room_invitation_ok.setVisibility(View.INVISIBLE);
            }
        });

        debate_board = findViewById(R.id.debate_board);
        debate_board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(loginsuccess.this, Chat_Post.class);
                startActivity(myintent);
            }
        });

        ether_wallet = findViewById(R.id.ethereum_wallet);
        ether_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(loginsuccess.this);
                LayoutInflater inflater = LayoutInflater.from(loginsuccess.this);
                final View mView = inflater.inflate(R.layout.ether_wallet_password, null);
                ethereum_wallet_password_input_edit = mView.findViewById(R.id.ethereum_wallet_password_input_edit);
                ethereum_wallet_password_input_ok = mView.findViewById(R.id.ethereum_wallet_password_input_ok);
                ethereum_wallet_password_input_cancel = mView.findViewById(R.id.ethereum_wallet_password_input_cancel);
                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.setCancelable(false);
                dialog.show();

                ethereum_wallet_password_input_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, login.login_url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONArray jsonArray = new JSONArray(response);
                                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                                            String code = jsonObject.getString("code");
                                            if (code.equals("login_failed")) {
                                                Toast.makeText(loginsuccess.this, "비밀번호를 정확히 입력해주세요.", Toast.LENGTH_SHORT).show();
                                            } else{
                                                Intent myintent = new Intent(loginsuccess.this, MainActivity.class);
                                                startActivity(myintent);
                                                dialog.dismiss();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(loginsuccess.this, "Error", Toast.LENGTH_SHORT).show();
                                error.printStackTrace();

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {

                                Map<String, String> params = new HashMap<String, String>();
                                params.put("id", id);
                                params.put("password", ethereum_wallet_password_input_edit.getText().toString());

                                return params;
                            }
                        };

                        MySingleton.getmInstance(loginsuccess.this).addToRequestQue(stringRequest);
                    }
                });

                ethereum_wallet_password_input_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });




            }
        });


        debate_open_chat = findViewById(R.id.debate_open_chat);
        debate_open_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(loginsuccess.this, Open_Chat_RoomList.class);
                startActivity(myintent);
            }
        });

        debate_broad_cast = findViewById(R.id.debate_broad_cast);
        debate_broad_cast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(loginsuccess.this, BroadCast_RoomList.class);
                startActivity(myintent);
            }
        });

        dream_friend_apply = findViewById(R.id.dream_friend_apply);
        dream_friend = findViewById(R.id.dream_friend);
        dream_chat = findViewById(R.id.dream_chat);

        dream_music = findViewById(R.id.dream_music);
        dream_friend_apply_layout = findViewById(R.id.dream_friend_apply_layout);
        dream_friend_list_layout = findViewById(R.id.dream_friend_list_layout);
        dream_friend_chatlist_layout = findViewById(R.id.dream_friend_chatlist_layout);
        make_chatroom_with_friends = findViewById(R.id.make_chatroom_with_friends);

        make_chatroom_with_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dream_friend_apply_layout.setVisibility(View.INVISIBLE);
                dream_friend_list_layout.setVisibility(View.VISIBLE);
                dream_friend_chatlist_layout.setVisibility(View.INVISIBLE);
                friend_room_invitation_ok.setVisibility(View.VISIBLE);
                friend_room_invitation_mode = true;
                chatFriendAdapter.notifyDataSetChanged();
                Toast.makeText(loginsuccess.this, "채팅방에 초대할 친구를 선택하고 확인버튼을 눌러주세요!", Toast.LENGTH_SHORT).show();
            }
        });

        friend_room_invitation_ok = findViewById(R.id.friend_room_invitation_ok);

        friend_room_invitation_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mixedRoomFullSubject = id + "]";
                for (int i = 0; i < FriendPool.size(); i++) {
                    if (FriendPool.get(i).ischecked()) {
                        mixedRoomFullSubject += FriendPool.get(i).getFriend_id() + "]";
                    }
                }

                String final_subject = SeperationAndReOrder(mixedRoomFullSubject);

                Intent myintent = new Intent(loginsuccess.this, ChatRoom.class);
                myintent.putExtra("subject", final_subject);
                startActivity(myintent);

                Toast.makeText(loginsuccess.this, "1:1채팅을 시작했습니다.", Toast.LENGTH_SHORT).show();

                friend_room_invitation_mode = false;
                chatFriendAdapter.notifyDataSetChanged();

                dream_friend_apply_layout.setVisibility(View.INVISIBLE);
                dream_friend_list_layout.setVisibility(View.INVISIBLE);
                dream_friend_chatlist_layout.setVisibility(View.VISIBLE);
            }
        });

        // 꿈친추를 FrameLayout의 기본화면으로 설정
        dream_friend_apply_layout.setVisibility(View.VISIBLE);
        dream_friend_list_layout.setVisibility(View.INVISIBLE);
        dream_friend_chatlist_layout.setVisibility(View.INVISIBLE);


        Button_nickname.setText(id);

        Button_profilechange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(loginsuccess.this, profileinfo.class);
                startActivity(myintent);
            }
        });

        dream_friend_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dream_music.setVisibility(View.INVISIBLE);
                dream_friend_apply_layout.setVisibility(View.VISIBLE);
                dream_friend_list_layout.setVisibility(View.INVISIBLE);
                dream_friend_chatlist_layout.setVisibility(View.INVISIBLE);
                friend_room_invitation_ok.setVisibility(View.INVISIBLE);
                friend_room_invitation_mode = false;
                chatFriendAdapter.notifyDataSetChanged();
            }
        });

        dream_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dream_music.setVisibility(View.INVISIBLE);
                dream_friend_apply_layout.setVisibility(View.INVISIBLE);
                dream_friend_list_layout.setVisibility(View.VISIBLE);
                dream_friend_chatlist_layout.setVisibility(View.INVISIBLE);
                friend_room_invitation_ok.setVisibility(View.INVISIBLE);
                friend_room_invitation_mode = false;
                chatFriendAdapter.notifyDataSetChanged();
            }
        });

        dream_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dream_music.setVisibility(View.INVISIBLE);
                dream_friend_apply_layout.setVisibility(View.INVISIBLE);
                dream_friend_list_layout.setVisibility(View.INVISIBLE);
                dream_friend_chatlist_layout.setVisibility(View.VISIBLE);
                friend_room_invitation_ok.setVisibility(View.INVISIBLE);
                friend_room_invitation_mode = false;
                chatFriendAdapter.notifyDataSetChanged();
            }
        });


        myDb = new DatabaseHelper(this);
        myDb2 = new DatabaseHelper2(this);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        FriendApplylist = findViewById(R.id.dream_friend_apply_listview);
        Friendlist = findViewById(R.id.dream_friend_listview);
        Roomlist = findViewById(R.id.dream_friend_chat_listview);


        try {
            pw = new PrintWriter(new BufferedOutputStream(MyService.s.getOutputStream()), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FriendApplyPool = new ArrayList<>();
        chatFriendApplyAdapter = new ChatFriendApplyAdapter(this, FriendApplyPool);
        FriendApplylist.setAdapter(chatFriendApplyAdapter);

        FriendPool = new ArrayList<>();
        chatFriendAdapter = new ChatFriendAdapter(this, FriendPool);
        Friendlist.setAdapter(chatFriendAdapter);

        RoomPool = new ArrayList<>();
        chatRoomListAdapter = new ChatRoomListAdapter(this, RoomPool);
        Roomlist.setAdapter(chatRoomListAdapter); //뒤에 네트워크 핸들러에서는 setadapter를 통해 다른 사람이 나에게 전송한 메세지를 화면상에 표시해줌

        /**여기부터 음악부분*/

        btn_repeat = findViewById(R.id.repeat_playing);
        btn_repeat_random = findViewById(R.id.repeat_playing_random);
        btn_repeat_all = findViewById(R.id.repeat_playing_all);
        btn_next_song = findViewById(R.id.next_song);
        btn_previous_song = findViewById(R.id.previous_song);
        name_of_song = findViewById(R.id.name_of_song);
        tv_streaming_time = findViewById(R.id.tv_streaming_time);
        tv_streaming_time_now = findViewById(R.id.tv_streaming_time_now);
        button_convert_play_pause = findViewById(R.id.button_convert_play_pause);
        LinearLayout_time = findViewById(R.id.LinearLayout_time);
        sb = findViewById(R.id.seek_bar);

        /**노래 선택하기전에 숨기는 것*/
        btn_repeat_random.setVisibility(View.INVISIBLE);
        btn_repeat.setVisibility(View.INVISIBLE);
        btn_repeat_all.setVisibility(View.INVISIBLE);
        sb.setVisibility(View.GONE);
        LinearLayout_time.setVisibility(View.GONE);
        btn_previous_song.setVisibility(View.INVISIBLE);
        btn_next_song.setVisibility(View.INVISIBLE);

        button_convert_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    if (pos > 0) {//이제 시작은 다 여기서
                        /**재개*/
                        mp.seekTo(pos); // 일시정지 시점으로 이동
                        mp.start(); // 시작
                    } else {
                        /** 처음 재생*/
                        /** 노래 선택하고 보여주는 것*/
                        if (repeat_playing_random == true) {
                            song_number = (int) ((Math.random() * 9) + 1);
                        } else {
                            song_number = 1;
                        }
                        btn_repeat_random.setVisibility(View.VISIBLE);
                        btn_repeat.setVisibility(View.VISIBLE);
                        btn_repeat_all.setVisibility(View.VISIBLE);
                        sb.setVisibility(View.VISIBLE);
                        LinearLayout_time.setVisibility(View.VISIBLE);
                        btn_previous_song.setVisibility(View.VISIBLE);
                        btn_next_song.setVisibility(View.VISIBLE);

                        /**노래 제목에따른 재생시간과 시크바길이 결정*/
                        PutSongName_SongStart_EndingEvent();
                        name_of_song.setText(now_song);
                        int a = mp.getDuration();  //노래 재생시간
                        sb.setMax(a); // 씨크바의 최대범위를 노래의 재생시간으로 설정
                        /**노래 시간 표시*/
                        int minute = a / 1000 / 60;//분
                        int second = a / 1000 % 60;///초
                        if (second >= 10) {
                            streaming_time = minute + ":" + second;
                        } else {
                            streaming_time = minute + ":0" + second;
                        }
                        tv_streaming_time.setText(streaming_time);

                        /**잠시만기다려주세요 메세지*/
                        /**노래재시작 + 쓰레드재시작(시크바이동 +시간표시)*/
                        song_change_Task Task = new song_change_Task();
                        Task.execute();
                    }
                } else {
                    /**일시정지 버튼눌렀을때*/
                    pos = mp.getCurrentPosition(); //현재 시간위치를 미리 받아온다.
                    mp.pause(); //일시중지
                }
            }
        });

        btn_previous_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**실행중인 노래종료 + 쓰레드종료*/
                task.cancel(true);
                breaker = true;
                isPlaying = false;
                mp.stop();
                mp.release();
                /**재생안누르고 멈춘상태로 곡만 바뀔수도 있으니까 바뀌게해야함*/
                button_convert_play_pause.setChecked(true);
                /**노래 선택*/
                /**노래제목에 따른 재생시간,시크바길이 설정*/
                minus = true;
                PutSongName_SongStart_EndingEvent();
                name_of_song.setText(now_song);
                int a = mp.getDuration(); // 노래의 재생시간 (miliSecond)
                sb.setMax(a); // 씨크바의 최대범위를 노래의 재생시간으로 설정

                /**노래 시간 설정*/
                int minute = a / 1000 / 60;//분
                int second = a / 1000 % 60;///초
                if (second >= 10) {
                    streaming_time = minute + ":" + second;
                } else {
                    streaming_time = minute + ":0" + second;
                }
                tv_streaming_time.setText(streaming_time);

                /**잠시만기다려주세요 메세지*/
                /**노래재시작 + 쓰레드재시작(시크바이동 +시간표시)*/
                song_change_Task Task = new song_change_Task();
                Task.execute();

            }
        });
        btn_next_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**실행중인 노래종료 + 쓰레드종료*/
                task.cancel(true);
                breaker = true;
                isPlaying = false;
                mp.stop();
                mp.release();
                /**재생안누르고 멈춘상태로 곡만 바뀔수도 있으니까 바뀌게해야함*/
                button_convert_play_pause.setChecked(true);
                /**노래 선택*/

                /**노래제목에 따른 재생시간,시크바길이 설정*/
                plus = true;
                PutSongName_SongStart_EndingEvent();
                name_of_song.setText(now_song);
                int a = mp.getDuration(); // 노래의 재생시간 (miliSecond)
                sb.setMax(a); // 씨크바의 최대범위를 노래의 재생시간으로 설정

                /**노래 시간 설정*/
                int minute = a / 1000 / 60;//분
                int second = a / 1000 % 60;///초
                if (second >= 10) {
                    streaming_time = minute + ":" + second;
                } else {
                    streaming_time = minute + ":0" + second;
                }
                tv_streaming_time.setText(streaming_time);

                /**잠시만기다려주세요 메세지*/
                /**노래재시작 + 쓰레드재시작(시크바이동 +시간표시)*/
                song_change_Task Task = new song_change_Task();
                Task.execute();
            }

        });

        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_repeat_random.setChecked(false);
                repeat_playing_random = false;
                btn_repeat_all.setChecked(false);
                repeat_playing_all = false;
                if (repeat_playing == true) {
                    mp.setLooping(false);
                    repeat_playing = false;
                    Toast.makeText(loginsuccess.this, "한곡 반복 비활성화", Toast.LENGTH_SHORT).show();
                } else {
                    mp.setLooping(true);
                    repeat_playing = true;
                    Toast.makeText(loginsuccess.this, "한곡 반복 활성화", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_repeat_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_repeat_random.setChecked(false);
                repeat_playing_random = false;
                btn_repeat.setChecked(false);
                repeat_playing = false;
                mp.setLooping(false);
                if (repeat_playing_all == true) {
                    repeat_playing_all = false;
                    Toast.makeText(loginsuccess.this, "전체 반복 비활성화", Toast.LENGTH_SHORT).show();
                } else {
                    repeat_playing_all = true;
                    Toast.makeText(loginsuccess.this, "전체 반복 활성화", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btn_repeat_random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_repeat.setChecked(false);
                repeat_playing = false;
                mp.setLooping(false);
                btn_repeat_all.setChecked(false);
                repeat_playing_all = false;
                if (repeat_playing_random == true) {
                    repeat_playing_random = false;
                    Toast.makeText(loginsuccess.this, "임의 반복 비활성화", Toast.LENGTH_SHORT).show();
                } else {
                    repeat_playing_random = true;
                    Toast.makeText(loginsuccess.this, "임의 반복 활성화", Toast.LENGTH_SHORT).show();
                }
            }
        });


        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getMax() == seekBar.getProgress()) { // 한곡반복누르면 해당되지 않음, 전체반복
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mp.pause();
            }

            /**
             *  SeekBar 컨트롤 -> 노래 위치변경, 노래 시작,중지-> 쓰레드가 노래위치에따라 현재시간, SeekBar위치 변경
             * **/
            public void onStopTrackingTouch(SeekBar seekBar) {
                int ttt = seekBar.getProgress();
                if (ttt >= seekBar.getMax()) {
                    mp.seekTo(seekBar.getMax());
                } else {
                    if (button_convert_play_pause.isChecked() == true) {
                        mp.seekTo(ttt);
                        mp.start();
                    } else if (button_convert_play_pause.isChecked() == false) {
                        mp.seekTo(ttt);
                        pos = ttt;
                        mp.pause();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * 프로필 이미지 받아오는 부분
         */
        load_profile_image_url_and_text_from_DB();

        /**
         * 챗룸 Resume부분
         */
        load_FriendApplylist();
        chatFriendApplyAdapter = new ChatFriendApplyAdapter(this, FriendApplyPool);
        FriendApplylist.setAdapter(chatFriendApplyAdapter); //뒤에 네트워크 핸들러에서는 setadapter를 통해 다른 사람이 나에게 전송한 메세지를 화면상에 표시해줌

        load_Friendlist();
        chatFriendAdapter = new ChatFriendAdapter(this, FriendPool);
        Friendlist.setAdapter(chatFriendAdapter); //뒤에 네트워크 핸들러에서는 setadapter를 통해 다른 사람이 나에게 전송한 메세지를 화면상에 표시해줌

        load_Roomlist();
        chatRoomListAdapter = new ChatRoomListAdapter(this, RoomPool);
        Roomlist.setAdapter(chatRoomListAdapter); //뒤에 네트워크 핸들러에서는 setadapter를 통해 다른 사람이 나에게 전송한 메세지를 화면상에 표시해줌


    }

    private void load_FriendApplylist() {
        FriendApplyPool = new ArrayList<>();
        viewAllFriendApply();
    }

    private void viewAllFriendApply() {
        load_friend_apply_info(loginsuccess.id, new CallBack() { // 인터페이스는 딱히 의미가 없다. 단지 코드를 눈에띄게 정리하고 정렬하는 용도로 사용한다. 앞으로 적극 사용하자.
            @Override
            public void getJsonArray(JSONArray jsonArray) throws JSONException {
                /**
                 * 발생 문제 : For{Msg.add} =(미연동)> Adapter(Msg) [ Msg.size()가 계속 0으로 나옴 ]
                 * For문안에다가 Adapter넣어서 문제 일시적으로 해결 (왜 For과 Array.add가 궁합이 안맞는지는 근본적인은 못참음)
                 */
                for (int i = 0; jsonArray.getJSONObject(i) != null; i++) {
//                                Toast.makeText(OpenChatRoom.this, i + "통과", Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String code = jsonObject.getString("code");
                    String Frequest_Sender = jsonObject.getString("Frequest_Sender");

//                           Toast.makeText(OpenChatRoom.this, code + "//" + RoomName + "//" + Sender + "//" + Msg + "//" + Regdate + "//" + Type, Toast.LENGTH_SHORT).show();

                    ChatFriendApplyItem chatfriendapplyitem = new ChatFriendApplyItem(Frequest_Sender);
                    FriendApplyPool.add(0, chatfriendapplyitem);

                    chatFriendApplyAdapter = new ChatFriendApplyAdapter(loginsuccess.this, FriendApplyPool); //--1
                    FriendApplylist.setAdapter(chatFriendApplyAdapter);  //--2
//                  Toast.makeText(OpenChatRoom.this, msgPool.size()+"", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void load_Friendlist() {
        FriendPool = new ArrayList();
        viewAllFriend();
    }

    private void viewAllFriend() {
        load_friend_info(loginsuccess.id, new CallBack() { // 인터페이스는 딱히 의미가 없다. 단지 코드를 눈에띄게 정리하고 정렬하는 용도로 사용한다. 앞으로 적극 사용하자.
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
                    FriendPool.add(0, chatfrienditem);

                    chatFriendAdapter = new ChatFriendAdapter(loginsuccess.this, FriendPool); //--1
                    Friendlist.setAdapter(chatFriendAdapter);  //--2
                }
            }
        });
    }

    public static void load_Roomlist() {
        RoomPool = new ArrayList<>();
        viewAllRoom();
    }

    public static void viewAllRoom() {
        Cursor res = myDb2.getRoomData(id);
        if (res.getCount() == 0) {
//                    showMessage("Error", "Nothing found");
            // show message
        } else {
            while (res.moveToNext()) { // res => 채팅방리스트 , res2 => 메세지리스트
                String LatestMsgTime = "";
                String LatestMsg = "";

                Cursor res2 = myDb.getAllData(res.getString(2)); //최근메세지를 채팅방리스트의 개별아이템에 표시해주기위해, 방이름을 기준으로 MsgList db수색
                if (res2.moveToLast()) {  //보통 해당 db의 가장 마지막번째가 최근메세지이므로 해당 메세지 찾음 여기서 if/while활용 필수!!
                    // -> 신기하다! 여기서 변수 res2는 사실상 여러개의 값 Cursor(LIST)를 가리키는데
                    // -> if나 while을 활용해서 이 값을 String으로 사용했구나!, 여기서 내가 if로 사용한 것은 참 잘한것!
//Good Point!!               Cursor a = res2.moveToPosition(res2.getCount()); // 이거는 boolean이라 while이나 if안에 넣어서 값을 찾아야함!!
                    LatestMsgTime = res2.getString(4);
                    LatestMsg = res2.getString(2) + " : " + res2.getString(3);
                }
                StringTokenizer str = new StringTokenizer(res.getString(2), "]");
                int UserNum = str.countTokens();

                ChatRoomListItem chatroomlistitem = new ChatRoomListItem(res.getString(2), UserNum, LatestMsgTime, LatestMsg);
                RoomPool.add(0, chatroomlistitem);
            }
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent mintent = new Intent();
        mintent.setAction(Intent.ACTION_MAIN);
        mintent.addCategory(Intent.CATEGORY_HOME);
        startActivity(mintent);
    }

    private void logout() {
        try {
            pw.println("roomout/roomout/" + "메시지 받는 사람 없음" + "/" + id + "/" + id + "님의 채팅연결이 끊어졌습니다. ");
            MyService.s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(loginsuccess.this, MyService.class);
        stopService(intent);

        if (task != null) {
            task.cancel(true);
        }
        breaker = true;
        isPlaying = false; //쓰레드 정지
        if (mp != null) {
            mp.release(); //자원해제
        }
    }

    private void load() {
        // 프사띄워주는 부분
        SharedPreferences sharedPref = getSharedPreferences("Operation", MODE_PRIVATE);
        id = sharedPref.getString("Operation", null);
        SharedPreferences sharedPreferences = getSharedPreferences(id, MODE_PRIVATE); // 로그인한 id 저장소를 불러오기 load, 이 때 불려와지는 friend list는 각각 다름
        if (sharedPreferences.getString("profilephoto", null) != null) {
            String savefile = sharedPreferences.getString("profilephoto", null);
//            Toast.makeText(this, savefile, Toast.LENGTH_SHORT).show();
            Uri_String = savefile;
            uri = Uri.parse(Uri_String);
            Glide.with(this).load(uri).centerCrop().into(Button_profilechange);
        }
    }

    private void load_profile_image_url_and_text_from_DB() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, load_profile_image_url_and_text_from_DB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
//                            JSONObject jsonObject1 = jsonArray.getJSONObject(1);
                            String code = jsonObject.getString("code");
//                            Toast.makeText(loginsuccess.this, "code :" + code, Toast.LENGTH_SHORT).show();

                            String profile_image_url = jsonObject.getString("profile_image_url").toString();
//                            String profile_image_url1 = jsonObject1.getString("profile_image_url").toString();

//                            Toast.makeText(loginsuccess.this, profile_image_url+"//"+profile_image_url1, Toast.LENGTH_SHORT).show();
                            if (!profile_image_url.equals("default")) { // 처음에 하얀화면 뜨는거 방지
                                if (profileinfo.Goto_loginsuccess == true) {
                                    load();
                                    profileinfo.Goto_loginsuccess = false;
                                } else {
                                    Glide.with(loginsuccess.this).load(profile_image_url).centerCrop().into(Button_profilechange);
                                }
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
        MySingleton.getmInstance(loginsuccess.this).addToRequestQue(stringRequest);
    }


    @Override
    protected void onPause() {
        super.onPause();
        friend_room_invitation_mode = false;
        friend_room_invitation_ok.setVisibility(View.INVISIBLE);
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

    private void load_friend_apply_info(final String Frequest_Receiver, final CallBack onCallBack) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, load_friend_apply_info_url,
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

                Toast.makeText(loginsuccess.this, "Error", Toast.LENGTH_SHORT).show();
                error.printStackTrace();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("Frequest_Receiver", Frequest_Receiver);
                return params;
            }
        };

        MySingleton.getmInstance(loginsuccess.this).addToRequestQue(stringRequest);
    }

    private void load_friend_info(final String id, final CallBack onCallBack) {
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

                Toast.makeText(loginsuccess.this, "Error", Toast.LENGTH_SHORT).show();
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

        MySingleton.getmInstance(loginsuccess.this).addToRequestQue(stringRequest);
    }

    public interface CallBack {
        void getJsonArray(JSONArray jsonArray) throws JSONException;
    }

    public static String gettime(){
        long mNow;
        Date mDate;
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd aa hh:mm:ss");

        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        logout();
    }

    private void PutSongName_SongStart_EndingEvent() {

        if (plus == true) {
            if (repeat_playing_random == true) {
                int randomValue = (int) (Math.random() * 9) + 1;
                song_number = randomValue;
                random_song_name();
            } else {
                song_number++;
                if (song_number > 9) {
                    if (repeat_playing_all == true) {
                        song_number = 1;
                    } else {
                        song_number = 9;
                    }
                }
                plus = false;
            }
        }

        if (minus == true) {
            if (repeat_playing_random == true) {
                int randomValue = (int) (Math.random() * 9) + 1;
                song_number = randomValue;
                random_song_name();
            } else {
                song_number--;
                if (song_number < 1) {
                    if (repeat_playing_all == true) {
                        song_number = 9;
                    } else {
                        song_number = 1;
                    }
                }
                minus = false;
            }
        }


        switch (song_number) {
            case 1:
                now_song = "1.Collapsed_tower";
                mp = MediaPlayer.create(getApplicationContext(), R.raw.collapsed_tower);
                mp.start();
                if (round == true) {
                    button_convert_play_pause.setChecked(false);
                    pos = 1;
                    mp.pause();
                    round = false;
                }
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == false) {

                            song_number++;
                            now_song = "2.City_of_twilight";


                            if (song_number > 9) {
                                button_convert_play_pause.setChecked(false);
                                pos = 1;
                                mp.pause();
                                song_number = 1;
                            }

//                            button_convert_play_pause.setChecked(false);
//                            pos = 1;
//                            mp.pause();
                        }
                        if (repeat_playing == false && repeat_playing_all == true && repeat_playing_random == false) {
                            song_number++;
                            now_song = "2.City_of_twilight";

                            if (song_number > 9) {
                                song_number = 1;
                            }
                        }

                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == true) {
                            int randomValue = (int) (Math.random() * 9) + 1;
                            song_number = randomValue;
                            random_song_name();
                        }
                        endding_event();
                    }
                });
                break;
            case 2:
                now_song = "2.City_of_twilight";
                mp = MediaPlayer.create(getApplicationContext(), R.raw.city_of_twilight);

                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == false) {

                            song_number++;
                            now_song = "3.Velvetroom";

                            if (song_number > 9) {
                                button_convert_play_pause.setChecked(false);
                                pos = 1;
                                mp.pause();
                                song_number = 1;
                            }

//                            button_convert_play_pause.setChecked(false);
//                            pos = 1;
//                            mp.pause();
                        }
                        if (repeat_playing == false && repeat_playing_all == true && repeat_playing_random == false) {
                            song_number++;
                            now_song = "3.Velvetroom";

                            if (song_number > 9) {
                                song_number = 1;
                            }
                        }
                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == true) {
                            int randomValue = (int) (Math.random() * 9) + 1;
                            song_number = randomValue;
                            random_song_name();
                        }
                        endding_event();
                    }
                });
                break;
            case 3:
                now_song = "3.Velvetroom";
                mp = MediaPlayer.create(getApplicationContext(), R.raw.velvetroom);

                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == false) {
                            song_number++;
                            now_song = "4.Crymore";


                            if (song_number > 9) {
                                button_convert_play_pause.setChecked(false);
                                pos = 1;
                                mp.pause();
                                song_number = 1;
                            }

//                            button_convert_play_pause.setChecked(false);
//                            pos = 1;
//                            mp.pause();
                        }
                        if (repeat_playing == false && repeat_playing_all == true && repeat_playing_random == false) {
                            song_number++;
                            now_song = "4.Crymore";

                            if (song_number > 9) {
                                song_number = 1;
                            }
                        }
                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == true) {
                            int randomValue = (int) (Math.random() * 9) + 1;
                            song_number = randomValue;
                            random_song_name();
                        }
                        endding_event();
                    }
                });
                break;
            case 4:
                now_song = "4.Crymore";
                mp = MediaPlayer.create(getApplicationContext(), R.raw.crymore);
                mp.start();

                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == false) {

                            song_number++;
                            now_song = "5.dream_music";

                            if (song_number > 9) {
                                button_convert_play_pause.setChecked(false);
                                pos = 1;
                                mp.pause();
                                song_number = 1;
                            }

//                            button_convert_play_pause.setChecked(false);
//                            pos = 1;
//                            mp.pause();
                        }
                        if (repeat_playing == false && repeat_playing_all == true && repeat_playing_random == false) {
                            song_number++;
                            now_song = "5.dream_music";

                            if (song_number > 9) {
                                song_number = 1;
                            }
                        }
                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == true) {
                            int randomValue = (int) (Math.random() * 9) + 1;
                            song_number = randomValue;
                            random_song_name();
                        }
                        endding_event();
                    }
                });
                break;
            case 5:
                now_song = "5.dream_music";
                mp = MediaPlayer.create(getApplicationContext(), R.raw.dream_music);


                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == false) {

                            song_number++;
                            now_song = "6.Late_autumn";

                            if (song_number > 9) {
                                button_convert_play_pause.setChecked(false);
                                pos = 1;
                                mp.pause();
                                song_number = 1;
                            }

//                            button_convert_play_pause.setChecked(false);
//                            pos = 1;
//                            mp.pause();
                        }
                        if (repeat_playing == false && repeat_playing_all == true && repeat_playing_random == false) {
                            song_number++;
                            now_song = "6.Late_autumn";

                            if (song_number > 9) {
                                song_number = 1;
                            }
                        }
                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == true) {
                            int randomValue = (int) (Math.random() * 9) + 1;
                            song_number = randomValue;
                            random_song_name();
                        }
                        endding_event();
                    }
                });
                break;
            case 6:
                now_song = "6.Late_autumn";
                mp = MediaPlayer.create(getApplicationContext(), R.raw.late_autumn);

                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == false) {

                            song_number++;
                            now_song = "7.Moment";

                            if (song_number > 9) {
                                button_convert_play_pause.setChecked(false);
                                pos = 1;
                                mp.pause();
                                song_number = 1;
                            }

//                            button_convert_play_pause.setChecked(false);
//                            pos = 1;
//                            mp.pause();
                        }
                        if (repeat_playing == false && repeat_playing_all == true && repeat_playing_random == false) {
                            song_number++;
                            now_song = "7.Moment";

                            if (song_number > 9) {
                                song_number = 1;
                            }
                        }
                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == true) {
                            int randomValue = (int) (Math.random() * 9) + 1;
                            song_number = randomValue;
                            random_song_name();
                        }
                        endding_event();
                    }
                });
                break;
            case 7:
                now_song = "7.Moment";
                mp = MediaPlayer.create(getApplicationContext(), R.raw.moment);

                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == false) {

                            song_number++;
                            now_song = "8.Utakata";

                            if (song_number > 9) {
                                button_convert_play_pause.setChecked(false);
                                pos = 1;
                                mp.pause();
                                song_number = 1;
                            }

//                            button_convert_play_pause.setChecked(false);
//                            pos = 1;
//                            mp.pause();
                        }
                        if (repeat_playing == false && repeat_playing_all == true && repeat_playing_random == false) {
                            song_number++;
                            now_song = "8.Utakata";

                            if (song_number > 9) {
                                song_number = 1;
                            }
                        }
                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == true) {
                            int randomValue = (int) (Math.random() * 9) + 1;
                            song_number = randomValue;
                            random_song_name();
                        }
                        endding_event();
                    }
                });
                break;
            case 8:
                now_song = "8.Utakata";
                mp = MediaPlayer.create(getApplicationContext(), R.raw.utakata);

                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == false) {

                            song_number++;
                            now_song = "9.Apparition";


                            if (song_number > 9) {
                                button_convert_play_pause.setChecked(false);
                                pos = 1;
                                mp.pause();
                                song_number = 1;
                            }

//                            button_convert_play_pause.setChecked(false);
//                            pos = 1;
//                            mp.pause();
                        }
                        if (repeat_playing == false && repeat_playing_all == true && repeat_playing_random == false) {
                            song_number++;
                            now_song = "9.Apparition";

                            if (song_number > 9) {
                                song_number = 1;
                            }
                        }
                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == true) {
                            int randomValue = (int) (Math.random() * 9) + 1;
                            song_number = randomValue;
                            random_song_name();
                        }
                        endding_event();
                    }
                });
                break;
            case 9:
                now_song = "9.Apparition";
                mp = MediaPlayer.create(getApplicationContext(), R.raw.apparition);

                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == false) {
                            song_number++;
                            now_song = "1.Collapsed_tower";

                            if (song_number > 9) {
                                button_convert_play_pause.setChecked(false);
                                pos = 1;
                                mp.pause();
                                song_number = 1;
                                round = true;
                            }
//                            button_convert_play_pause.setChecked(false);
//                            pos = 1;
//                            mp.pause();
                        }
                        if (repeat_playing == false && repeat_playing_all == true && repeat_playing_random == false) {
                            song_number++;
                            now_song = "1.Collapsed_tower";

                            if (song_number > 9) {
                                song_number = 1;
                            }
                        }
                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == true) {
                            int randomValue = (int) (Math.random() * 9) + 1;
                            song_number = randomValue;
                            random_song_name();
                        }
                        endding_event();
                    }
                });
                break;
            default:
                now_song = "1.Moment";
                mp = MediaPlayer.create(getApplicationContext(), R.raw.moment);
                break;
        }
        if (repeat_playing == true) {
            mp.setLooping(true);
        } else {
            mp.setLooping(false);
        }

    }


    class song_change_Task extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            simpleWaitDialog = ProgressDialog.show(loginsuccess.this, "잠시만 기다려주세요", "노래 변경중...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            simpleWaitDialog.dismiss();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            /**시크바이동 + 시간표시 쓰레드 시작*/
            breaker = false;
            isPlaying = true; // 씨크바 쓰레드 반복하도록
            task = new seekbar_time_thread();
            task.execute();
            /**노래 바꾸고 (다시) 시작*/
        }
    }

    class seekbar_time_thread extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            while (isPlaying) {
                if (breaker) {
                    break;
                }
                if (mp != null) {
                    sb.setProgress(mp.getCurrentPosition());
                    if (breaker == true || mp == null) {
                        break;
                    }

                    int time_now = mp.getCurrentPosition(); // 노래의 재생시간 (miliSecond)

                    if (breaker == true || mp == null) {
                        break;
                    }
                    if (mp.getCurrentPosition() <= mp.getDuration()) {
                        //시간세팅
                        int minute = time_now / 1000 / 60;//분
                        int second = time_now / 1000 % 60;///초
                        if (second >= 10) {
                            streaming_time_now = minute + ":" + second;
                        } else {
                            streaming_time_now = minute + ":0" + second;
                        }
                        publishProgress(streaming_time_now);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            tv_streaming_time_now.setText(values[0]);
        }
    }

    public void endding_event() {
        //repeat_playing_all == true || repeat_playing_random == true
        if (repeat_playing == false) {
            /**실행중인 노래종료 + 쓰레드종료*/
            task.cancel(true);
            breaker = true;
            isPlaying = false;
            mp.stop();
            mp.release();
            /**재생안누르고 멈춘상태로 곡만 바뀔수도 있으니까 바뀌게해야함*/
            button_convert_play_pause.setChecked(true);
            /**노래 선택*/
            /**노래제목에 따른 재생시간,시크바길이 설정*/
            name_of_song.setText(now_song);

            PutSongName_SongStart_EndingEvent();
            int a = mp.getDuration(); // 노래의 재생시간 (miliSecond)
            sb.setMax(a); // 씨크바의 최대범위를 노래의 재생시간으로 설정

            /**노래 시간 설정*/
            int minute = a / 1000 / 60;//분
            int second = a / 1000 % 60;///초
            if (second >= 10) {
                streaming_time = minute + ":" + second;
            } else {
                streaming_time = minute + ":0" + second;
            }
            tv_streaming_time.setText(streaming_time);
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    song_change_Task Task = new song_change_Task();
                    Task.execute();
                }
            });

            //이상하게 다음곡 이전곡 넘어가기는 바로바로 되는데,
            //여기서 Async를 쓰게되면 다음으로 잘 안넘어감

        }

    }

    public void random_song_name() {
        if (song_number == 1) {
            now_song = "1.Collapsed_tower";
        } else if (song_number == 2) {
            now_song = "2.City_of_twilight";
        } else if (song_number == 3) {
            now_song = "3.Velvetroom";
        } else if (song_number == 4) {
            now_song = "4.Crymore";
        } else if (song_number == 5) {
            now_song = "5.dream_music";
        } else if (song_number == 6) {
            now_song = "6.Late_autumn";

        } else if (song_number == 7) {
            now_song = "7.Moment";
        } else if (song_number == 8) {
            now_song = "8.Utakata";
        } else if (song_number == 9) {
            now_song = "9.Apparition";

        }

    }
}
