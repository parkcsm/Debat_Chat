<?php
include "init_open_chat.php";

$sql = "select * from RoomList ";
$result = mysqli_query($con,$sql);
$response = array();

if(mysqli_num_rows($result)>0)
{
     while($row = mysqli_fetch_row($result))
     {
        $RoomName = $row[1];
        $Detail = $row[2];
        $code = "나에게 친구신청한 사람들 리스트 불러오기 성공";
        array_push($response,array("code"=>$code,"RoomName"=>$RoomName,"Detail"=>$Detail));
     }
       echo json_encode($response);
}


mysqli_close($con);
?>
