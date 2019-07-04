<?php
include "init_open_chat.php";

$RoomName = $_POST["RoomName"];

$sql = "select * from RoomList where RoomName like '".$RoomName."'";
$result = mysqli_query($con,$sql);



$response = array();

if(mysqli_num_rows($result)>0)
{
        $code = "openchat_roomname_duplicate";
        $message = "동일한 이름의 방이 이미 존재해서 새로운 방을 생성할 수 없습니다.";
        array_push($response, array("code"=>$code,"message"=>$message));
        echo json_encode($response);
} else {
        $code = "openchat_roomname_empty";
        $message = "동일한 이름의 방이 존재하지 않아 새로운 방을 생성합니다.";
        array_push($response, array("code"=>$code,"message"=>$message));
        echo json_encode($response);
}

mysqli_close($con);
?>
