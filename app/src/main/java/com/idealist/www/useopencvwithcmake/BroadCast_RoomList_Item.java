package com.idealist.www.useopencvwithcmake;

public class BroadCast_RoomList_Item {


    String Broad_Cast_Room_Subject, Broad_Cast_Room_Entitle;

    public BroadCast_RoomList_Item( String Broad_Cast_Room_Subject, String Broad_Cast_Room_Entitle) {
        this.Broad_Cast_Room_Subject = Broad_Cast_Room_Subject;
        this.Broad_Cast_Room_Entitle = Broad_Cast_Room_Entitle;
    }

    public String getBroad_Cast_Room_Subject() {
        return Broad_Cast_Room_Subject;
    } //-> 일단은 구현 안할예정

    public String getBroad_Cast_Room_Entitle() {
        return Broad_Cast_Room_Entitle;
    }

}
