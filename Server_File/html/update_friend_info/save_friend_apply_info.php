<?php
include "init_friend.php";

$Frequest_Sender = $_POST["Frequest_Sender"];
$Frequest_Receiver = $_POST["Frequest_Receiver"];
$Regdate = $_POST["Regdate"];

$sql = "select * from Friend_Info where Frequest_Sender like '".$Frequest_Sender."' and Frequest_Receiver like '".$Frequest_Receiver."';";
$result = mysqli_query($con,$sql);


$sql2 = "select * from Friend_Info where Frequest_Sender like '".$Frequest_Receiver."' and Frequest_Receiver like '".$Frequest_Sender."';";
$result2 = mysqli_query($con,$sql2);


$response = array();

if(mysqli_num_rows($result)>0)
{
        $code = "friend_apply_insert_duplicate";

        $row = mysqli_fetch_row($result); 
        $Request_Result = $row[3];
        if($Request_Result == "friend"){
         $message = "이미 친구입니다..)";
         } else if($Request_Result == "apply"){
         $message = "이미 친구신청을 하셨습니다.)";
        }
        array_push($response, array("code"=>$code,"message"=>$message));
        echo json_encode($response);
}

else if(mysqli_num_rows($result2)>0)
{
        $code = "friend_apply_insert_duplicate";

       $row = mysqli_fetch_row($result2); 
       $Request_Result = $row[3];
        if($Request_Result == "friend"){
         $message = "이미 친구입니다..)";
         } else if($Request_Result == "apply"){
         $message = "상대방이 이미 친구신청을 한 상태입니다. 친구신청 목록을 확인해주세요.)";
        }
        array_push($response, array("code"=>$code,"message"=>$message));
        echo json_encode($response);

}
else
{
        $sql = "insert into Friend_Info(Frequest_Sender,Frequest_Receiver,Regdate) values('".$Frequest_Sender."','".$Frequest_Receiver."','".$Regdate."');";
        $result = mysqli_query($con,$sql);

        $code = "friend_apply_insert_success";
        $message = "친구신청이 성공적으로 완료됐습니다.)";
        array_push($response, array("code"=>$code,"message"=>$message));
        echo json_encode($response);
}
mysqli_close($con);

?>
