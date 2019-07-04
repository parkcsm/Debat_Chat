<?php
include "init_broadcast.php";

$RoomName = $_POST["RoomName"];

$sql = "select * from MessageList where RoomName like '".$RoomName."' ";

$result = mysqli_query($con,$sql);
$response = array();


if(mysqli_num_rows($result)>0)
{
     while($row = mysqli_fetch_row($result))
     {
        $RoomName = $row[1];
        $Sender = $row[2];
        $Msg = $row[3];
        $Regdate = $row[4];
        $Type = $row[5];
        $code = "방송방 불러오기 성공";
        array_push($response,array("code"=>$code,"RoomName"=>$RoomName,"Sender"=>$Sender,"Msg"=>$Msg,
                    "Regdate"=>$Regdate,"Type"=>$Type));
     }
       echo json_encode($response);
}


mysqli_close($con);
?>
