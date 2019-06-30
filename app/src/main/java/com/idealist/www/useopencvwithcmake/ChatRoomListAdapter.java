package com.idealist.www.useopencvwithcmake;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class ChatRoomListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ChatRoomListItem> listitem;

    ImageView chat_room_photo;
    TextView chat_room_subject, chat_room_time, chat_room_message;
    Button chat_room_number_of_member;



    public ChatRoomListAdapter(Context context, ArrayList<ChatRoomListItem> listItem) {
        mContext = context;
        listitem = listItem;
    }


    @Override
    public int getCount() {
        return listitem.size();
    }

    @Override
    public Object getItem(int position) {
        return listitem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ChatRoomListItem item = (ChatRoomListItem) getItem(position); //전역으로 할수도 있지만 그냥 안에다가 여러번선언

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
//            LayoutInflater inflater = mContext.getLayoutInflater(); -> 서비스에서 접근해야되기때문에 위의 LayoutInflater로 선언을 바꿨다.
            convertView = inflater.inflate(R.layout.custom_chatroomlist, null);
        }

        chat_room_photo = convertView.findViewById(R.id.chatroom_image);
        chat_room_subject = convertView.findViewById(R.id.chatroom_subject);
        chat_room_number_of_member = convertView.findViewById(R.id.chatroom_number_of_member);
        chat_room_time = convertView.findViewById(R.id.chatroom_time);
        chat_room_message = convertView.findViewById(R.id.chatroom_front_message);


        chat_room_subject.setText(item.getChat_room_subject());
        chat_room_number_of_member.setText(item.getChat_room_number_of_member()+"");
        chat_room_time.setText(item.getChat_room_time());
        chat_room_message.setText(item.getChat_room_message());


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(mContext, ChatRoom.class);
                myintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                myintent.putExtra("subject", item.getChat_room_subject());
                mContext.startActivity(myintent);

            }
        });

        chat_room_number_of_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, item.getChat_room_subject(), Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

}
