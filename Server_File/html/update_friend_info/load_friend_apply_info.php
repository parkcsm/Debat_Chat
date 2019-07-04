<?php
include "init_friend.php";

$Frequest_Receiver = $_POST["Frequest_Receiver"];

$Request_Result = "apply";

$sql = "select * from Friend_Info where Frequest_Receiver like '".$Frequest_Receiver."' and Request_Result like '".$Request_Result."' ";

$result = mysqli_query($con,$sql);
$response = array();


if(mysqli_num_rows($result)>0)
{
     while($row = mysqli_fetch_row($result))
     {
        $Frequest_Sender = $row[1];
        $code = "나에게 친구신청한 사람들 리스트 불러오기 성공";
        array_push($response,array("code"=>$code,"Frequest_Sender"=>$Frequest_Sender));
        echo json_encode($response);
     }
}


mysqli_close($con);
?>
