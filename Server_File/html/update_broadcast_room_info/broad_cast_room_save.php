<?php
include "init_broadcast.php";

$RoomName = $_POST["RoomName"];
$Detail = $_POST["Detail"];
$Regdate = $_POST["Regdate"];

$response = array();

        $sql = "insert into RoomList(RoomName,Detail,Regdate) values('".$RoomName."','".$Detail."','".$Regdate."');";
        $result = mysqli_query($con,$sql);

        $code = "broad_cast_room successfully created";
        $message = "방송방이 성공적으로 생성되었습니다.)";
        array_push($response, array("code"=>$code,"message"=>$message));
        echo json_encode($response);

mysqli_close($con);

?>
