package com.idealist.www.useopencvwithcmake;

public class ChatRoomItem {

    String messege_send_id;
    String messege_text;
    String messege_time;
    String type,chatMsg;

    public ChatRoomItem(String type, String chatMsg ,String messege_send_id, String messege_text,  String messege_time){
        this.type = type;
        this.chatMsg = chatMsg;
        this.messege_send_id = messege_send_id;
        this.messege_text = messege_text;
        this.messege_time = messege_time;
    }

    public String getType(){
        return type;
    }

    public String getChatMsg(){
        return chatMsg;
    }

    public String getMessege_send_id() {
        return messege_send_id;
    }

    public String getMessege_text() {
        return messege_text;
    }

    public String getMessege_time() {
        return messege_time;
    }

}
