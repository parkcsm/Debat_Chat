<?php
include "init_friend.php";

$Frequest_Sender = $_POST["Frequest_Sender"];
$Frequest_Receiver = $_POST["Frequest_Receiver"];

$Request_Result = "friend";

$response = array();

         $sql = "update Friend_Info set
         Request_Result =  '".$Request_Result."'
         where Frequest_Sender like '".$Frequest_Sender."' and Frequest_Receiver like '".$Frequest_Receiver."' ";

        $result = mysqli_query($con,$sql);

        $code = "friend_accept_success";
        $message = "친구수락이 성공적으로 완료됐습니다.)";
        array_push($response, array("code"=>$code,"message"=>$message));
        echo json_encode($response);

mysqli_close($con);

?>
