<?php
include "init_broadcast.php";

$RoomName = $_POST["RoomName"];
$response = array();

         $sql = "delete from MessageList 
         where RoomName like '".$RoomName."' ";

        $result = mysqli_query($con,$sql);

         $sql = "delete from RoomList 
         where RoomName like '".$RoomName."' ";

        $result = mysqli_query($con,$sql);

        $code = "broad_cast_record_deleted";
        $message = "방송기록을 삭제했습니다.";
        array_push($response, array("code"=>$code,"message"=>$message));
        echo json_encode($response);

mysqli_close($con);

?>
