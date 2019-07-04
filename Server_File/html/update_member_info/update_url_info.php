<?php
include "init.php";

$id = $_POST["id"];
$profile_image_url = $_POST["profile_image_url"];
$profile_text = $_POST["profile_text"];


$sql = "select * from user_info where id like '".$id."';";

$result = mysqli_query($con,$sql);
$response = array();

if(mysqli_num_rows($result)>0)
{
         $sql = "update user_info set
         profile_image_url =  '".$profile_image_url."',
         profile_text =  '".$profile_text."'
         where id = '".$id."' ";
         $result = mysqli_query($con,$sql);

        $code = "update_success";
        $message = "회원 프로필정보를 수정했습니다.";
        array_push($response, array("code"=>$code,"message"=>$message));
        echo json_encode($response);
}
else
{

        $code = "update_failed";
        $message = "회원 정보가 없습니다. 프로필 정보를 수정 할 수 없습니다.";
        array_push($response, array("code"=>$code,"message"=>$message));
        echo json_encode($response);
}

mysqli_close($con);
?>
