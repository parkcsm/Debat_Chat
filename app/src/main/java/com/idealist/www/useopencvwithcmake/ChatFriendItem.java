package com.idealist.www.useopencvwithcmake;

public class ChatFriendItem {

    String friend_id;
    private boolean ischecked;

    public ChatFriendItem(String friend_id) {
        this.friend_id = friend_id;
    }


    public String getFriend_id() { return friend_id; }

    public boolean ischecked() { return ischecked;}

    public void setIschecked(boolean ischecked){ this.ischecked = ischecked;}
}
