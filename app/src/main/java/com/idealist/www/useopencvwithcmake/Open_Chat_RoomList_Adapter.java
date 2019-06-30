package com.idealist.www.useopencvwithcmake;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Open_Chat_RoomList_Adapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<Open_Chat_RoomList_Item> listitem;

    TextView open_chatroom_subject, open_chatroom_entitle;



    public Open_Chat_RoomList_Adapter(Context context, ArrayList<Open_Chat_RoomList_Item> listItem) {
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

        final Open_Chat_RoomList_Item item = (Open_Chat_RoomList_Item) getItem(position); //전역으로 할수도 있지만 그냥 안에다가 여러번선언

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
//            LayoutInflater inflater = mContext.getLayoutInflater(); -> 서비스에서 접근해야되기때문에 위의 LayoutInflater로 선언을 바꿨다.
            convertView = inflater.inflate(R.layout.custom_openchatroomlist, null);
        }

        open_chatroom_subject = convertView.findViewById(R.id.open_chatroom_subject);
        open_chatroom_entitle = convertView.findViewById(R.id.open_chatroom_entitle);
        open_chatroom_subject.setText(item.getOpen_Chat_Room_Subject());
        open_chatroom_entitle.setText(item.getOpen_Chat_Room_Entitle());


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(mContext, OpenChatRoom.class);
                myintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                myintent.putExtra("subject", item.getOpen_Chat_Room_Subject());
                mContext.startActivity(myintent);
            }
        });
        return convertView;
    }
}
