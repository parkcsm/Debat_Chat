package com.idealist.www.useopencvwithcmake;

public class Chat_Post_Comment_Item {
    String id;
    String text;
    String time;


    public Chat_Post_Comment_Item(String id,String text,String time) {
        this.id = id;
        this.text = text;
        this.time = time;
    }

    public String getId() {return id;}
    public String getText() {return text;}
    public String  getTime() {return time;}


}
