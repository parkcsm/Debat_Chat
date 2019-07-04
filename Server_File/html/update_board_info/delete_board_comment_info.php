<?php
include "init_board_info.php";

$Writer = $_POST["Writer"];
$Regdate = $_POST["Regdate"];

$response = array();

         $sql = "delete from comment_info 
         where Writer like '".$Writer."' and Regdate like '".$Regdate."' ";

        $result = mysqli_query($con,$sql);

        $code = "board_comment_deleted";
        $message = "해당 댓글을 삭제했습니다.";
        array_push($response, array("code"=>$code,"message"=>$message));
        echo json_encode($response);

mysqli_close($con);

?>
