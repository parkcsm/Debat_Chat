package com.idealist.www.useopencvwithcmake;

public class ChatRoomListItem {

    String chat_room_photo, chat_room_subject, chat_room_time, chat_room_message;
    int chat_room_number_of_member;

    public ChatRoomListItem(/*String chat_room_photo,*/ String chat_room_subject, int chat_room_number_of_member, String chat_room_time, String chat_room_message) {
       /* this.chat_room_photo = chat_room_photo;*/ //-> 일단은 구현 안할예정
        this.chat_room_subject = chat_room_subject;
        this.chat_room_number_of_member = chat_room_number_of_member;
        this.chat_room_time = chat_room_time;
        this.chat_room_message = chat_room_message;
    }

    public String getChat_room_photo() {
        return chat_room_photo;
    } //-> 일단은 구현 안할예정

    public String getChat_room_subject() {
        return chat_room_subject;
    }


    public int getChat_room_number_of_member() {
        return chat_room_number_of_member;
    }


    public String getChat_room_time() {
        return chat_room_time;
    }


    public String getChat_room_message() {
        return chat_room_message;
    }



}
