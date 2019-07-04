<?php
include "init_broadcast.php";

$RoomName = $_POST["RoomName"];
$Sender = $_POST["Sender"];
$Msg = $_POST["Msg"];
$Regdate = $_POST["Regdate"];
$Type = $_POST["Type"];

$response = array();

        $sql = "insert into MessageList(RoomName,Sender,Msg,Regdate,Type) values('".$RoomName."','".$Sender."','".$Msg."','".$Regdate."','".$Type."');";
        $result = mysqli_query($con,$sql);

        $code = "message_insert_success";
        $message = "방송방에 메세지가 저장됐습니다 :)";
        array_push($response, array("code"=>$code,"message"=>$message));
        echo json_encode($response);

mysqli_close($con);

?>
