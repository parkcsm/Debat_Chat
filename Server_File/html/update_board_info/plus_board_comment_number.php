<?php
include "init_board_info.php";

$Writer = $_POST["Writer"];
$Regdate = $_POST["Regdate"];

$response = array();

         $sql = "update post_info set
         Comment_Num =  Comment_Num+1
         where Writer like '".$Writer."' and Regdate like '".$Regdate."' ";

        $result = mysqli_query($con,$sql);

        $code = "The Number of post comments successfully updated!";
        $message = "포스트 댓글수가 성공적으로 수정되었습니다.";
        array_push($response, array("code"=>$code,"message"=>$message));
        echo json_encode($response);

mysqli_close($con);

?>
