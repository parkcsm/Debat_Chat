<?php
include "init_friend.php";

$id = $_POST["id"];
$Request_Result = "friend";
$sql = "select * from Friend_Info where (Frequest_Receiver like '".$id."' or Frequest_Sender like '".$id."') and Request_Result like '".$Request_Result."' ";

$result = mysqli_query($con,$sql);
$response = array();

if(mysqli_num_rows($result)>0)
{
     while($row = mysqli_fetch_row($result))
     {
        $Frequest_Sender = $row[1];
        $Frequest_Receiver = $row[2];

        if($id == $Frequest_Sender){
            $value = $Frequest_Receiver;
        }
        else if($id == $Frequest_Receiver){
            $value = $Frequest_Sender;
        }
        $code = "내 친구리스트 불러오기 성공";
        array_push($response,array("code"=>$code,"result"=>$value));
     }
        echo json_encode($response);
}
mysqli_close($con);
?>
