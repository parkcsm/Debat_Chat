package com.idealist.www.useopencvwithcmake;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.StringTokenizer;

import static android.content.ContentValues.TAG;

public class MyService extends Service {

    int mCount = 0;
    String msgv;
    static boolean ChatRoomCheck = false;

    static DatabaseHelper myDb;
    static DatabaseHelper2 myDb2;

    static Socket s;
    private Thread mThread;
    private IBinder mBinder = new MyBinder();
    private BufferedReader in;
    private PrintWriter pw;
    private Handler networkdHandler;

    public static String hostv;
    public static String namev;
    public static int portv;

    public class MyBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myDb2 = new DatabaseHelper2(this);
        hostv = intent.getStringExtra("hostv");
        namev = intent.getStringExtra("namev");
        portv = intent.getIntExtra("portv", 0);
        myDb = new DatabaseHelper(this);
        networkdHandler = new Handler();
        Toast.makeText(this, hostv + "/" + namev + "/" + portv + "/ MyService.class", Toast.LENGTH_SHORT).show();
        testServer(hostv, namev, portv);

        if (mThread == null) {
            mThread = new Thread("My Thread") {
                @Override
                public void run() {

                    try {
                        in = new BufferedReader(new InputStreamReader(MyService.s.getInputStream(), "UTF-8"));
                        while (true) {
                            final String protocol = in.readLine();
                            Log.d("Msg", protocol);
                            StringTokenizer stz = new StringTokenizer(protocol, "/");
                            final String type = stz.nextToken();
                            final String type2 = stz.nextToken();
                            final String receiver = stz.nextToken(); // 일단 변수명이 receiver로 되있지만, 오픈챗의 경우 이 변수명안에 오픈채팅방 이름(subject)가 들어간다.
                            String sender = stz.nextToken();
                            String msg = stz.nextToken();
                            msgv = sender + ": " + msg + "\n" + gettime() + "\n";

                            if (type2.equals("chat")) {
                                if (!type.equals("entry") && !type.equals("start_call") && !type.equals("finish_call")) { // 최초 소켓접속시에는 Roomlist, Msg를 SQLite에 저장하지 않는다.
                                    Log.d("Msg", "entry");
                                    boolean isInserted = myDb.insertData(SeperationAndReOrder(sender + "]" + receiver), sender, msg, gettime(), type);
                                    if (isInserted == true) {
//                                Toast.makeText(ChatRoom.this, "Data Inserted", Toast.LENGTH_SHORT).show();
                                    } else {
//                                Toast.makeText(ChatRoom.this, "Data not Inserted", Toast.LENGTH_SHORT).show();
                                    }

                                    if (myDb2.getRoomDupCheck(namev, SeperationAndReOrder(sender + "]" + receiver)).getCount() == 0) {
                                        boolean isInserted2 = myDb2.insertData(namev, SeperationAndReOrder(sender + "]" + receiver));
                                        if (isInserted2 == true) {
//                                    Toast.makeText(ChatRoomList.this, "Data Inserted", Toast.LENGTH_SHORT).show();
                                        } else {
//                                    Toast.makeText(ChatRoomList.this, "Data not Inserted", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
//                                    Toast.makeText(this, "추가안됨", Toast.LENGTH_SHORT).show();
                                    }


                                    if (SeperationAndReOrder(sender + "]" + receiver).equals(ChatRoom.room_clist_string) && (ChatRoomCheck == true)) {
                                        if (type.equals("text")) {
                                            ChatRoomItem chatroomitem = new ChatRoomItem(type, msgv, sender, msg, gettime());
                                            ChatRoom.msgPool.add(chatroomitem);
                                        } else if (type.equals("image")) {
                                            ChatRoomItem chatroomitem = new ChatRoomItem(type, msg, sender, msg, gettime());
                                            ChatRoom.msgPool.add(chatroomitem);
                                        }
                                    } else {
                                        if (!type.equals("start_call") && !type.equals("finish_call")) {
                                            String mixStr;
                                            mixStr = sender + "]" + receiver;
                                            show(sender, msg, SeperationAndReOrder(mixStr));
                                        }
                                    }
                                } else if (type.equals("start_call")) {
                                    if (ChatRoomCheck == false) {
                                        Intent mIntent1 = new Intent(MyService.this, ChatRoom.class);
                                        String mixStr = SeperationAndReOrder(sender + "]" + receiver);
                                        mIntent1.putExtra("subject", mixStr);
                                        mIntent1.putExtra("from_my_service", "from_my_service");
                                        mIntent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(mIntent1);
                                    } else {
                                        Intent mIntent2 = new Intent(MyService.this, Video_Call.class);
                                        mIntent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mIntent2.putExtra("mode", "publisher_plus_subscriber");
                                        mIntent2.putExtra("ask_if_can_accept_or_not", "ask_if_can_accept_or_not");
                                        startActivity(mIntent2);
                                    }

                                } else if (type.equals("finish_call")) {
                                    Intent intent_finish_video_call_activity = new Intent("finish_video_call_activity");
                                    sendBroadcast(intent_finish_video_call_activity);
                                }
                            } else if (type2.equals("open_chat")) {
                                if (OpenChatRoom.AreYouWatchingThisActivity.equals(receiver)) {
                                    if (type.equals("text")) {
                                        ChatRoomItem chatroomitem = new ChatRoomItem(type, msgv, sender, msg, gettime()); // 여기의 msgv는 딱히 의미없는 코드이나 일단 급해서 씀.
                                        OpenChatRoom.msgPool.add(chatroomitem);
                                    } else if (type.equals("image")) {
                                        ChatRoomItem chatroomitem = new ChatRoomItem(type, msg, sender, msg, gettime());
                                        OpenChatRoom.msgPool.add(chatroomitem);
                                    }
                                }
                            } else if (type2.equals("broad_cast")) {
                                Log.d("Msg", "broad_cast_pass");
                                Log.d("Msg", "Streaming.AreYouWatchingThisActivity :" + Streaming.AreYouWatchingThisActivity);
                                Log.d("Msg", "receiver :" + receiver);
                                if (Streaming.AreYouWatchingThisActivity.equals(receiver)) {
                                    Log.d("Msg", "equal!!");
                                    if (type.equals("text")) {
                                        ChatRoomItem chatroomitem = new ChatRoomItem(type, msgv, sender, msg, gettime()); // 여기의 msgv는 딱히 의미없는 코드이나 일단 급해서 씀.
                                        Streaming.msgPool.add(chatroomitem);
                                    } /*else if (type.equals("image")) { // Image_Chat_Function has been blocked in broad_cast_chat since appearing of broad_cast_chat
                                        ChatRoomItem chatroomitem = new ChatRoomItem(type, msg, sender, msg, gettime());
                                        OpenChatRoom.msgPool.add(chatroomitem);}*/
                                    if (type.equals("finish_streaming")) {
                                        Log.d("Msg", "finish_streaming_activity");
                                        Intent intent_finish_streaming_activity = new Intent("finish_streaming_activity");
                                        sendBroadcast(intent_finish_streaming_activity);
                                    }
                                }
                            } else {
                                Toast.makeText(MyService.this, "예외상황이니 에러메세지 띄워랑~", Toast.LENGTH_SHORT).show();
                            }

                            networkdHandler.post(new Runnable() { // setadapter를 통해 다른 사람이 나에게 전송한 메세지를 화면상에 표시해줌
                                @Override
                                public void run() {

                                    int i_end = 0;
                                    if (type.equals("text")) {
                                        i_end = 10;
                                    } else if (type.equals("image")) {
                                        i_end = 50;
                                    }

                                    for (int i = 0; ChatRoomCheck == true && i < i_end; i++) {
                                        if (type2.equals("chat")) {
                                            ChatRoom.adapter.notifyDataSetChanged();
                                        }
                                        try {
                                            sleep(50);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        i++;
                                    }

                                    for (int i = 0; OpenChatRoom.AreYouWatchingThisActivity.equals(receiver) && i < i_end; i++) {
                                        if (type2.equals("open_chat")) {
                                            OpenChatRoom.adapter.notifyDataSetChanged();
                                        }
                                        try {
                                            sleep(50);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        i++;
                                    }

                                    for (int i = 0; Streaming.AreYouWatchingThisActivity.equals(receiver) && i < i_end; i++) {
                                        if (type2.equals("broad_cast")) {
                                            Streaming.adapter.notifyDataSetChanged();
                                        }
                                        try {
                                            sleep(50);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        i++;
                                    }


                                    if (type2.equals("chat")) { // Current MsgInfo Update in RoomList!
                                        loginsuccess.load_Roomlist();
                                        loginsuccess.chatRoomListAdapter = new ChatRoomListAdapter(MyService.this, loginsuccess.RoomPool);
                                        loginsuccess.Roomlist.setAdapter(loginsuccess.chatRoomListAdapter); //뒤에 네트워크 핸들러에서는 setadapter를 통해 다른 사람이 나에게 전송한 메세지를 화면상에 표시해줌
                                    } else if (type2.equals("open_chat")) {
                                        // 오픈채팅방리스트에는 별다른 표시를하지않을 예정
                                    }
                                }
                            });

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            mThread.start();
        }
        return START_STICKY;
    }


    private boolean testServer(String hostv, String namev, int portv) {
        try {
            s = new Socket(hostv, portv); //앞에서 소켓 연결했으니!
            pw = new PrintWriter(new BufferedOutputStream(MyService.s.getOutputStream()), true);
            pw.println("entry" + "/chat/" + "admin_check" + "/" + namev + "/님이 소켓에 처음 연결되었습니다../"); // 서버 스트링버퍼에 저장. ChatResponse가 구동하면 자동으로 출력됨(왜냐하면 이미 서버 스트링버퍼에 저장했으니)


            Toast.makeText(this, "Socket Connected!" + "/ MyService.class", Toast.LENGTH_LONG).show();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
            mCount = 0;
        }
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

    public String gettime() {
        long mNow;
        Date mDate;
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd aa hh:mm:ss");

        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    private void show(String UserName, String UserMsg, String RoomName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(RoomName);
        builder.setContentText(UserName + " : " + UserMsg);

        Intent intent = new Intent(this, ChatRoom.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("subject2", RoomName);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        builder.setLargeIcon(largeIcon);
        builder.setAutoCancel(true);


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel("default", "기본 채널",
                    NotificationManager.IMPORTANCE_DEFAULT));
        }
        manager.notify(1, builder.build());
    }


}
