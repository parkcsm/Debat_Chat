package com.idealist.www.useopencvwithcmake;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BroadCast_RoomList_Adapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<BroadCast_RoomList_Item> listitem;

    TextView broad_cast_room_subject, broad_cast_room_entitle;

    public BroadCast_RoomList_Adapter(Context context, ArrayList<BroadCast_RoomList_Item> listItem) {
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

        final BroadCast_RoomList_Item item = (BroadCast_RoomList_Item) getItem(position); //전역으로 할수도 있지만 그냥 안에다가 여러번선언

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
//            LayoutInflater inflater = mContext.getLayoutInflater(); -> 서비스에서 접근해야되기때문에 위의 LayoutInflater로 선언을 바꿨다.
            convertView = inflater.inflate(R.layout.custom_openchatroomlist, null);
        }

        broad_cast_room_subject = convertView.findViewById(R.id.open_chatroom_subject); // open_chat을 만들기 위해 만들었던 레이아웃을 빌려쓰기 때문에 R.id.open_chatroom은 그대로 유지
        broad_cast_room_entitle = convertView.findViewById(R.id.open_chatroom_entitle);
        broad_cast_room_subject.setText(item.getBroad_Cast_Room_Subject());
        broad_cast_room_entitle.setText(item.getBroad_Cast_Room_Entitle());


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(mContext, Streaming.class);
                myintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                myintent.putExtra("subject_of_broadcast", item.getBroad_Cast_Room_Subject());
                myintent.putExtra("mode", "subscriber");
                mContext.startActivity(myintent);
            }
        });
        return convertView;
    }

}
