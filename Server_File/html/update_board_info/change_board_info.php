<?php

include "init_board_info.php";

$Writer = $_POST["Writer"];
$Regdate = $_POST["Regdate"];

$Post_Image_Url = $_POST["Post_Image_Url"];
$Post_Text = $_POST["Post_Text"];


$response = array();

         $sql = "update post_info set
         Post_Image_Url =  '".$Post_Image_Url."',
         Post_Text =  '".$Post_Text."'
         where Writer like '".$Writer."' and Regdate like '".$Regdate."' ";

        $result = mysqli_query($con,$sql);

        $code = "post successfully updated!";
        $message = "포스트가 성공적으로 수정되었습니다.";
        array_push($response, array("code"=>$code,"message"=>$message));
        echo json_encode($response);

mysqli_close($con);

?>
