package com.idealist.www.useopencvwithcmake;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 *
 * 현재 안쓰는 엑티비티
 *
 */

public class ChatRoomList extends AppCompatActivity {


    static DatabaseHelper myDb;
    static DatabaseHelper2 myDb2;
    String hostv;
    static String namev;
    String receiverv;
    String mixStr;
    int portv;
    TextView Nick_Name;
    public static ListView Roomlist;
    public static ArrayList<ChatRoomListItem> RoomPool;
    public static ChatRoomListAdapter chatRoomListAdapter;

    static Socket s;
    private PrintWriter pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_list);
        myDb = new DatabaseHelper(this);
        myDb2 = new DatabaseHelper2(this);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        hostv = bundle.getString("host");
        namev = bundle.getString("name");
        portv = bundle.getInt("port");
        receiverv = bundle.getString("receiver");
        Nick_Name = findViewById(R.id.Nick_Name);
        Roomlist = findViewById(R.id.chat_room_list);
        Nick_Name.setText(namev);

        try {
            pw = new PrintWriter(new BufferedOutputStream(MyService.s.getOutputStream()), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        testServer(hostv,namev,portv);


        SharedPreferences sharedPref = getSharedPreferences("admin", MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEdit = sharedPref.edit();
        sharedPrefEdit.putString("host", hostv);
        sharedPrefEdit.putString("name", namev);
        sharedPrefEdit.putInt("port", portv);

        sharedPrefEdit.commit();

        RoomPool = new ArrayList<>();
        ChatRoomList.chatRoomListAdapter = new ChatRoomListAdapter(this, ChatRoomList.RoomPool);
        ChatRoomList.Roomlist.setAdapter(ChatRoomList.chatRoomListAdapter); //뒤에 네트워크 핸들러에서는 setadapter를 통해 다른 사람이 나에게 전송한 메세지를 화면상에 표시해줌


    }


//    public String gettime() {
//        long mNow;
//        Date mDate;
//        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd aa hh:mm:ss");
//
//        mNow = System.currentTimeMillis();
//        mDate = new Date(mNow);
//        return mFormat.format(mDate);
//    }
//
//    private boolean testServer(String hostv, String namev, int portv) {
//        try {
//            s = new Socket(hostv, portv);
//
//            pw = new PrintWriter(new BufferedOutputStream(s.getOutputStream()), true);
//            pw.println("talk" + "/chat/" + receiverv + "/" + namev + "/님이 서버에 접속했습니다./"); // 서버 스트링버퍼에 저장. ChatResponse가 구동하면 자동으로 출력됨(왜냐하면 이미 서버 스트링버퍼에 저장했으니)
//            Toast.makeText(this, "사용자:" + namev + " 서버연결성공", Toast.LENGTH_SHORT).show();
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
//            return false;
//        }
//    }

    private static String SeperationAndReOrder(String string) {

        StringTokenizer str = new StringTokenizer(string, "]");
        int UserNum = str.countTokens();
        String[] test = new String[UserNum];
        for (int i = 0; i < UserNum; i++) {
            test[i] = str.nextToken();
            // System.out.println("test[" + i + "] = " + test[i]);
        }

        String sum;
        sum = SplitAndMigrateToString(test, UserNum);
        System.out.println("initial form : " + string);
        System.out.println("rearranged form : " + sum);
        return sum;
    }

    private static String SplitAndMigrateToString(String[] test, int ArraySize) {
        Arrays.sort(test);

        String sum = "";
        for (int i = 0; i < ArraySize; i++) {
            sum += test[i] + "]";
        }
        return sum;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mixStr = namev + "]" + receiverv;
//        Toast.makeText(this, myDb2.getRoomDupCheck(namev, SeperationAndReOrder(mixStr)).getCount() + "", Toast.LENGTH_SHORT).show();
        if (myDb2.getRoomDupCheck(namev, SeperationAndReOrder(mixStr)).getCount() == 0) {
            boolean isInserted = myDb2.insertData(namev, SeperationAndReOrder(mixStr));
            if (isInserted == true) {
              Toast.makeText(ChatRoomList.this, "Data Inserted", Toast.LENGTH_SHORT).show();
            } else {
              Toast.makeText(ChatRoomList.this, "Data not Inserted", Toast.LENGTH_SHORT).show();
            }
        } else {
//            Toast.makeText(this, "추가안됨", Toast.LENGTH_SHORT).show();
        }

        load();

         /*
         load();
        SharedPreferences shared_room_dupcheck = getSharedPreferences(namev+"room_dup_check",MODE_PRIVATE);
        SharedPreferences.Editor  shared_room_dupcheck_editor = shared_room_dupcheck.edit();

        if(shared_room_dupcheck.getString(SeperationAndReOrder(mixStr),null)==null) {
            // 클라내에서 자체적으로 방중복여부 검사
            ChatRoomListItem chatroomlistitem = new ChatRoomListItem(SeperationAndReOrder(mixStr), 0, gettime(), "나중에 알림으로 메세지 띄울것임");
            RoomPool.add(0, chatroomlistitem);
            // SAVE
            SharedPreferences shared_list = getSharedPreferences(namev, MODE_PRIVATE);
            SharedPreferences.Editor shared_list_editor = shared_list.edit();
            Gson gson = new Gson();
            String json = gson.toJson(RoomPool);
            shared_list_editor.putString("list", json);
            shared_list_editor.commit();

            shared_room_dupcheck_editor.putString(SeperationAndReOrder(mixStr),"exist");
            shared_room_dupcheck_editor.commit();
        }
        */

        chatRoomListAdapter = new ChatRoomListAdapter(this, RoomPool);
        Roomlist.setAdapter(chatRoomListAdapter); //뒤에 네트워크 핸들러에서는 setadapter를 통해 다른 사람이 나에게 전송한 메세지를 화면상에 표시해줌
    }

    public static void load() {
/*        SharedPreferences sharedPreferences = getSharedPreferences(namev, MODE_PRIVATE); // 로그인한 id 저장소를 불러오기, 이 때 불려와지는 friend list는 각각 다름
        Gson gson = new Gson();
        String json = sharedPreferences.getString("list", null);
        Type type = new TypeToken<ArrayList<ChatRoomListItem>>() {
        }.getType();
        RoomPool = gson.fromJson(json, type);
        if (RoomPool == null) {
            RoomPool = new ArrayList<>();
        }*/

        RoomPool = new ArrayList<>();
        viewAll();
    }

    public static void viewAll() {
        Cursor res = myDb2.getRoomData(namev);
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
        super.onBackPressed();
        try {
            pw.println("roomout/roomout/" + "메시지 받는 사람 없음" + "/" + namev + "/" + namev + "님의 채팅연결이 끊어졌습니다. ");
            MyService.s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        mBound = false;

        Intent intent = new Intent(ChatRoomList.this, MyService.class);
        stopService(intent);
    }

}
