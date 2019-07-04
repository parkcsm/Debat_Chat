<?php
include "init.php";

$id = $_POST["id"];

$sql = "select * from user_info where id like '".$id."' ";

$result = mysqli_query($con,$sql);
$response = array();



if(mysqli_num_rows($result)>0)
{
       while($row = mysqli_fetch_row($result))
     {
        $profile_image_url = $row[5];
        $profile_text = $row[6];
        $code = "id_exist";
        array_push($response,array("code"=>$code,"profile_image_url"=>$profile_image_url,"profile_text"=>$profile_text));
     }
    echo json_encode($response);
}
//else
//{
 //       $code = "id_not_exist";
 //       $message = "아이디가 존재하지 않습니다.";
 //       array_push($response,array("code"=>$code,"message"=>$message));
//        echo json_encode($response);
//}

mysqli_close($con);
?>
