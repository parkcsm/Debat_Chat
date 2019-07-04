<?php
include "init_board_info.php";

$Writer = $_POST["Writer"];
$Regdate = $_POST["Regdate"];
$Like_Num = (int)$_POST["Like_Num"];
$Plus_or_Minus = $_POST["Plus_or_Minus"];

$response = array();

         $sql = "update post_info set
         Like_Num =  '".$Like_Num."'
         where Writer like '".$Writer."' and Regdate like '".$Regdate."' ";

        $result = mysqli_query($con,$sql);

        $code = "post like +1 successfully updated!";
        $message = "포스트 좋아요가 성공적으로 업데이트되었습니다.";
        array_push($response, array("code"=>$code,"message"=>$message));
        echo json_encode($response);

mysqli_close($con);

?>
