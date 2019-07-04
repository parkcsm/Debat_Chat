<?php
include "init_friend.php";

$Frequest_Sender = $_POST["Frequest_Sender"];
$Frequest_Receiver = $_POST["Frequest_Receiver"];

$response = array();

         $sql = "delete from Friend_Info 
         where Frequest_Sender like '".$Frequest_Sender."' and Frequest_Receiver like '".$Frequest_Receiver."' ";

        $result = mysqli_query($con,$sql);

        $code = "friend_reject_success";
        $message = "해당 친구의 친구신청을 거절했습니다.)";
        array_push($response, array("code"=>$code,"message"=>$message));
        echo json_encode($response);

mysqli_close($con);

?>
